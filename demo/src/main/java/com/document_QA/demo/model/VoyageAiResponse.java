package com.document_QA.demo.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VoyageAiResponse {
    private List<VoyageAiEmbedding> data;
}
