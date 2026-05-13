package com.example.studentfeedback.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EssayDetailResponse {
    private Long id;
    private String studentName;
    private String imageUrl;
    private String ocrText;
    private AiFeedbackDto aiFeedback;
    private TeacherFeedbackDto teacherFeedback;
}
