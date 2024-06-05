package com.kokab.twodocscomparetest.open_ai;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Service
public class OpenAIServiceImpl implements OpenAIService{

    private static final String API_KEY = "Your-api-key";
    private static final Logger logger = LoggerFactory.getLogger(OpenAIServiceImpl.class);

    public String summarizeText(String file1Content, String file2Content) {
        // Prepare the messages for the comparison task
        JSONArray messages = new JSONArray();

        // System message to set the role of the AI

        String taskMessage = "Please summarize the following two texts:\n\n" + // highlighting key points and differences
                "Text 1: " + file1Content + "\n\n" +
                "Text 2: " + file2Content;

        messages.put(new JSONObject().put("role", "system").put("content", "You are a helpful assistant."));
        messages.put(new JSONObject().put("role", "user").put("content", taskMessage));

        logger.info("Task message: {}", taskMessage);
        try {
            HttpResponse<String> response = Unirest.post("https://api.openai.com/v1/chat/completions")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + API_KEY)
                    .body(new JSONObject()
                            .put("model", "gpt-3.5-turbo")
                            .put("messages", messages)
                            // .put("max_tokens", 150)
                            //.put("temperature", 0.7)
                            .toString())
                    .asString();

            logger.info("Response status: {}", response.getStatus());
            logger.info("Response body: {}", response.getBody());
            //logger.info("Response body: {}", response.getHeaders());

            JSONObject jsonResponse = new JSONObject(response.getBody());

            // Log the entire JSON response
            logger.info("OpenAI response: {}", jsonResponse.toString(4)); // Pretty print the JSON

            JSONArray choices = jsonResponse.getJSONArray("choices");

            if (jsonResponse.has("choices") && !jsonResponse.getJSONArray("choices").isEmpty()) {
                JSONObject firstChoice = jsonResponse.getJSONArray("choices").getJSONObject(0);

                if (firstChoice.has("message")) {
                    JSONObject message = firstChoice.getJSONObject("message");
                    if ("assistant".equals(message.getString("role"))) {
                        String messageContent = message.getString("content");
                        logger.info("OpenAI assistant message: {}", messageContent);
                        return messageContent; // Return the assistant's message content directly
                    }
                }
            }

            return "No response from GPT-3.5-turbo.";
        } catch (UnirestException e) {
            e.printStackTrace();
            return "Error calling OpenAI API";
        }
    }

    public String generateSpeechFromText(String summarizedText) {
        try {
            HttpResponse<byte[]> response = Unirest.post("https://api.openai.com/v1/audio/speech")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + API_KEY)
                    .body(new JSONObject()
                            .put("model", "tts-1")
                            .put("voice", "alloy")
                            .put("input", summarizedText)
                            .toString())
                    .asBytes(); // Get response as byte array

            if (response.getStatus() == 200) {
                String filename = "summarized_speech.mp3";
                Path filePath = Paths.get("src/main/resources/static/" + filename);
                Files.write(filePath, response.getBody(), StandardOpenOption.CREATE);

                // Assuming your Spring Boot app is running on localhost:8080
                // Adjust the URL based on where and how your app is hosted
                String fileUrl = "/"+ filename; // URL to access the file
                logger.info("Audio file generated and saved at {}", filePath);

                return fileUrl;

            } else {
                logger.error("Failed to generate speech: {}", response.getStatusText());
                return null;
            }
        } catch (UnirestException e) {
            logger.error("Error calling OpenAI API", e);
            return null;
        } catch (IOException e) {
            logger.error("Error saving audio file", e);
            return null;
        }
    }


}
