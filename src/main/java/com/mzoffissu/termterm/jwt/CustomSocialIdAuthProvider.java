package com.mzoffissu.termterm.jwt;

import com.mzoffissu.termterm.response.exception.BizException;
import com.mzoffissu.termterm.response.exception.MemberExceptionType;
import com.mzoffissu.termterm.service.auth.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomSocialIdAuthProvider implements AuthenticationProvider {

    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;
    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    protected void additionalAuthenticationChecks(UserDetails userDetails, CustomSocialIdAuthToken authentication) throws BizException{
        log.debug("additionalAuthenticationChecks authentication = {}",authentication);

        if (authentication.getCredentials() == null) {
            log.debug("additionalAuthenticationChecks is null !");
            throw new BizException(MemberExceptionType.NOT_FOUND_PASSWORD);
        }
        String presentedPassword = authentication.getCredentials().toString();

        if (!presentedPassword.equals(userDetails.getPassword()) && !passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
            throw new BizException(MemberExceptionType.WRONG_PASSWORD);
        }
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws BizException {
        UserDetails user = null;
        try {
            user = retrieveUser(authentication.getName());
        }catch (BizException ex) {
            throw ex;
        }

        Object principalToReturn = user;
        CustomSocialIdAuthToken result = new CustomSocialIdAuthToken(principalToReturn
                ,authentication.getCredentials()
                ,this.authoritiesMapper.mapAuthorities(user.getAuthorities()));
        additionalAuthenticationChecks(user, result);
        result.setDetails(authentication.getDetails());
        return result;
    }

    protected final UserDetails retrieveUser(String username ) throws BizException {
        try {
            UserDetails loadedUser = customUserDetailsService.loadUserByUsername(username);
            if (loadedUser == null) {
                throw new InternalAuthenticationServiceException(
                        "UserDetailsService returned null, which is an interface contract violation");
            }
            return loadedUser;
        }
        catch (BizException ex) {
            log.debug("error in retrieveUser = {}", ex.getMessage());
            throw ex;
        }
        catch (Exception ex) {
            throw new InternalAuthenticationServiceException(
                    "?????? ?????? ????????? ?????? ?????? ????????? ?????????????????????.");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(CustomSocialIdAuthToken.class);
    }
}

