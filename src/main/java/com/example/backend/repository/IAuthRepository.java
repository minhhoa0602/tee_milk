package com.example.backend.repository;

import com.example.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface IAuthRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {
    User findByEmail(String email);
}
