package com.angel.citas.controller;

import com.angel.citas.dto.CitaRequest;
import com.angel.citas.dto.CitaResponse;
import com.angel.citas.service.CitaService;
import com.angel.commons.controller.CommonController;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class CitaController extends CommonController<CitaRequest, CitaResponse, CitaService> {

    public CitaController(CitaService service) {
        super(service);
    }

    @PatchMapping("/{idCita}/estado/{idEstado}")
    public ResponseEntity<Void> actualizarEstadoCita(
            @PathVariable @Positive(message = "El idCita debe ser positivo") Long idCita,
            @PathVariable @Positive(message = "El idEstado debe ser positivo") Long idEstado) {

        service.actualizarEstadoCita(idCita, idEstado);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pacientes/{idPaciente}/citas-activas")
    public ResponseEntity<Boolean> pacienteTieneCitasConfirmadasOEnCurso(
            @PathVariable @Positive(message = "El idPaciente debe ser positivo") Long idPaciente) {

        return ResponseEntity.ok(service.pacienteTieneCitasConfirmadasOEnCurso(idPaciente));
    }

    @GetMapping("/medicos/{idMedico}/citas-activas")
    public ResponseEntity<Boolean> medicoTieneCitasConfirmadasOEnCurso(
            @PathVariable @Positive(message = "El idMedico debe ser positivo") Long idMedico) {

        return ResponseEntity.ok(service.medicoTieneCitasConfirmadasOEnCurso(idMedico));
    }

}
