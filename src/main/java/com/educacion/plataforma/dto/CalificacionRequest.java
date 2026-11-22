/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.educacion.plataforma.dto;

import javax.validation.constraints.NotNull;
/**
 *
 * @author Willi
 */


public class CalificacionRequest {

    @NotNull(message = "El id del docente es obligatorio")
    private Long docenteId;

    @NotNull(message = "La nota es obligatoria")
    private Double nota;

    private String retroalimentacion;

    public CalificacionRequest() {
    }

    public Long getDocenteId() { return docenteId; }
    public void setDocenteId(Long docenteId) { this.docenteId = docenteId; }

    public Double getNota() { return nota; }
    public void setNota(Double nota) { this.nota = nota; }

    public String getRetroalimentacion() { return retroalimentacion; }
    public void setRetroalimentacion(String retroalimentacion) { this.retroalimentacion = retroalimentacion; }
}