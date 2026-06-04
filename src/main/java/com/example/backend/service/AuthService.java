package com.example.backend.service;

import com.example.backend.config.CustomUserDetails;
import com.example.backend.config.JwtAuthFilter;
import com.example.backend.config.JwtService;
import com.example.backend.dto.request.*;
import com.example.backend.dto.response.ResetPasswordRequest;
import com.example.backend.dto.response.ResetPasswordWithTokenRequest;
import com.example.backend.dto.response.TokenResponse;
import com.example.backend.entity.BlacklistedToken;
import com.example.backend.entity.User;
import com.example.backend.exception.ApplicationErrors;
import com.example.backend.mapper.UserMapper;
import com.example.backend.repository.IAuthRepository;
import com.example.backend.repository.IBlacklistedTokenRepository;
import com.example.backend.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.Random;

import static com.example.backend.exception.ApplicationErrors.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Validated
public class AuthService implements IAuthService {

    private final IUserRepository userRepository;
    private final IAuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final IEmailService emailService;
    private final UserMapper userMapper;
    private final IBlacklistedTokenRepository blacklistedTokenRepository;
    private final JwtAuthFilter jwtAuthFilter;

    @Override
    public String generateOtp() {
        Random rand = new Random();
        return String.format("%06d", rand.nextInt(10000));
    }

    @Transactional
    @Override
    public String register(RegisterRequest registerRequest) {
        //validate
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw ApplicationErrors.EXISTED_USER_BY_EMAIL;
        }
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw ApplicationErrors.WRONG_CONFIRM_PASSWORD;
        }
        //ma hoa password
        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());
        registerRequest.setPassword(encodedPassword);
        //convert
        User newUser = userMapper.fromRegister(registerRequest);
        //thiet lap OTP 5 phut
        String otp = generateOtp();
        newUser.setOtp(otp);
        newUser.setOtpExpiryTime(LocalDateTime.now().plusMinutes(5));
        //luu
        authRepository.save(newUser);
        //Bắn email chạy ngầm không làm chậm luồng
        emailService.sendOtpEmail(newUser.getEmail(), otp);

        return "Đăng ký thành công! Hãy kiểm tra hòm thư Email để lấy mã kích hoạt.";
    }

    @Override
    public String verifyAccount(VerifyRequest verifyRequest) {
        //validate
        User user = userRepository.findByEmail(verifyRequest.getEmail())
                .orElseThrow(() -> ApplicationErrors.USER_NOT_FOUND);
        if (user.getIsVerified()){
            return "Tài khoản này đã được kích hoạt từ trước.";
        }
        //kiem tra thoi han ma otp
        if (user.getOtpExpiryTime().isBefore(LocalDateTime.now())) {
            //neu het han sinh ma thi gui lai ma
            String newOtp = generateOtp();
            user.setOtp(newOtp);
            user.setOtpExpiryTime(LocalDateTime.now().plusMinutes(5));
            userRepository.save(user);
            emailService.sendOtpEmail(user.getEmail(), newOtp);
            throw ApplicationErrors.OTP_EXPIRED;
        }
        //kiem tra dung otp
        if (!user.getOtp().equals(verifyRequest.getOtp())) {
            throw ApplicationErrors.OTP_ERROR;
        }
        //otp dung-> kich hoat
        user.setIsVerified(true);
        user.setOtp(null);
        user.setOtpExpiryTime(null);
        userRepository.save(user);

        return "Kích hoạt tài khoản thành công! Bây giờ bạn đã có thể đăng nhập.";
    }

    @Override
    public TokenResponse login(LoginRequest loginRequest) {
        //validate
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> ApplicationErrors.EMAIL_NOT_FOUND);
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw ApplicationErrors.PASSWORD_ERROR;
        }
        if (!user.getIsVerified()) {
            throw ApplicationErrors.VERIFI;
        }

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String token = jwtService.generateToken(userDetails);

        return TokenResponse.builder()
                .token(token)
                .role(user.getRole().name())
                .message("Đăng nhập thành công!")
                .build();
    }

    // ==================== LOGIC 1: YÊU CẦU QUÊN MẬT KHẨU ====================
    @Override
    public String requestForgotPassword(ForgotPasswordRequest request) {
        // 1. Kiểm tra xem email có tồn tại trên hệ thống không
        User user = userRepository.findByEmail(request.getEmail().trim())
                .orElseThrow(() -> new com.example.backend.exception.ApplicationException("Email này chưa được đăng ký tài khoản!", 404, 404));

        // 2. Sinh mã OTP mới và cập nhật vào bảng User (Tái sử dụng các cột OTP đã có ở luồng Đăng ký)
        String otp = generateOtp();
        user.setOtp(otp);
        // Nếu bảng User của Đốm có cột thời gian hết hạn OTP (Ví dụ: expiryTime), hãy set cộng thêm 5 phút ở đây

        userRepository.save(user);

        // 3. Gửi OTP qua Email của khách hàng
        emailService.sendOtpEmail(user.getEmail(), otp);

        return "Mã xác thực OTP đã được gửi đến email của bạn. Vui lòng kiểm tra!";
    }

    // ==================== BƯỚC 2: XÁC THỰC OTP (Tận dụng logic so sánh của Đốm) ====================
    @Override
    @Transactional
    public String verifyForgotPasswordOtp(ForgotPasswordVerifyRequest request) {
        User user = userRepository.findByEmail(request.getEmail().trim())
                .orElseThrow(() -> new com.example.backend.exception.ApplicationException("Không tìm thấy người dùng!", 404, 404));

        // Tận dụng chính xác logic so sánh OTP mà bạn đã chạy thành công
        if (user.getOtp() == null || !user.getOtp().equals(request.getOtp().trim())) {
            throw new com.example.backend.exception.ApplicationException("Mã OTP không chính xác!", 400, 400);
        }

        // Nếu OTP đúng -> Sinh ra 1 chuỗi Reset Token ngẫu nhiên (chìa khóa phụ)
        String resetToken = java.util.UUID.randomUUID().toString();

        // Lưu token vào DB và xóa mã OTP cũ đi để không bị dùng lại
        user.setResetToken(resetToken);
        user.setOtp(null);
        userRepository.save(user);

        // Trả Token này về cho Mobile làm "bằng chứng" để đi tiếp sang màn sau
        return resetToken;
    }

    // ==================== BƯỚC 3: ĐẶT LẠI MẬT KHẨU BẰNG TOKEN ====================
    @Override
    @Transactional
    public String resetPasswordWithToken(ResetPasswordWithTokenRequest request) {
        User user = userRepository.findByEmail(request.getEmail().trim())
                .orElseThrow(() -> new com.example.backend.exception.ApplicationException("Không tìm thấy người dùng!", 404, 404));

        // Kiểm tra xem Mobile có mang đúng cái resetToken mà BE đã phát ở bước 2 lên không
        if (user.getResetToken() == null || !user.getResetToken().equals(request.getResetToken().trim())) {
            throw new com.example.backend.exception.ApplicationException("Hành động không hợp lệ! Vui lòng xác thực lại OTP.", 403, 403);
        }

        if (request.getNewPassword() == null || request.getNewPassword().trim().length() < 6) {
            throw new com.example.backend.exception.ApplicationException("Mật khẩu mới phải từ 6 ký tự trở lên!", 400, 400);
        }

        // Tiến hành đổi mật khẩu
        String encodedPassword = passwordEncoder.encode(request.getNewPassword().trim());
        user.setPassword(encodedPassword);

        // Xóa nốt cái Reset Token đi để vô hiệu hóa hoàn toàn luồng này
        user.setResetToken(null);
        userRepository.save(user);

        return "Đổi mật khẩu thành công! Bạn có thể đăng nhập bằng mật khẩu mới.";
    }

    @Override
    @Transactional
    public String logout(String bearerToken) {
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            throw new com.example.backend.exception.ApplicationException("Token không hợp lệ!", 400, 400);
        }

        String token = bearerToken.substring(7);

        if (blacklistedTokenRepository.existsByToken(token)) {
            return "Đã đăng xuất trước đó!";
        }

        // Đốm kiểm tra xem trong file JwtService của bạn hàm lấy ngày hết hạn tên là gì nhé.
        // Thường sẽ là extractExpiration(token) hoặc getExpirationDateFromToken(token) trả về kiểu java.util.Date
        Date expirationDate = jwtService.extractExpiration(token);

        LocalDateTime expiryTime = expirationDate.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime();

        BlacklistedToken blacklistedToken = BlacklistedToken.builder()
                .token(token)
                .expiryTime(expiryTime)
                .build();

        blacklistedTokenRepository.save(blacklistedToken);

        return "Đăng xuất thành công!";
    }
}
