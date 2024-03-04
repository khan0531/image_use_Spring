package com.example.image_use_spring.message.service;

import com.example.image_use_spring.member.domain.Member;
import com.example.image_use_spring.message.domain.Message;

public interface MessageService {

  void saveAndSend(Message message, Member member);
}
