package com.example.carsharing.repository.user;

import com.example.carsharing.model.user.Role;
import com.example.carsharing.model.user.RoleName;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    @Query("SELECT r FROM Role r where r.name = :roleName")
    Optional<Role> findRoleByName(RoleName roleName);
}
