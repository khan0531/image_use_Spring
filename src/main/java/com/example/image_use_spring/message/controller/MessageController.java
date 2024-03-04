package com.example.image_use_spring.message.controller;

import com.example.image_use_spring.member.domain.Member;
import com.example.image_use_spring.message.domain.Message;
import com.example.image_use_spring.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@CrossOrigin
public class MessageController {
  private final MessageService messageService;

  @MessageMapping("/groups/{groupId}")
  public void chat(@DestinationVariable("groupId") Long groupId, Message message,
      SimpMessageHeaderAccessor headerAccessor) {
    Authentication authentication = (Authentication) headerAccessor.getSessionAttributes().get("member");
    Member member = (Member) authentication.getPrincipal();
    messageService.saveAndSend(message, member);
  }
}
