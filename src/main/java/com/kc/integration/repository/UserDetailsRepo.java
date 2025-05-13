package com.kc.integration.repository;

import com.kc.integration.model.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDetailsRepo extends JpaRepository<UserDetails, String> {
}
