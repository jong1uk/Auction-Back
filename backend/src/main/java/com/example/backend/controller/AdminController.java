package com.example.backend.controller;


import com.example.backend.dto.admin.*;
import com.example.backend.entity.LuckyDraw;
import com.example.backend.entity.Product;
import com.example.backend.entity.enumData.LuckyProcessStatus;
import com.example.backend.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping("/admin")
@RequiredArgsConstructor
@Log4j2
@RestController
public class AdminController {

    private final AdminService adminService;
    //요청상품 전체조회
    @GetMapping("/requests")
    public ResponseEntity<?> findReqProduct() {
        AdminRespDto.ReqProductsRespDto regProductRespDto = adminService.reqProducts();
        return new ResponseEntity<>(regProductRespDto, HttpStatus.OK);
    }

    //요청상품 단건 조회
    @GetMapping("/requests/{productId}")
    public ResponseEntity<?> findReqProduct(@PathVariable Long productId) {
        AdminRespDto.ReqProductRespDto reqProductRespDto = adminService.reqProduct(productId);
        return new ResponseEntity<>(reqProductRespDto, HttpStatus.OK);
    }

    // 요청상품 판매상품으로 등록
    @PutMapping("/requests/{productId}")
    public ResponseEntity<?> acceptReqProduct(@PathVariable Long productId) {
        try {
            AdminRespDto.RegProductRespDto regProductRespDto = adminService.acceptRequest(productId);
            return new ResponseEntity<>(regProductRespDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    //관리자 상품 카테고리별 조회(대분류, 소분류)
    @GetMapping("/products/{mainDepartment}")
    public ResponseEntity<?> getProductsByDepartment(@PathVariable String mainDepartment, @RequestParam(value = "subDepartment", required = false) String subDepartment) {

        AdminRespDto.AdminProductResponseDto products= adminService.getProducts(mainDepartment, subDepartment);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    //관리자 상품 상세 조회(구매입찰 + 판매입찰)
    @GetMapping("/products/detailed/{modelNum}")
    public ResponseEntity<?> findDetailedProduct(@PathVariable String modelNum, @RequestParam(value = "productSize", required = false) String productSize) {
        log.info("$$$$$$$$$$$$$$");
        log.info("modelNum{} productSize{}", modelNum, productSize);

        //modelnum을 이용해서 상품 상세 조회, size에 따라 카테고리 조회하듯이 입찰 정보 불러오기
        AdminRespDto.AdminProductDetailRespDto detailedProduct =  adminService.getDetailProduct(modelNum, productSize);
        return new ResponseEntity<>(detailedProduct,HttpStatus.OK);
    }

    //판매입찰(검수중) 상품 검수 승인
    //위에서 판매입찰 id를 받아와서 해당 판매입찰 상태 변경
    @PostMapping("/sales/{salesBiddingId}/approve")
    public ResponseEntity<AdminRespDto.ChangeRespDto> acceptSaleBidding(@PathVariable Long salesBiddingId){
        //salesBiddingId를 통하여 검수요청중인 salesStatus = INSPECTION ->PROCESS 으로 변경

        AdminRespDto.ChangeRespDto acceptSaleRespDto = adminService.acceptSales(salesBiddingId);

        return new ResponseEntity<>(acceptSaleRespDto,HttpStatus.OK);
    }

    //관리자 럭키드로우 LuckDrawStatus로 다건 조회
    @GetMapping("/luckyList")
    public ResponseEntity<?> findLuckydarwList(@RequestParam(defaultValue = "READY") LuckyProcessStatus luckyProcessStatus) {
        AdminRespDto.LuckyDrawsRespDto luckyDrawList = adminService.getLuckyDraws(luckyProcessStatus);
        return new ResponseEntity<>(luckyDrawList,HttpStatus.OK);
    }

    //럭키드로우 상품 등록
    @PostMapping("/luckydraw/insert")
    public ResponseEntity<?> registerLuckyDraw(@ModelAttribute AdminReqDto.AdminLuckDrawDto adminLuckDrawDto){

        //관리자가 상품 폼만 등록, 실제 럭키드로우 상품을 schedule을 통하여 등록됨
        log.info("럭키드로우등록"+adminLuckDrawDto.getLuckyName());
        log.info("포토"+adminLuckDrawDto.getLuckyphoto());
        log.info("콘텐츠"+adminLuckDrawDto.getContent());
        log.info("피플"+adminLuckDrawDto.getLuckyPeople());
        log.info("이미지"+adminLuckDrawDto.getLuckyImage());
        log.info("--------------------------------------------");


        AdminReqDto.AdminLuckDrawDto result = adminService.insertLucky(adminLuckDrawDto);

        //관리자가 상품 폼만 등록, 실제 럭키드로우 상품을 schedule을 통하여 등록됨
        log.info("럭키드로우등록"+adminLuckDrawDto.getLuckyName());
        log.info("포토"+adminLuckDrawDto.getLuckyphoto());
        log.info("콘텐츠"+adminLuckDrawDto.getContent());
        log.info("피플"+adminLuckDrawDto.getLuckyPeople());
        log.info("이미지"+adminLuckDrawDto.getLuckyImage());


        return new ResponseEntity<>(result,HttpStatus.OK);
    }
}
