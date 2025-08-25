package com.example.GOLDNet.service;

import com.example.GOLDNet.domain.JobPosting;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JobPostingAiParser {

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Value(("${openai.api.url}"))
    private String openaiApiUrl;


    public JobPosting parse(Map<String, String> crawledData) {
        String district = crawledData.get("구");
        String content = crawledData.get("상세내용");
        String prompt = createPromptForGpt(district, content);
        String rawResponse = callGptApi(prompt);


        String cleanedJson = extractJsonString(rawResponse);

        try {
            return objectMapper.readValue(cleanedJson, JobPosting.class);
        } catch (JsonProcessingException e) {
            System.err.println("AI가 반환한 JSON 파싱 실패: " + cleanedJson);
            throw new RuntimeException("Failed to parse JSON response from AI.", e);
        }
    }

    /**
     * RestTemplate을 사용하여 OpenAI Chat Completions API를 호출합니다.
     */
    private String callGptApi(String prompt) {
        var headers = new HttpHeaders();
        headers.setBearerAuth(openaiApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> message = Map.of(
                "role", "user",
                "content", prompt
        );
        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4o",
                "messages", List.of(message)
        );

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        String rawResponse = restTemplate.postForObject(openaiApiUrl, requestEntity, String.class);

        try {
            JsonNode root = objectMapper.readTree(rawResponse);
            return root.path("choices").path(0).path("message").path("content").asText();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse the main API response structure.", e);
        }
    }

    private String createPromptForGpt(String district, String content) {
        return """
        다음은 하나의 채용 공고에 대한 텍스트 데이터입니다. 이 텍스트를 분석하여 아래의 JSON 객체 형식으로 정확하게 변환해주세요.

        [분석할 텍스트]
        - 구: "%s"
        - 상세내용: "%s"

        [추출 규칙 및 JSON 형식]
        - 모든 값은 문자열이어야 합니다.
        - 정보를 찾을 수 없는 필드는 반드시 `null`로 설정해주세요.
        - `location` 필드는 주어진 "구" 값을 사용하세요.
        - `salaryInfo` 필드에는 '임금액' 또는 '활동비'에서 숫자만 추출하여 입력하세요 (예: "29만원" -> "290000").
        - `workDays`와 `workHours`는 '주근무시간' 또는 '활동시간'에서 각각 분리하여 추출하세요.
        - `recruitmentConditions` 필드에는 '신청자격'과 '신청제외자' 내용을 요약하여 넣어주세요.
        - `detailedDescription` 필드에는 '직무내용' 전체를 넣어주세요.
        - `title` 필드는 '구인사항' 또는 '등록자' 정보에서 핵심적인 공고 제목을 추출하세요.
        - 최종 결과는 오직 JSON 객체 하나만 반환해야 합니다. **Markdown 코드 블록(` ``` `)이나 다른 설명은 절대 추가하지 마세요.**
        
        {
          "title": "...",
          "category": "...",
          "salaryInfo": "...",
          "location": "%s",
          "preferredAgeGroup": "...",
          "industry": "...",
          "brandName": "...",
          "workDays": "...",
          "workHours": "...",
          "recruitmentConditions": "...",
          "workRegion": "...",
          "detailedDescription": "..."
        }
        """.formatted(district, content, district);
    }
    private String extractJsonString(String rawResponse) {
        // ` ```json ` 과 ` ``` ` 사이의 내용 찾음
        String cleaned = rawResponse.trim();
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }
        return cleaned.trim();
    }
}