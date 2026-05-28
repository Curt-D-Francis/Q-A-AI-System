package com.document_QA.demo.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.document_QA.demo.model.QueryRequest;
import com.document_QA.demo.service.DocumentService;
import com.document_QA.demo.service.QueryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/document")
public class QA_Controller {
    @Autowired
    private DocumentService documentService;

    @PostMapping("/upload")
    public void uploadPDF(@RequestPart("Uploaded_PDF_File") MultipartFile File) {

        documentService.processDocument(File);
    }

    @Autowired
    private QueryService querysService;

    @PostMapping("/query")
    public String postMethodName(@RequestBody QueryRequest request) {
        return querysService.buildQuery(request.getQuestion());
    }

}
