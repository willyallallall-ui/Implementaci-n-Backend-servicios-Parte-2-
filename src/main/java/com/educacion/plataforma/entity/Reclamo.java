/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.educacion.plataforma.entity;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Willi
 */

@Entity
@Table(name = "reclamos")
public class Reclamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El motivo es obligatorio")
    @Column(length = 1000)
    private String motivo;

    // ABIERTO, RESUELTO o RECHAZADO (lo asigna el servicio)
    private String estado;

    @Column(length = 1000)
    private String respuesta;

    private Double notaAnterior;

    private LocalDate fechaCreacion;

    private LocalDate fechaRespuesta;

    @NotNull(message = "La entrega es obligatoria")
    @ManyToOne
    @JoinColumn(name = "entrega_id")
    private Entrega entrega;

    @NotNull(message = "El estudiante es obligatorio")
    @ManyToOne
    @JoinColumn(name = "estudiante_id")
    private Usuario estudiante;

    public Reclamo() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getRespuesta() { return respuesta; }
    public void setRespuesta(String respuesta) { this.respuesta = respuesta; }

    public Double getNotaAnterior() { return notaAnterior; }
    public void setNotaAnterior(Double notaAnterior) { this.notaAnterior = notaAnterior; }

    public LocalDate getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDate fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDate getFechaRespuesta() { return fechaRespuesta; }
    public void setFechaRespuesta(LocalDate fechaRespuesta) { this.fechaRespuesta = fechaRespuesta; }

    public Entrega getEntrega() { return entrega; }
    public void setEntrega(Entrega entrega) { this.entrega = entrega; }

    public Usuario getEstudiante() { return estudiante; }
    public void setEstudiante(Usuario estudiante) { this.estudiante = estudiante; }
}