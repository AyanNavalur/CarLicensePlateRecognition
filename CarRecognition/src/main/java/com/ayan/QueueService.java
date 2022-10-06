package com.ayan;

import java.util.HashMap;
import java.util.Map;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;

public class QueueService {

    public final static String queueName = "ayan.fifo";

    public static void pushMessages(SqsClient sqsClient, MessageData msg) {
        try {
            MessageAttributeValue attributeValue = MessageAttributeValue.builder()
                    .stringValue(msg.getName())
                    .dataType("String")
                    .build();

            Map<String, MessageAttributeValue> myMap = new HashMap<>();
            myMap.put("Name", attributeValue);
            GetQueueUrlRequest getQueueRequest = GetQueueUrlRequest.builder()
                    .queueName(QueueService.queueName)
                    .build();

            // // We will get the language code for the incoming message.
            // ComprehendClient comClient = getComClient();

            // // Specify the Langauge code of the incoming message.
            // String lanCode = "";
            // DetectDominantLanguageRequest request =
            // DetectDominantLanguageRequest.builder()
            // .text(msg.getBody())
            // .build();

            // DetectDominantLanguageResponse resp =
            // comClient.detectDominantLanguage(request);
            // List<DominantLanguage> allLanList = resp.languages();
            // for (DominantLanguage lang : allLanList) {
            // System.out.println("Language is " + lang.languageCode());
            // lanCode = lang.languageCode();
            // }

            String queueUrl = sqsClient.getQueueUrl(getQueueRequest).queueUrl();
            SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageAttributes(myMap)
                    .messageGroupId("GroupAyan")
                    .messageDeduplicationId(msg.getId())
                    .messageBody(msg.getBody())
                    .build();

            sqsClient.sendMessage(sendMsgRequest);
            System.out.println("Message sent to the queue");

        } catch (SqsException e) {
            e.getStackTrace();
        }
    }
}
