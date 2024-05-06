package com.dacs1.library.repository;

import com.dacs1.library.model.AdminDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminDetailsRepository extends JpaRepository<AdminDetails, Long> {

    Optional<AdminDetails> findByUsername(String username);


}
