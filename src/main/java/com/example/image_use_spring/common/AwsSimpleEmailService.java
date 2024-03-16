package com.example.image_use_spring.common;

import static com.example.image_use_spring.exception.type.ErrorCode.EMAIL_SEND_FAILED;

import com.example.image_use_spring.exception.EmailException;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.Body;
import software.amazon.awssdk.services.ses.model.Content;
import software.amazon.awssdk.services.ses.model.Destination;
import software.amazon.awssdk.services.ses.model.Message;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;

@Slf4j
@Service
public class AwsSimpleEmailService {

  @Value("${cloud.aws.ses.access-key}")
  private String accessKey;
  @Value("${cloud.aws.ses.secret-key}")
  private String secretKey;
  @Value("${cloud.aws.ses.sender-email}")
  private String SENDER_EMAIL;
  private SesClient sesClient;

  @PostConstruct
  private void AwsSimpleEmailService() {
    AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);
    sesClient = SesClient.builder()
        .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
        .region(Region.AP_NORTHEAST_2)
        .build();
  }

  public void sendEmail(String to, String subject, String body) {
    SendEmailRequest request = SendEmailRequest.builder()
        .destination(Destination.builder()
            .toAddresses(to)
            .build())
        .message(Message.builder()
            .subject(Content.builder()
                .data(subject)
                .charset("UTF-8")
                .build())
            .body(Body.builder()
                .text(Content.builder()
                    .data(body)
                    .charset("UTF-8")
                    .build())
                .build())
            .build())
        .source(SENDER_EMAIL)
        .build();
    try {
      sesClient.sendEmail(request);
    }catch (Exception e) {
      log.info("error: {}", e.getMessage());
      throw new EmailException(EMAIL_SEND_FAILED);
    }
  }
}