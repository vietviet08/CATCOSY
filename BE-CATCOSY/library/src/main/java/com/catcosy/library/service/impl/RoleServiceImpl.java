package com.catcosy.library.service.impl;

import com.catcosy.library.repository.RoleRepository;
import com.catcosy.library.model.Role;
import com.catcosy.library.service.RoleService;
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
