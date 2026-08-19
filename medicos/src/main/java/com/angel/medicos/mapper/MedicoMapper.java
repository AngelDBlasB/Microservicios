package com.angel.medicos.mapper;

import com.angel.commons.dto.medicos.MedicoRequest;
import com.angel.commons.dto.medicos.MedicoResponse;
import com.angel.commons.enums.DisponibilidadMedico;
import com.angel.commons.enums.EstadoRegistro;
import com.angel.commons.mapper.CommonMapper;
import com.angel.medicos.entity.Medico;
import org.springframework.stereotype.Component;

@Component
public class MedicoMapper implements CommonMapper<MedicoRequest, MedicoResponse, Medico> {

    @Override
    public Medico requestAEntidad(MedicoRequest request) {
        if  (request == null) return null;

        return Medico.builder()
                .nombre(request.nombre().trim())
                .apellidoPaterno(request.apellidoPaterno().trim())
                .apellidoMaterno(request.apellidoMaterno().trim())
                .edad(request.edad())
                .email(request.email().toLowerCase().trim())
                .telefono(request.telefono().trim())
                .cedulaProfesional(request.cedulaProfesional().trim())
                .disponibilidad(DisponibilidadMedico.DISPONIBLE)
                .estadoRegistro(EstadoRegistro.ACTIVO)
                .build();
    }

    @Override
    public MedicoResponse entidadAResponse(Medico entidad) {
        if  (entidad == null) return null;
        return new MedicoResponse(
                entidad.getId(),
                String.join(" ",
                    entidad.getNombre(),
                    entidad.getApellidoMaterno(),
                    entidad.getApellidoMaterno()),
                entidad.getEdad(),
                entidad.getEmail(),
                entidad.getTelefono(),
                entidad.getCedulaProfesional(),
                entidad.getEspecialidad().getDescripcion(),
                entidad.getDisponibilidad().getDescripcion(),
                entidad.getDisponibilidad().getCodigo()
        );
    }
}
