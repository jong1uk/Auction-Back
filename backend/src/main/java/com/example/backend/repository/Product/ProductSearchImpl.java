package com.example.backend.repository.Product;

import com.example.backend.dto.product.Detail.*;
import com.example.backend.dto.product.ProductResponseDto;
import com.example.backend.entity.*;
import com.example.backend.entity.enumData.BiddingStatus;
import com.example.backend.entity.enumData.ProductStatus;
import com.example.backend.entity.enumData.SalesStatus;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
@Log4j2

public class ProductSearchImpl implements ProductSearch {

    private final JPAQueryFactory queryFactory;

    private final QProduct product = QProduct.product;

    private final QBuyingBidding buying = QBuyingBidding.buyingBidding;
    private final QSalesBidding sales = QSalesBidding.salesBidding;

    @Override
    public List<ProductResponseDto> searchAllProduct(String mainDepartment) {
        return queryFactory
                .select(Projections.constructor(ProductResponseDto.class,
                        product.productId,
                        product.productImg,
                        product.productBrand,
                        product.productName,
                        product.modelNum,
                        buying.buyingBiddingPrice.min(),
                        product.createDate
                ))
                .from(product)
                .leftJoin(buying).on(product.productId.eq(buying.product.productId))
                .where(product.productStatus.eq(ProductStatus.REGISTERED)
                        .and(buying.biddingStatus.eq(BiddingStatus.PROCESS))
                        .and(product.mainDepartment.eq(mainDepartment)))
                .orderBy(product.createDate.desc())
                .groupBy(product.modelNum)
                .fetch();

    }

    // 소분류 상품 조회
    @Override
    public Slice<ProductResponseDto> subProductInfo(String subDepartment, Pageable pageable) {
        int pageSize = pageable.getPageSize();
        List<ProductResponseDto> products = queryFactory
                .select(Projections.constructor(ProductResponseDto.class,
                        product.productId,
                        product.productImg,
                        product.productBrand,
                        product.productName,
                        product.modelNum,
                        buying.buyingBiddingPrice.min()
                ))
                .from(product)
                .leftJoin(buying).on(product.productId.eq(buying.product.productId))
                .where(product.productStatus.eq(ProductStatus.REGISTERED)
                        .and(buying.biddingStatus.eq(BiddingStatus.PROCESS))
                        .and(product.subDepartment.eq(subDepartment)))
                .groupBy(product.modelNum)
                .offset(pageable.getOffset())
                .limit(pageSize + 1)
                .fetch();

        // 다음 페이지 유무
        boolean hasNext = false;
        if (products.size() > pageSize) {
            products.remove(pageSize);
            hasNext = true;
        }

        // Slice 객체 변환
        return new SliceImpl<>(products, pageable, hasNext);
    }

    // 해당 상품의 사이즈 상관없이 구매(최저), 판매(최고)가 조회
    @Override
    public ProductDetailDto searchProductPrice(String modelNum) {

        log.info("ModelNum : {}", modelNum);
        JPAQuery<Long> lowPriceQuery = queryFactory.select(buying.buyingBiddingPrice.min().castToNum(Long.class))
                .from(buying)
                .where(buying.biddingStatus.eq(BiddingStatus.PROCESS)
                        .and(buying.product.modelNum.eq(modelNum))
                        .and(product.productStatus.eq(ProductStatus.REGISTERED)));

        JPAQuery<Long> topPriceQuery = queryFactory.select(sales.salesBiddingPrice.max().castToNum(Long.class))
                .from(sales)
                .where(sales.salesStatus.eq(SalesStatus.PROCESS)
                        .and(sales.product.modelNum.eq(modelNum))
                        .and(product.productStatus.eq(ProductStatus.REGISTERED)));

        Long lowestPriceLong = lowPriceQuery.fetchOne();
        Long highestPriceLong = topPriceQuery.fetchOne();

        // Long 값을 BigDecimal로 변환
        BigDecimal lowestPrice = (lowestPriceLong != null) ? BigDecimal.valueOf(lowestPriceLong) : BigDecimal.ZERO;
        BigDecimal highestPrice = (highestPriceLong != null) ? BigDecimal.valueOf(highestPriceLong) : BigDecimal.ZERO;

        log.info("Lowest Price: {}", lowestPrice);
        log.info("Highest Price: {}", highestPrice);

        // ProductDetailDto 생성 및 설정
        ProductDetailDto priceValue = new ProductDetailDto();
        priceValue.setBuyingBiddingPrice(lowestPrice);
        priceValue.setSalesBiddingPrice(highestPrice);

        log.info("ProductDetailDto: {}", priceValue);

        return priceValue;
    }

    @Override
    public List<SalesBiddingDto> recentlyTransaction(String modelNum) {

        List<SalesBiddingDto> salesBiddingDtoList = queryFactory.select(Projections.bean(SalesBiddingDto.class,
                        product.productId,
                        product.modelNum,
                        product.productSize,
                        product.latestPrice,
                        product.previousPrice,
                        product.previousPercentage,
                        sales.salesBiddingTime.as("salesBiddingTime"),
                        sales.salesBiddingPrice.as("salesBiddingPrice")
                ))
                .from(product)
                .leftJoin(sales).on(sales.product.eq(product))
                .leftJoin(buying).on(buying.product.eq(product))
                .where(product.modelNum.eq(modelNum)
                        .and(sales.salesStatus.eq(SalesStatus.COMPLETE))
                        .and(buying.biddingStatus.eq(BiddingStatus.COMPLETE))
                        .and(product.productStatus.eq(ProductStatus.REGISTERED)))
                .orderBy(sales.salesBiddingTime.desc())
                .fetch();

        return salesBiddingDtoList.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<SalesHopeDto> salesHopeInfo(String modelNum) {

        return queryFactory.select(Projections.bean(SalesHopeDto.class,
                        sales.salesBiddingPrice,
                        product.productSize,
                        sales.salesQuantity))
                .from(product)
                .leftJoin(sales).on(sales.product.eq(product))
                .where(product.modelNum.eq(modelNum)
                        .and(sales.salesStatus.eq(SalesStatus.PROCESS))
                        .and(product.productStatus.eq(ProductStatus.REGISTERED)))
                .orderBy(product.createDate.desc())
                .fetch();
    }

    @Override
    public List<BuyingHopeDto> buyingHopeInfo(String modelNum) {
        return queryFactory.select(Projections.bean(BuyingHopeDto.class,
                        buying.buyingBiddingPrice,
                        product.productSize,
                        buying.buyingQuantity))
                .from(product)
                .leftJoin(buying).on(buying.product.eq(product))
                .where(product.modelNum.eq(modelNum)
                        .and(buying.biddingStatus.eq(BiddingStatus.PROCESS))
                        .and(product.productStatus.eq(ProductStatus.REGISTERED)))
                .orderBy(product.createDate.desc())
                .fetch();
    }

    @Override
    public List<GroupByBuyingDto> groupByBuyingSize(String modelNum) {
        List<GroupByBuyingDto> groupByBuyingDtoList = queryFactory.select(Projections.bean(GroupByBuyingDto.class,
                        product.productImg,
                        product.productName,
                        product.modelNum,
                        product.productSize,
                        buying.buyingBiddingPrice.min().as("buyingBiddingPrice")))
                .from(product)
                .leftJoin(buying).on(buying.product.eq(product))
                .where(product.modelNum.eq(modelNum)
                        .and(buying.biddingStatus.eq(BiddingStatus.PROCESS))
                        .and(product.productStatus.eq(ProductStatus.REGISTERED)))
                .groupBy(product.productSize)
                .orderBy(buying.buyingBiddingPrice.min().asc())
                .fetch();

        log.info("GroupByBuyingDtoList Success : {}", groupByBuyingDtoList);
        return groupByBuyingDtoList.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<GroupBySalesDto> groupBySalesSize(String modelNum) {
        List<GroupBySalesDto> groupBySalesDtoList = queryFactory.select(Projections.bean(GroupBySalesDto.class,
                        product.productImg,
                        product.productName,
                        product.modelNum,
                        product.productSize,
                        sales.salesBiddingPrice.max().as("productMaxPrice")))
                .from(product)
                .leftJoin(sales).on(sales.product.eq(product))
                .where(product.modelNum.eq(modelNum)
                        .and(sales.salesStatus.eq(SalesStatus.PROCESS))
                        .and(product.productStatus.eq(ProductStatus.REGISTERED)))
                .groupBy(product.productSize)
                .orderBy(sales.salesBiddingPrice.desc())
                .fetch();
        return groupBySalesDtoList.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public BuyingBidResponseDto BuyingBidResponse(BuyingBidRequestDto bidRequestDto) {

        JPAQuery<Long> lowPriceQuery = queryFactory.select(buying.buyingBiddingPrice.min().castToNum(Long.class))
                .from(buying)
                .where(buying.biddingStatus.eq(BiddingStatus.PROCESS)
                        .and(buying.product.modelNum.eq(bidRequestDto.getModelNum()))
                        .and(product.productStatus.eq(ProductStatus.REGISTERED))
                        .and(product.productSize.eq(bidRequestDto.getProductSize())));

        JPAQuery<Long> topPriceQuery = queryFactory.select(sales.salesBiddingPrice.max().castToNum(Long.class))
                .from(sales)
                .where(sales.salesStatus.eq(SalesStatus.PROCESS)
                        .and(sales.product.modelNum.eq(bidRequestDto.getModelNum()))
                        .and(product.productStatus.eq(ProductStatus.REGISTERED))
                        .and(product.productSize.eq(bidRequestDto.getProductSize())));

        Long lowestPriceLong = lowPriceQuery.fetchOne();
        Long highestPriceLong = topPriceQuery.fetchOne();

        // Long 값을 BigDecimal로 변환
        BigDecimal lowestPrice = (lowestPriceLong != null) ? BigDecimal.valueOf(lowestPriceLong) : BigDecimal.ZERO;
        BigDecimal highestPrice = (highestPriceLong != null) ? BigDecimal.valueOf(highestPriceLong) : BigDecimal.ZERO;

        // BuyingBidResponseDto 생성 및 설정
        BuyingBidResponseDto priceValue = BuyingBidResponseDto.builder()
                .productBuyPrice(lowestPrice)
                .productSalePrice(highestPrice)
                .build();

        log.info("BuyingBidResponseDto: {}", priceValue.toString());

        return priceValue;
    }

    @Override
    public List<AveragePriceDto> getAllContractData(String modelNum, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Model Number: " + modelNum);
        log.info("Start Date: " + startDate.toString());
        log.info("End Date: " + endDate.toString());

        List<AveragePriceDto> averagePriceDto = queryFactory.select(Projections.bean(AveragePriceDto.class,
                        sales.salesBiddingTime.as("contractDateTime"),
                        sales.salesBiddingPrice.as("averagePrice")))
                .from(product)
                .leftJoin(sales).on(sales.product.eq(product))
                .leftJoin(buying).on(buying.product.eq(product))
                .where(sales.salesBiddingTime.between(startDate, endDate)
                        .and(product.modelNum.eq(modelNum))
                        .and(product.productStatus.eq(ProductStatus.REGISTERED))
                        .and(buying.biddingStatus.eq(BiddingStatus.COMPLETE))
                        .and(sales.salesStatus.eq(SalesStatus.COMPLETE)))
                .fetch();

        log.info("Query Result: " + averagePriceDto.toString());

        return averagePriceDto;
    }


}