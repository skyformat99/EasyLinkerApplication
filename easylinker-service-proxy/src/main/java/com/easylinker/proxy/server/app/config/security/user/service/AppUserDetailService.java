package com.easylinker.proxy.server.app.config.security.user.service;

import com.easylinker.proxy.server.app.config.security.user.model.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * SpringSecurity的接口，交给用户自己实现数据库认证
 */
@Service(value = "AppUserUserDetailService")
public class AppUserDetailService implements UserDetailsService {
    @Autowired
    AppUserService appUserService;

    /**
     * @param parameter 可以用Username Or Email Or Phone 登录
     *                  默认是Username
     * @return AppUser
     * @throws
     */

    @Override
    public UserDetails loadUserByUsername(String parameter) throws UsernameNotFoundException {


        AppUser appUser;
        try {
            appUser = appUserService.getAAppUserWithUsername(parameter);
            if (appUser != null) {
                return appUser;
            } else {
                throw new UsernameNotFoundException("User not exist!");
            }

        } catch (UsernameNotFoundException e) {
            throw e;
        }

    }
}