package com.document_QA.demo.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.document_QA.demo.service.DocumentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/document")
public class QA_Controller {
    @Autowired
    private DocumentService documentService;

    @PostMapping("/upload")
    public void uploadPDF(@RequestPart("Uploaded_PDF_File") MultipartFile File) {

        documentService.processDocument(File);
    }

}
