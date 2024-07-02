package com.example.backend.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDto {

    private Long productId;
    private String productPhoto;
    private String productBrand;
    private String productName;
    private String modelNum;
    private BigDecimal originalPrice;
    private int productLike;
    private LocalDateTime createdAt;
}