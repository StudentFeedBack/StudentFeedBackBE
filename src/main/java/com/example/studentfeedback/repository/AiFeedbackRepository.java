package com.example.studentfeedback.repository;

import com.example.studentfeedback.domain.AiFeedback;
import com.example.studentfeedback.domain.Essay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AiFeedbackRepository extends JpaRepository<AiFeedback, Long> {
    Optional<AiFeedback> findByEssay(Essay essay);
}
