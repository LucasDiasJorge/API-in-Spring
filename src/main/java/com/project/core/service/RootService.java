package com.project.core.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RootService{

    private final JdbcTemplate jdbcTemplate;

    public RootService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void switchRoles(Long userId, String role) {

        Long roleId = switch (role) {
            case "root" -> 4L;
            case "admin" -> 3L;
            case "manager" -> 2L;
            default -> 1L;
        };

        try{
            jdbcTemplate.update("UPDATE tb_users_roles SET role_id = ? WHERE user_id = ?", roleId, userId);
        }catch (Exception e){
            System.out.println("Was not possible to update user role.");
        }

    }
}
