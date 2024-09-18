package com.puppeteer.pdf.controller;


import com.puppeteer.pdf.serivce.CreatePDFService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/pdf")
public class CreatePDFController {


    @Autowired
    private CreatePDFService pdfService;


    @GetMapping("/generate/{name}")
    public ResponseEntity<?> createPDF(@PathVariable String name) throws IOException {
        File file =pdfService.createPDF(name);
        Path path = Paths.get(file.getAbsolutePath());
        ByteArrayResource resource=new ByteArrayResource(Files.readAllBytes(path));
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=img.pdf");
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");
        return ResponseEntity.ok()
                .headers(header)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }


}
