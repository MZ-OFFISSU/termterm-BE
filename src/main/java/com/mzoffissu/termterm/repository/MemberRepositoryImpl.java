package com.mzoffissu.termterm.repository;

import com.mzoffissu.termterm.domain.auth.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

@RequiredArgsConstructor
@Repository
public class MemberRepositoryImpl implements MemberRepositoryCustom{
    private final EntityManager em;

    @Override
    public boolean existsByNicknameCustom(String nickname) {
        try{
            em.
                createQuery("select m from Member m where m.nickname = :nickname", Member.class)
                .setParameter("nickname", nickname)
                .getSingleResult();
            return true;
        }catch (NoResultException e){
            return false;
        }
    }

    @Override
    public boolean existsByNicknameExceptMeCustom(Member member, String newNickname) {
        try{
            Member foundMember = em.
                createQuery("select m from Member m where m.nickname = :nickname", Member.class)
                .setParameter("nickname", newNickname)
                .getSingleResult();

            return member != foundMember;
        }catch (NoResultException e){
            return false;
        }
    }
}
