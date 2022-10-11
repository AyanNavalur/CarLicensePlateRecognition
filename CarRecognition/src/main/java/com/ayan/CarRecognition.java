package com.ayan;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
// import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.sqs.SqsClient;

public class CarRecognition {

    public static final String bucket = "njit-cs-643";
    public static final Region region = Region.US_EAST_1;

    public static List<String> listBucketObjects(S3Client s3, String bucket) {
        List<String> objectKeyList = new ArrayList<String>();
        try {
            ListObjectsRequest listObjects = ListObjectsRequest
                    .builder()
                    .bucket(bucket)
                    .build();

            ListObjectsResponse res = s3.listObjects(listObjects);
            List<S3Object> objects = res.contents();
            System.out.println(objects.size());
            for (S3Object myValue : objects) {
                // System.out.print("\n The name of the key is " + myValue.key());
                objectKeyList.add(myValue.key());
                // System.out.print("\n The object is " + calKb(myValue.size()) + " KBs");
                // System.out.print("\n The owner is " + myValue.owner());
            }

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return objectKeyList;
    }

    // convert bytes to kbs.
    // private static long calKb(Long val) {
    // return val / 1024;
    // }

    public static void main(String[] args) {
        // temp usage of credentials. delete later. figure out how to use
        // ProfileCredentialsProvider class
        // AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
        // "ASIAZ7VNOJL7C75PHM42",
        // "9NFOV0RUkZBkNtCK14KC5DEgRBvAY/TgonHM3o0h");

        S3Client s3 = S3Client.builder()
                .region(CarRecognition.region)
                // .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
        System.out.println("Listing objects");
        List<String> objectKeyList = listBucketObjects(s3, CarRecognition.bucket);

        // calling rekognition service
        RekognitionClient rekClient = RekognitionClient.builder()
                .region(CarRecognition.region)
                // temp
                // .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        SqsClient sqsClient = SqsClient.builder()
                .region(CarRecognition.region)
                // .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        for (String key : objectKeyList) {
            System.out.println("key: " + key);
            if (RecognitionService.getLabelsfromImage(rekClient, CarRecognition.bucket, key)) {
                System.out.println("Car detected in the image");
                MessageData msg = new MessageData();
                // UUID uuid = UUID.randomUUID();
                // String msgId = uuid.toString();
                msg.setName(key);
                msg.setId(UUID.randomUUID().toString());
                msg.setBody("Random message body");
                QueueService.pushMessages(sqsClient, msg);
            }
        }
        // sending -1 to indicate end of messages
        MessageData msg = new MessageData();
        // UUID uuid = UUID.randomUUID();
        // String msgId = uuid.toString();
        msg.setName("-1");
        msg.setId(UUID.randomUUID().toString());
        msg.setBody("End of messages");
        QueueService.pushMessages(sqsClient, msg);

        s3.close();
        rekClient.close();
        sqsClient.close();
    }
}
