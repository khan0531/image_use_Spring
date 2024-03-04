package com.example.image_use_spring.message.service.impl;

import com.example.image_use_spring.member.domain.Member;
import com.example.image_use_spring.member.persist.entity.MemberEntity;
import com.example.image_use_spring.member.persist.repository.MemberRepository;
import com.example.image_use_spring.member.service.MemberService;
import com.example.image_use_spring.message.domain.Message;
import com.example.image_use_spring.message.persist.repository.MessageRepository;
import com.example.image_use_spring.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

  private final MessageRepository messageRepository;
  private final SimpMessagingTemplate messagingTemplate;
  private final MemberService memberService;

  @Override
  public void saveAndSend(Message message, Member member) {
    MemberEntity memberEntity = memberService.validateAndGetMember(member);
    message.setSenderId(memberEntity.getId());
    messageRepository.save(message.toEntity());
    messagingTemplate.convertAndSend("/chat/groups/" + message.getGroupId(), message);
  }
}
