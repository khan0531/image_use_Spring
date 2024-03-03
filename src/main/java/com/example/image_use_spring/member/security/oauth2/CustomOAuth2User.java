package com.example.image_use_spring.member.security.oauth2;


import com.example.image_use_spring.member.dto.constants.AuthType;
import com.example.image_use_spring.member.dto.constants.Authority;
import com.example.image_use_spring.member.persist.entity.MemberEntity;
import java.util.Collection;
import java.util.Map;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

  private String email;

  /**
   * Constructs a {@code DefaultOAuth2User} using the provided parameters.
   *
   * @param authorities      the authorities granted to the user
   * @param attributes       the attributes about the user
   * @param nameAttributeKey the key used to access the user's &quot;name&quot; from {@link #getAttributes()}
   */
  public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes,
      String nameAttributeKey) {
    super(authorities, attributes, nameAttributeKey);
    this.email = (String) attributes.get("email");
  }

  public MemberEntity toEntity() {
    return MemberEntity.builder()
        .authType(AuthType.GOOGLE)
        .name((String) getAttributes().get("name"))
        .email((String) getAttributes().get("email"))
        .role(Authority.USER)
        .build();
  }
}
