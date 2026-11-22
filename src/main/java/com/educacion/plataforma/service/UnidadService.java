/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.educacion.plataforma.service;

import com.educacion.plataforma.entity.Curso;
import com.educacion.plataforma.entity.Unidad;
import com.educacion.plataforma.repository.CursoRepository;
import com.educacion.plataforma.repository.UnidadRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author Willi
 */

@Service
public class UnidadService {

    private final UnidadRepository unidadRepository;
    private final CursoRepository cursoRepository;

    public UnidadService(UnidadRepository unidadRepository, CursoRepository cursoRepository) {
        this.unidadRepository = unidadRepository;
        this.cursoRepository = cursoRepository;
    }

    public List<Unidad> listar() {
        return unidadRepository.findAll();
    }

    public List<Unidad> listarPorCurso(Long cursoId) {
        return unidadRepository.findByCursoIdOrderByOrdenAsc(cursoId);
    }

    public Unidad buscarPorId(Long id) {
        return unidadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Unidad no encontrada con id " + id));
    }

    public Unidad crear(Unidad unidad) {
        Curso curso = buscarCurso(unidad.getCurso());
        if (unidadRepository.existsByCursoIdAndOrden(curso.getId(), unidad.getOrden())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Ya existe una unidad con el orden "
                    + unidad.getOrden() + " en este curso");
        }
        unidad.setCurso(curso);
        return unidadRepository.save(unidad);
    }

    public Unidad actualizar(Long id, Unidad datos) {
        Unidad unidad = buscarPorId(id);
        Curso curso = buscarCurso(datos.getCurso());

        boolean cambioElOrdenOElCurso =
                !unidad.getOrden().equals(datos.getOrden())
                || !unidad.getCurso().getId().equals(curso.getId());

        if (cambioElOrdenOElCurso
                && unidadRepository.existsByCursoIdAndOrden(curso.getId(), datos.getOrden())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Ya existe una unidad con el orden "
                    + datos.getOrden() + " en este curso");
        }

        unidad.setTitulo(datos.getTitulo());
        unidad.setDescripcion(datos.getDescripcion());
        unidad.setOrden(datos.getOrden());
        unidad.setCurso(curso);
        return unidadRepository.save(unidad);
    }

    public void eliminar(Long id) {
        Unidad unidad = buscarPorId(id);
        unidadRepository.delete(unidad);
    }

    // Verifica que el curso enviado exista de verdad
    private Curso buscarCurso(Curso curso) {
        if (curso.getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Debe indicar el id del curso");
        }
        return cursoRepository.findById(curso.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Curso no encontrado con id " + curso.getId()));
    }
}