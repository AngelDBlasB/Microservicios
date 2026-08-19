package com.angel.pacientes.service;

import com.angel.commons.dto.medicos.MedicoRequest;
import com.angel.commons.dto.pacientes.PacienteRequest;
import com.angel.commons.dto.pacientes.PacienteResponse;
import com.angel.commons.enums.EstadoRegistro;
import com.angel.commons.exceptions.RecursoNoEncontradoException;
import com.angel.pacientes.entity.Paciente;
import com.angel.pacientes.mapper.PacienteMapper;
import com.angel.pacientes.repository.PacienteRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class PacienteServiceImpl implements PacienteService {

    private final PacienteRepository pacienteRepository;
    private final PacienteMapper pacienteMapper;

    @Override
    public List<PacienteResponse> listar() {

        log.info("Listando pacientes activos");
        return pacienteRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(pacienteMapper::entidadAResponse).toList();

    }

    @Override
    public PacienteResponse obtenerPorId(Long id) {
        return pacienteMapper.entidadAResponse(obtenerPacienteActivoOExcepcion(id));
    }

    @Override
    public PacienteResponse obtenerPacientePorIdSinEstado(Long id) {
        log.info("Buscando paciente con id: {}", id);
        return pacienteMapper.entidadAResponse(pacienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Paciente sin estado no encontrado con id: " + id)));
    }

    @Override
    public PacienteResponse registrar(PacienteRequest request) {

        log.info("Registrando nuevo paciente...");

        validarDatosUnicos(request);

        Paciente paciente = pacienteMapper.requestAEntidad(request);
        paciente.calcularImc();
        paciente.generarNumExpediente();

        pacienteRepository.save(paciente);

        log.info("Nuevo paciente {} registrado",paciente.getNombre());

        return pacienteMapper.entidadAResponse(paciente);
    }

    @Override
    public PacienteResponse actualizar(PacienteRequest request, Long id) {

        Paciente paciente = obtenerPacienteActivoOExcepcion(id);
        log.info("Actualizando paciente con id: {}", id);

        validarCambiosUnicos(request,id);

        paciente.actualizar(
                    request.nombre(),
                    request.apellidoPaterno(),
                    request.apellidoMaterno(),
                    request.edad(),
                    request.peso(),
                    request.estatura(),
                    request.email(),
                    request.telefono(),
                    request.direccion()
            );

            log.info("Actualizando paciente con id: {}", id);

        return pacienteMapper.entidadAResponse(paciente);
    }

    @Override
    public void eliminar(Long id) {

        log.info("Eliminando paciente con ID: {}", id);

        Paciente paciente = obtenerPacienteActivoOExcepcion(id);

        paciente.eliminar();

        log.info("Paciente con ID {} eliminado exitosamente", paciente.getId());
    }


    private Paciente obtenerPacienteActivoOExcepcion(Long id){
        log.info("Buscando paciente activo con id: {}", id);
        return pacienteRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO)
                .orElseThrow(() -> new RecursoNoEncontradoException("Paciente activo no encontrada con id: " + id));

    }

    private void validarDatosUnicos(PacienteRequest request){

        log.info("Validando email nulo...");

        if (pacienteRepository.existsByEmailIgnoreCaseAndEstadoRegistro(
                request.email().trim(), EstadoRegistro.ACTIVO))
            throw new IllegalArgumentException("Ya existe un paciente activo registrado con el email: " + request.email());

        log.info("Validando telefono nulo...");

        if (pacienteRepository.existsByTelefonoAndEstadoRegistro(
                request.telefono().trim(), EstadoRegistro.ACTIVO))
            throw new IllegalArgumentException("Ya existe un paciente activo registrado con el telefono: " + request.telefono());

    }

    private void validarCambiosUnicos(PacienteRequest request, Long id) {

        log.info("Validando email nulo...");

        if (pacienteRepository.existsByEmailIgnoreCaseAndEstadoRegistroAndIdNot(
                request.email().trim(), EstadoRegistro.ACTIVO, id))
            throw new IllegalArgumentException("Ya existe un paciente activo registrado con el email: " + request.email());

        log.info("Validando telefono nulo...");

        if (pacienteRepository.existsByTelefonoAndEstadoRegistroAndIdNot(
                request.telefono().trim(), EstadoRegistro.ACTIVO, id))
            throw new IllegalArgumentException("Ya existe un paciente activo registrado con el telefono: " + request.telefono());
    }
}
