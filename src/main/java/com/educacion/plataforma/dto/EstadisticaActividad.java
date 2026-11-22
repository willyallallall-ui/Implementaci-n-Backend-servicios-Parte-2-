/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.educacion.plataforma.dto;

/**
 *
 * @author Willi
 */
public class EstadisticaActividad {

    private Long actividadId;
    private String titulo;
    private double puntajeMaximo;
    private int entregas;
    private double notaPromedio;
    private double notaMaxima;
    private double notaMinima;

    public EstadisticaActividad() {
    }

    public Long getActividadId() { return actividadId; }
    public void setActividadId(Long actividadId) { this.actividadId = actividadId; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public double getPuntajeMaximo() { return puntajeMaximo; }
    public void setPuntajeMaximo(double puntajeMaximo) { this.puntajeMaximo = puntajeMaximo; }

    public int getEntregas() { return entregas; }
    public void setEntregas(int entregas) { this.entregas = entregas; }

    public double getNotaPromedio() { return notaPromedio; }
    public void setNotaPromedio(double notaPromedio) { this.notaPromedio = notaPromedio; }

    public double getNotaMaxima() { return notaMaxima; }
    public void setNotaMaxima(double notaMaxima) { this.notaMaxima = notaMaxima; }

    public double getNotaMinima() { return notaMinima; }
    public void setNotaMinima(double notaMinima) { this.notaMinima = notaMinima; }
}