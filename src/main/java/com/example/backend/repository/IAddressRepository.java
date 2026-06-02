package com.example.backend.repository;

import com.example.backend.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IAddressRepository extends JpaRepository<Address, Integer> {
    List<Address> findByUserId(Integer id);
}
