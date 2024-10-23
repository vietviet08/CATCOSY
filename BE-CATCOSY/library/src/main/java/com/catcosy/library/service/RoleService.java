package com.catcosy.library.service;

import com.catcosy.library.model.Role;

public interface RoleService {

    Role findByRoleName(String roleName);

    Role changeRole(Long id, String role);
}
