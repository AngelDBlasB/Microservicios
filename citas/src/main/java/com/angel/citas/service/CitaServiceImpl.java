package com.angel.citas.service;

import com.angel.citas.dto.CitaRequest;
import com.angel.citas.dto.CitaResponse;
import com.angel.citas.entity.Cita;
import com.angel.citas.enums.EstadoCita;
import com.angel.citas.mapper.CitaMapper;
import com.angel.citas.repository.CitaRepository;
import com.angel.commons.client.MedicoClient;
import com.angel.commons.client.PacienteClient;
import com.angel.commons.dto.medicos.MedicoResponse;
import com.angel.commons.dto.pacientes.PacienteResponse;
import com.angel.commons.enums.EstadoRegistro;
import com.angel.commons.exceptions.RecursoNoEncontradoException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class CitaServiceImpl implements CitaService {

    private final CitaRepository citaRepository;

    private final CitaMapper citaMapper;

    private final MedicoClient medicoClient;
    private final PacienteClient pacienteClient;

    @Override
    @Transactional(readOnly = true)
    public List<CitaResponse> listar() {

        log.info("Listando todas las citas activas");

        return citaRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(cita -> citaMapper.entidadAResponse(
                        cita,
                        obtenerPacienteSinEstado(cita.getIdPaciente()),
                        obtenerMedicoSinEstado(cita.getIdMedico())
                )).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CitaResponse obtenerPorId(Long id) {

        Cita cita = obtenerCitaOException(id);

        return citaMapper.entidadAResponse(
                cita,
                obtenerPacienteSinEstado(cita.getIdPaciente()),
                obtenerMedicoSinEstado(cita.getIdMedico())
        );
    }

    @Override
    public CitaResponse registrar(CitaRequest request) {

        log.info("Registrando nueva cita");

        MedicoResponse medico = obtenerMedicoActivo(request.idMedico());
        PacienteResponse paciente = obtenerPacienteActivo(request.idPaciente());

        Cita cita = citaMapper.requestAEntidad(request);

        citaRepository.save(cita);

        log.info("Cita registrada exitosamente");

        return citaMapper.entidadAResponse(
                cita,
                paciente,
                medico
        );
    }

    @Override
    public CitaResponse actualizar(CitaRequest request, Long id) {

        Cita cita = obtenerCitaOException(id);

        MedicoResponse medico = obtenerMedicoActivo(request.idMedico());
        PacienteResponse paciente = obtenerPacienteActivo(request.idPaciente());

        log.info("Actualizando cita con id {}", id);

        cita.actualizar(
                request.idPaciente(),
                request.idMedico(),
                request.fechaCita(),
                request.sintomas()
        );

        log.info("Cita actualizada con id: {}", id);

        return citaMapper.entidadAResponse(
                cita,
                paciente,
                medico
        );

    }

    @Override
    public void actualizarEstadoCita(Long idCita, Long idEstadoCita) {

        Cita cita = obtenerCitaOException(idCita);

        log.info("Actualizando cita con id {}", idCita);

        cita.actualizarEstadoCita(EstadoCita.obtenerEstadoCitaPorCodigo(idEstadoCita));

        log.info("Estado de la cita {} actualizando correctamente", idCita);
    }

    @Override
    public void eliminar(Long id) {

        Cita cita = obtenerCitaOException(id);

        log.info("Eliminando cita con id {}", id);

        cita.eliminar();

        log.info("Cita con id {} ha sido marcada como eliminada", id);

    }

    private Cita obtenerCitaOException(Long id){

        log.info("Buscando cita por ID {}", id);

        return citaRepository.findById(id).orElseThrow(() ->
                new RecursoNoEncontradoException("Cita no encontrada con id: " + id));

    }

    private MedicoResponse obtenerMedicoActivo(Long id) {

        log.info("Buscando médico activo con id {} en el servicio remoto...",id);

        return medicoClient.obtenerMedicoActivoId(id);
    }

    private MedicoResponse obtenerMedicoSinEstado(Long id) {

        log.info("Buscando médico sin estado con id {} en el servicio remoto...",id);

        return medicoClient.obtenerMedicoPorIdSinEstado(id);
    }

    private PacienteResponse obtenerPacienteActivo(Long id) {

        log.info("Buscando paciente activo con id {} en el servicio remoto...",id);

        return pacienteClient.obtenerPacienteActivoId(id);
    }

    private PacienteResponse obtenerPacienteSinEstado(Long id) {

        log.info("Buscando paciente sin estado con id {} en el servicio remoto...",id);

        return pacienteClient.obtenerPacientePorIdSinEstado(id);
    }

}
