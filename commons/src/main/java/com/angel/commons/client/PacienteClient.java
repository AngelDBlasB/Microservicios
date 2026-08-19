package com.angel.commons.client;


import com.angel.commons.dto.pacientes.PacienteResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "pacientes")
public interface PacienteClient {

    @GetMapping("/{id}")
    PacienteResponse obtenerPacienteActivoId(@PathVariable Long id);

    @GetMapping("/id-paciente/{id}")
    PacienteResponse obtenerPacientePorIdSinEstado(@PathVariable Long id);

}
