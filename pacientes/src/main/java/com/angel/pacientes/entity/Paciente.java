package com.angel.pacientes.entity;

import com.angel.commons.enums.EstadoRegistro;
import com.angel.commons.utils.StringCustomUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.stream.Collectors;

@Entity
@Table(name="PACIENTES")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PACIENTE")
    private Long id;

    @Column(name = "NOMBRE", nullable = false, length = 50)
    private String nombre;

    @Column(name = "APELLIDO_PATERNO", nullable = false, length = 50)
    private String apellidoPaterno;

    @Column(name = "APELLIDO_MATERNO", nullable = false, length = 50)
    private String apellidoMaterno;

    @Column(name = "EDAD", nullable = false)
    private Short edad;

    @Column(name = "PESO", columnDefinition = "double precision", nullable = false)
    private Double peso;

    @Column(name = "ESTATURA", columnDefinition = "double precision", nullable = false)
    private Double estatura;

    @Column(name = "IMC", columnDefinition = "double precision", nullable = false)
    private Double imc;

    @Column(name = "EMAIL", nullable = false, length = 100)
    private String email;

    @Column(name = "NUM_EXPEDIENTE", nullable = false, length = 20)
    private String numExpediente;

    @Column(name = "TELEFONO", nullable = false, length = 10)
    private String telefono;

    @Column(name = "DIRECCION", nullable = false, length = 150)
    private String direccion;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "ESTADO_REGISTRO")
    private EstadoRegistro estadoRegistro;

    public void calcularImc() {
        this.imc = this.peso / (this.estatura * this.estatura);
    }

    public void generarNumExpediente() {
        if (this.telefono == null) return;

        this.numExpediente = this.telefono
                .chars()
                .mapToObj(c -> (char) c + "X")
                .collect(Collectors.joining());
    }

    public void validarDatos(String nombre, String apellidoPaterno, String apellidoMaterno,
                             Short edad, Double peso, Double estatura, String email,
                             String telefono, String direccion) {

        StringCustomUtils.validarTamanio(nombre, 1, 50,
                "El nombre es requerido y debe tener entre 1 y 50 caracteres");

        StringCustomUtils.validarTamanio(apellidoPaterno, 1, 50,
                "El apPaterno es requerido y debe tener entre 1 y 50 caracteres");

        StringCustomUtils.validarTamanio(apellidoMaterno, 1, 50,
                "El apMaterno es requerido y debe tener entre 1 y 50 caracteres");

        StringCustomUtils.validarTamanio(telefono, 10, 10,
                "El telefono es requerido y debe tener exactamente 10 caracteres");

        StringCustomUtils.validarTamanio(direccion, 1, 150,
                "La direccion es requerida y debe tener entre 1 y 150 caracteres");

        StringCustomUtils.validarTamanio(email, 1, 100,
                "El email es requerido y debe tener entre 1 y 100 caracteres");

        if (edad == null || edad < 0 || edad > 100)
            throw new IllegalArgumentException("La edad en requerida y debe estar entre 1 y 100 años");

        if (peso == null || peso < 0.1 || peso > 200)
            throw new IllegalArgumentException("El peso es requerido y debe estar entre 0.1kg y 200kg");

        if (estatura == null || estatura < 1.0 || estatura > 2.0)
            throw new IllegalArgumentException("La estatura es requerida y debes estar entre los 1.0 g 2.0 metros");

    }

    private void validarNoEliminado() {
        if (this.estadoRegistro == EstadoRegistro.ELIMINADO)
            throw new IllegalStateException("El medico ya está eliminado");
    }

    /*public boolean cambioEnDatos(String nombre, String apellidoPaterno, String apellidoMaterno,
                                 Short edad, Double peso, Double estatura,
                                 String email, String telefono, String direccion) {
        return !this.nombre.equals(nombre) ||
                !this.apellidoPaterno.equals(apellidoPaterno) ||
                !this.apellidoMaterno.equals(apellidoMaterno) ||
                !this.edad.equals(edad) ||
                !this.peso.equals(peso) ||
                !this.estatura.equals(estatura) ||
                !this.email.equals(email) ||
                !this.telefono.equals(telefono) ||
                !this.direccion.equals(direccion);
    }*/

    public void actualizar(String nombre, String apellidoPaterno, String apellidoMaterno,
                           Short edad, Double peso, Double estatura,
                           String email, String telefono, String direccion) {

        validarNoEliminado();
        validarDatos( nombre,  apellidoPaterno,  apellidoMaterno,
                 edad,  peso, estatura,
                 email,  telefono,  direccion);

        this.nombre = nombre.trim();
        this.apellidoPaterno = apellidoPaterno.trim();
        this.apellidoMaterno = apellidoMaterno.trim();
        this.edad = edad;
        this.peso = peso;
        this.estatura = estatura;
        this.email = email.trim();
        this.telefono = telefono.trim();
        this.direccion = direccion.trim();

        this.calcularImc();
        this.generarNumExpediente();

    }

    public void eliminar() {
        validarNoEliminado();
        this.estadoRegistro = EstadoRegistro.ELIMINADO;
    }

}

