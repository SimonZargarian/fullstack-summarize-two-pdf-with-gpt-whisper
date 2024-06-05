package com.kokab.twodocscomparetest.open_ai;

public interface OpenAIService {
    String summarizeText(String file1Content, String file2Content);

    String generateSpeechFromText(String summarizedText);




}
