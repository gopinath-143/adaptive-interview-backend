package com.interview.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.interview.entity.User;

public interface UserRepository
        extends JpaRepository<User, Long> {

    User findByUsername(String username);
}