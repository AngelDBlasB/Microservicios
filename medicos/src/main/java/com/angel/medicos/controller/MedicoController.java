package com.angel.medicos.controller;

import com.angel.commons.controller.CommonController;
import com.angel.commons.dto.medicos.MedicoRequest;
import com.angel.commons.dto.medicos.MedicoResponse;
import com.angel.medicos.service.MedicoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class MedicoController  extends CommonController<MedicoRequest, MedicoResponse, MedicoService> {

    public MedicoController(MedicoService service) {
        super(service);
    }

    @GetMapping("/id-medico/{id}")
    public ResponseEntity<MedicoResponse> obtenerMedicoPorIdSinEstado(
            @PathVariable @Positive(message = "El ID debe ser positivo") Long id
    ){
        return  ResponseEntity.ok(service.obtenerMedicoPorIdSinEstado(id));
    }

    @PutMapping("/{idMedico}/disponibilidad/{idDisponibilidad}")
    public ResponseEntity<Void> actualizarDisponibilidadMedico(
            @PathVariable @Positive(message = "El ID debe ser positivo") Long idMedico,
            @PathVariable @Positive(message = "El idDisponibilidad debe ser positivo") Long idDisponibilidad
    ){
        service.actualizarDisponibilidadMedico(idMedico, idDisponibilidad);
        return ResponseEntity.noContent().build();
    }

}
