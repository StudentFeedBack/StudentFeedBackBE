package com.example.studentfeedback.repository;

import com.example.studentfeedback.domain.Essay;
import com.example.studentfeedback.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EssayRepository extends JpaRepository<Essay, Long> {
    List<Essay> findAllByTeacher(Member teacher);
}
