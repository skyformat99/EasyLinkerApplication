package com.easylinker.proxy.server.app.config.security.utils;

import com.easylinker.proxy.server.app.config.security.user.model.AppUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * SpringSecurity 帮助类
 */
public class SecurityHelper {
    public static UserDetails getCurrentUser() {
        return (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
