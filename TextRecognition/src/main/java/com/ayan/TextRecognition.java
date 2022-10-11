package com.ayan;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.sqs.SqsClient;

public class TextRecognition {

    public final static String bucket = "njit-cs-643";
    public static final Region region = Region.US_EAST_1;
    public final static String queue = "ayan.fifo";

    public static void fileWriter(String index, String text) throws IOException {
        FileWriter fileWriter = new FileWriter("output.txt", true);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.print("\nIndex: " + index + ", Text: " + text);
        printWriter.close();
        fileWriter.close();
        System.out.println("Wrote for index: " + index);
    }

    public static void main(String[] args) throws IOException {

        // AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
        // "your_access_key_id",
        // "your_secret_access_key");
        RekognitionClient rekClient = RekognitionClient.builder()
                .region(TextRecognition.region)
                // .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        SqsClient sqsClient = SqsClient.builder()
                .region(TextRecognition.region)
                // .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        boolean flag = true;

        while (true) {
            List<String> indexes = QueueService.getMessages(sqsClient, TextRecognition.queue);
            if (indexes.isEmpty()) {
                System.out.println("No messages in the queue");
                continue;
            }
            for (String index : indexes) {
                if (index.equals("-1")) {
                    flag = false;
                    System.out.println("End of queue");
                    break;
                }
                System.out.println(index);
                String text = RecognitionService.detectTextLabels(rekClient, TextRecognition.bucket, index);
                if (text != null) {
                    fileWriter(index, text);
                }
            }
            if (!flag) {
                break;
            }
        }
        rekClient.close();
        sqsClient.close();
    }
}
