package com.example.studentfeedback.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherFeedbackDto {
    private String content;
    private String aiReview;
}
