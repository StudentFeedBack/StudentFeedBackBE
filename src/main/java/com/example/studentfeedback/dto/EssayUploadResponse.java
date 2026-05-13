package com.example.studentfeedback.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EssayUploadResponse {
    private Long essayId;
    private String ocrText;
    private AiFeedbackDto aiFeedback;
}
