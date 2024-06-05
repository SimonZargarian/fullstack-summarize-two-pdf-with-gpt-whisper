package com.kokab.twodocscomparetest.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PdfService{
    List<String> extractTextFromPDFs(List<MultipartFile> files);
}
