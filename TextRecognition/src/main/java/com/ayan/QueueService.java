package com.ayan;

import java.util.ArrayList;
import java.util.List;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;

public class QueueService {
    public static List<String> getMessages(SqsClient sqsClient, String queueName) {

        List<String> indexes = new ArrayList<>();

        try {
            GetQueueUrlRequest getQueueRequest = GetQueueUrlRequest.builder()
                    .queueName(queueName)
                    .build();

            String queueUrl = sqsClient.getQueueUrl(getQueueRequest).queueUrl();
            ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .maxNumberOfMessages(10)
                    .waitTimeSeconds(20)
                    .messageAttributeNames("Name")
                    .build();

            List<Message> messages = sqsClient.receiveMessage(receiveRequest).messages();
            // MessageData myMessage;
            // List<MessageData> allMessages = new ArrayList<>();

            // Push the messages to a list.
            for (Message m : messages) {
                myMessage = new MessageData();
                myMessage.setBody(m.body());
                myMessage.setId(m.messageId());

                Map<String, MessageAttributeValue> map = m.messageAttributes();
                MessageAttributeValue val = map.get("Name");
                myMessage.setName(val.stringValue());
                indexes.add(myMessage.getName());
            }

        } catch (SqsException e) {
            e.getStackTrace();
        }
        return indexes;
    }
}
