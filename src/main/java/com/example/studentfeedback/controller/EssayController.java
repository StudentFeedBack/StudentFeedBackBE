package com.example.studentfeedback.controller;

import com.example.studentfeedback.common.dto.ApiResponse;
import com.example.studentfeedback.domain.Member;
import com.example.studentfeedback.dto.*;
import com.example.studentfeedback.service.EssayService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/essays")
@RequiredArgsConstructor
public class EssayController {

    private final EssayService essayService;

    @PostMapping("/upload")
    public ApiResponse<EssayUploadResponse> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("studentName") String studentName,
            @AuthenticationPrincipal Member teacher) {
        EssayUploadResponse response = essayService.uploadAndAnalyze(file, studentName, teacher);
        return ApiResponse.success("Upload and analysis successful", response);
    }

    @GetMapping
    public ApiResponse<List<EssayListResponse>> getList(@AuthenticationPrincipal Member teacher) {
        List<EssayListResponse> responses = essayService.getEssayList(teacher);
        return ApiResponse.success(responses);
    }

    @GetMapping("/{id}")
    public ApiResponse<EssayDetailResponse> getDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal Member teacher) {
        EssayDetailResponse response = essayService.getEssayDetail(id, teacher);
        return ApiResponse.success(response);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(
            @PathVariable Long id,
            @RequestBody EssayUpdateRequest request,
            @AuthenticationPrincipal Member teacher) {
        essayService.updateEssay(id, request, teacher);
        return ApiResponse.success("Update successful", null);
    }

    @PostMapping("/{id}/teacher-feedback")
    public ApiResponse<TeacherFeedbackDto> submitTeacherFeedback(
            @PathVariable Long id,
            @RequestBody TeacherFeedbackRequest request,
            @AuthenticationPrincipal Member teacher) {
        TeacherFeedbackDto response = essayService.submitTeacherFeedback(id, request, teacher);
        return ApiResponse.success("Teacher feedback and AI review saved", response);
    }
}
