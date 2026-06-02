package com.example.backend.service;

import com.example.backend.config.CustomUserDetails;
import com.example.backend.config.JwtService;
import com.example.backend.dto.request.LoginRequest;
import com.example.backend.dto.request.RegisterRequest;
import com.example.backend.dto.request.VerifyRequest;
import com.example.backend.dto.response.TokenResponse;
import com.example.backend.entity.User;
import com.example.backend.exception.ApplicationErrors;
import com.example.backend.mapper.UserMapper;
import com.example.backend.repository.IAuthRepository;
import com.example.backend.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
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
}
