package com.example.image_use_spring.groups.service.impl;

import com.example.image_use_spring.groups.domain.ChatGroup;
import com.example.image_use_spring.groups.domain.MemberGroup;
import com.example.image_use_spring.groups.dto.ChatGroupDto;
import com.example.image_use_spring.groups.persist.entity.ChatGroupEntity;
import com.example.image_use_spring.groups.persist.entity.MemberGroupEntity;
import com.example.image_use_spring.groups.persist.repository.ChatGroupRepository;
import com.example.image_use_spring.groups.persist.repository.MemberGroupRepository;
import com.example.image_use_spring.groups.service.ChatGroupService;
import com.example.image_use_spring.member.domain.Member;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatGroupServiceImpl implements ChatGroupService {

  private final ChatGroupRepository chatGroupRepository;
  private final MemberGroupRepository memberGroupRepository;

  @Override
  public ChatGroupDto.Response createChallengeGroup(ChatGroupDto.Request request,
      Member member) {
    ChatGroupEntity chatGroupEntity = chatGroupRepository.save(
        ChatGroup.fromRequest(request, member).toEntity());

    memberGroupRepository.save(MemberGroup.create(ChatGroup.fromEntity(chatGroupEntity), member)
        .toEntity());

    return ChatGroupDto.Response.fromEntity(chatGroupEntity);
  }

  @Override
  public List<ChatGroupDto.Response> getChallengeGroups(Member member) {
    List<ChatGroupEntity> chatGroupEntities = memberGroupRepository.findByMember(member.toEntity())
        .stream()
        .map(MemberGroupEntity::getChatGroup)
        .collect(Collectors.toList());

    return ChatGroupDto.Response.fromEntities(chatGroupEntities);
  }
}
