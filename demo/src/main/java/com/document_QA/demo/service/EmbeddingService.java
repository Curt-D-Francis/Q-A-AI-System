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

import com.document_QA.demo.model.VoyageAiEmbedding;
import com.document_QA.demo.model.VoyageAiResponse;

import tools.jackson.databind.ObjectMapper;

@Service
public class EmbeddingService {
    @Value("${voyageai.api.key}")
    private String voyageAiApiKey;

    public List<List<Float>> requestEmbedding(List<String> substringChunks) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("input", substringChunks);
        requestBody.put("model", "voyage-4-lite");
        String json = mapper.writeValueAsString(requestBody);
        HttpClient client = HttpClient.newHttpClient();

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.voyageai.com/v1/embeddings"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + voyageAiApiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            VoyageAiResponse responseJson = mapper.readValue(response.body(), VoyageAiResponse.class);
            List<VoyageAiEmbedding> responseData = responseJson.getData();
            List<List<Float>> responseEmbedding = new ArrayList<>();
            for (int i = 0; i < responseData.size(); i++) {
                responseEmbedding.add(responseData.get(i).getEmbedding());
            }
            return responseEmbedding;
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
