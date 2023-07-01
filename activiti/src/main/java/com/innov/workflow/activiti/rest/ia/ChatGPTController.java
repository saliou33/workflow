package com.innov.workflow.activiti.rest.ia;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api")

public class ChatGPTController {

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String OPENAI_API_KEY = "YOUR_API_KEY";

//    private ProcessEngine processEngine;


    private String getChatGptResponse(String prompt) {
        RestTemplate restTemplate = new RestTemplate();

        // Set headers with API key and content type
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(OPENAI_API_KEY);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Set the request payload
        Map<String, Object> requestPayload = new HashMap<>();
        requestPayload.put("prompt", prompt);
        requestPayload.put("max_tokens", 100);
        // Add any other desired parameters to the requestPayload

        // Send a POST request to the OpenAI ChatGPT API
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestPayload, headers);
        ResponseEntity<ChatGptResponse> responseEntity = restTemplate.exchange(
                OPENAI_API_URL,
                HttpMethod.POST,
                requestEntity,
                ChatGptResponse.class
        );

        // Extract the response body from the ResponseEntity
        ChatGptResponse chatGptResponse = responseEntity.getBody();
        if (chatGptResponse != null) {
            return chatGptResponse.getChoices().get(0).getText();
        } else {
            throw new RuntimeException("Failed to get response from ChatGPT API");
        }
    }

    private boolean isXml(String response) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(response)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Define a class to map the response from the ChatGPT API
    private static class ChatGptResponse {
        private List<Choice> choices;

        public List<Choice> getChoices() {
            return choices;
        }

        public void setChoices(List<Choice> choices) {
            this.choices = choices;
        }
    }

    private static class Choice {
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
