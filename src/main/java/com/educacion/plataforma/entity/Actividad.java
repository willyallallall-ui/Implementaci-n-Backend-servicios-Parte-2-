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
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Willi
 */

@Entity
@Table(name = "actividades")
public class Actividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título de la actividad es obligatorio")
    private String titulo;

    private String descripcion;

    // "TALLER", "QUIZ", "TAREA" o "PROYECTO"
    private String tipo;

    @NotNull(message = "El puntaje máximo es obligatorio")
    @DecimalMin(value = "0.1", message = "El puntaje máximo debe ser mayor que 0")
    private Double puntajeMaximo;

    private LocalDate fechaLimite;

    @NotNull(message = "La unidad es obligatoria")
    @ManyToOne
    @JoinColumn(name = "unidad_id")
    private Unidad unidad;

    public Actividad() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Double getPuntajeMaximo() { return puntajeMaximo; }
    public void setPuntajeMaximo(Double puntajeMaximo) { this.puntajeMaximo = puntajeMaximo; }

    public LocalDate getFechaLimite() { return fechaLimite; }
    public void setFechaLimite(LocalDate fechaLimite) { this.fechaLimite = fechaLimite; }

    public Unidad getUnidad() { return unidad; }
    public void setUnidad(Unidad unidad) { this.unidad = unidad; }
}