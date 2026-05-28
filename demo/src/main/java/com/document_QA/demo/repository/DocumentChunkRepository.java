package com.document_QA.demo.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.document_QA.demo.model.DocumentChunk;

public interface DocumentChunkRepository extends JpaRepository<DocumentChunk, UUID> {
    @Query(value = "SELECT * FROM document_chunks ORDER BY embedding <=> CAST(:questionVector AS vector) LIMIT 5", nativeQuery = true)
    List<DocumentChunk> findAlikeChunks(@Param("questionVector") float[] questionVector);

}