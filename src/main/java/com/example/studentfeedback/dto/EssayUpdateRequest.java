package com.example.studentfeedback.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EssayUpdateRequest {
    private String studentName;
    private String ocrText;
    private AiFeedbackDto aiFeedback;
}
