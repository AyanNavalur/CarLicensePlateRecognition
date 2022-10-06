package com.ayan;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;

public class TextRecognition {

    public static void fileWriter(String index, String text) throws IOException {
        FileWriter fileWriter = new FileWriter("output.txt");
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.print("Index: " + index + ", Text: " + text);
        printWriter.close();
    }

    public static void main(String[] args) throws IOException {
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
        boolean flag = true;

        while (true) {
            List<String> indexes = QueueService.getMessages("ayan.fifo");
            if (indexes.isEmpty()) {
                System.out.println("No messages in the queue");
                continue;
            }
            for (String index : indexes) {
                if (index.equals("-1")) {
                    System.out.println("End of queue");
                    break;
                }
                System.out.println(index);
                RecognitionService.detectTextLabels(rekClient, bucket, index);
                fileWriter(index, bucket);
            }
            if (!flag) {
                break;
            }
        }
        rekClient.close();
    }
}
