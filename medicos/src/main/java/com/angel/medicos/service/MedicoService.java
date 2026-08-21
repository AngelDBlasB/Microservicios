package com.angel.medicos.service;

import com.angel.commons.dto.medicos.MedicoRequest;
import com.angel.commons.dto.medicos.MedicoResponse;
import com.angel.commons.service.CrudService;


public interface MedicoService extends CrudService<MedicoRequest, MedicoResponse> {

    MedicoResponse obtenerMedicoPorIdSinEstado(Long id);

    void actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad);




}
