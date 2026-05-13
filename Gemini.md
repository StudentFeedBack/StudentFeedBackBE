# Project: Student Feedback App Backend

## 📝 Overview
이 프로젝트는 논술 교사의 학생 글 피드백을 도와주는 AI 학생 피드백 도우미 서비스의 백엔드입니다.

학생이 작성한 글 사진을 업로드하면 서버에서 이미지를 저장하고, OCR을 통해 글 내용을 추출한 뒤, AI가 논술 피드백을 생성합니다.  
교사는 AI가 생성한 피드백을 검토하고 수정할 수 있으며, 학생별 글 제출 이력과 피드백 이력을 관리할 수 있습니다.

## 🛠 Tech Stack
- Language: Java
- Framework: Spring Boot
- Build Tool: Gradle
- Database: MySQL
- ORM: Spring Data JPA, Hibernate
- Security: Spring Security, JWT
- API Documentation: Swagger / Springdoc OpenAPI
- Validation: Bean Validation
- Test: JUnit5, Mockito
- AI Integration: 외부 AI API 연동

🤖 AI Stack
AI Provider:
OpenAI API
AI Model:
초기 개발: 비용 효율적인 경량 모델 사용
고품질 피드백: 추론 능력이 좋은 고성능 모델 사용
AI Integration:
Spring Boot 서버에서 외부 AI API 호출
프론트엔드에서 AI API를 직접 호출하지 않음
OCR Integration:
업로드된 글 사진을 OCR API로 전송
OCR 결과에서 텍스트만 추출
OCR 결과를 AI 피드백 입력값으로 사용
Prompt Management:
프롬프트는 코드에 직접 하드코딩하지 않음
별도 Prompt Builder 또는 Prompt Template 클래스로 관리
피드백 유형별 프롬프트를 분리
AI Response Format:
가능한 경우 JSON 형식으로 응답을 요청
AI 응답은 DTO로 파싱한 뒤 저장
프론트엔드에서 사용하기 쉬운 구조로 가공
AI Safety:
학생 개인정보, 학교명, 연락처 등 민감 정보가 프롬프트에 불필요하게 포함되지 않도록 주의
AI 응답을 최종 정답으로 간주하지 않음
교사가 검토하고 수정할 수 있는 구조로 설계
AI Reliability:
AI API 호출 실패, timeout, rate limit 상황을 예외 처리
OCR 실패와 AI 실패를 구분해서 처리
AI 응답 파싱 실패 시 기본 에러 응답 반환
AI Cost Management:
OCR 결과 텍스트 길이에 따라 token 사용량 고려
긴 글은 문단 단위로 나누어 처리하는 방식을 고려
동일 글에 대한 중복 AI 요청 방지

👁 OCR Stack
OCR Provider:
Google Cloud Vision API
OCR Input:
학생이 업로드한 글 사진
지원 이미지 형식: jpg, jpeg, png, webp
OCR Output:
추출된 원문 텍스트
줄 단위 텍스트
문단 단위로 정리된 텍스트
OCR Processing:
이미지 업로드 후 OCR 수행
OCR 결과를 Essay 엔티티 또는 별도 OcrResult 엔티티에 저장
OCR 결과는 교사가 직접 수정할 수 있도록 설계
OCR Error Handling:
이미지가 흐리거나 글자가 인식되지 않는 경우 예외 처리
지원하지 않는 파일 형식은 업로드 단계에서 차단
OCR API 실패 시 사용자에게 명확한 에러 메시지 반환
OCR Quality Guidelines:
사진 품질이 낮을 경우 재업로드를 안내
기울어진 사진, 어두운 사진, 손글씨 인식 실패 가능성을 고려
OCR 결과는 AI 피드백 전에 교사가 확인할 수 있는 구조를 고려

## 📏 Coding Standards

- 모든 코드는 Java로 작성합니다.
- Spring Boot의 계층형 아키텍처를 따릅니다.
    - `controller`
    - `service`
    - `repository`
    - `domain`
    - `dto`
    - `config`
    - `exception`
- Controller에서는 비즈니스 로직을 작성하지 않습니다.
- Service에서 핵심 비즈니스 로직을 처리합니다.
- Repository는 데이터 접근 역할만 담당합니다.
- Entity를 API 응답으로 직접 반환하지 않습니다.
- 요청과 응답에는 반드시 DTO를 사용합니다.
- 메서드와 클래스 이름은 역할이 명확하게 드러나도록 작성합니다.
- 가독성을 성능 최적화보다 우선합니다.
- 불필요한 추상화는 피하고, 현재 요구사항에 맞는 단순한 구조를 선호합니다.

## 🧱 Architecture Guidelines

### Controller
- REST API 엔드포인트를 정의합니다.
- 요청 값 검증은 `@Valid`를 사용합니다.
- HTTP 상태 코드를 명확하게 반환합니다.
- Controller에서는 Entity를 직접 다루지 않습니다.
- 응답은 공통 Response DTO 또는 명확한 응답 DTO를 사용합니다.