package com.angel.citas.service;

import com.angel.citas.dto.CitaRequest;
import com.angel.citas.dto.CitaResponse;
import com.angel.commons.service.CrudService;

public interface CitaService extends CrudService<CitaRequest, CitaResponse> {
    void actualizarEstadoCita(Long idCita, Long idEstadoCita);

    boolean pacienteTieneCitasConfirmadasOEnCurso(Long idPaciente);
    boolean medicoTieneCitasConfirmadasOEnCurso(Long idMedico);
}
