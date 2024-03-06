//package com.example.image_use_spring.groups.persist.repository;
//
//import com.example.image_use_spring.groups.persist.entity.ChatGroupEntity;
//import com.example.image_use_spring.groups.persist.entity.MemberGroupEntity;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import java.time.LocalDate;
//import java.util.List;
//import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
//
//public class MemberGroupRepositoryImpl implements MemberGroupRepositoryCustom {
//
//  private final JPAQueryFactory queryFactory;
//  public MemberGroupRepositoryImpl(JPAQueryFactory queryFactory) {
//    this.queryFactory = queryFactory;
//  }
//  public MemberGroupRepositoryImpl() {
//    super(MemberGroupEntity.class);
//  }
//
//  @Override
//  public List<MemberGroupEntity> findByChatGroupWithCondition(ChatGroupEntity chatGroupEntity, LocalDate joinDate) {
//    QMemberGroupEntity qMemberGroup = QMemberGroupEntity.memberGroupEntity;
//    return from(qMemberGroup)
//        .where(qMemberGroup.chatGroup.eq(chatGroupEntity)
//            .and(qMemberGroup.joinDate.after(joinDate)))
//        .fetch();
//  }
//}
