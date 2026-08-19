package com.angel.auth.dto;

public record CustomErrorResponse(
        int codigo,
        String mensaje
) {
}
