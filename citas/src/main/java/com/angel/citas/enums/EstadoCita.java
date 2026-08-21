package com.angel.citas.enums;

import com.angel.commons.enums.DisponibilidadMedico;
import com.angel.commons.exceptions.RecursoNoEncontradoException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

@RequiredArgsConstructor
@Getter
public enum EstadoCita {

    PENDIENTE(1L, "PENDIENTE", true, true, DisponibilidadMedico.NO_DISPONIBLE){
        @Override
        public Set<EstadoCita> puedeCambiar() {
            return EnumSet.of(CONFIRMADA,CANCELADA);
        }
    },
    CONFIRMADA(2L, "CONFIRMADA", true, false, DisponibilidadMedico.NO_DISPONIBLE) {
        @Override
        public Set<EstadoCita> puedeCambiar() {
            return EnumSet.of(EN_CURSO,CANCELADA);
        }
    },
    EN_CURSO(3L, "EN_CURSO", false, false, DisponibilidadMedico.EN_CONSULTA) {
        @Override
        public Set<EstadoCita> puedeCambiar() {
            return EnumSet.of(FINALIZADA);
        }
    },
    FINALIZADA(4L, "FINALIZADA", false, true, DisponibilidadMedico.DISPONIBLE) {
        @Override
        public Set<EstadoCita> puedeCambiar() {
            return Set.of();
        }
    },
    CANCELADA(5L, "CANCELADA", false, true, DisponibilidadMedico.DISPONIBLE){        @Override
        public Set<EstadoCita> puedeCambiar() {
            return Set.of();
        }
    };

    private final Long codigo;

    private final String descripcion;

    private final boolean actualizable;

    private final boolean eliminable;

    private final DisponibilidadMedico disponibilidadMedicoResultante;

    public abstract Set<EstadoCita> puedeCambiar();

    public boolean puedeCambiarA(EstadoCita nuevoEstado){
        return puedeCambiar().contains(nuevoEstado);
    }

    public static EstadoCita obtenerEstadoCitaPorCodigo(Long codigo){
        for (EstadoCita e : values()) {
            if (Objects.equals(e.codigo, codigo)) {
                return e;
            }
        }
        throw new RecursoNoEncontradoException("Codigo de cita no válido: " + codigo);
    }

}
