package com.goito.springboot.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) 
            throws IOException, ServletException {
        
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String redirectUri = request.getParameter("redirect_uri");
        
        // Default redirect URI if none provided
        if (redirectUri == null || redirectUri.isEmpty()) {
            redirectUri = "/";
        }
        
        // Validate redirect URI to prevent open redirect vulnerability
        if (!isValidRedirectUri(redirectUri)) {
            redirectUri = "/";
        }

        // Generate JWT token
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String token = tokenProvider.generateToken(oAuth2User.getEmail());

        // Append token to redirect URI
        return UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("token", token)
                .build().toUriString();
    }
    
    private boolean isValidRedirectUri(String uri) {
        // Add your validation logic here
        // For example, check if the URI is in a whitelist of allowed URIs
        // or if it belongs to your application's domain
        
        // For simplicity, we'll just check if it's a relative URI or belongs to our domain
        return uri.startsWith("/") || uri.startsWith("http://localhost") || uri.startsWith("https://yourdomain.com");
    }
}
