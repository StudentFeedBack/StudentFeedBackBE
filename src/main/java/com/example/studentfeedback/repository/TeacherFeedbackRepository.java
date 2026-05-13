package com.example.studentfeedback.repository;

import com.example.studentfeedback.domain.Essay;
import com.example.studentfeedback.domain.TeacherFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeacherFeedbackRepository extends JpaRepository<TeacherFeedback, Long> {
    Optional<TeacherFeedback> findByEssay(Essay essay);
}
