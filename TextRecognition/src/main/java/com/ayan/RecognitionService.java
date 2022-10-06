package com.ayan;

import java.util.List;

import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.DetectTextRequest;
import software.amazon.awssdk.services.rekognition.model.DetectTextResponse;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.RekognitionException;
import software.amazon.awssdk.services.rekognition.model.S3Object;
import software.amazon.awssdk.services.rekognition.model.TextDetection;

public class RecognitionService {
    public static String detectTextLabels(RekognitionClient rekClient, String bucket, String image) {

        try {
            S3Object s3Object = S3Object.builder()
                    .bucket(bucket)
                    .name(image)
                    .build();

            Image myImage = Image.builder()
                    .s3Object(s3Object)
                    .build();

            DetectTextRequest textRequest = DetectTextRequest.builder()
                    .image(myImage)
                    .build();

            DetectTextResponse textResponse = rekClient.detectText(textRequest);
            List<TextDetection> textCollection = textResponse.textDetections();
            System.out.println("Detected lines and words");
            for (TextDetection text : textCollection) {
                System.out.println("Detected: " + text.detectedText());
                System.out.println("Confidence: " + text.confidence().toString());
                System.out.println("Id : " + text.id());
                System.out.println("Parent Id: " + text.parentId());
                System.out.println("Type: " + text.type());
                System.out.println();
            }

        } catch (RekognitionException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return "Done";
    }
}
