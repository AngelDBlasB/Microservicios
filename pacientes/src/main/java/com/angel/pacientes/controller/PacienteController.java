package com.angel.pacientes.controller;

import com.angel.commons.controller.CommonController;
import com.angel.commons.dto.medicos.MedicoResponse;
import com.angel.commons.dto.pacientes.PacienteRequest;
import com.angel.commons.dto.pacientes.PacienteResponse;
import com.angel.pacientes.service.PacienteService;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class PacienteController extends CommonController<PacienteRequest, PacienteResponse, PacienteService> {

    public PacienteController(PacienteService service) {
        super(service);
    }

    @GetMapping("/id-paciente/{id}")
    public ResponseEntity<PacienteResponse> obtenerMedicoPorIdSinEstado(
            @PathVariable @Positive(message = "El ID debe ser positivo") Long id
    ){
        return  ResponseEntity.ok(service.obtenerPacientePorIdSinEstado(id));
    }

}
