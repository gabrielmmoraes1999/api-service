package io.github.gabrielmmoraes1999.service.repository;

import io.github.gabrielmmoraes1999.db.DBRepository;
import io.github.gabrielmmoraes1999.service.entity.User;

public interface UserRepository extends DBRepository<User, String> {

    User findByEmail(String email);

}
