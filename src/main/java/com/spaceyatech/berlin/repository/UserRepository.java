package com.spaceyatech.berlin.repository;

import com.spaceyatech.berlin.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface UserRepository extends JpaRepository<User, UUID > {
// TODO: 06/11/2022 interacts with db queries

    Optional<User> findByUsername(String username);


    Optional<User> findById(UUID userId);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

}
