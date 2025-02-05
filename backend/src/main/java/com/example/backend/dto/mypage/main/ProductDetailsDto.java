package com.example.backend.dto.mypage.main;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductDetailsDto {

    private Long productId;
    private String productImg;
    private String productBrand;
    private String productName;
    private String modelNum;
}
