package com.easylinker.proxy.server.app.config.securityconfig.user.dao;

import com.easylinker.proxy.server.app.config.securityconfig.user.model.AppUser;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AppUserRepository extends MongoRepository<AppUser,Long> {
    AppUser findTopByUsername(String username);

    AppUser findTopById(Long id);
}
