package com.angel.citas.repository;

import com.angel.citas.entity.Cita;
import com.angel.citas.enums.EstadoCita;
import com.angel.commons.enums.EstadoRegistro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByEstadoRegistro(EstadoRegistro estadoRegistro);

    Optional<Cita> findByIdAndEstadoRegistro(Long id, EstadoRegistro estadoRegistro);

    boolean existsByIdPacienteAndEstadoCitaInAndEstadoRegistro(
            Long idPaciente,
            List<EstadoCita> estados,
            EstadoRegistro estadoRegistro
    );

    boolean existsByIdPacienteAndEstadoCitaInAndEstadoRegistroAndIdNot(
            Long idPaciente,
            List<EstadoCita> estados,
            EstadoRegistro estadoRegistro,
            Long idCita
    );

    boolean existsByIdMedicoAndEstadoCitaInAndEstadoRegistro(
            Long idMedico,
            List<EstadoCita> estadosActivos,
            EstadoRegistro estadoRegistro
    );

    boolean existsByIdMedicoAndEstadoCitaInAndEstadoRegistroAndIdNot(
            Long idMedico,
            List<EstadoCita> estadosActivos,
            EstadoRegistro estadoRegistro,
            Long idCita
    );

}
