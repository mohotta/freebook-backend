package org.freebook.backend.user;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthUserRepository extends MongoRepository<AuthUser, UUID> {

    Optional<AuthUser> findByEmail(String email);

}
