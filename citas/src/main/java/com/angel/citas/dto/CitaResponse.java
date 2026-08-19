package com.angel.citas.dto;

import com.angel.commons.dto.medicos.DatosMedico;
import com.angel.commons.dto.pacientes.DatosPaciente;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Objects;

public record CitaResponse(

        Long id,
        DatosPaciente paciente,
        DatosMedico medico,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime fechaCita,
        String sintomas,
        String estadoCita

) {
}
