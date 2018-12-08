package com.easylinker.proxy.server.app.config.security.user.dao;

import com.easylinker.proxy.server.app.config.security.user.model.AppUser;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AppUserRepository extends MongoRepository<AppUser,Long> {
    AppUser findTopByUsername(String username);

    AppUser findTopById(Long id);

    AppUser findTopByPhone(String phone);
}
