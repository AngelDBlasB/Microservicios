package com.angel.commons.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "citas")
public interface CitasClient {

    @GetMapping("/pacientes/{idPaciente}/citas-activas")
    Boolean pacientTieneCitasConfirmadasOEnCurso(@PathVariable("idPaciente") Long idPaciente);

    @GetMapping("/medicos/{idMedico}/citas-activas")
    Boolean medicoTieneCitasConfirmadasOEnCurso(@PathVariable("idMedico") Long idMedico);

}
