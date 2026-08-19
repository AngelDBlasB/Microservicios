package com.angel.pacientes.mapper;


import com.angel.commons.dto.medicos.MedicoRequest;
import com.angel.commons.dto.medicos.MedicoResponse;
import com.angel.commons.dto.pacientes.PacienteRequest;
import com.angel.commons.dto.pacientes.PacienteResponse;
import com.angel.commons.enums.EstadoRegistro;
import com.angel.commons.mapper.CommonMapper;
import com.angel.pacientes.entity.Paciente;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PacienteMapper implements CommonMapper<PacienteRequest, PacienteResponse, Paciente> {

    public Paciente requestAEntidad(PacienteRequest request){

        if(request == null) return null;

        return Paciente.builder()
                .nombre(request.nombre().trim())
                .apellidoPaterno(request.apellidoPaterno().trim())
                .apellidoMaterno(request.apellidoMaterno().trim())
                .edad(request.edad())
                .peso(request.peso())
                .estatura(request.estatura())
                .email(request.email().toLowerCase().trim())
                .telefono(request.telefono().trim())
                .direccion(request.direccion().trim())
                .estadoRegistro(EstadoRegistro.ACTIVO)
                .build();

    }

    public PacienteResponse entidadAResponse(Paciente entidad){

        if (entidad == null) return null;

        return new PacienteResponse(
                entidad.getId(),
                String.join(" ",
                        entidad.getNombre(),
                        entidad.getApellidoPaterno(),
                        entidad.getApellidoMaterno()),
                entidad.getEdad(),
                entidad.getPeso(),
                entidad.getEstatura(),
                entidad.getImc(),
                entidad.getEmail(),
                entidad.getTelefono(),
                entidad.getDireccion(),
                entidad.getNumExpediente()

        );

    }

}
