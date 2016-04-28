package com.foreach.across.modules.platform.application.controllers;

import com.foreach.across.modules.user.business.User;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Marc Vanbrabant
 * @since 1.1.2
 */
@RestController
public class PreAuthorizedController {
    @RequestMapping(value = "/api/testshouldbeauthenticated", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("isAuthenticated() && hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserResponse> authenticatedPage(OAuth2Authentication authentication) {
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(authentication.getPrincipal(), response, "roles");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public static class UserResponse extends User {
    }
}
