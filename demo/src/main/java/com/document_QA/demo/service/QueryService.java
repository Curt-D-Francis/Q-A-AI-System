package com.document_QA.demo.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.document_QA.demo.model.DocumentChunk;
import com.document_QA.demo.repository.DocumentChunkRepository;

@Service
public class QueryService {
    @Autowired
    private DocumentChunkRepository documentChunkRepository;
    @Autowired
    private EmbeddingService embeddingService;
    @Autowired
    private ClaudeService claudeService;

    public String buildQuery(String question) {
        List<List<Float>> embeddedQuestion = embeddingService.requestEmbedding(Arrays.asList(question));
        List<Float> embeddedVectors = embeddedQuestion.get(0);
        float[] questionVector = new float[embeddedVectors.size()];
        for (int i = 0; i < embeddedVectors.size(); i++) {
            questionVector[i] = embeddedVectors.get(i);
        }
        List<DocumentChunk> alikeChunks = documentChunkRepository.findAlikeChunks(questionVector);
        StringBuilder builtUserQuery = new StringBuilder();
        builtUserQuery.append("Context: ");
        for (int i = 0; i < alikeChunks.size(); i++) {
            builtUserQuery.append(alikeChunks.get(i).getChunkText());
        }
        builtUserQuery.append("\n\nQuestions:" + question);
        String finalUserQuery = builtUserQuery.toString();
        String answer = claudeService.requestClaudeResponse(finalUserQuery);
        return answer;
    }
}
