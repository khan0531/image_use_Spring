package com.example.image_use_spring.groups.controller;

import com.example.image_use_spring.groups.dto.ChatGroupDto;
import com.example.image_use_spring.groups.service.ChatGroupService;
import com.example.image_use_spring.member.domain.Member;
import com.example.image_use_spring.message.domain.Message;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupController {

  private final ChatGroupService chatGroupService;

  @PostMapping
  public ResponseEntity<?> createGroup(@RequestBody ChatGroupDto.Request request,
      @AuthenticationPrincipal Member member) {
    return ResponseEntity.ok(chatGroupService.createChatGroup(request, member));
  }

  @GetMapping
  public ResponseEntity<?> getGroups(@AuthenticationPrincipal Member member) {
    return ResponseEntity.ok(chatGroupService.getChatGroups(member));
  }

  @GetMapping("/{groupId}/invite-link")
  public ResponseEntity<?> createInviteLink(@PathVariable Long groupId, @AuthenticationPrincipal Member member) {
    return ResponseEntity.ok(chatGroupService.createInviteLink(groupId, member));
  }

  @PostMapping("/join/{inviteLink}")
  public ResponseEntity<?> joinGroup(@PathVariable String inviteLink, @AuthenticationPrincipal Member member) {
    return ResponseEntity.ok(chatGroupService.joinChatGroup(inviteLink, member));
  }

  @PutMapping("/{groupId}")
  public ResponseEntity<?> updateGroup(@PathVariable Long groupId,
      @RequestBody ChatGroupDto.Request request, @AuthenticationPrincipal Member member) {
    return ResponseEntity.ok(chatGroupService.updateChatGroup(groupId, request, member));
  }

  @DeleteMapping("/{groupId}")
  public ResponseEntity<?> deleteGroup(@PathVariable Long groupId, @AuthenticationPrincipal Member member) {
    return ResponseEntity.ok(chatGroupService.deleteChatGroup(groupId, member));
  }

  @GetMapping("/{groupId}/messages")
  public ResponseEntity<?> getMessages(@PathVariable Long groupId, @AuthenticationPrincipal Member member) {
    List<Message> messages = chatGroupService.getMessages(groupId, member);
    return ResponseEntity.ok(messages);
  }

  //TODO: 그룹 나가기, 그룹 멤버 추방,
}
