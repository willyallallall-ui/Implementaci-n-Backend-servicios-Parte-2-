/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.educacion.plataforma.service;

import com.educacion.plataforma.entity.Curso;
import com.educacion.plataforma.entity.Inscripcion;
import com.educacion.plataforma.entity.Usuario;
import com.educacion.plataforma.repository.CursoRepository;
import com.educacion.plataforma.repository.InscripcionRepository;
import com.educacion.plataforma.repository.UsuarioRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author Willi
 */


@Service
public class InscripcionService {

    private final InscripcionRepository inscripcionRepository;
    private final CursoRepository cursoRepository;
    private final UsuarioRepository usuarioRepository;

    public InscripcionService(InscripcionRepository inscripcionRepository,
                              CursoRepository cursoRepository,
                              UsuarioRepository usuarioRepository) {
        this.inscripcionRepository = inscripcionRepository;
        this.cursoRepository = cursoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Inscripcion> listar() {
        return inscripcionRepository.findAll();
    }

    public List<Inscripcion> listarPorCurso(Long cursoId) {
        return inscripcionRepository.findByCursoId(cursoId);
    }

    public List<Inscripcion> listarPorEstudiante(Long estudianteId) {
        return inscripcionRepository.findByEstudianteId(estudianteId);
    }

    public Inscripcion buscarPorId(Long id) {
        return inscripcionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Inscripción no encontrada con id " + id));
    }

    public Inscripcion crear(Inscripcion inscripcion) {
        Curso curso = buscarCurso(inscripcion.getCurso());
        Usuario estudiante = buscarEstudiante(inscripcion.getEstudiante());

        if (!"ACTIVO".equals(curso.getEstado())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El curso no está activo, no admite inscripciones");
        }
        if (inscripcionRepository.existsByCursoIdAndEstudianteId(curso.getId(), estudiante.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Este estudiante ya está inscrito en este curso");
        }

        inscripcion.setCurso(curso);
        inscripcion.setEstudiante(estudiante);
        inscripcion.setFechaInscripcion(LocalDate.now());
        inscripcion.setEstado("ACTIVA");
        return inscripcionRepository.save(inscripcion);
    }

    public Inscripcion cancelar(Long id) {
        Inscripcion inscripcion = buscarPorId(id);
        if ("CANCELADA".equals(inscripcion.getEstado())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "La inscripción ya está cancelada");
        }
        inscripcion.setEstado("CANCELADA");
        return inscripcionRepository.save(inscripcion);
    }

    public void eliminar(Long id) {
        Inscripcion inscripcion = buscarPorId(id);
        inscripcionRepository.delete(inscripcion);
    }

    private Curso buscarCurso(Curso curso) {
        if (curso.getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Debe indicar el id del curso");
        }
        return cursoRepository.findById(curso.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Curso no encontrado con id " + curso.getId()));
    }

    private Usuario buscarEstudiante(Usuario estudiante) {
        if (estudiante.getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Debe indicar el id del estudiante");
        }
        Usuario usuario = usuarioRepository.findById(estudiante.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado con id " + estudiante.getId()));
        if (!"ESTUDIANTE".equalsIgnoreCase(usuario.getRol().getNombre())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Solo un usuario con rol ESTUDIANTE puede inscribirse");
        }
        return usuario;
    }
}