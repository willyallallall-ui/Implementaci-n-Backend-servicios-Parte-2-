/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.educacion.plataforma.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import javax.persistence.Column;
/**
 *
 * @author Willi
 */
@Entity
@Table(name = "cursos")
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del curso es obligatorio")
    private String nombre;

    private String descripcion;

    // "ACTIVO" o "INACTIVO"
    private String estado;
    @Column(length = 1000)
    private String objetivos;

    @Column(length = 1000)
    private String resultadosAprendizaje;

    private Integer cupos;

    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    // "BORRADOR" o "PUBLICADO" (lo cambia el coordinador)
    private String publicacion;

    @NotNull(message = "La oferta es obligatoria")
    @ManyToOne
    @JoinColumn(name = "oferta_id")
    private Oferta oferta;

    @NotNull(message = "El docente es obligatorio")
    @ManyToOne
    @JoinColumn(name = "docente_id")
    private Usuario docente;

    public Curso() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Oferta getOferta() { return oferta; }
    public void setOferta(Oferta oferta) { this.oferta = oferta; }

    public Usuario getDocente() { return docente; }
    public void setDocente(Usuario docente) { this.docente = docente; }
    
    public String getObjetivos() { return objetivos; }
    public void setObjetivos(String objetivos) { this.objetivos = objetivos; }

    public String getResultadosAprendizaje() { return resultadosAprendizaje; }
    public void setResultadosAprendizaje(String resultadosAprendizaje) { this.resultadosAprendizaje = resultadosAprendizaje; }

    public Integer getCupos() { return cupos; }
    public void setCupos(Integer cupos) { this.cupos = cupos; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public String getPublicacion() { return publicacion; }
    public void setPublicacion(String publicacion) { this.publicacion = publicacion; }
}