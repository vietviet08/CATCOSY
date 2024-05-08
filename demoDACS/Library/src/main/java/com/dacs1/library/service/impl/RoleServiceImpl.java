package com.dacs1.library.service.impl;

import com.dacs1.library.model.Role;
import com.dacs1.library.repository.RoleRepository;
import com.dacs1.library.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Role findByRoleName(String roleName) {
        return roleRepository.findByName(roleName);
    }

    @Override
    public Role changeRole(Long id, String role) {

        return null;
    }
}
