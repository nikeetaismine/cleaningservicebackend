package com.denise.cleaningservices.repository;

import com.denise.cleaningservices.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String role);

    boolean existsByName(String role);

    @Query("SELECT r.name FROM User u JOIN u.roles r WHERE u.id = :userId")
    List<String> findRolesByUserId(@Param("userId") Long userId);
}
