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



public class MensajeRequest {

    @NotNull(message = "El autor es obligatorio")
    private Long autorId;

    @NotBlank(message = "El texto es obligatorio")
    private String texto;

    public MensajeRequest() {
    }

    public Long getAutorId() { return autorId; }
    public void setAutorId(Long autorId) { this.autorId = autorId; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
}