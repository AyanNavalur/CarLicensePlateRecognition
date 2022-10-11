package com.ayan;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

public class GetImages {

    public static void getObjectBytes(S3Client s3, String bucketName, String keyName) {

        try {
            GetObjectRequest objectRequest = GetObjectRequest
                    .builder()
                    .key(keyName)
                    .bucket(bucketName)
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes = s3.getObjectAsBytes(objectRequest);
            byte[] data = objectBytes.asByteArray();
            System.out.println(data);

            // saving byte array to image
            ByteArrayInputStream inStreambj = new ByteArrayInputStream(data);
            BufferedImage newImage = ImageIO.read(inStreambj);
            ImageIO.write(newImage, "jpg", new File(keyName));
            System.out.println(keyName + " Image generated from the byte array.");

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        String bucketName = "njit-cs-643";
        // passing hard coded key name for now. will change later
        String[] keyList = { "1.jpg", "2.jpg", "3.jpg", "4.jpg", "5.jpg", "6.jpg",
                "7.jpg", "8.jpg", "9.jpg",
                "10.jpg" };

        ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
        Region region = Region.US_EAST_1;
        S3Client s3 = S3Client.builder()
                .region(region)
                .credentialsProvider(credentialsProvider)
                .build();
        for (String key : keyList) {
            getObjectBytes(s3, bucketName, key);
        }
        s3.close();
    }
}
