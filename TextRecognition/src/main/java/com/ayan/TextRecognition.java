package com.ayan;

import java.util.List;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;

/**
 * Hello world!
 *
 */
public class TextRecognition {

    public static void main(String[] args) {
        String bucket = "njit-cs-643";
        Region region = Region.US_EAST_1;
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
                "your_access_key_id",
                "your_secret_access_key");
        RekognitionClient rekClient = RekognitionClient.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                // .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        rekClient.close();
        while (true) {
            List<String> indexes = QueueService.getMessages("ayan.fifo");
            for (String index : indexes) {
                System.out.println(index);
                RecognitionService.detectTextLabels(rekClient, bucket, index);
            }
        }
    }
}
