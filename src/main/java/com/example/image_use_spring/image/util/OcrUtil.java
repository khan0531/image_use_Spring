package com.example.image_use_spring.image.util;

import com.azure.ai.formrecognizer.FormRecognizerClient;
import com.azure.ai.formrecognizer.FormRecognizerClientBuilder;
import com.azure.ai.formrecognizer.models.RecognizedForm;
import com.azure.core.credential.AzureKeyCredential;
import com.example.image_use_spring.image.dto.OcrResult;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
@RequiredArgsConstructor
public class OcrUtil {
  @Value("${azure.formrecognizer.endpoint}")
  private String AZURE_OCR_ENDPOINT;

  @Value("${azure.formrecognizer.apikey}")
  private String AZURE_OCR_KEY;

  public OcrResult analyzeReceipt(MultipartFile file) throws IOException {
    // FormRecognizerClient 생성
    FormRecognizerClient client = new FormRecognizerClientBuilder()
        .credential(new AzureKeyCredential(AZURE_OCR_KEY))
        .endpoint(AZURE_OCR_ENDPOINT)
        .buildClient();

    var poller = client.beginRecognizeReceipts(file.getInputStream(), file.getSize(), null, null);
    List<RecognizedForm> recognizedForms  = poller.getFinalResult();

    RecognizedForm form = recognizedForms.get(0);
    OcrResult ocrResult = new OcrResult();
    StringBuilder dateTimeBuilder = new StringBuilder();

    form.getFields().forEach((fieldName, formField) -> {
      log.info("Field: {}, Value: {}", fieldName, formField.getValueData() == null ? "null" : formField.getValueData().getText());

      if (formField.getValue() != null) {
        switch (fieldName) {
          case "Total":
            if (formField.getValue().asFloat() != null) {
              ocrResult.setOcrAmount(formField.getValue().asFloat());
            }
            break;
          case "MerchantName":
            if (formField.getValue().asString() != null) {
              ocrResult.setOcrVendor(formField.getValue().asString());
            }
            break;
          case "TransactionDate":
            if (formField.getValue().asDate() != null) {
              String dateString = formField.getValue().asDate()
                  .format(DateTimeFormatter.ISO_LOCAL_DATE);
              dateTimeBuilder.append(dateString);
            }
            break;
          case"TransactionTime":
            if (formField.getValue().asTime() != null) {
              String timeString = formField.getValue().asTime()
                  .format(DateTimeFormatter.ISO_LOCAL_TIME);
              dateTimeBuilder.append(timeString);
            }
          break;
        }
      }
    });

    if (dateTimeBuilder.length() > 0) {
      String dateTime = dateTimeBuilder.toString();
      String numericDate = dateTime.replaceAll("[^\\d]", "");

      // 숫자로만 구성된 날짜와 시간 형식을 정의
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

      // 문자열을 LocalDateTime 객체로 변환
      try {
        LocalDateTime localDateTime = LocalDateTime.parse(numericDate, formatter);
        ocrResult.setOcrDate(localDateTime);
      } catch (Exception e) {
        log.error("DateTime parsing error", e.getMessage());
      }
    }

    return ocrResult;
  }
}

