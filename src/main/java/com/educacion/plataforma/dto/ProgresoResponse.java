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
public class ProgresoResponse {

    private Long cursoId;
    private String cursoNombre;
    private Long estudianteId;
    private String estudianteNombre;
    private int totalActividades;
    private int actividadesEntregadas;
    private int actividadesCalificadas;
    private double porcentajeAvance;
    private double puntajeObtenido;
    private double puntajeMaximoCurso;
    private double promedioSobre5;
    private double porcentajeAsignado;   // suma de porcentajes de las actividades del curso
    private double porcentajeEvaluado;    

    public ProgresoResponse() {
    }

    public Long getCursoId() { return cursoId; }
    public void setCursoId(Long cursoId) { this.cursoId = cursoId; }

    public String getCursoNombre() { return cursoNombre; }
    public void setCursoNombre(String cursoNombre) { this.cursoNombre = cursoNombre; }

    public Long getEstudianteId() { return estudianteId; }
    public void setEstudianteId(Long estudianteId) { this.estudianteId = estudianteId; }

    public String getEstudianteNombre() { return estudianteNombre; }
    public void setEstudianteNombre(String estudianteNombre) { this.estudianteNombre = estudianteNombre; }

    public int getTotalActividades() { return totalActividades; }
    public void setTotalActividades(int totalActividades) { this.totalActividades = totalActividades; }

    public int getActividadesEntregadas() { return actividadesEntregadas; }
    public void setActividadesEntregadas(int actividadesEntregadas) { this.actividadesEntregadas = actividadesEntregadas; }

    public int getActividadesCalificadas() { return actividadesCalificadas; }
    public void setActividadesCalificadas(int actividadesCalificadas) { this.actividadesCalificadas = actividadesCalificadas; }

    public double getPorcentajeAvance() { return porcentajeAvance; }
    public void setPorcentajeAvance(double porcentajeAvance) { this.porcentajeAvance = porcentajeAvance; }

    public double getPuntajeObtenido() { return puntajeObtenido; }
    public void setPuntajeObtenido(double puntajeObtenido) { this.puntajeObtenido = puntajeObtenido; }

    public double getPuntajeMaximoCurso() { return puntajeMaximoCurso; }
    public void setPuntajeMaximoCurso(double puntajeMaximoCurso) { this.puntajeMaximoCurso = puntajeMaximoCurso; }

    public double getPromedioSobre5() { return promedioSobre5; }
    public void setPromedioSobre5(double promedioSobre5) { this.promedioSobre5 = promedioSobre5; }
    
    public double getPorcentajeAsignado() { return porcentajeAsignado; }
    public void setPorcentajeAsignado(double porcentajeAsignado) { this.porcentajeAsignado = porcentajeAsignado; }

    public double getPorcentajeEvaluado() { return porcentajeEvaluado; }
    public void setPorcentajeEvaluado(double porcentajeEvaluado) { this.porcentajeEvaluado = porcentajeEvaluado; }    
}