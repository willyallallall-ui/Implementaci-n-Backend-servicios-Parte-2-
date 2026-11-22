/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.educacion.plataforma.service;

import com.educacion.plataforma.entity.Actividad;
import com.educacion.plataforma.entity.Unidad;
import com.educacion.plataforma.repository.ActividadRepository;
import com.educacion.plataforma.repository.UnidadRepository;
import java.util.Arrays;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
/**
 *
 * @author Willi
 */


@Service
public class ActividadService {

    private static final List<String> TIPOS_VALIDOS =
            Arrays.asList("TALLER", "QUIZ", "TAREA", "PROYECTO");

    private final ActividadRepository actividadRepository;
    private final UnidadRepository unidadRepository;

    public ActividadService(ActividadRepository actividadRepository,
                            UnidadRepository unidadRepository) {
        this.actividadRepository = actividadRepository;
        this.unidadRepository = unidadRepository;
    }

    public List<Actividad> listar() {
        return actividadRepository.findAll();
    }

    public List<Actividad> listarPorUnidad(Long unidadId) {
        return actividadRepository.findByUnidadId(unidadId);
    }

    public List<Actividad> listarPorCurso(Long cursoId) {
        return actividadRepository.findByUnidadCursoId(cursoId);
    }

    public Actividad buscarPorId(Long id) {
        return actividadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Actividad no encontrada con id " + id));
    }

    public Actividad crear(Actividad actividad) {
        actividad.setUnidad(buscarUnidad(actividad.getUnidad()));
        normalizarTipo(actividad);
        validarPorcentaje(actividad, null);
        return actividadRepository.save(actividad);
    }

    public Actividad actualizar(Long id, Actividad datos) {
        Actividad actividad = buscarPorId(id);
        actividad.setTitulo(datos.getTitulo());
        actividad.setDescripcion(datos.getDescripcion());
        actividad.setTipo(datos.getTipo());
        actividad.setPuntajeMaximo(datos.getPuntajeMaximo());
        actividad.setFechaLimite(datos.getFechaLimite());
        actividad.setUnidad(buscarUnidad(datos.getUnidad()));
        actividad.setPorcentaje(datos.getPorcentaje());
        normalizarTipo(actividad);
        validarPorcentaje(actividad, id);
        return actividadRepository.save(actividad);
    }
    
    public Map<String, Object> resumenPorcentajes(Long cursoId) {
        double asignado = 0;
        for (Actividad a : actividadRepository.findByUnidadCursoId(cursoId)) {
            asignado += a.getPorcentaje() == null ? 0 : a.getPorcentaje();
        }
        Map<String, Object> r = new LinkedHashMap<String, Object>();
        r.put("cursoId", cursoId);
        r.put("porcentajeAsignado", asignado);
        r.put("porcentajeDisponible", 100.0 - asignado);
        r.put("completo", Math.abs(100.0 - asignado) < 0.0001);
        return r;
    }

    // La suma de porcentajes del curso no puede superar 100
    private void validarPorcentaje(Actividad actividad, Long idExcluir) {
        Long cursoId = actividad.getUnidad().getCurso().getId();
        double suma = 0;
        for (Actividad a : actividadRepository.findByUnidadCursoId(cursoId)) {
            if (idExcluir != null && a.getId().equals(idExcluir)) {
                continue;
            }
            suma += a.getPorcentaje() == null ? 0 : a.getPorcentaje();
        }
        if (suma + actividad.getPorcentaje() > 100.0001) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Los porcentajes del curso no pueden superar 100%. Disponible: "
                    + (100.0 - suma) + "%");
        }
    }    

    public void eliminar(Long id) {
        Actividad actividad = buscarPorId(id);
        actividadRepository.delete(actividad);
    }

    // Verifica que la unidad enviada exista de verdad
    private Unidad buscarUnidad(Unidad unidad) {
        if (unidad.getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Debe indicar el id de la unidad");
        }
        return unidadRepository.findById(unidad.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Unidad no encontrada con id " + unidad.getId()));
    }

    private void normalizarTipo(Actividad actividad) {
        if (actividad.getTipo() == null || actividad.getTipo().trim().isEmpty()) {
            actividad.setTipo("TAREA");
        }
        actividad.setTipo(actividad.getTipo().toUpperCase());
        if (!TIPOS_VALIDOS.contains(actividad.getTipo())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El tipo debe ser TALLER, QUIZ, TAREA o PROYECTO");
        }
    }
}