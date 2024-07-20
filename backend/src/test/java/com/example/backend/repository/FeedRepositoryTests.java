package com.example.backend.repository;

import com.example.backend.entity.*;
import com.example.backend.repository.Notice.NoticeRepository;
import com.example.backend.repository.FeedBookmark.FeedBookmarkRepository;
import com.example.backend.repository.Inquiry.InquiryRepository;
import com.example.backend.repository.StyleFeed.StyleFeedRepository;
import com.example.backend.repository.User.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Log4j2
@SpringBootTest
public class FeedRepositoryTests {
    @Autowired
    private UserRepository UserRepository;

    @Autowired
    private StyleFeedRepository styleFeedRepository;

    @Autowired
    private FeedBookmarkRepository feedBookmarkRepository;

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InquiryRepository inquiryRepository;

    @Test
    void contextLoads() {
    }

    @Test
    public void testInsertStyleFeed() {

        StyleFeed styleFeed = StyleFeed.builder()
                .feedTitle("타이틀")
                .feedImage("abc1")
                .likeCount(1)
                .build();

        styleFeedRepository.save(styleFeed);
    }

    @Test
    public void testSelectStyleFeed() {

        Long feedId = 1L;
        Optional<StyleFeed> styleFeed = styleFeedRepository.findById(feedId);
        StyleFeed styleFeed1 = styleFeed.orElseThrow();

        log.info("=========================================");
        log.info(styleFeed1);
    }

    @Test
    public void testUpdateStyleFeed() {

        Long feedId = 1L;

        Optional<StyleFeed> result = styleFeedRepository.findById(feedId);
        StyleFeed styleFeed2 = result.orElseThrow();
        styleFeed2.change("집에", "가고싶다");

        styleFeedRepository.save(styleFeed2);
    }

    @Test
    public void testDeleteStyleFeed() {

        Long feedId = 1L;

        styleFeedRepository.deleteById(feedId);
    }

    @Test
    public void testInsertFeedBookmark(){

        Users user = userRepository.findById(1L).orElseThrow();
        StyleFeed styleFeed = styleFeedRepository.findById(3L).orElseThrow();

        FeedBookmark feedBookmark = FeedBookmark.builder()
                .user(user)
                .styleFeed(styleFeed)
                .build();

        feedBookmarkRepository.save(feedBookmark);
    }

    @Test
    public void testSelectFeedBookmark() {

        Long styleSavedId = 1L;
        Optional<FeedBookmark> feedBookmark = feedBookmarkRepository.findById(styleSavedId);
        FeedBookmark result = feedBookmark.orElseThrow();

        log.info("==================================");
        log.info(result);
        log.info("==================================");
    }

    @Test
    @Transactional
    public void testUpdateFeedBookmark() {
        Long styleSavedId = 1L;

        Optional<FeedBookmark> result = feedBookmarkRepository.findById(styleSavedId);
        FeedBookmark feedBookmark = result.orElseThrow();

        Users newUser = userRepository.findById(2L).orElseThrow();
        StyleFeed newStyleFeed = styleFeedRepository.findById(4L).orElseThrow();

        feedBookmark.setUser(newUser);
        feedBookmark.setStyleFeed(newStyleFeed);

        feedBookmarkRepository.save(feedBookmark);
    }

    @Test
    public void testDeleteFeedBookmark() {
        Long styleSavedId = 1L;

        feedBookmarkRepository.deleteById(styleSavedId);
    }

    @Test
    public void testInsertAnnouncement(){

        Notice announcement = Notice.builder()
                .noticeTitle("집에 갈래")
                .noticeContent("집 보내줘")
                .build();

        noticeRepository.save(announcement);
    }

    @Test
    public void testSelectAnnouncement(){

        Long announcementId = 1L;
        Optional<Notice> result = noticeRepository.findById(announcementId);
        Notice announcement1 = result.orElseThrow();

        log.info(announcement1);
    }

    @Test
    public void testUpdateAnnouncement(){

        Long announcementId = 3L;
        Optional<Notice> result = noticeRepository.findById(announcementId);
        Notice announcement = result.orElseThrow();
        announcement.change("안녕하세요", "불향티비입니다.");

        noticeRepository.save(announcement);
    }

    @Test
    public void testDeleteAnnouncement(){

        Long announcementId = 1L;
        noticeRepository.deleteById(announcementId);
    }

    @Test
    public void testInsertInquiry(){

        Users user = userRepository.findById(1L).orElseThrow();

        Inquiry inquiry = Inquiry.builder()
                .inquiryTitle("공지")
                .inquiryContent("점심 뭐 먹을가")
                .user(user)
                .build();

        inquiryRepository.save(inquiry);
    }

    @Test
    public void testSelectInquiry(){

        Long inquiryId = 1L;
        Optional<Inquiry> result = inquiryRepository.findById(inquiryId);
        Inquiry inquiry = result.orElseThrow();

        log.info(inquiry);
        log.info("===============================");
    }

//    @Test
//    public void testUpdateInquiry(){
//
//        Long inquiryId = 1L;
//
//        Optional<Inquiry> result = inquiryRepository.findById(inquiryId);
//        Inquiry inquiry = result.orElseThrow();
//        Inquiry.change("집에", "가고싶다");
//
//        inquiryRepository.save(inquiry);
//    }

    @Test
    public void testDeleteInquiry(){
        Long inquiryId = 1L;
        inquiryRepository.deleteById(inquiryId);
    }
}