package com.example.studentfeedback.service;

import com.example.studentfeedback.domain.AiFeedback;
import com.example.studentfeedback.domain.Essay;
import com.example.studentfeedback.domain.Member;
import com.example.studentfeedback.domain.TeacherFeedback;
import com.example.studentfeedback.dto.*;
import com.example.studentfeedback.exception.CustomException;
import com.example.studentfeedback.repository.AiFeedbackRepository;
import com.example.studentfeedback.repository.EssayRepository;
import com.example.studentfeedback.repository.TeacherFeedbackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EssayService {

    private final EssayRepository essayRepository;
    private final AiFeedbackRepository aiFeedbackRepository;
    private final TeacherFeedbackRepository teacherFeedbackRepository;
    private final OcrService ocrService;
    private final AiService aiService;

    private final String uploadDir = "uploads/";

    @Transactional
    public EssayUploadResponse uploadAndAnalyze(MultipartFile file, String studentName, Member teacher) {
        // 1. 이미지 저장
        String imageUrl = saveImage(file);

        // 2. OCR 추출
        String ocrText = ocrService.extractText(file);

        // 3. Essay 엔티티 생성 및 저장
        Essay essay = Essay.builder()
                .studentName(studentName)
                .imageUrl(imageUrl)
                .ocrText(ocrText)
                .teacher(teacher)
                .build();
        essayRepository.save(essay);

        // 4. AI 1차 피드백 생성
        AiFeedback aiFeedback = aiService.generateInitialFeedback(ocrText, essay);
        aiFeedbackRepository.save(aiFeedback);

        return EssayUploadResponse.builder()
                .essayId(essay.getId())
                .ocrText(ocrText)
                .aiFeedback(toAiFeedbackDto(aiFeedback))
                .build();
    }

    public List<EssayListResponse> getEssayList(Member teacher) {
        return essayRepository.findAllByTeacher(teacher).stream()
                .map(essay -> EssayListResponse.builder()
                        .id(essay.getId())
                        .studentName(essay.getStudentName())
                        .bookTitle(essay.getAiFeedback() != null ? essay.getAiFeedback().getBookTitle() : "")
                        .createdAt(essay.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    public EssayDetailResponse getEssayDetail(Long id, Member teacher) {
        Essay essay = essayRepository.findById(id)
                .orElseThrow(() -> new CustomException("Essay not found", HttpStatus.NOT_FOUND));

        if (!essay.getTeacher().getId().equals(teacher.getId())) {
            throw new CustomException("Access denied", HttpStatus.FORBIDDEN);
        }

        return EssayDetailResponse.builder()
                .id(essay.getId())
                .studentName(essay.getStudentName())
                .imageUrl(essay.getImageUrl())
                .ocrText(essay.getOcrText())
                .aiFeedback(toAiFeedbackDto(essay.getAiFeedback()))
                .teacherFeedback(toTeacherFeedbackDto(essay.getTeacherFeedback()))
                .build();
    }

    @Transactional
    public void updateEssay(Long id, EssayUpdateRequest request, Member teacher) {
        Essay essay = essayRepository.findById(id)
                .orElseThrow(() -> new CustomException("Essay not found", HttpStatus.NOT_FOUND));

        if (!essay.getTeacher().getId().equals(teacher.getId())) {
            throw new CustomException("Access denied", HttpStatus.FORBIDDEN);
        }

        essay.setStudentName(request.getStudentName());
        essay.setOcrText(request.getOcrText());

        AiFeedback aiFeedback = essay.getAiFeedback();
        if (aiFeedback != null) {
            aiFeedback.setBookTitle(request.getAiFeedback().getBookTitle());
            aiFeedback.setCurriculumConnection(request.getAiFeedback().getCurriculumConnection());
            aiFeedback.setStrengths(request.getAiFeedback().getStrengths());
            aiFeedback.setWeaknesses(request.getAiFeedback().getWeaknesses());
        }
    }

    @Transactional
    public TeacherFeedbackDto submitTeacherFeedback(Long id, TeacherFeedbackRequest request, Member teacher) {
        Essay essay = essayRepository.findById(id)
                .orElseThrow(() -> new CustomException("Essay not found", HttpStatus.NOT_FOUND));

        if (!essay.getTeacher().getId().equals(teacher.getId())) {
            throw new CustomException("Access denied", HttpStatus.FORBIDDEN);
        }

        // AI 리뷰 생성
        String aiReview = aiService.reviewTeacherFeedback(essay.getOcrText(), request.getContent());

        TeacherFeedback teacherFeedback = essay.getTeacherFeedback();
        if (teacherFeedback == null) {
            teacherFeedback = TeacherFeedback.builder()
                    .content(request.getContent())
                    .aiReview(aiReview)
                    .essay(essay)
                    .build();
            teacherFeedbackRepository.save(teacherFeedback);
        } else {
            teacherFeedback.setContent(request.getContent());
            teacherFeedback.setAiReview(aiReview);
        }

        return toTeacherFeedbackDto(teacherFeedback);
    }

    private String saveImage(MultipartFile file) {
        try {
            Path path = Paths.get(uploadDir);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = path.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Image upload failed", e);
        }
    }

    private AiFeedbackDto toAiFeedbackDto(AiFeedback aiFeedback) {
        if (aiFeedback == null) return null;
        return AiFeedbackDto.builder()
                .bookTitle(aiFeedback.getBookTitle())
                .curriculumConnection(aiFeedback.getCurriculumConnection())
                .strengths(aiFeedback.getStrengths())
                .weaknesses(aiFeedback.getWeaknesses())
                .build();
    }

    private TeacherFeedbackDto toTeacherFeedbackDto(TeacherFeedback teacherFeedback) {
        if (teacherFeedback == null) return null;
        return TeacherFeedbackDto.builder()
                .content(teacherFeedback.getContent())
                .aiReview(teacherFeedback.getAiReview())
                .build();
    }
}
