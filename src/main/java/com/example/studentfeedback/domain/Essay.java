package com.example.studentfeedback.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Essay extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String studentName;

    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String ocrText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member teacher;

    @OneToOne(mappedBy = "essay", cascade = CascadeType.ALL)
    private AiFeedback aiFeedback;

    @OneToOne(mappedBy = "essay", cascade = CascadeType.ALL)
    private TeacherFeedback teacherFeedback;
}
