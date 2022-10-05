package com.ayan;

import java.util.ArrayList;
import java.util.List;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;

public class CarRecognition {

    public static List<String> listBucketObjects(S3Client s3, String bucketName) {
        List<String> objectKeyList = new ArrayList<String>();
        try {
            ListObjectsRequest listObjects = ListObjectsRequest
                    .builder()
                    .bucket(bucketName)
                    .build();

            ListObjectsResponse res = s3.listObjects(listObjects);
            List<S3Object> objects = res.contents();
            System.out.println(objects.size());
            for (S3Object myValue : objects) {
                System.out.print("\n The name of the key is " + myValue.key());
                objectKeyList.add(myValue.key());
                System.out.print("\n The object is " + calKb(myValue.size()) + " KBs");
                System.out.print("\n The owner is " + myValue.owner());
            }

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return objectKeyList;
    }

    // convert bytes to kbs.
    private static long calKb(Long val) {
        return val / 1024;
    }

    public static void main(String[] args) {

        String bucket = "njit-cs-643";
        // temp usage of credentials. delete later. figure out how to use
        // ProfileCredentialsProvider class
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
                "your_access_key_id",
                "your_secret_access_key");
        // ProfileCredentialsProvider credentialsProvider =
        // ProfileCredentialsProvider.create();

        Region region = Region.US_EAST_1;
        S3Client s3 = S3Client.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                // .credentialsProvider(credentialsProvider)
                .build();
        System.out.println("Listing objects");
        List<String> objectKeyList = listBucketObjects(s3, bucket);
        s3.close();

        // calling rekognition service
        RekognitionClient rekClient = RekognitionClient.builder()
                .region(region)
                // temp
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                // .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
        for (String key : objectKeyList) {
            if (RecognitionService.getLabelsfromImage(rekClient, bucket, key)) {
                System.out.println("Car detected in the image");
                // push index or key to SQS
            }
        }

    }
}
