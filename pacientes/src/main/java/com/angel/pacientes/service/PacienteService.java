package com.angel.pacientes.service;


import com.angel.commons.dto.pacientes.PacienteRequest;
import com.angel.commons.dto.pacientes.PacienteResponse;
import com.angel.commons.service.CrudService;

import java.util.List;

public interface PacienteService extends CrudService<PacienteRequest, PacienteResponse> {

    PacienteResponse obtenerPacientePorIdSinEstado(Long id);

    //List<PacienteResponse> listarCanceladas();

}
