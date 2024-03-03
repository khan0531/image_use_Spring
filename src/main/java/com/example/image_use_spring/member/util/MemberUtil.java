package com.example.image_use_spring.member.util;

import java.util.Random;
import org.springframework.stereotype.Component;

@Component
public class MemberUtil {
  public static String verificationCodeGenerator() {
    Random random = new Random();
    int verificationCode = 100000 + random.nextInt(900000);
    return String.valueOf(verificationCode);
  }
}
