package com.example.studentfeedback.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class EssayListResponse {
    private Long id;
    private String studentName;
    private String bookTitle;
    private LocalDateTime createdAt;
}
