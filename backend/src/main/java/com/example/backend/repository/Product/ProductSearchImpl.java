package com.example.backend.repository.Product;

import com.example.backend.dto.admin.ProductRespDto;
import com.example.backend.dto.product.Detail.*;
import com.example.backend.dto.product.ProductResponseDto;
import com.example.backend.entity.*;
import com.example.backend.entity.enumData.BiddingStatus;
import com.example.backend.entity.enumData.ProductStatus;
import com.example.backend.entity.enumData.SalesStatus;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
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

    //판매 상품 대분류 조회
    @Override
    public List<ProductRespDto> findProductsByDepartment(String mainDepartment) {

        BooleanExpression buyingCondition = buying.biddingStatus.eq(BiddingStatus.PROCESS);
        BooleanExpression eqMainDepartment = product.mainDepartment.eq(mainDepartment);
        BooleanExpression productCondition = product.productStatus.eq(ProductStatus.REGISTERED);

        // 쿼리 실행 및 결과를 DTO로 매핑
        return queryFactory.select(
                        Projections.constructor(ProductRespDto.class,
                                product.productBrand,  // 상품 브랜드
                                product.productName,  // 상품 이름
                                product.modelNum,  // 모델명
                                product.productImg,  // 상품 이미지
                                product.mainDepartment,  // 대분류
                                // coalesce 함수 사용 부분
                                Expressions.numberTemplate(BigDecimal.class, "coalesce({0}, {1})", // coalesce 함수 사용, BigDecimal 타입으로 반환
                                                buying.buyingBiddingPrice.min(), // 첫 번째 인자: 최소 입찰 가격
                                                product.originalPrice) // 두 번째 인자: 원래 가격
                                        .as("buyingBiddingPrice") // 결과를 "최저가"로 이름 붙임
                        )
                )
                .from(product)
                // salesBidding 테이블과 LEFT JOIN
                .leftJoin(buying)
                .on(buying.product.modelNum.eq(product.modelNum)
                        .and(buyingCondition))  // LEFT JOIN의 ON 절에 조건 추가
                // 조건 결합
                .where(eqMainDepartment.and(productCondition))
                // 모델명을 기준으로 그룹화
                .groupBy(
                        product.modelNum
                )
                // 결과를 리스트로 반환
                .fetch();
    }

    // 모든 상품에 대해 최신 등록순
    @Override
    public List<ProductResponseDto> searchAllProduct(String mainDepartment) {
        return queryFactory
                .select(Projections.constructor(ProductResponseDto.class,
                        product.productId,
                        product.productImg,
                        product.productBrand,
                        product.productName,
                        product.modelNum,
                        buying.buyingBiddingPrice.min().as("biddingPrice"),
                        product.createDate.as("registerDate")
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

    // 모든 상품 구매입찰 등록된게 많은 순서
    @Override
    public List<ProductResponseDto> searchAllProductManyBid(String mainDepartment) {
        return queryFactory
                .select(Projections.constructor(ProductResponseDto.class,
                        product.productId,
                        product.productImg,
                        product.productBrand,
                        product.productName,
                        product.modelNum,
                        buying.buyingBiddingPrice.min().as("biddingPrice"),
                        product.createDate.as("registerDate")
                ))
                .from(product)
                .leftJoin(buying).on(product.productId.eq(buying.product.productId))
                .where(product.productStatus.eq(ProductStatus.REGISTERED)
                        .and(buying.biddingStatus.eq(BiddingStatus.PROCESS))
                        .and(product.mainDepartment.eq(mainDepartment)))
                .orderBy(buying.count().desc())
                .groupBy(product.modelNum)
                .fetch();
    }

    // 가장 낮은 구매가격 + 가장 최신에 입찰이 들어온 순서
    // Dto 생성하기 애매해서 createDate를 받아오지만 실제 체결시간 기준으로 잘불러와지니까 신경쓰지 않아도됌
    @Override
    public List<ProductResponseDto> searchAllProductNewBuying(String mainDepartment) {
        return queryFactory
                .select(Projections.constructor(ProductResponseDto.class,
                        product.productId,
                        product.productImg,
                        product.productBrand,
                        product.productName,
                        product.modelNum,
                        buying.buyingBiddingPrice.min().as("biddingPrice"),
                        product.createDate.as("registerDate")
                ))
                .from(product)
                .leftJoin(buying).on(product.productId.eq(buying.product.productId))
                .where(product.productStatus.eq(ProductStatus.REGISTERED)
                        .and(buying.biddingStatus.eq(BiddingStatus.PROCESS))
                        .and(product.mainDepartment.eq(mainDepartment)))
                .orderBy(buying.buyingBiddingTime.desc())
                .groupBy(product.modelNum)
                .fetch();
    }

    // 판매 입찰이니까 가장 높은거 + 가장 최신에 입찰이 들어온 순서
    // Dto 생성하기 애매해서 createDate를 받아오지만 실제 체결시간 기준으로 잘불러와지니까 신경쓰지 않아도됌
    @Override
    public List<ProductResponseDto> searchAllProductNewSelling(String mainDepartment) {
        return queryFactory
                .select(Projections.constructor(ProductResponseDto.class,
                        product.productId,
                        product.productImg,
                        product.productBrand,
                        product.productName,
                        product.modelNum,
                        sales.salesBiddingPrice.max().as("biddingPrice"),
                        product.createDate.as("registerDate")
                ))
                .from(product)
                .leftJoin(sales).on(product.productId.eq(sales.product.productId))
                .where(product.productStatus.eq(ProductStatus.REGISTERED)
                        .and(sales.salesStatus.eq(SalesStatus.PROCESS))
                        .and(product.mainDepartment.eq(mainDepartment)))
                .orderBy(sales.salesBiddingTime.desc())
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
                        buying.buyingBiddingPrice.min().as("biddingPrice"),
                        product.createDate.as("registerDate")
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