package com.goito.springboot.security;

import com.goito.springboot.entity.User;
import com.goito.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        
        // Process user based on OAuth provider
        if ("google".equals(registrationId)) {
            return processGoogleUser(oAuth2User);
        } else if ("facebook".equals(registrationId)) {
            return processFacebookUser(oAuth2User);
        }
        
        throw new OAuth2AuthenticationException("Unsupported OAuth2 provider");
    }
    
    private OAuth2User processFacebookUser(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String facebookId = (String) attributes.get("id");
        
        User user = userService.processOAuthUser(
                email, 
                name, 
                User.AuthProvider.FACEBOOK, 
                facebookId
        );
        
        return new CustomOAuth2User(oAuth2User, user.getEmail());
    }
    
    private OAuth2User processGoogleUser(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String googleId = (String) attributes.get("sub");
        
        User user = userService.processOAuthUser(
                email, 
                name, 
                User.AuthProvider.GOOGLE, 
                googleId
        );
        
        return new CustomOAuth2User(oAuth2User, user.getEmail());
    }
}
