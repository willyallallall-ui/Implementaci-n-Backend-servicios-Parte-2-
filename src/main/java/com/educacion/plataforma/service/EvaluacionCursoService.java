/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.educacion.plataforma.service;

import com.educacion.plataforma.entity.Curso;
import com.educacion.plataforma.entity.EvaluacionCurso;
import com.educacion.plataforma.entity.Usuario;
import com.educacion.plataforma.repository.CursoRepository;
import com.educacion.plataforma.repository.EvaluacionCursoRepository;
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
public class EvaluacionCursoService {

    private final EvaluacionCursoRepository evaluacionRepository;
    private final CursoRepository cursoRepository;
    private final UsuarioRepository usuarioRepository;

    public EvaluacionCursoService(EvaluacionCursoRepository evaluacionRepository,
            CursoRepository cursoRepository,
            UsuarioRepository usuarioRepository) {
        this.evaluacionRepository = evaluacionRepository;
        this.cursoRepository = cursoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<EvaluacionCurso> listar() {
        return evaluacionRepository.findAll();
    }

    public List<EvaluacionCurso> listarPorCurso(Long cursoId) {
        return evaluacionRepository.findByCursoId(cursoId);
    }

    public EvaluacionCurso buscarPorId(Long id) {
        return evaluacionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Evaluación no encontrada con id " + id));
    }

    public Double promedioCurso(Long cursoId) {
        if (!cursoRepository.existsById(cursoId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Curso no encontrado con id " + cursoId);
        }
        return evaluacionRepository.promedioPorCurso(cursoId);
    }

    public EvaluacionCurso crear(EvaluacionCurso evaluacion) {
        Curso curso = buscarCurso(evaluacion.getCurso());
        Usuario estudiante = buscarEstudiante(evaluacion.getEstudiante());

        if (evaluacionRepository.existsByCursoIdAndEstudianteId(curso.getId(), estudiante.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Este estudiante ya evaluó este curso");
        }

        evaluacion.setCurso(curso);
        evaluacion.setEstudiante(estudiante);
        evaluacion.setFecha(LocalDate.now());
        return evaluacionRepository.save(evaluacion);
    }

    public EvaluacionCurso actualizar(Long id, EvaluacionCurso datos) {
        EvaluacionCurso evaluacion = buscarPorId(id);
        // Solo se pueden cambiar la calificación y el comentario
        evaluacion.setCalificacion(datos.getCalificacion());
        evaluacion.setComentario(datos.getComentario());
        return evaluacionRepository.save(evaluacion);
    }

    public void eliminar(Long id) {
        EvaluacionCurso evaluacion = buscarPorId(id);
        evaluacionRepository.delete(evaluacion);
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

    // Verifica que el usuario exista Y que tenga el rol ESTUDIANTE
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
                    HttpStatus.BAD_REQUEST, "Solo un usuario con rol ESTUDIANTE puede evaluar un curso");
        }
        return usuario;
    }
}