package com.example.backend.controller;

import com.example.backend.dto.luckyDraw.DrawDTO;
import com.example.backend.dto.luckyDraw.LuckyDrawsDTO;
import com.example.backend.service.DrawService;
import com.example.backend.service.LuckyDrawService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("luckydraw")
@Log4j2
public class LuckyDrawController {

    private final LuckyDrawService luckyDrawService;
    private final DrawService drawService;

    @GetMapping("")
    public List<LuckyDrawsDTO> luckyDraws() {
        return luckyDrawService.getAllLuckyDraws();
    }

    @GetMapping("/{luckyId}")
    public LuckyDrawsDTO luckyDrawById(@PathVariable("luckyId") Long luckyId) {
        return luckyDrawService.getLuckyDrawById(luckyId);
    }

//    @PostMapping("/{luckyId}/enter")
//    public DrawDTO enterLuckyDraw(@PathVariable("luckyId") Long luckyId, @RequestBody DrawDTO drawDTO) {
//        drawDTO.setLuckyId(luckyId);
//
//        return drawService.saveDraw(drawDTO);
//    }

    @PostMapping("/{luckyId}/enter")
    public ResponseEntity<DrawDTO> enterLuckyDraw(@PathVariable("luckyId") Long luckyId, @RequestBody DrawDTO drawDTO) {
        drawDTO.setLuckyId(luckyId);
        log.info(drawDTO.toString());

        drawService.saveDraw(drawDTO);
        return ResponseEntity.ok().body(drawDTO);
    }

}