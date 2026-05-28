package com.document_QA.demo.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.document_QA.demo.model.ClaudeAiReponse;

import tools.jackson.databind.ObjectMapper;

@Service
public class ClaudeService {
    @Value("${claude.api.key}")
    private String claudeApiKey;

    public String requestClaudeResponse(String context) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> requestBody = new HashMap<>();

        Map<String, String> messageMap = new HashMap<>();
        messageMap.put("content", context);
        messageMap.put("role", "user");
        List<Map<String, String>> messageObject = new ArrayList<>();

        messageObject.add(messageMap);

        requestBody.put("messages", messageObject);
        requestBody.put("model", "claude-sonnet-4-6");
        requestBody.put("max_tokens", 1024);
        requestBody.put("system",
                "Answer the user's question or comment using only the provided context. If the answer cannot be found in the context, say so.");
        String json = mapper.writeValueAsString(requestBody);
        HttpClient client = HttpClient.newHttpClient();

        try {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.anthropic.com/v1/messages"))
                    .header("Content-Type", "application/json")
                    .header("anthropic-version", "2023-06-01")
                    .header("X-Api-Key", claudeApiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            ClaudeAiReponse responseJson = mapper.readValue(response.body(), ClaudeAiReponse.class);
            String content = responseJson.getContent().get(0).getText();
            return content;

        } catch (IOException e) {
            System.err.println("IOException Error has occured");
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("InterruptedException Error has occured");
            e.printStackTrace();
        }
        return null;
    }
}
