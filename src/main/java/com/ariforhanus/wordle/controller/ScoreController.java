package com.ariforhanus.wordle.controller;


import com.ariforhanus.wordle.dto.ScoreRequest;
import com.ariforhanus.wordle.dto.ScoreResponse;
import com.ariforhanus.wordle.service.ScoreService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ScoreController {
    private final ScoreService service;

    public ScoreController(ScoreService service){
        this.service=service;
    }

    @PostMapping("/scores")
    public ResponseEntity<ScoreResponse> submit(@Valid @RequestBody ScoreRequest req){
        return ResponseEntity.ok(service.save(req));
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<ScoreResponse>> global(){
        return ResponseEntity.ok(service.topGlobal());
    }

    @GetMapping("/leaderboard/daily")
    public ResponseEntity<List<ScoreResponse>> daily(){
        return ResponseEntity.ok(service.dailyTop(10, ZoneId.of("Europe/Istanbul")));
    }


}
