package com.example.backend.repository.Product;

import com.example.backend.dto.admin.*;
import com.example.backend.entity.*;
import com.example.backend.entity.enumData.BiddingStatus;
import com.example.backend.entity.enumData.ProductStatus;
import com.example.backend.entity.enumData.SalesStatus;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Log4j2
public class AdminProductImpl implements AdminProduct {

    private final JPAQueryFactory queryFactory;

    QProduct product = new QProduct("product");
    QBuyingBidding buying = new QBuyingBidding("buyingBidding");
    QSalesBidding sale = new QSalesBidding("salesBidding");
    QUsers user = new QUsers("user");

    private BooleanExpression eqMain(String mainDepartment){    // 대분류값이 존재하면
        if (StringUtils.isBlank(mainDepartment)){
            return null;
        }
        // selectfrom(product) where p.mainDepartment = :mainDepartment
        return product.mainDepartment.eq(mainDepartment);
    }

    private BooleanExpression eqSub(String subDepartment){  // 소분류 값이 존재하면
        if (StringUtils.isBlank(subDepartment)){
            return null;
        }
        // selectfrom(product) where p.subDepartment = :subDepartment
        return product.subDepartment.eq(subDepartment);
    }

    private BooleanExpression isRegistered(){   //ProductStatus.REGISTERED
        return product.productStatus.eq(ProductStatus.REGISTERED);
    }

    private BooleanExpression eqModelNum(String modelNum){  //modelNum 상풍 조회
        return product.modelNum.eq(modelNum);
    }

    private BooleanExpression eqProductSize(String productSize){    //productSize=
        if (StringUtils.isBlank(productSize)){
            return null;
        }
        return product.productSize.eq(productSize);
    }
// 판매입찰 -> 입찰가격 낮은순으로 정렬
    //구매입찰 -> 입찰가격 높은순으로 정렬





    //판매 상품 대분류 소분류별로 조회
    @Override
    public List<AdminProductDto> getProductsByDepartment(String mainDepartment, String subDepartment) {
        QProduct product = new QProduct("product");
        log.info("mainDepartment {} subDepartment from 쿼리디에셀", mainDepartment, subDepartment);
        return queryFactory.select(
//                순서 중요
                        Projections.constructor(AdminProductDto.class,

                                product.productId,
                                product.productName,
                                product.modelNum,
                                product.productBrand,
                                product.productSize,
                                product.mainDepartment,
                                product.subDepartment
                        )
                ).from(product)
                .where(
                        eqMain(mainDepartment).and(isRegistered()),
                        eqSub(subDepartment)
                )
                .distinct() //중복 제거
                .fetch();
    }

    //
//    //상품상세 조회(모델명 + 사이즈) 기준으로 구분, 사이즈별 구매입찰, 판매입찰 상태 조회,
//    @Override
//    public List<AdminProductRespDto> getDetailedProduct(String modelNum, String productSize) {
//        QProduct product = QProduct.product;
//        QBuyingBidding buyingBidding = QBuyingBidding.buyingBidding;
//        QSalesBidding salesBidding = QSalesBidding.salesBidding;
//        QUsers buyer = new QUsers("buyer");
//        QUsers seller = new QUsers("seller");
//        log.info("modelNum {} productSize {}", modelNum, productSize);
//
//        BooleanExpression modelNumCondition = product.modelNum.eq(modelNum);
//        BooleanExpression sizeCondition = productSize != null ? product.productSize.eq(productSize) : null;
//        BooleanExpression saleCondition1 = salesBidding.salesStatus.eq(SalesStatus.PROCESS);
//        BooleanExpression saleCondition2 = salesBidding.salesStatus.eq(SalesStatus.INSPECTION);
//        BooleanExpression buyingCondition = buyingBidding.biddingStatus.eq(BiddingStatus.PROCESS );
//
//        List<AdminProductDetailDto> productDetails = queryFactory
//                .select(Projections.fields(
//                        AdminProductDetailDto.class,
//                        product.productId.as("productId"),
//                        product.productImg.as("productImg"),
//                        product.productBrand.as("productBrand"),
//                        product.productName.as("productName"),
//                        product.modelNum,
//                        product.originalPrice,
//                        product.productQuantity.as("productQuantity"),
//                        product.productSize.as("productSize")
//                ))
//                .from(product)
//                .where(modelNumCondition.and(sizeCondition != null ? sizeCondition : null).and(isRegistered()))
//                .fetch();
//
//        List<BuyinBiddingDto> buyingBiddings = queryFactory
//                .select(Projections.fields(
//                        BuyinBiddingDto.class,
//                        buyingBidding.buyingBiddingId.as("buyingBiddingId"),
//                        buyingBidding.buyingBiddingPrice.as("buyingBiddingPrice"),
//                        Projections.fields(
//                                AdminUserDto.class,
//                                buyer.email.as("email"),
//                                buyer.nickname.as("nickname")
//                        ).as("buyer")
//                ))
//        .from(buyingBidding)
//                .innerJoin(buyingBidding.user, buyer)
//                .where(buyingBidding.product.modelNum.eq(modelNum)
//                        .and(sizeCondition != null ? buyingBidding.product.productSize.eq(productSize) : modelNumCondition).and(isRegistered()).and(buyingCondition))
//                .fetch();
////                .where(buyingBidding.product.modelNum.eq(modelNum))
////                .fetch();
//
//        List<SalesBiddingDto> sales = queryFactory
//                .select(Projections.fields(
//                        SalesBiddingDto.class,
//                        salesBidding.salesBiddingId.as("salesBiddingId"),
//                        salesBidding.salesBiddingPrice.as("salesBiddingPrice"),
//                        salesBidding.salesStatus.as("salesStatus"),
//                        Projections.fields(
//                                AdminUserDto.class,
//                                seller.email.as("email"),
//                                seller.nickname.as("nickname")
//                        ).as("seller")
//                ))
//                .from(salesBidding)
//                .innerJoin(salesBidding.user, seller)
//                .where(salesBidding.product.modelNum.eq(modelNum)
//                        .and(sizeCondition != null ? salesBidding.product.productSize.eq(productSize) : modelNumCondition).and(isRegistered()).and(saleCondition1).or(saleCondition2))
//                .fetch();
////                .where(salesBidding.product.modelNum.eq(modelNum))
////                .fetch();
//
////        return productDetails.stream().map(detail -> AdminProductRespDto.builder()
////                .adminProductDetailDto(detail)
////                .buyingBiddingDtoList(buyingBiddings)
////                .salesBiddingDtoList(sales)
////                .build()).collect(Collectors.toList());
//
//        return productDetails.stream().map(detail -> AdminProductRespDto.builder()
//                .adminProductDetailDto(detail)
//                .buyingBiddingDtoList(buyingBiddings)
//                .salesBiddingDtoList(sales)
//                .build()).collect(Collectors.toList());
//
//    }
    @Override
    public List<AdminProductRespDto> getDetailedProduct(String modelNum, String productSize) {
        QProduct product = QProduct.product;
        QBuyingBidding buyingBidding = QBuyingBidding.buyingBidding;
        QSalesBidding salesBidding = QSalesBidding.salesBidding;
        QUsers buyer = new QUsers("buyer");
        QUsers seller = new QUsers("seller");
        log.info("modelNum {} productSize {}", modelNum, productSize);

        BooleanExpression modelNumCondition = product.modelNum.eq(modelNum);
        BooleanExpression sizeCondition = productSize != null ? product.productSize.eq(productSize) : null;
        BooleanExpression saleCondition1 = salesBidding.salesStatus.eq(SalesStatus.PROCESS);
        BooleanExpression saleCondition2 = salesBidding.salesStatus.eq(SalesStatus.INSPECTION);
        BooleanExpression buyingCondition = buyingBidding.biddingStatus.eq(BiddingStatus.PROCESS);

        List<AdminProductDetailDto> productDetails = queryFactory
                .select(Projections.fields(
                        AdminProductDetailDto.class,
                        product.productId.as("productId"),
                        product.productImg.as("productImg"),
                        product.productBrand.as("productBrand"),
                        product.productName.as("productName"),
                        product.modelNum,
                        product.originalPrice,
                        product.productQuantity.as("productQuantity"),
                        product.productSize.as("productSize")
                ))
                .from(product)
                .where(modelNumCondition.and(sizeCondition != null ? sizeCondition : null).and(isRegistered()))
                .fetch();

        // 각 상품에 대해 해당 사이즈의 입찰 정보 필터링
        return productDetails.stream().map(detail -> {
            List<BuyinBiddingDto> filteredBuyingBiddings = queryFactory
                    .select(Projections.fields(
                            BuyinBiddingDto.class,
                            buyingBidding.buyingBiddingId.as("buyingBiddingId"),
                            buyingBidding.buyingBiddingPrice.as("buyingBiddingPrice"),
                            Projections.fields(
                                    AdminUserDto.class,
                                    buyer.email.as("email"),
                                    buyer.nickname.as("nickname")
                            ).as("buyer")
                    ))
                    .from(buyingBidding)
                    .innerJoin(buyingBidding.user, buyer)
                    .where(buyingBidding.product.modelNum.eq(modelNum)
                            .and(buyingBidding.product.productSize.eq(detail.getProductSize()))
                            .and(isRegistered())
                            .and(buyingCondition))
                    .fetch();

            List<SalesBiddingDto> filteredSalesBiddings = queryFactory
                    .select(Projections.fields(
                            SalesBiddingDto.class,
                            salesBidding.salesBiddingId.as("salesBiddingId"),
                            salesBidding.salesBiddingPrice.as("salesBiddingPrice"),
                            salesBidding.salesStatus.as("salesStatus"),
                            Projections.fields(
                                    AdminUserDto.class,
                                    seller.email.as("email"),
                                    seller.nickname.as("nickname")
                            ).as("seller")
                    ))
                    .from(salesBidding)
                    .innerJoin(salesBidding.user, seller)
                    .where(salesBidding.product.modelNum.eq(modelNum)
                            .and(salesBidding.product.productSize.eq(detail.getProductSize()))
                            .and(isRegistered())
                            .and(saleCondition1.or(saleCondition2)))
                    .fetch();

            return AdminProductRespDto.builder()
                    .adminProductDetailDto(detail)
                    .buyingBiddingDtoList(filteredBuyingBiddings)
                    .salesBiddingDtoList(filteredSalesBiddings)
                    .build();
        }).collect(Collectors.toList());
    }

    //범수
    //판매 상품 대분류 조회
    @Override
    public List<ProductRespDto> findProductsByDepartment(String mainDepartment) {
        QProduct product = new QProduct("product");
        QSalesBidding salesBidding = new QSalesBidding("salesBidding");

        BooleanExpression saleCondition = salesBidding.salesStatus.eq(SalesStatus.PROCESS);
        BooleanExpression eqMainDepartment = product.mainDepartment.eq(mainDepartment);
        BooleanExpression productCondition = product.productStatus.eq(ProductStatus.REGISTERED);
//        BooleanExpression eqSubDepartment = product.subDepartment.eq(subDepartment);

        // 쿼리 실행 및 결과를 DTO로 매핑
        return queryFactory.select(
                        Projections.constructor(ProductRespDto.class,
                                product.productId,  // 상품 ID
                                product.productBrand,  // 상품 브랜드
                                product.productName,  // 상품 이름
                                product.modelNum,  // 모델명
                                product.productImg,  // 상품 이미지
                                product.mainDepartment,  // 대분류
                                product.subDepartment,  // 소분류
                                product.productSize,
                                salesBidding.salesBiddingPrice.min().as("최저가")  // 최저가
                        )
                )
                .from(product)
                // salesBidding 테이블과 LEFT JOIN
                .leftJoin(salesBidding)
                .on(salesBidding.product.modelNum.eq(product.modelNum)
                        .and(saleCondition))  // LEFT JOIN의 ON 절에 조건 추가
                // 조건 결합
                .where(eqMainDepartment.and(productCondition))
                // 모델명을 기준으로 그룹화
                .groupBy(
                        product.modelNum
                )
                // 결과를 리스트로 반환
                .fetch();
    }
}













