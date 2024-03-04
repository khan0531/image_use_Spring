package com.example.image_use_spring.groups.service;

import com.example.image_use_spring.groups.dto.ChatGroupDto;
import com.example.image_use_spring.groups.dto.ChatGroupDto.Request;
import com.example.image_use_spring.groups.dto.ChatGroupDto.Response;
import com.example.image_use_spring.groups.dto.InviteLinkResponseDto;
import com.example.image_use_spring.member.domain.Member;
import com.example.image_use_spring.message.domain.Message;
import java.util.List;

public interface ChatGroupService {

  ChatGroupDto.Response createChallengeGroup(Request request, Member member);

  List<ChatGroupDto.Response> getChallengeGroups(Member member);

  InviteLinkResponseDto createInviteLink(Long groupId, Member member);

  ChatGroupDto.Response joinChatGroup(String inviteLink, Member member);

  Response updateChatGroup(Long groupId, Request request, Member member);

  Object deleteChatGroup(Long groupId, Member member);

  List<Message> getMessages(Long groupId, Member member);
}
