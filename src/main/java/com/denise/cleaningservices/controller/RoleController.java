package com.denise.cleaningservices.controller;

import com.denise.cleaningservices.model.Role;
import com.denise.cleaningservices.model.User;
import com.denise.cleaningservices.exception.RoleAlreadyExistsException;
import com.denise.cleaningservices.service.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final IRoleService roleService;

    @GetMapping("/all-roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleService.getRoles();
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    @PostMapping("/create-new-role")
    public ResponseEntity<String> createRole(@RequestBody Role role) {
        try {
            roleService.createRole(role);
            return ResponseEntity.ok("New role created successfully!");
        } catch (RoleAlreadyExistsException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }

    @DeleteMapping("/delete/{roleId}")
    public ResponseEntity<Void> deleteRole(@PathVariable("roleId") Long roleId) {
        roleService.deleteRole(roleId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/remove-all-users-from-role/{roleId}")
    public ResponseEntity<Role> removeAllUsersFromRole(@PathVariable("roleId") Long roleId) {
        Role updatedRole = roleService.removeAllUsersFromRole(roleId);
        return ResponseEntity.ok(updatedRole);
    }

    @PostMapping("/remove-user-from-role")
    public ResponseEntity<User> removeUserFromRole(
            @RequestParam("userId") Long userId,
            @RequestParam("roleId") Long roleId) {
        User updatedUser = roleService.removeUserFromRole(userId, roleId);
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/assign-user-to-role")
    public ResponseEntity<User> assignUserToRole(
            @RequestParam("userId") Long userId,
            @RequestParam("roleId") Long roleId) {
        User updatedUser = roleService.assignRoleToUser(userId, roleId);
        return ResponseEntity.ok(updatedUser);
    }
}
