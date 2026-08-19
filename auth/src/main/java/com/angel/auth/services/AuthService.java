package com.angel.auth.services;

import com.angel.auth.dto.LoginRequest;
import com.angel.auth.dto.TokenResponse;

public interface AuthService {

    TokenResponse autenticar(LoginRequest request) throws Exception;
}

