package com.example.image_use_spring.groups.service.impl;

import static com.example.image_use_spring.exception.type.ErrorCode.GROUP_NOT_FOUND;
import static com.example.image_use_spring.exception.type.ErrorCode.GROUP_NOT_MEMBER;

import com.example.image_use_spring.exception.GroupException;
import com.example.image_use_spring.exception.type.ErrorCode;
import com.example.image_use_spring.groups.domain.ChatGroup;
import com.example.image_use_spring.groups.domain.MemberGroup;
import com.example.image_use_spring.groups.dto.ChatGroupDto;
import com.example.image_use_spring.groups.dto.InviteLinkResponseDto;
import com.example.image_use_spring.groups.persist.entity.ChatGroupEntity;
import com.example.image_use_spring.groups.persist.entity.MemberGroupEntity;
import com.example.image_use_spring.groups.persist.repository.ChatGroupRepository;
import com.example.image_use_spring.groups.persist.repository.MemberGroupRepository;
import com.example.image_use_spring.groups.service.ChatGroupService;
import com.example.image_use_spring.member.domain.Member;
import java.security.SecureRandom;
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

  private static int INVITE_LINK_LENGTH = 10;
  private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  private static final SecureRandom RANDOM = new SecureRandom();


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

  @Override
  public InviteLinkResponseDto createInviteLink(Long groupId, Member member) {
    ChatGroup chatGroup = chatGroupRepository.findById(groupId)
        .map(ChatGroup::fromEntity)
        .orElseThrow(() -> new GroupException(GROUP_NOT_FOUND));

    if (!isChatGroupMember(chatGroup, member)) {
      throw new GroupException(GROUP_NOT_MEMBER);
    }

    if (chatGroup.isLinkValid()) {
      return InviteLinkResponseDto.fromEntity(chatGroup.toEntity());
    }

    String inviteLink = generateRandomString(INVITE_LINK_LENGTH);
    ChatGroupEntity chatGroupEntity = chatGroup.updateInviteLink(inviteLink).toEntity();

    return InviteLinkResponseDto.fromEntity(
        chatGroupRepository.save(chatGroupEntity));
  }

  @Override
  public ChatGroupDto.Response joinChatGroup(String inviteLink, Member member) {
    ChatGroup chatGroup = chatGroupRepository.findByInviteLink(inviteLink)
        .map(ChatGroup::fromEntity)
        .orElseThrow(() -> new IllegalArgumentException("그룹이 존재하지 않습니다."));

    if (isChatGroupMember(chatGroup, member)) {
      throw new IllegalArgumentException("이미 그룹에 속해있습니다.");
    }

    if (chatGroup.getMaxMembers() <= memberGroupRepository.countByChatGroup(chatGroup.toEntity())) {
      throw new IllegalArgumentException("그룹 인원이 꽉 찼습니다.");
    }

//    Message enterMessage = Message.createEnterMessage(chatGroup, member);
//
//    messageService.saveAndSend(enterMessage, member);

    MemberGroup memberGroup = MemberGroup.create(chatGroup, member);
    memberGroupRepository.save(memberGroup.toEntity());

    return ChatGroupDto.Response.fromEntity(chatGroup.toEntity());
  }

  private static String generateRandomString(int length) {
    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      int randomIndex = RANDOM.nextInt(CHARACTERS.length());
      char randomChar = CHARACTERS.charAt(randomIndex);
      sb.append(randomChar);
    }
    return sb.toString();
  }

  private boolean isChatGroupMember(ChatGroup chatGroup, Member member) {
    if (chatGroup.isAdmin(member)) {
      return true;
    }

    List<Long> memberIds = memberGroupRepository.findByChatGroup(chatGroup.toEntity())
        .stream()
        .map(memberGroup -> memberGroup.getMember().getId())
        .collect(Collectors.toList());

    return memberIds.contains(member.getId());
  }

}
