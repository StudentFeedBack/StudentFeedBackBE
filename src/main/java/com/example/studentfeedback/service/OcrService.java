package com.example.studentfeedback.service;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class OcrService {

    @Value("${google.cloud.api-key:}")
    private String apiKey;

    public String extractText(MultipartFile file) {
        try {
            List<AnnotateImageRequest> requests = new ArrayList<>();

            ByteString imgBytes = ByteString.copyFrom(file.getBytes());

            Image img = Image.newBuilder().setContent(imgBytes).build();
            Feature feat = Feature.newBuilder().setType(Feature.Type.DOCUMENT_TEXT_DETECTION).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feat)
                    .setImage(img)
                    .build();
            requests.add(request);

            ImageAnnotatorSettings settings;
            if (apiKey != null && !apiKey.isEmpty()) {
                settings = ImageAnnotatorSettings.newBuilder()
                        .setCredentialsProvider(com.google.api.gax.core.NoCredentialsProvider.create())
                        .setHeaderProvider(() -> {
                            java.util.Map<String, String> headers = new java.util.HashMap<>();
                            headers.put("X-Goog-Api-Key", apiKey);
                            return headers;
                        })
                        .build();
            } else {
                settings = ImageAnnotatorSettings.newBuilder().build();
            }

            try (ImageAnnotatorClient client = ImageAnnotatorClient.create(settings)) {
                BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
                List<AnnotateImageResponse> responses = response.getResponsesList();

                for (AnnotateImageResponse res : responses) {
                    if (res.hasError()) {
                        log.error("Error: {}", res.getError().getMessage());
                        return "OCR extraction failed: " + res.getError().getMessage();
                    }

                    TextAnnotation annotation = res.getFullTextAnnotation();
                    return annotation.getText();
                }
            }
        } catch (IOException e) {
            log.error("OCR extraction failed", e);
            throw new RuntimeException("OCR extraction failed", e);
        }
        return "";
    }
}
