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


public class NotificacionRequest {

    @NotNull(message = "El remitente es obligatorio")
    private Long remitenteId;

    @NotBlank(message = "El mensaje es obligatorio")
    private String mensaje;

    // Solo UNO de estos tres según a quién va dirigida:
    private Long destinatarioId; // a un usuario
    private Long cursoId;        // a todos los inscritos activos de un curso
    private String paraRol;      // a todos los usuarios de un rol (solo coordinador)

    public NotificacionRequest() {
    }

    public Long getRemitenteId() { return remitenteId; }
    public void setRemitenteId(Long remitenteId) { this.remitenteId = remitenteId; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public Long getDestinatarioId() { return destinatarioId; }
    public void setDestinatarioId(Long destinatarioId) { this.destinatarioId = destinatarioId; }

    public Long getCursoId() { return cursoId; }
    public void setCursoId(Long cursoId) { this.cursoId = cursoId; }

    public String getParaRol() { return paraRol; }
    public void setParaRol(String paraRol) { this.paraRol = paraRol; }
}