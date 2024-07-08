package com.example.backend.controller.notice;

import com.example.backend.dto.luckyDraw.LuckyDrawAnnouncementListDto;
import com.example.backend.dto.notice.CombinedNoticeDto;
import com.example.backend.dto.notice.NoticeDto;
import com.example.backend.dto.user.UserDTO;
import com.example.backend.entity.Notice;
import com.example.backend.service.NoticeService;
import com.example.backend.service.announcement.LuckyDrawAnnouncementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@RestController
@Log4j2
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private LuckyDrawAnnouncementService luckyDrawAnnouncementService;

    // 공지사항 등록
    @PostMapping("/noticeRegistration")
    @ResponseStatus(HttpStatus.CREATED)
    public void createNotice(@RequestBody NoticeDto noticeDto,
                             @AuthenticationPrincipal UserDTO userDTO) {
        if (!userDTO.isRole()) {
            throw new RuntimeException("Only administrators can create notices");
        }

        noticeDto.setUserId(userDTO.getUserId());
        Notice createdNotice = noticeService.createNotice(noticeDto);
        log.info("공지사항 생성: {}", createdNotice);
    }

    // 공지사항 조회
    @GetMapping("/noticeList")
    public List<NoticeDto> getAllNoticeLlist(){

        List<NoticeDto> notices = noticeService.getAllNoticeList();
        log.info("공지사항 조회 완료: {}", notices);
        return notices;
    }

    // 공지사항 상세 조회
    @GetMapping("/notice/{noticeId}")
    public NoticeDto getNoticeById(@PathVariable Long noticeId){
        NoticeDto noticeDto = noticeService.getNoticeById(noticeId);
        log.info("공지사항 상세 조회: {}", noticeDto);
        return noticeDto;
    }

    // 공지사항 수정
    @PutMapping("/modifyNotice/{noticeId}")
    public NoticeDto updateNotice(@PathVariable Long noticeId,
                                  @RequestBody NoticeDto noticeDto,
                                  @AuthenticationPrincipal UserDTO userDTO) {
        if (!userDTO.isRole()) {
            throw new RuntimeException("Only administrators can update notices");
        }

        return noticeService.updateNotice(noticeId, noticeDto);
    }


    // 공지사항 삭제
    @DeleteMapping("/deleteNotice/{noticeId}")
    public void deleteNotice(@PathVariable Long noticeId,
                             @AuthenticationPrincipal UserDTO userDTO) {
        if (!userDTO.isRole()) {
            throw new RuntimeException("Only administrators can delete notices");
        }

        noticeService.deleteNotice(noticeId);
    }

    // 공지사항 전체 조회
    @GetMapping("/combinedNoticeList")
    public CombinedNoticeDto getCombinedNoticeList() {
        List<NoticeDto> notices = noticeService.getAllNoticeList();
        List<LuckyDrawAnnouncementListDto> luckyDrawAnnouncements = luckyDrawAnnouncementService.getAllLuckyDrawAnnouncementList();

        CombinedNoticeDto combinedNotice = new CombinedNoticeDto();
        combinedNotice.setNotices(notices);
        combinedNotice.setLuckyDrawAnnouncements(luckyDrawAnnouncements);

        log.info("통합 공지사항 조회 완료: {}", combinedNotice);
        return combinedNotice;
    }
}
