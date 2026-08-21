package com.angel.medicos.service;

import com.angel.commons.client.CitasClient;
import com.angel.commons.dto.medicos.MedicoRequest;
import com.angel.commons.dto.medicos.MedicoResponse;
import com.angel.commons.enums.DisponibilidadMedico;
import com.angel.commons.enums.EspecialidadMedico;
import com.angel.commons.enums.EstadoRegistro;
import com.angel.commons.exceptions.RecursoNoEncontradoException;
import com.angel.medicos.entity.Medico;
import com.angel.medicos.mapper.MedicoMapper;
import com.angel.medicos.repository.MedicoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class MedicoServiceImpl implements MedicoService {

    private final MedicoRepository medicoRepository;
    private final MedicoMapper medicoMapper;

    private final CitasClient citasClient;


    @Override
    @Transactional(readOnly = true)
    public List<MedicoResponse> listar() {

        log.info("Listando todos los medicos activos");

        return medicoRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(medicoMapper :: entidadAResponse).toList();
    }

    @Override
    public MedicoResponse obtenerPorId(Long id) {
        return medicoMapper.entidadAResponse(obtenerMedicoActivoOExcepciom(id));
    }

    @Override
    public MedicoResponse obtenerMedicoPorIdSinEstado(Long id) {

        log.info("Buscando medico sin estado con id{}",id);

        return medicoMapper.entidadAResponse(medicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Medico sin estado no encontrado con id: " + id)));
    }

    @Override
    public MedicoResponse registrar(MedicoRequest request) {

        log.info("Registrando nuevo medico: {}", request.nombre());

        validarDatosUnicos(request);

        Medico medico = medicoMapper.requestAEntidad(request);

        medico.actualizarEspecialidad(
                EspecialidadMedico.obtenerEspecialidadPorCodigo(request.idEspecialidad()));

        medicoRepository.save(medico);

        log.info("Nuevo médico registrado: {}", medico.getNombre());

        return medicoMapper.entidadAResponse(medico);
    }

    @Override
    public MedicoResponse actualizar(MedicoRequest request, Long id) {

        Medico medico = obtenerMedicoActivoOExcepciom(id);

        log.info("Actualizando medico con id: {}", id);

        validarSinCitasConfirmadasOEnCurso(id);

        validarCambiosUnicos(request,id);

        medico.actualizar(
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno(),
                request.edad(),
                request.email(),
                request.telefono(),
                request.cedulaProfesional(),
                EspecialidadMedico.obtenerEspecialidadPorCodigo(request.idEspecialidad()));

        log.info("Medico actualizado correctamente");

        return medicoMapper.entidadAResponse(medico);
    }

    @Override
    public void eliminar(Long id) {

        Medico medico = obtenerMedicoActivoOExcepciom(id);

        log.info("Eliminando medico con id: {}", id);

        validarSinCitasConfirmadasOEnCurso(id);

        medico.eliminar();

        log.info("Medico eliminado correctamente");

    }

    @Override
    public void actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad) {

        Medico medico = obtenerMedicoActivoOExcepciom(idMedico);

        log.info("Actualizando disponibilidad del medico con id: {}",idMedico);

        DisponibilidadMedico nuevaDisponibilidad = DisponibilidadMedico.
                obtenerDisponibilidadPorCodigo(idDisponibilidad);

        medico.actualizarDisponibilidad(nuevaDisponibilidad);

        log.info("Disponibilidad del medico con id {} cambio a {}",
                idMedico, nuevaDisponibilidad);

    }

    private Medico obtenerMedicoActivoOExcepciom(Long id){
        log.info("Buscando medico activo con id {} ", id);

        return medicoRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO)
                .orElseThrow(()-> new RecursoNoEncontradoException(
                        "Medico activo no encontrado con id: " + id
                ));
    }

    private void validarDatosUnicos(MedicoRequest request){

        log.info("Validando email nulo...");

        if (medicoRepository.existsByEmailIgnoreCaseAndEstadoRegistro(
                request.email().trim(), EstadoRegistro.ACTIVO))
            throw new IllegalArgumentException("Ya existe un medico activo registrado con el email: " + request.email());

        log.info("Validando telefono nulo...");

        if (medicoRepository.existsByTelefonoAndEstadoRegistro(
                request.telefono().trim(), EstadoRegistro.ACTIVO))
            throw new IllegalArgumentException("Ya existe un medico activo registrado con el telefono: " + request.telefono());

        log.info("Validando cedula profesional unica ...");

        if (medicoRepository.existsByCedulaProfesionalIgnoreCaseAndEstadoRegistro(
                request.cedulaProfesional().trim(), EstadoRegistro.ACTIVO))
            throw new IllegalArgumentException("Ya existe un medico activo registrado con la cedula profesional: "
                    + request.cedulaProfesional());
    }

    private void validarCambiosUnicos(MedicoRequest request, Long id){

        log.info("Validando email nulo...");

        if (medicoRepository.existsByEmailIgnoreCaseAndEstadoRegistroAndIdNot(
                request.email().trim(), EstadoRegistro.ACTIVO, id))
            throw new IllegalArgumentException("Ya existe un medico activo registrado con el email: " + request.email());

        log.info("Validando telefono nulo...");

        if (medicoRepository.existsByTelefonoAndEstadoRegistroAndIdNot(
                request.telefono().trim(), EstadoRegistro.ACTIVO,id))
            throw new IllegalArgumentException("Ya existe un medico activo registrado con el telefono: " + request.telefono());

        log.info("Validando cedula profesional unica ...");

        if (medicoRepository.existsByCedulaProfesionalIgnoreCaseAndEstadoRegistroAndIdNot(
                request.cedulaProfesional().trim(), EstadoRegistro.ACTIVO,id))
            throw new IllegalArgumentException("Ya existe un medico activo registrado con la cedula profesional: "
                    + request.cedulaProfesional());
    }

    private void validarSinCitasConfirmadasOEnCurso(Long idMedico) {
        Boolean tieneCitas = citasClient.medicoTieneCitasConfirmadasOEnCurso(idMedico);

        if (Boolean.TRUE.equals(tieneCitas)) {
            throw new IllegalStateException(
                    String.format("El medico con ID %d tiene citas en estado CONFIRMADA o EN_CURSO y no se puede actualizar ni eliminar.", idMedico)
            );
        }
    }


}
