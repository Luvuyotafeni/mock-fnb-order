package com.fnb.orderUsermanagementService.service;

import com.fnb.orderUsermanagementService.dto.AuthResponse;
import com.fnb.orderUsermanagementService.dto.LoginRequest;
import com.fnb.orderUsermanagementService.dto.RegisterRequest;

public interface UserService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
