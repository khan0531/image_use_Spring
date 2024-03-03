package com.example.image_use_spring.groups.dto;

import com.example.image_use_spring.groups.persist.entity.ChatGroupEntity;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ChatGroupDto {

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Request {
    private String name;

    private String description;

    private Long maxMembers;

    private LocalDate startAt;

    private LocalDate endAt;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Response {
    private Long id;

    private String name;

    private String description;

    private Long maxMembers;

    private LocalDate startAt;

    private LocalDate endAt;

    private Long adminId;

    public static Response fromEntity(ChatGroupEntity chatGroupEntity) {
      return Response.builder()
          .id(chatGroupEntity.getId())
          .name(chatGroupEntity.getName())
          .description(chatGroupEntity.getDescription())
          .maxMembers(chatGroupEntity.getMaxMembers())
          .startAt(chatGroupEntity.getStartAt())
          .endAt(chatGroupEntity.getEndAt())
          .adminId(chatGroupEntity.getAdmin().getId())
          .build();
    }

    public static List<Response> fromEntities(List<ChatGroupEntity> chatGroupEntities) {
      return chatGroupEntities.stream()
          .map(Response::fromEntity)
          .collect(Collectors.toList());
    }
  }
}
