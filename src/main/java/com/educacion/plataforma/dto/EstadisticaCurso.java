/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.educacion.plataforma.dto;

import java.util.List;
/**
 *
 * @author Willi
 */


public class EstadisticaCurso {

    private Long cursoId;
    private String cursoNombre;
    private int totalInscritos;
    private int estudiantesConEntregas;
    private int totalActividades;
    private int totalEntregas;
    private int entregasCalificadas;
    private double promedioGeneralSobre5;
    private int aprobados;
    private int reprobados;
    private double porcentajeAprobacion;
    private Double calificacionPromedioCurso;
    private List<EstadisticaActividad> actividades;

    public EstadisticaCurso() {
    }

    public Long getCursoId() { return cursoId; }
    public void setCursoId(Long cursoId) { this.cursoId = cursoId; }

    public String getCursoNombre() { return cursoNombre; }
    public void setCursoNombre(String cursoNombre) { this.cursoNombre = cursoNombre; }

    public int getTotalInscritos() { return totalInscritos; }
    public void setTotalInscritos(int totalInscritos) { this.totalInscritos = totalInscritos; }

    public int getEstudiantesConEntregas() { return estudiantesConEntregas; }
    public void setEstudiantesConEntregas(int estudiantesConEntregas) { this.estudiantesConEntregas = estudiantesConEntregas; }

    public int getTotalActividades() { return totalActividades; }
    public void setTotalActividades(int totalActividades) { this.totalActividades = totalActividades; }

    public int getTotalEntregas() { return totalEntregas; }
    public void setTotalEntregas(int totalEntregas) { this.totalEntregas = totalEntregas; }

    public int getEntregasCalificadas() { return entregasCalificadas; }
    public void setEntregasCalificadas(int entregasCalificadas) { this.entregasCalificadas = entregasCalificadas; }

    public double getPromedioGeneralSobre5() { return promedioGeneralSobre5; }
    public void setPromedioGeneralSobre5(double promedioGeneralSobre5) { this.promedioGeneralSobre5 = promedioGeneralSobre5; }

    public int getAprobados() { return aprobados; }
    public void setAprobados(int aprobados) { this.aprobados = aprobados; }

    public int getReprobados() { return reprobados; }
    public void setReprobados(int reprobados) { this.reprobados = reprobados; }

    public double getPorcentajeAprobacion() { return porcentajeAprobacion; }
    public void setPorcentajeAprobacion(double porcentajeAprobacion) { this.porcentajeAprobacion = porcentajeAprobacion; }

    public Double getCalificacionPromedioCurso() { return calificacionPromedioCurso; }
    public void setCalificacionPromedioCurso(Double calificacionPromedioCurso) { this.calificacionPromedioCurso = calificacionPromedioCurso; }

    public List<EstadisticaActividad> getActividades() { return actividades; }
    public void setActividades(List<EstadisticaActividad> actividades) { this.actividades = actividades; }
}