package com.example.backend.controller;

import com.example.backend.dto.mypage.accountSettings.AccountDTO;
import com.example.backend.dto.mypage.accountSettings.AccountReqDTO;
import com.example.backend.dto.mypage.addressSettings.AddressDto;
import com.example.backend.dto.mypage.addressSettings.AddressReqDto;
import com.example.backend.dto.mypage.main.BookmarkProductsDto;
import com.example.backend.dto.mypage.main.MypageMainDto;
import com.example.backend.dto.mypage.saleHistory.SaleHistoryDto;
import com.example.backend.dto.mypage.drawHistory.DrawHistoryDto;
import com.example.backend.dto.mypage.buyHistory.BuyHistoryDto;
import com.example.backend.dto.user.*;
import com.example.backend.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
@Log4j2
public class MypageController {

    private final UserService userService;
    private final AddressService addressService;
    private final AccountService accountService;
    private final OrdersService ordersService;
    private final SalesBiddingService salesBiddingService;
    private final DrawService drawService;
    private final BookmarkProductService bookmarkProductService;

    /**
     * 마이페이지 메인
     */
    @GetMapping("")
    public ResponseEntity<MypageMainDto> getMyPage(@AuthenticationPrincipal UserDTO userDTO) {
        Long userId = userDTO.getUserId();

        MypageMainDto mypageMainDto = userService.getMyPageInfo(userId);

        return ResponseEntity.ok(mypageMainDto);
    }


    /**
     * 내 정보 - 프로필 관리
     * 회원 정보 수정
     */
    @PutMapping("/modify")
    public ResponseEntity<String> modifyUser(@RequestBody UserModifyDTO userModifyDTO) {
        log.info("UserModifyDTO: {}", userModifyDTO);

        userService.modifyUser(userModifyDTO);
        // TODO: 사용자 검증 방법 통일

        return ResponseEntity.ok("User information updated successfully!");
    }


    /**
     * 내 정보 - 배송지
     * 배송지 조회, 등록, 수정
     */
    // 배송지 조회
    @GetMapping("/address")
    public List<AddressDto> getAddress(@AuthenticationPrincipal UserDTO userDTO) {
        Long userId = userDTO.getUserId();

        return addressService.getAllAddress(userId);
    }

    // 배송지 등록
    @PostMapping("/address")
    public ResponseEntity<AddressDto> addAddress(@Valid @RequestBody AddressReqDto addressReqDto, @AuthenticationPrincipal UserDTO userDTO) {
        Long userId = userDTO.getUserId();

        AddressDto addressDto = addressService.addAddress(userId, addressReqDto);

        return ResponseEntity.ok(addressDto);
    }

    // 배송지 수정
    @PutMapping("/address")
    public ResponseEntity<AddressDto> modifyAddress(@Valid @RequestBody AddressReqDto addressReqDto, @RequestParam Long addressId, @AuthenticationPrincipal UserDTO userDTO){
        Long userId = userDTO.getUserId();

        AddressDto addressDto = addressService.updateAddress(userId, addressId, addressReqDto);

        return ResponseEntity.ok(addressDto);
    }


    /**
     * 내 정보 - 계좌
     * 계좌 조회 및 등록(수정)
     */
    // 등록 계좌 조회
    @GetMapping("/account")
    public ResponseEntity<AccountDTO> getAccount(@AuthenticationPrincipal UserDTO userDTO) {

        Long userId = userDTO.getUserId();

        AccountDTO accountDTO =  accountService.getAccount(userId);

        return ResponseEntity.ok(accountDTO);
    }

    // 계좌 등록 및 수정
    @PostMapping("/account")
    public ResponseEntity<AccountDTO> registerOrModifyAccount(@RequestBody AccountReqDTO accountReqDTO, @AuthenticationPrincipal UserDTO userDTO) {

        log.info("AccountReqDTO: {}", accountReqDTO);

        Long userId = userDTO.getUserId();

        AccountDTO accountDTO = accountService.updateAccount(userId, accountReqDTO);

        return ResponseEntity.ok(accountDTO);
    }


    /**
     * 쇼핑 정보 - 구매 내역
     * 전체, 진행 중, 종료 건수
     * 구매 내역 전체 조회 (주문 날짜 최신순 정렬)
     */
    @GetMapping("/buyHistory")
    public ResponseEntity<BuyHistoryDto> getBuyHistory(@AuthenticationPrincipal UserDTO userDTO) {
        Long userId = userDTO.getUserId();

        BuyHistoryDto buyHistoryDTO = ordersService.getBuyHistory(userId);

        return ResponseEntity.ok(buyHistoryDTO);
    }


    /**
     * 쇼핑 정보 - 판매 내역
     * 전체, 검수 중, 진행 중, 종료 건수
     * 판매 내역 전체 조회 (판매 입찰 시간 최신순 정렬)
     */
    @GetMapping("/saleHistory")
    ResponseEntity<SaleHistoryDto> getSaleHistory(@AuthenticationPrincipal UserDTO userDTO) {
        Long userId = userDTO.getUserId();

        SaleHistoryDto saleHistoryDTO = salesBiddingService.getSaleHistory(userId);

        return ResponseEntity.ok(saleHistoryDTO);
    }


    /**
     * 쇼핑 정보 - 응모 내역
     * 전체, 진행 중, 당첨 건수
     * 럭키드로우 응모 내역 전체 조회 (당첨발표일 최신순 정렬)
     */
    // TODO: 추후 당첨, 미당첨 필터 추가
    @GetMapping("/drawHistory")
    public ResponseEntity<DrawHistoryDto> getDrawHistory(@AuthenticationPrincipal UserDTO userDTO) {
        Long userId = userDTO.getUserId();

        DrawHistoryDto drawHistoryDto = drawService.getDrawHistory(userId);

        return ResponseEntity.ok(drawHistoryDto);
    }


    /**
     * 쇼핑 정보 - 관심
     * 1) 관심 상품
     */
    @GetMapping("/bookmark/product")
    public List<BookmarkProductsDto> getBookmarkProduct(@AuthenticationPrincipal UserDTO userDTO) {
        Long userId = userDTO.getUserId();

        return bookmarkProductService.getAllBookmarkProducts(userId);
    }


    /**
     * 쇼핑 정보 - 관심
     * 2) 관심 스타일
     */
//    @GetMapping("/bookmark/style")

}
