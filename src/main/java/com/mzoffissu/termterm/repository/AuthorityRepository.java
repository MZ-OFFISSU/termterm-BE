package com.mzoffissu.termterm.repository;

import com.mzoffissu.termterm.domain.auth.Authority;
import com.mzoffissu.termterm.domain.auth.MemberAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AuthorityRepository{
    private final EntityManager em;

    public Optional<Authority> findByAuthorityName(MemberAuth authorityName){
        Authority authority = em.
                createQuery("select a from Authority a where a.authorityName = :authorityName", Authority.class)
                .setParameter("authorityName", authorityName)
                .getSingleResult();
        return Optional.of(authority);
    }
}