package com.example.backend.dto.request;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReviewRequest {
    @NotNull(message = "Mã đơn hàng không được để trống")
    private Integer orderId;

    @NotNull(message = "Mã sản phẩm không được để trống")
    private Integer productId;

    @Min(value = 1, message = "Số sao thấp nhất là 1")
    @Max(value = 5, message = "Số sao cao nhất là 5")
    @NotNull(message = "Vui lòng chọn số sao đánh giá")
    private Integer ratingStar;

    private String comment;
    private String imageUrl;
}
