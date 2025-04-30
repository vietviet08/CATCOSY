package com.catcosy.customer.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.catcosy.customer.config.EnvConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class GeminiService {

    private CloseableHttpClient httpClient;

    private final EnvConfig envConfig;
    
    @Value("${gemini.api.url:}")
    private String geminiApiUrl;

    @Value("${gemini.api.key:}")
    private String geminiApiKey;
    
    @PostConstruct
    public void init() {
        httpClient = HttpClients.createDefault();
    }

    private String getApiKey() {
        String apiKey = envConfig.getProperty("gemini.api.key", geminiApiKey);
        return apiKey;
    }

    private String getApiUrl() {
        String apiKey = envConfig.getProperty("gemini.api.url", geminiApiUrl);
        return apiKey;
    }

    public String getResponse(String userMessage) throws Exception {
        System.out.println("API URL: " + getApiUrl() + "?key=" + getApiKey());
        HttpPost post = new HttpPost(getApiUrl() + "?key=" + getApiKey());
        post.setHeader("Content-Type", "application/json");

        // Construct the request body for Gemini API
        String requestBody = String.format(
                "{\"contents\": [{\"parts\": [{\"text\": \"%s\"}]}]}",
                userMessage.replace("\"", "\\\"")
        );

        post.setEntity(new StringEntity(requestBody, StandardCharsets.UTF_8));

        try (var response = httpClient.execute(post)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            // Parse the response to extract the generated text
            ObjectMapper mapper = new ObjectMapper();
            var jsonNode = mapper.readTree(result.toString());
            return jsonNode.at("/candidates/0/content/parts/0/text").asText();
        }
    }

    public String checkApiKey() {
        HttpPost post = new HttpPost(getApiUrl() + "?key=" + getApiKey());
        System.out.println("API URL: " + getApiUrl() + "?key=" + getApiKey());
        post.setHeader("Content-Type", "application/json");

        // Gửi một yêu cầu đơn giản để kiểm tra
        String requestBody = "{\"contents\": [{\"parts\": [{\"text\": \"Hello\"}]}]}";
        try {
            post.setEntity(new StringEntity(requestBody, StandardCharsets.UTF_8));
            try (var response = httpClient.execute(post)) {
                int statusCode = response.getCode();
                if (statusCode == 200) {
                    return "API Key is valid.";
                } else if (statusCode == 401 || statusCode == 403) {
                    return "API Key is invalid or unauthorized. Status code: " + statusCode;
                } else {
                    return "Unexpected response. Status code: " + statusCode;
                }
            }
        } catch (Exception e) {
            return "Error checking API Key: " + e.getMessage();
        }
    }
}
