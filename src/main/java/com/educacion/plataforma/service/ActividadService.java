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
        normalizarTipo(actividad);
        return actividadRepository.save(actividad);
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