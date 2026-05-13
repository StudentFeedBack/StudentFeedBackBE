package com.example.studentfeedback.service;

import com.example.studentfeedback.domain.AiFeedback;
import com.example.studentfeedback.domain.Essay;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.model:gpt-4o-mini}")
    private String model;

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    public AiFeedback generateInitialFeedback(String ocrText, Essay essay) {
        String prompt = "다음은 학생이 작성한 논술 글의 OCR 추출 텍스트입니다. " +
                "이 글을 분석하여 다음 정보를 JSON 형식으로 응답해주세요. " +
                "JSON 필드: bookTitle (책 제목), curriculumConnection (교과 연계 내용), strengths (잘한 점), weaknesses (못한 점)\n\n" +
                "글 내용:\n" + ocrText;

        try {
            String response = callOpenAi(prompt);
            JsonNode root = objectMapper.readTree(response);
            JsonNode choice = root.path("choices").get(0).path("message").path("content");
            String content = choice.asText();

            JsonNode feedbackJson = objectMapper.readTree(extractJson(content));

            return AiFeedback.builder()
                    .bookTitle(feedbackJson.path("bookTitle").asText("분석된 책 제목 없음"))
                    .curriculumConnection(feedbackJson.path("curriculumConnection").asText("교과 연계 내용을 분석하지 못했습니다."))
                    .strengths(feedbackJson.path("strengths").asText("잘한 점을 분석하지 못했습니다."))
                    .weaknesses(feedbackJson.path("weaknesses").asText("보완할 점을 분석하지 못했습니다."))
                    .essay(essay)
                    .build();

        } catch (Exception e) {
            log.error("AI feedback generation failed. Returning dummy feedback.", e);
            return createDummyFeedback(essay);
        }
    }

    public String reviewTeacherFeedback(String essayText, String teacherFeedback) {
        String prompt = "학생의 글 내용과 선생님이 작성한 피드백을 바탕으로, 선생님의 피드백이 적절한지 리뷰해주세요. " +
                "개선할 점이나 추가하면 좋을 내용이 있다면 함께 제안해주세요.\n\n" +
                "학생 글:\n" + essayText + "\n\n" +
                "선생님 피드백:\n" + teacherFeedback;

        try {
            String response = callOpenAi(prompt);
            JsonNode root = objectMapper.readTree(response);
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            log.error("AI review failed. Returning dummy review.", e);
            return "AI 리뷰 생성에 실패했습니다. 임시 리뷰입니다: 선생님의 피드백은 학생 글의 장점과 보완점을 조금 더 구체적으로 연결하면 좋습니다.";
        }
    }

    private AiFeedback createDummyFeedback(Essay essay) {

        return AiFeedback.builder()
                .bookTitle("임시 책 제목")
                .curriculumConnection("임시 교과 연계 내용입니다. 글의 주제, 인물의 행동, 문제 해결 과정을 국어 독해 및 쓰기 활동과 연결할 수 있습니다.")
                .strengths("임시 장점 피드백입니다. 학생은 글의 핵심 내용을 이해하고 자신의 생각을 표현하려고 노력했습니다.")
                .weaknesses("임시 보완점 피드백입니다. 주장과 근거를 더 명확히 구분하고, 문단별 중심 생각을 정리하면 글의 완성도가 높아집니다.")
                .essay(essay)
                .build();
    }

    private String callOpenAi(String prompt) {
        String url = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", List.of(
                Map.of("role", "user", "content", prompt)
        ));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        return response.getBody();
    }

    private String extractJson(String content) {
        int start = content.indexOf("{");
        int end = content.lastIndexOf("}");
        if (start != -1 && end != -1 && start < end) {
            return content.substring(start, end + 1);
        }
        return content;
    }
}