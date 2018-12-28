package com.easyiot.easylinker.service.proxy.config.security.user.service;

import com.easyiot.easylinker.service.proxy.config.security.user.dao.AppUserRepository;
import com.easyiot.easylinker.service.proxy.config.security.user.model.AppUser;
import com.easyiot.easylinker.service.proxy.service.BaseService;
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
    public AppUser getAAppUserWithPhone(String phone) {
        return appUserRepository.findTopByPhone(phone);
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

    @Override
    public Page<AppUser> findAllByUserId(Long userId, Pageable pageable) {
        return null;
    }


    public AppUser findById(Long id) {
        return appUserRepository.findTopById(id);
    }

}