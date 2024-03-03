package com.example.image_use_spring.groups.controller;

import com.example.image_use_spring.groups.dto.ChatGroupDto;
import com.example.image_use_spring.groups.service.ChatGroupService;
import com.example.image_use_spring.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
public class GroupController {

  private final ChatGroupService chatGroupService;

  @PostMapping
  public ResponseEntity<?> createGroup(@RequestBody ChatGroupDto.Request request,
      @AuthenticationPrincipal Member member) {
    return ResponseEntity.ok(chatGroupService.createChallengeGroup(request, member));
  }

  @GetMapping
  public ResponseEntity<?> getGroups(@AuthenticationPrincipal Member member) {
    return ResponseEntity.ok(chatGroupService.getChallengeGroups(member));
  }

  @GetMapping("/{groupId}/invite-link")
  public ResponseEntity<?> createInviteLink(@PathVariable Long groupId, @AuthenticationPrincipal Member member) {
    return ResponseEntity.ok(chatGroupService.createInviteLink(groupId, member));
  }

  @PostMapping("/join/{inviteLink}")
  public ResponseEntity<?> joinGroup(@PathVariable String inviteLink, @AuthenticationPrincipal Member member) {
    return ResponseEntity.ok(chatGroupService.joinChatGroup(inviteLink, member));
  }
}
