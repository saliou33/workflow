package com.innov.workflow.core.service;

import com.innov.workflow.core.domain.entity.EnumSysRole;
import com.innov.workflow.core.domain.entity.SysRole;
import com.innov.workflow.core.domain.repository.SysRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysRoleService {

    private final SysRoleRepository roleRepository;

    public List<SysRole> getAllRoles() {
        return roleRepository.findAll();
    }


    public SysRole getRoleById(Long id) {
        return roleRepository.findById(id).orElse(null);
    }

    public SysRole getRoleByName(EnumSysRole name) {
        return roleRepository.findByName(name);
    }

    public SysRole createRole(SysRole roleTag) {
        return roleRepository.save(roleTag);
    }

    public SysRole updateRole(Long id, SysRole roleTag) {
        SysRole existingRole = roleRepository.findById(id).orElse(null);
        if (existingRole == null) {
            return null;
        }
        existingRole.setName(roleTag.getName());
        // set other properties as needed
        return roleRepository.save(existingRole);
    }

    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }
}
