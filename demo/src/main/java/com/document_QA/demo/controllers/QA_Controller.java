package com.document_QA.demo.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/document")
public class QA_Controller {
    @PostMapping("/upload")
    public MultipartFile uploadPDF(@RequestPart MultipartFile File) {

        return File;
    }

}
