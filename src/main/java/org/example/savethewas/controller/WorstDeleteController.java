package org.example.savethewas.controller;

import lombok.RequiredArgsConstructor;
import org.example.savethewas.service.WorstDeleteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class WorstDeleteController {

    private final WorstDeleteService worstDeleteService;

    @PostMapping(value = "/worst-delete")
    public WorstDeleteService.WorstDeleteResult worstDelete(@RequestParam String region) {
        return worstDeleteService.deleteMenusWorst(region); // 2.6GB를 잡아먹는다
    }

    // 조건 받아서 job 생성 (비동기 시작)
    @PostMapping(value = "/jobs/menu-delete")
    public ResponseEntity<Void> postMenuDelete() {
        // TODO implement
        return ResponseEntity.ok().build();
    }

    // 상태/진행률 조회
    @GetMapping(value = "/jobs/{jobId}")
    public ResponseEntity<Void> getJob() {
        // TODO implement
        return ResponseEntity.ok().build();
    }
}
