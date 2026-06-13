package com.example.backend.controller;

import com.example.backend.service.IOrderService;
import com.example.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
public class WebhookController {
    private final IOrderService orderService;

    @PostMapping("/sepay")
    public ResponseEntity<String> receiveSePayWebhook(@RequestBody Map<String, Object> payload) {
        try {
            // 1. SePay gửi dữ liệu về. Nội dung CK nằm ở "content", số tiền ở "transferAmount"
            String content = (String) payload.get("content");
            Object amountObj = payload.get("transferAmount");

            if (content == null || amountObj == null) {
                return ResponseEntity.ok("Bỏ qua giao dịch không hợp lệ");
            }

            BigDecimal transferAmount = new BigDecimal(amountObj.toString());

            // 2. Dùng Regex để "mò" mã đơn hàng trong nội dung chuyển khoản
            // Khách có thể ghi "Nguyen Van A thanh toan don hang 15", code sẽ tự động lọc lấy số 15
            Pattern pattern = Pattern.compile("teemilk\\s+(\\d+)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(content);

            if (matcher.find()) {
                Integer orderId = Integer.parseInt(matcher.group(1));

                System.out.println("✅ SePay Webhook - Đã nhận " + transferAmount + "đ cho đơn hàng: " + orderId);

                // 3. Gọi hàm trong Service để cập nhật trạng thái đơn hàng
                orderService.handleWebhookPayment(orderId, transferAmount);

            } else {
                System.out.println("⚠️ Không tìm thấy mã đơn hàng trong nội dung CK: " + content);
            }

            // BẮT BUỘC phải trả về mã 200 OK để SePay biết server đã nhận thành công
            return ResponseEntity.ok("{\"success\": true}");

        } catch (Exception e) {
            e.printStackTrace();
            // Dù có lỗi code nội bộ cũng nên trả 200 để SePay không bắn lại tin nhắn này liên tục
            return ResponseEntity.ok("{\"success\": false}");
        }
    }
}
