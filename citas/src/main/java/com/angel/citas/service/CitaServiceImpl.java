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
import com.angel.commons.enums.DisponibilidadMedico;
import com.angel.commons.enums.EstadoRegistro;
import com.angel.commons.exceptions.RecursoNoEncontradoException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class CitaServiceImpl implements CitaService {

    private final CitaRepository citaRepository;

    private final CitaMapper citaMapper;

    private final MedicoClient medicoClient;
    private final PacienteClient pacienteClient;

    private final List<EstadoCita> estadosRestrictivos = List.of(
            EstadoCita.CONFIRMADA,
            EstadoCita.EN_CURSO
    );

    private final List<EstadoCita> estadosActivos = List.of(
            EstadoCita.PENDIENTE,
            EstadoCita.CONFIRMADA,
            EstadoCita.EN_CURSO
    );

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

        validarMedicoSinCitasActivas(request.idMedico());
        validarDisponibilidadMedico(request.idMedico());

        PacienteResponse paciente = obtenerPacienteActivo(request.idPaciente());
        validarPacienteSinCitasActivas(request.idPaciente());

        Cita cita = citaMapper.requestAEntidad(request);

        citaRepository.save(cita);

        medicoClient.actualizarDisponibilidadMedico(
                medico.id(),
                DisponibilidadMedico.NO_DISPONIBLE.getCodigo()
        );

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
        Long idMedicoAnterior = cita.getIdMedico();

        MedicoResponse medico = obtenerMedicoActivo(request.idMedico());
        validarMedicoSinOtrasCitasActivas(request.idMedico(),id);
        PacienteResponse paciente = obtenerPacienteActivo(request.idPaciente());
        validarPacienteSinOtrasCitasActivas(request.idPaciente(),id);

        boolean hayCambioDeMedico = !Objects.equals(idMedicoAnterior, request.idMedico());

        if (hayCambioDeMedico) {
            validarDisponibilidadMedico(medico.id());
        }

        log.info("Actualizando cita con id {}", id);

        cita.actualizar(
                request.idPaciente(),
                request.idMedico(),
                request.fechaCita(),
                request.sintomas()
        );

        if (hayCambioDeMedico) {

            liberarMedico(idMedicoAnterior, id);

            medicoClient.actualizarDisponibilidadMedico(
                    request.idMedico(),
                    DisponibilidadMedico.NO_DISPONIBLE.getCodigo()
            );
        }


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
        EstadoCita nuevoEstado = EstadoCita.obtenerEstadoCitaPorCodigo(idEstadoCita);

        log.info("Actualizando cita con id {}", idCita);

        cita.actualizarEstadoCita(nuevoEstado);

        if (nuevoEstado == EstadoCita.FINALIZADA || nuevoEstado == EstadoCita.CANCELADA) {

            liberarMedico(cita.getIdMedico(), idCita);

        } else {
            DisponibilidadMedico disponibilidad = determinarDisponibilidadMedico(nuevoEstado);
            medicoClient.actualizarDisponibilidadMedico(
                    cita.getIdMedico(),
                    disponibilidad.getCodigo()
            );
        }

        log.info("Estado de la cita {} actualizando correctamente", idCita);
    }

    @Override
    public void eliminar(Long id) {

        Cita cita = obtenerCitaOException(id);

        log.info("Eliminando cita con id {}", id);

        cita.eliminar();

        liberarMedico(cita.getIdMedico(), cita.getId());

        log.info("Cita con id {} ha sido marcada como eliminada", id);

    }

    @Override
    @Transactional(readOnly = true)
    public boolean pacienteTieneCitasConfirmadasOEnCurso(Long idPaciente) {

        return citaRepository.existsByIdPacienteAndEstadoCitaInAndEstadoRegistro(
                idPaciente,
                estadosRestrictivos,
                EstadoRegistro.ACTIVO
        );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean medicoTieneCitasConfirmadasOEnCurso(Long idMedico) {

        return citaRepository.existsByIdMedicoAndEstadoCitaInAndEstadoRegistro(
                idMedico,
                estadosRestrictivos,
                EstadoRegistro.ACTIVO
        );
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

    private void validarPacienteSinCitasActivas(Long idPaciente) {

        if (citaRepository.existsByIdPacienteAndEstadoCitaInAndEstadoRegistro(
                idPaciente, estadosActivos, EstadoRegistro.ACTIVO)) {
            throw new IllegalStateException(
                    "El paciente ya tiene una cita activa en estado PENDIENTE, CONFIRMADA o EN_CURSO."
            );
        }
    }

    private void validarPacienteSinOtrasCitasActivas(Long idPaciente, Long idCita) {

        if (citaRepository.existsByIdPacienteAndEstadoCitaInAndEstadoRegistroAndIdNot(
                idPaciente, estadosActivos, EstadoRegistro.ACTIVO, idCita)) {
            throw new IllegalStateException(
                    "El paciente ya tiene una cita activa en estado PENDIENTE, CONFIRMADA o EN_CURSO."
            );
        }
    }

    private void validarMedicoSinCitasActivas(Long idMedico) {

        if (citaRepository.existsByIdMedicoAndEstadoCitaInAndEstadoRegistro(
                idMedico, estadosActivos, EstadoRegistro.ACTIVO)) {
            throw new IllegalStateException(
                    "El medico ya tiene una cita activa en estado PENDIENTE, CONFIRMADA o EN_CURSO."
            );
        }
    }

    private void validarMedicoSinOtrasCitasActivas(Long idMedico, Long idCita) {

        if (medicoTieneOtrasCitasActivas(idMedico, idCita)) {
            throw new IllegalStateException(
                    "El medico ya tiene una cita activa en estado PENDIENTE, CONFIRMADA o EN_CURSO."
            );
        }
    }


    private void validarDisponibilidadMedico(Long idMedico) {

        MedicoResponse medico = obtenerMedicoActivo(idMedico);

        if (!DisponibilidadMedico.DISPONIBLE.getDescripcion().equalsIgnoreCase(medico.disponibilidad())) {
            throw new IllegalStateException(
                    String.format("El médico con ID %d no está disponible para asignar cita. Estado actual: %s",
                            idMedico,
                            medico.disponibilidad())
            );
        }
    }

    private void liberarMedico(Long idMedico, Long idCitaActual) {

        if (!medicoTieneOtrasCitasActivas(idMedico, idCitaActual)) {
            medicoClient.actualizarDisponibilidadMedico(
                    idMedico,
                    DisponibilidadMedico.DISPONIBLE.getCodigo()
            );
        }
    }

    private boolean medicoTieneOtrasCitasActivas(Long idMedico, Long idCita) {
        return citaRepository.existsByIdMedicoAndEstadoCitaInAndEstadoRegistroAndIdNot(
                idMedico,
                estadosActivos,
                EstadoRegistro.ACTIVO,
                idCita
        );
    }

    private DisponibilidadMedico determinarDisponibilidadMedico(EstadoCita estadoCita) {
        return switch (estadoCita) {
            case PENDIENTE, CONFIRMADA -> DisponibilidadMedico.NO_DISPONIBLE;
            case EN_CURSO -> DisponibilidadMedico.EN_CONSULTA;
            case FINALIZADA, CANCELADA -> DisponibilidadMedico.DISPONIBLE;
        };
    }

}
