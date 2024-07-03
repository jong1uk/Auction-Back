package com.example.backend.dto.admin;

import com.example.backend.entity.Product;
import com.example.backend.entity.enumData.ProductStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdminRespDto {

    //다건조회 DTO
    @Setter
    @Getter
    public static class ReqProductsRespDto {
        private List<ProductDto> products = new ArrayList<>();

        public ReqProductsRespDto(List<Product> products) {
            this.products = products.stream().map(ProductDto::new).collect(Collectors.toList());
        }
        @Setter
        @Getter
        public class ProductDto {

            private String productName;
            private String productBrand;

            public ProductDto(Product product) {
                this.productName = product.getProductName();
                this.productBrand = product.getProductBrand();
            }
        }
    }

    @Getter
    @Setter
    public static class ReqProductRespDto{
        private String productName;
        private String productBrand;
        private String productImg;
        private String modelNum;
        private Long originalPrice;
        private String productSize;

        public ReqProductRespDto(Product product) {
            this.productName = product.getProductName();
            this.productBrand = product.getProductBrand();
            this.productImg = product.getProductImg();
            this.modelNum = product.getModelNum();
            this.originalPrice = product.getOriginalPrice();
            this.productSize = product.getProductSize();
        }
    }

    @Getter
    @Setter
    public static class RegProductRespDto{
        private String productName;
        private String productBrand;
        private String productImg;
        private String modelNum;
        private Long originalPrice;
        private String productSize;
        private ProductStatus productStatus;
        public RegProductRespDto(Product product) {
            this.productName = product.getProductName();
            this.productBrand = product.getProductBrand();
            this.productImg = product.getProductImg();
            this.modelNum = product.getModelNum();
            this.originalPrice = product.getOriginalPrice();
            this.productSize = product.getProductSize();
            this.productStatus = product.getProductStatus();
        }
    }





}