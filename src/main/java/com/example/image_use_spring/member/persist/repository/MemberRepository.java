package com.example.image_use_spring.member.persist.repository;

import com.example.image_use_spring.member.persist.entity.MemberEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

  Optional<MemberEntity> findByEmail(String email);

  Optional<MemberEntity> findByRefreshToken(String refreshToken);

  Optional<MemberEntity> findMemberIdByEmail(String email);
}
