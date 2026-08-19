package com.angel.commons.client;

import com.angel.commons.dto.medicos.MedicoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "medicos")
public interface MedicoClient {

    @GetMapping("/{id}")
    MedicoResponse obtenerMedicoActivoId(@PathVariable Long id);

    @GetMapping("/id-medico/{id}")
    MedicoResponse obtenerMedicoPorIdSinEstado(@PathVariable Long id);
}
