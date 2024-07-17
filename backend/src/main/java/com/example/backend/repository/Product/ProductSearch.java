package com.example.backend.repository.Product;


import com.example.backend.dto.product.Detail.*;
import com.example.backend.dto.product.ProductResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductSearch {

    List<ProductResponseDto> searchAllProduct(String mainDepartment);

    // 소분류 상품 전체 보기
    Slice<ProductResponseDto> subProductInfo(String subDepartment, Pageable pageable);

    // 상품 최고, 최저 입찰 희망가격 조회
    ProductDetailDto searchProductPrice(String modelNum);

    // 해당 상품의 기존 체결가가 있는지 확인
    List<SalesBiddingDto> recentlyTransaction(String modelNum);

    List<SalesHopeDto> salesHopeInfo(String modelNum);

    List<BuyingHopeDto> buyingHopeInfo(String modelNum);

    List<GroupByBuyingDto> groupByBuyingSize(String modelNum);

    List<GroupBySalesDto> groupBySalesSize(String modelNum);

    BuyingBidResponseDto BuyingBidResponse(BuyingBidRequestDto bidRequestDto);

    List<AveragePriceDto> getAllContractData(String modelNum, LocalDateTime startDate, LocalDateTime endDate);

}
