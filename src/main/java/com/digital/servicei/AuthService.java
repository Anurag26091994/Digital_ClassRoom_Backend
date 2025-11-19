package com.digital.servicei;


import com.digital.dto.AuthRequest;
import com.digital.dto.AuthResponse;

public interface AuthService {
    AuthResponse login(AuthRequest request);
}
