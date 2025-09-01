# 🌟 Spring Advanced 과제
> **개발 기간 : 2025.08.26 ~ 2025.09.01**

---

## 💁‍♀ 프로젝트 소개
> Spring Boot를 활용한 REST API 기반 웹 애플리케이션 구현

---

## 🛠️ 기술 스택
- **언어:** Java
- **프레임워크:** Spring Boot
- **데이터베이스:** MySQL, H2

---

## ✅ 필수 기능

### Lv 0. 프로젝트 세팅 - 에러 분석
- 프로젝트 실행 시 발생한 에러 원인 분석 후 해결

### Lv 1. ArgumentResolver
- `AuthUserArgumentResolver` 로직 정상 동작

### Lv 2. 코드 개선
#### - 코드 개선 퀴즈: Early Return
- `AuthService` 코드 리팩토링

#### - 리팩토링 퀴즈: 불필요한 if-else 피하기
- `WeatherClient` 코드 리팩토링

#### - 코드 개선 퀴즈: Validation
- `UserService` API validation 적용

### Lv 3. N+1 문제 해결
- Repository N+1 문제 → `@EntityGraph`로 최적화

### Lv 4. 테스트 코드 연습
#### - 테스트 코드 연습 1
- `PasswordEncoderTest` 테스트 의도대로 동작하도록 개선

#### - 테스트 코드 연습 2
- 예외 테스트 코드 적용

---

## 💡 도전 기능

### Lv 5. API 로깅
- AOP 활용
- Admin 권한 API (`deleteComment`, `changeUserRole`) 로깅 적용

### Lv 6. 내가 정의한 문제 & 해결 과정
- 직접 문제 정의 → 해결 → 회고 문서화
- 링크: [문서 확인](https://scarlet-lime-d5d.notion.site/DeleteManager-25e883b537e28039a354fe3281ef9062?source=copy_link)

### Lv 7. 테스트 커버리지
- 테스트 커버리지 결과 이미지  
  ![테스트 커버리지](<img width="654" height="634" alt="Image" src="https://github.com/user-attachments/assets/ec8e8966-9fba-443d-b74c-b9ec539ce980" />) 

---
