package com.example.image_use_spring.message.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageType {
  ENTER("ENTER"), LEAVE("LEAVE"), TALK("TALK");

  private final String type;
}
