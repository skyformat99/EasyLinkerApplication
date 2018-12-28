package com.easyiot.easylinker.service.proxy.config.security.user.dao;

import com.easyiot.easylinker.service.proxy.config.security.user.model.AppUser;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AppUserRepository extends MongoRepository<AppUser,Long> {
    AppUser findTopByUsername(String username);

    AppUser findTopById(Long id);

    AppUser findTopByPhone(String phone);
}
