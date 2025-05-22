package com.goito.springboot.repository;

import com.goito.springboot.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserAddressRepository extends JpaRepository<UserAddress, UUID> {
    List<UserAddress> findByUserUserId(UUID userId);
    
    List<UserAddress> findByUserUserIdOrderByIsDefaultDesc(UUID userId);
}
