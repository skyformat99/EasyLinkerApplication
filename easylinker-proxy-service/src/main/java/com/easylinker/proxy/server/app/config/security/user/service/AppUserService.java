package com.easylinker.proxy.server.app.config.security.user.service;

import com.easylinker.proxy.server.app.config.security.user.dao.AppUserRepository;
import com.easylinker.proxy.server.app.config.security.user.model.AppUser;
import com.easylinker.proxy.server.app.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service(value = "AppUserService")
public class AppUserService implements BaseService<AppUser> {
    private final AppUserRepository appUserRepository;

    @Autowired
    public AppUserService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }


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


    public AppUser findById(Long id) {
        return appUserRepository.findTopById(id);
    }

}