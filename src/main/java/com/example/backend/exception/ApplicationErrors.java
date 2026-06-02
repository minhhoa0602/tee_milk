package com.example.backend.exception;

public class ApplicationErrors {
    // Đã đảo lại: số lỗi nội bộ (code) là tham số thứ 2, còn mã HTTP mạng (400) là tham số thứ 3
    public static ApplicationException EXISTED_USER_BY_EMAIL = new ApplicationException("Email đã tồn tại trên hệ thống!", 1, 400);
    public static ApplicationException WRONG_CONFIRM_PASSWORD = new ApplicationException("Mật khẩu nhập lại không khớp!", 2, 400);
    public static ApplicationException USER_NOT_FOUND = new ApplicationException("Không tìm thấy người dùng!", 3, 400);
    public static ApplicationException OTP_EXPIRED = new ApplicationException("Mã OTP đã hết hạn!", 5, 400);
    public static ApplicationException OTP_ERROR = new ApplicationException("Mã xác thực OTP không chính xác!", 6, 400);
    public static ApplicationException EMAIL_NOT_FOUND = new ApplicationException("Không tìm thấy email người dùng!", 7, 400);
    public static ApplicationException PASSWORD_ERROR = new ApplicationException("Mật khẩu không chính xác!", 8, 400);
    public static ApplicationException VERIFI = new ApplicationException("Tài khoản chưa kích hoạt! Xin hãy xác thực OTP qua Email trước.", 9, 400);
    public static ApplicationException PRODUCT_NOT_FOUND = new ApplicationException("Sản phẩm không tồn tại!", 10, 400);
    public static ApplicationException SIZE_NOT_FOUND = new ApplicationException("Kích cỡ không tồn tại!", 11, 400);
    public static ApplicationException CART_NOT_FOUND = new ApplicationException("Không tìm thấy món trong giỏ!", 12, 400);
    public static ApplicationException ERRORS_USER = new ApplicationException("Bạn không có quyền thao tác!", 13, 400);
    public static ApplicationException ODER_EMPTY = new ApplicationException("Vui lòng tích chọn ít nhất 1 món để thanh toán!", 14, 400);
    public static ApplicationException ODER_ERROR = new ApplicationException("Có món hàng không hợp lệ hoặc đã bị xóa khỏi giỏ", 15, 400);
    public static ApplicationException ADDRESS_ERROR = new ApplicationException("Địa chỉ này không thuộc về bạn!", 16, 400);
    public static ApplicationException ADDRESS_NOT_FOUND = new ApplicationException("Không tìm thấy địa chỉ giao hàng!", 17, 400);
    public static ApplicationException NAMEORPHONE_ERROR = new ApplicationException("Vui lòng nhập tên và số điện thoại người nhận!", 18, 400);
}
