package com.example.image_use_spring.message.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.image_use_spring.message.domain.Message;
import com.example.image_use_spring.message.service.MessageService;
import java.time.LocalDateTime;
import java.util.HashMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.image_use_spring.member.domain.Member;
@ExtendWith(MockitoExtension.class)
public class MessageControllerTest {

  @InjectMocks
  private MessageController messageController;

  @Mock
  private MessageService messageService;

  @Test
  public void testChat() {
    // Member 객체 생성
    Member member = Member.builder()
        .id(1L)
        .name("John Doe")
        .email("john.doe@example.com")
        .build();

    // Authentication 객체 모킹
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(member);

    // SimpMessageHeaderAccessor 객체 모킹
    SimpMessageHeaderAccessor headerAccessor = mock(SimpMessageHeaderAccessor.class);
    // getSessionAttributes()가 null이 아닌 HashMap을 반환하도록 설정
    when(headerAccessor.getSessionAttributes()).thenReturn(new HashMap<>());
    // 모킹된 세션 속성에 Authentication 객체 추가
    headerAccessor.getSessionAttributes().put("member", authentication);

    // Message 객체 생성
    Message message = Message.builder()
        .id(1L)
        .groupId(1L)
        .senderId(1L)
        .message("Hello, World!")
        .createdAt(LocalDateTime.now())
        .build();

    // chat 메소드 호출
//    messageController.chat(message, headerAccessor);

    // saveAndSend 메소드 호출 검증
    Mockito.verify(messageService).saveAndSend(any(Message.class), any(Member.class));
  }
}