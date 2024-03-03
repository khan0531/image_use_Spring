package com.example.image_use_spring.member.persist.repository;

import com.example.image_use_spring.member.persist.entity.VerificationCode;
import org.springframework.data.repository.CrudRepository;

public interface VerificationCodeRepository extends CrudRepository<VerificationCode, String> {
}
