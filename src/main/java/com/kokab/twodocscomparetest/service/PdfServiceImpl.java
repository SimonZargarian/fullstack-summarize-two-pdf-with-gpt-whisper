package com.kokab.twodocscomparetest.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PdfServiceImpl implements PdfService {

    public List<String> extractTextFromPDFs(List<MultipartFile> files) {
        List<String> texts = new ArrayList<>();

        for (MultipartFile file : files) {
            String extractedText = "";
            try (PDDocument document = PDDocument.load(file.getInputStream())) {
                if (!document.isEncrypted()) {
                    PDFTextStripper stripper = new PDFTextStripper();
                    extractedText = stripper.getText(document);
                }
            } catch (IOException e) {
                e.printStackTrace();
                // Optionally, handle exceptions or throw a custom exception
                // For now, we'll add a placeholder to indicate a failure.
                extractedText = "Error extracting text from one of the files.";
            }
            texts.add(extractedText);
        }

        return texts;
    }
}

