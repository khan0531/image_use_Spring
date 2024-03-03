package com.example.image_use_spring.groups.service;

import com.example.image_use_spring.groups.dto.ChatGroupDto.Request;
import com.example.image_use_spring.member.domain.Member;

public interface ChatGroupService {

  Object createChallengeGroup(Request request, Member member);
}
