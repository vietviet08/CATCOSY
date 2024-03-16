package com.dacs1.library.repository;

import com.dacs1.library.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    public Admin findByUsername(String username);
}
