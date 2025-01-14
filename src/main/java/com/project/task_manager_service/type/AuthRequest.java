package com.project.task_manager_service.type;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
}
