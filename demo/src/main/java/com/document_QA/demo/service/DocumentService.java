package com.document_QA.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.document_QA.demo.model.DocumentChunk;
import com.document_QA.demo.repository.DocumentChunkRepository;

import java.io.IOException;
import java.util.ArrayList;

import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

@Service
public class DocumentService {
    @Autowired
    private EmbeddingService embeddingService;
    @Autowired
    private DocumentChunkRepository documentChunkRepository;

    private String fileExtract(MultipartFile File) {
        try {
            byte[] fileInputStream = File.getBytes();

            try (PDDocument ExtractedFile = Loader.loadPDF(fileInputStream)) {
                PDFTextStripper strip = new PDFTextStripper();
                String fileText = strip.getText(ExtractedFile);
                return fileText;
            }
        } catch (IOException e) {
            System.err.println("Error occured while extracting File");
            return null;
        }
    }

    private List<String> textChunker(String fileText) {
        int MAX_CHUNK_SIZE = 500;
        int Chunk_Overlap = 50;
        List<String> Chunks = new ArrayList<>();

        for (int start = 0; start < fileText.length(); start += (MAX_CHUNK_SIZE - Chunk_Overlap)) {
            String substring = fileText.substring(start, Math.min(start + MAX_CHUNK_SIZE, fileText.length()));
            Chunks.add(substring);
        }
        return Chunks;

    }

    public void processDocument(MultipartFile File) {
        String extractedFile = fileExtract(File);
        List<String> chunkedText = textChunker(extractedFile);
        List<List<Float>> embeddings = embeddingService.requestEmbedding(chunkedText);
        List<DocumentChunk> documentChunks = new ArrayList<>();
        for (int i = 0; i < embeddings.size(); i++) {
            float[] embedding = new float[embeddings.get(i).size()];
            String currText = chunkedText.get(i);
            for (int j = 0; j < embedding.length; j++) {
                embedding[j] = embeddings.get(i).get(j);
            }
            DocumentChunk chunk = new DocumentChunk();
            chunk.setEmbedding(embedding);
            chunk.setChunkText(currText);
            chunk.setDocumentName(File.getOriginalFilename());
            documentChunks.add(chunk);

        }

        documentChunkRepository.saveAll(documentChunks);

    }
}
