package com.angel.commons.dto;

public record CustomErrorResponse(
        int codigo,
        String mensaje
) {
}
