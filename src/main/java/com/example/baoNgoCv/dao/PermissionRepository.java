package com.example.baoNgoCv.dao;

import com.example.baoNgoCv.model.Permission;
import com.example.baoNgoCv.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
Permission findByName(String name);


}
