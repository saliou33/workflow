package com.innov.workflow.core.service;

import com.innov.workflow.core.domain.entity.Role;
import com.innov.workflow.core.domain.repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;


    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Page<Role> getAllRoles(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return roleRepository.findAll(pageable);
    }

    public Role getRoleById(Long id) {
        return roleRepository.findById(id).orElse(null);
    }

    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    public Role updateRole(Long id, Role role) {
        Role existingRole = roleRepository.findById(id).orElse(null);
        if (existingRole == null) {
            return null;
        }
        existingRole.setName(role.getName());
        existingRole.setDescription(role.getDescription());
        // set other properties as needed
        return roleRepository.save(existingRole);
    }

    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }
}
