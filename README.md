# Student Feedback App Backend

## 🚀 실행 가이드

이 프로젝트는 보안을 위해 민감한 정보를 환경 변수로 관리합니다. 실행 시 아래의 환경 변수들을 설정해야 합니다.

### 필수 환경 변수
| 변수명 | 설명 |
| :--- | :--- |
| `DB_PASSWORD` | MySQL 데이터베이스 비밀번호 |
| `JWT_SECRET` | JWT 토큰 생성 및 검증을 위한 비밀키 |
| `OPENAI_API_KEY` | OpenAI API 키 |
| `GOOGLE_CLOUD_API_KEY` | Google Cloud Vision API 키 |

### 실행 방법

#### 1. 터미널에서 직접 실행 (Gradle)
```bash
./gradlew bootRun --args='--DB_PASSWORD=your_password --JWT_SECRET=your_secret --OPENAI_API_KEY=your_openai_key --GOOGLE_CLOUD_API_KEY=your_google_key'
```

#### 2. JAR 파일 실행
```bash
java -jar build/libs/student-feedback-be-0.0.1-SNAPSHOT.jar \
  --DB_PASSWORD=your_password \
  --JWT_SECRET=your_secret \
  --OPENAI_API_KEY=your_openai_key \
  --GOOGLE_CLOUD_API_KEY=your_google_key
```

#### 3. IntelliJ IDEA 설정
1. `Edit Configurations` 메뉴로 들어갑니다.
2. `Environment variables` 항목에 위 변수들을 추가합니다.
   - 예: `DB_PASSWORD=2580;JWT_SECRET=mysecretkey...`

---
## 🛠 Tech Stack
- Spring Boot 3.2.5
- Java 17
- MySQL
- JPA / Hibernate
- Google Cloud Vision API (OCR)
- OpenAI API (GPT-3.5)
