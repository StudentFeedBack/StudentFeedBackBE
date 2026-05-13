package com.example.studentfeedback.service;

import com.example.studentfeedback.domain.AiFeedback;
import com.example.studentfeedback.domain.Essay;
import com.example.studentfeedback.domain.Member;
import com.example.studentfeedback.dto.EssayUploadResponse;
import com.example.studentfeedback.repository.AiFeedbackRepository;
import com.example.studentfeedback.repository.EssayRepository;
import com.example.studentfeedback.repository.TeacherFeedbackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EssayServiceTest {

    @Mock
    private EssayRepository essayRepository;
    @Mock
    private AiFeedbackRepository aiFeedbackRepository;
    @Mock
    private TeacherFeedbackRepository teacherFeedbackRepository;
    @Mock
    private OcrService ocrService;
    @Mock
    private AiService aiService;

    @InjectMocks
    private EssayService essayService;

    private Member teacher;

    @BeforeEach
    void setUp() {
        teacher = Member.builder()
                .id(1L)
                .username("teacher1")
                .name("Teacher Kim")
                .build();
    }

    @Test
    void uploadAndAnalyze_Success() {
        // given
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test content".getBytes());
        String studentName = "Student Park";
        String ocrText = "Extracted text";
        AiFeedback aiFeedback = AiFeedback.builder()
                .bookTitle("Test Book")
                .curriculumConnection("Connect")
                .strengths("Good")
                .weaknesses("Bad")
                .build();

        when(ocrService.extractText(file)).thenReturn(ocrText);
        when(aiService.generateInitialFeedback(eq(ocrText), any())).thenReturn(aiFeedback);

        // when
        EssayUploadResponse response = essayService.uploadAndAnalyze(file, studentName, teacher);

        // then
        assertNotNull(response);
        assertEquals(ocrText, response.getOcrText());
        assertEquals("Test Book", response.getAiFeedback().getBookTitle());
        verify(essayRepository, times(1)).save(any(Essay.class));
        verify(aiFeedbackRepository, times(1)).save(any(AiFeedback.class));
    }
}
