package com.minkyu.myproject.user.domain.repository;

import com.minkyu.myproject.user.domain.Email;
import com.minkyu.myproject.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(Email email);
}
