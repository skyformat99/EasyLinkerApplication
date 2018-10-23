package com.easylinker.proxy.server.app.config.securityconfig.user.service;

import com.easylinker.proxy.server.app.config.securityconfig.user.dao.AppUserRepository;
import com.easylinker.proxy.server.app.config.securityconfig.user.model.AppUser;
import com.easylinker.proxy.server.app.vertx.vertxmqtt.client.service.BaseServive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service(value = "AppUserService")
public class AppUserService implements BaseServive<AppUser>{
    @Autowired
    AppUserRepository appUserRepository;


    public AppUser getAAppUserWithUsername(String username) {
        return appUserRepository.findTopByUsername(username);
    }

    @Override
    public void save(AppUser appUser) {
        appUserRepository.save(appUser);
    }

    @Override
    public void delete(AppUser appUser) {
        appUserRepository.delete(appUser);

    }

    @Override
    public Page<AppUser> getAll(Pageable pageable) {
        return appUserRepository.findAll(pageable);
    }
}