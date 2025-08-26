package com.example.GOLDNet.controller;

import com.example.GOLDNet.dto.ResumeUpsertRequest;
import com.example.GOLDNet.dto.ResumeResponse;
import com.example.GOLDNet.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/resumes")
public class ResumesController {
    private final ResumeService resumeService;

    @GetMapping
    public ResponseEntity<ResumeResponse> getResume(@RequestParam Long memberId){
        return ResponseEntity.ok(resumeService.getResume(memberId));
    }

    @PostMapping
    public ResponseEntity<ResumeResponse> upsertResume(@RequestParam Long memberId,
                                                       @RequestBody ResumeUpsertRequest request){
        return ResponseEntity.ok(resumeService.upsertResume(memberId, request));
    }
}
