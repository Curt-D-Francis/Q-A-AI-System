package com.document_QA.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import jakarta.persistence.GenerationType;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "document_chunks")
@Data
public class DocumentChunk {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "document_name")
    private String documentName;
    @Column(name = "chunk_text")
    private String chunkText;
    @Column(name = "embedding_vector")
    private List<Float> vector;

}
