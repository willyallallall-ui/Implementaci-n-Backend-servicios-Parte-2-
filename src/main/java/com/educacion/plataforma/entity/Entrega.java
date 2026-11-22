/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.educacion.plataforma.entity;

import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
/**
 *
 * @author Willi
 */


@Entity
@Table(name = "entregas")
public class Entrega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Texto o enlace con la respuesta del estudiante
    private String contenido;

    // Se asigna sola en el servicio
    private LocalDate fechaEntrega;

    // "ENTREGADA" o "CALIFICADA" (se asigna en el servicio)
    private String estado;
    
        // Nota puesta por el docente (null mientras no esté calificada)
    private Double nota;

    // Comentario del docente
    private String retroalimentacion;

    // Se asigna sola al calificar
    private LocalDate fechaCalificacion;

    @NotNull(message = "La actividad es obligatoria")
    @ManyToOne
    @JoinColumn(name = "actividad_id")
    private Actividad actividad;

    @NotNull(message = "El estudiante es obligatorio")
    @ManyToOne
    @JoinColumn(name = "estudiante_id")
    private Usuario estudiante;

    public Entrega() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public LocalDate getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDate fechaEntrega) { this.fechaEntrega = fechaEntrega; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Actividad getActividad() { return actividad; }
    public void setActividad(Actividad actividad) { this.actividad = actividad; }

    public Usuario getEstudiante() { return estudiante; }
    public void setEstudiante(Usuario estudiante) { this.estudiante = estudiante; }
    
    public Double getNota() { return nota; }
    public void setNota(Double nota) { this.nota = nota; }

    public String getRetroalimentacion() { return retroalimentacion; }
    public void setRetroalimentacion(String retroalimentacion) { this.retroalimentacion = retroalimentacion; }

    public LocalDate getFechaCalificacion() { return fechaCalificacion; }
    public void setFechaCalificacion(LocalDate fechaCalificacion) { this.fechaCalificacion = fechaCalificacion; }
    
    
}