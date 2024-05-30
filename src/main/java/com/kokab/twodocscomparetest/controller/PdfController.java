package com.kokab.twodocscomparetest.controller;

import com.kokab.twodocscomparetest.open_ai.OpenAIService;
import com.kokab.twodocscomparetest.service.PdfServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/pdf")
public class PdfController {

    @Autowired
    private PdfServiceImpl pdfService;

    @Autowired
    private OpenAIService openAIService;

    /*@Autowired
    public PdfController(PdfServiceImpl pdfService) {
        this.pdfService = pdfService;
    }*/

    @PostMapping("/extract-texts")
    public ResponseEntity<Map<String, Object>> extractTexts(@RequestParam("file1") MultipartFile file1,
                                                     @RequestParam("file2") MultipartFile file2) {
        List<MultipartFile> files = Arrays.asList(file1, file2);
        List<String> texts = pdfService.extractTextFromPDFs(files);
        System.out.println("File 1: " + texts.get(0) + "File 2: " + texts.get(1));

        String summarizedText = openAIService.summarizeText(texts.get(0), texts.get(1));

        String mp3Url = openAIService.generateSpeechFromText(summarizedText);

        Map<String, Object> response = new HashMap<>();
        response.put("texts", texts);
        response.put("mp3Url", mp3Url);

        System.out.println("file1 : " + texts.get(0) + "file1 : " + texts.get(1));

        System.out.println("Sum text: " + summarizedText);

        return ResponseEntity.ok(response);
    }
}
