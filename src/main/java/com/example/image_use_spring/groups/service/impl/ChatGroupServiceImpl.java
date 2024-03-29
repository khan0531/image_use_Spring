package com.example.image_use_spring.groups.service.impl;

import static com.example.image_use_spring.exception.type.ErrorCode.ALREADY_GROUP_MEMBER;
import static com.example.image_use_spring.exception.type.ErrorCode.GROUP_FULL;
import static com.example.image_use_spring.exception.type.ErrorCode.GROUP_IS_DELETED;
import static com.example.image_use_spring.exception.type.ErrorCode.GROUP_NOT_ADMIN;
import static com.example.image_use_spring.exception.type.ErrorCode.GROUP_NOT_FOUND;
import static com.example.image_use_spring.exception.type.ErrorCode.GROUP_NOT_MEMBER;

import com.example.image_use_spring.exception.GroupException;
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
import com.example.image_use_spring.message.domain.Message;
import com.example.image_use_spring.message.persist.entity.MessageEntity;
import com.example.image_use_spring.message.persist.repository.MessageRepository;
import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatGroupServiceImpl implements ChatGroupService {

  private final ChatGroupRepository chatGroupRepository;
  private final MemberGroupRepository memberGroupRepository;
  private final MessageRepository messageRepository;

  @Value("${invite.link.length}")
  private int INVITE_LINK_LENGTH;
  @Value("${invite.link.characters}")
  private static String CHARACTERS;
  private static final SecureRandom RANDOM = new SecureRandom();

  @Override
  public ChatGroupDto.Response createChatGroup(ChatGroupDto.Request request,
      Member member) {

    ChatGroupEntity chatGroupEntity = chatGroupRepository.save(
        ChatGroup.fromRequest(request, member).updateChatGroupStatus(true).toEntity());

    memberGroupRepository.save(MemberGroup.create(ChatGroup.fromEntity(chatGroupEntity), member)
        .toEntity());

    return ChatGroupDto.Response.fromEntity(chatGroupEntity);
  }

  @Override
  public List<ChatGroupDto.Response> getChatGroups(Member member) {
    List<ChatGroupEntity> chatGroupEntities = memberGroupRepository.findByMember(member.toEntity())
        .stream()
        .map(MemberGroupEntity::getChatGroup)
        .collect(Collectors.toList());

    return ChatGroupDto.Response.fromEntities(chatGroupEntities);
  }

  @Override
  public InviteLinkResponseDto createInviteLink(Long groupId, Member member) {
    ChatGroup chatGroup = getChatGroup(groupId);

    if (!isChatGroupMember(chatGroup, member)) {
      throw new GroupException(GROUP_NOT_MEMBER);
    }

    if (chatGroup.isLinkValid()) {
      return InviteLinkResponseDto.fromEntity(chatGroup.toEntity());
    }

    String newInviteLink = generateRandomString(INVITE_LINK_LENGTH);
    ChatGroupEntity chatGroupEntity = chatGroup.updateInviteLink(newInviteLink).toEntity();

    return InviteLinkResponseDto.fromEntity(
        chatGroupRepository.save(chatGroupEntity));
  }

  @Override
  public ChatGroupDto.Response joinChatGroup(String inviteLink, Member member) {
    ChatGroup chatGroup = chatGroupRepository.findByInviteLink(inviteLink)
        .map(ChatGroup::fromEntity)
        .orElseThrow(() -> new GroupException(GROUP_NOT_FOUND));

    if (!chatGroup.isActivated()) {
      throw new GroupException(GROUP_IS_DELETED);
    }

    if (isChatGroupMember(chatGroup, member)) {
      throw new GroupException(ALREADY_GROUP_MEMBER);
    }

    if (chatGroup.getMaxMembers() <= memberGroupRepository.countByChatGroup(chatGroup.toEntity())) {
      throw new GroupException(GROUP_FULL);
    }

//        Message enterMessage = Message.createEnterMessage(chatGroup, member);

    MemberGroup memberGroup = MemberGroup.create(chatGroup, member);
    memberGroupRepository.save(memberGroup.toEntity());

    return ChatGroupDto.Response.fromEntity(chatGroup.toEntity());
  }

  @Override
  public ChatGroupDto.Response updateChatGroup(Long groupId, ChatGroupDto.Request request,
      Member member) {
    ChatGroup challengeGroup = getChatGroup(groupId);

    if (!challengeGroup.isAdmin(member)) {
      throw new GroupException(GROUP_NOT_ADMIN);
    }
    return ChatGroupDto.Response.fromEntity(
        chatGroupRepository.save(challengeGroup.update(request).toEntity()));
  }

  @Override
  public ChatGroupDto.Response deleteChatGroup(Long groupId, Member member) {
    ChatGroup challengeGroup = getChatGroup(groupId);

    if (!challengeGroup.isAdmin(member)) {
      throw new GroupException(GROUP_NOT_ADMIN);
    }

    return ChatGroupDto.Response.fromEntity(
        chatGroupRepository.save(challengeGroup.updateChatGroupStatus(false).toEntity()));
  }

  private ChatGroup getChatGroup(Long groupId) {
    ChatGroup challengeGroup = chatGroupRepository.findById(groupId)
        .map(ChatGroup::fromEntity)
        .orElseThrow(() -> new GroupException(GROUP_NOT_FOUND));
    return challengeGroup;
  }

  public List<Message> getMessages(Long groupId, Member member) {
    ChatGroupEntity challengeGroupEntity = chatGroupRepository.findById(groupId)
        .orElseThrow(() -> new GroupException(GROUP_NOT_FOUND));

    if (!isChatGroupMember(ChatGroup.fromEntity(challengeGroupEntity), member)) {
      throw new GroupException(GROUP_NOT_MEMBER);
    }
    List<MessageEntity> messageEntities = messageRepository.findByGroup(challengeGroupEntity);
    return Message.fromEntities(messageEntities);
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
