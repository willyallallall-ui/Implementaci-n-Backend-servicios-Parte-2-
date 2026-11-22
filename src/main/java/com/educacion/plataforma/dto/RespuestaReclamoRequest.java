/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.educacion.plataforma.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Willi
 */

public class RespuestaReclamoRequest {

    @NotNull(message = "El id del docente es obligatorio")
    private Long docenteId;

    // true = se acepta el reclamo (RESUELTO), false = se rechaza (RECHAZADO)
    @NotNull(message = "Debe indicar si el reclamo se acepta")
    private Boolean aceptado;

    @NotBlank(message = "La respuesta es obligatoria")
    private String respuesta;

    // Solo si se acepta y se corrige la nota
    private Double nuevaNota;

    public RespuestaReclamoRequest() {
    }

    public Long getDocenteId() { return docenteId; }
    public void setDocenteId(Long docenteId) { this.docenteId = docenteId; }

    public Boolean getAceptado() { return aceptado; }
    public void setAceptado(Boolean aceptado) { this.aceptado = aceptado; }

    public String getRespuesta() { return respuesta; }
    public void setRespuesta(String respuesta) { this.respuesta = respuesta; }

    public Double getNuevaNota() { return nuevaNota; }
    public void setNuevaNota(Double nuevaNota) { this.nuevaNota = nuevaNota; }
}