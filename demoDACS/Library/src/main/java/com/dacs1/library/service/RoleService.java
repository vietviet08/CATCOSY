package com.dacs1.library.service;

import com.dacs1.library.model.Role;

public interface RoleService {

    Role findByRoleName(String roleName);

    Role changeRole(Long id, String role);
}
