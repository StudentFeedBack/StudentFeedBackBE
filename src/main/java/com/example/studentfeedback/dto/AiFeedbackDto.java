package com.example.studentfeedback.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiFeedbackDto {
    private String bookTitle;
    private String curriculumConnection;
    private String strengths;
    private String weaknesses;
}
