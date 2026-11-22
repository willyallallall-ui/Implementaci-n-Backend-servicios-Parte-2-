/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.educacion.plataforma.service;

import com.educacion.plataforma.entity.Actividad;
import com.educacion.plataforma.entity.Entrega;
import com.educacion.plataforma.entity.Inscripcion;
import com.educacion.plataforma.entity.Usuario;
import com.educacion.plataforma.repository.ActividadRepository;
import com.educacion.plataforma.repository.EntregaRepository;
import com.educacion.plataforma.repository.InscripcionRepository;
import com.educacion.plataforma.repository.UsuarioRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.educacion.plataforma.dto.CalificacionRequest;

/**
 *
 * @author Willi
 */

@Service
public class EntregaService {

    private final EntregaRepository entregaRepository;
    private final ActividadRepository actividadRepository;
    private final UsuarioRepository usuarioRepository;
    private final InscripcionRepository inscripcionRepository;

    public EntregaService(EntregaRepository entregaRepository,
                          ActividadRepository actividadRepository,
                          UsuarioRepository usuarioRepository,
                          InscripcionRepository inscripcionRepository) {
        this.entregaRepository = entregaRepository;
        this.actividadRepository = actividadRepository;
        this.usuarioRepository = usuarioRepository;
        this.inscripcionRepository = inscripcionRepository;
    }

    public List<Entrega> listar() {
        return entregaRepository.findAll();
    }

    public List<Entrega> listarPorActividad(Long actividadId) {
        return entregaRepository.findByActividadId(actividadId);
    }

    public List<Entrega> listarPorEstudiante(Long estudianteId) {
        return entregaRepository.findByEstudianteId(estudianteId);
    }

    public Entrega buscarPorId(Long id) {
        return entregaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Entrega no encontrada con id " + id));
    }

    public Entrega crear(Entrega entrega) {
        Actividad actividad = buscarActividad(entrega.getActividad());
        Usuario estudiante = buscarEstudiante(entrega.getEstudiante());
        Long cursoId = actividad.getUnidad().getCurso().getId();

        // Debe estar inscrito y con la inscripción ACTIVA
        Inscripcion inscripcion = inscripcionRepository
                .findByCursoIdAndEstudianteId(cursoId, estudiante.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "El estudiante no está inscrito en el curso de esta actividad"));
        if (!"ACTIVA".equals(inscripcion.getEstado())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "La inscripción del estudiante no está activa");
        }

        if (entregaRepository.existsByActividadIdAndEstudianteId(actividad.getId(), estudiante.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Este estudiante ya entregó esta actividad");
        }

        if (actividad.getFechaLimite() != null
                && LocalDate.now().isAfter(actividad.getFechaLimite())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "La fecha límite de la actividad ya venció");
        }

        entrega.setActividad(actividad);
        entrega.setEstudiante(estudiante);
        entrega.setFechaEntrega(LocalDate.now());
        entrega.setEstado("ENTREGADA");
        return entregaRepository.save(entrega);
    }

    public void eliminar(Long id) {
        Entrega entrega = buscarPorId(id);
        entregaRepository.delete(entrega);
    }
    
        public Entrega calificar(Long id, CalificacionRequest datos) {
        Entrega entrega = buscarPorId(id);

        // El que califica debe existir y ser DOCENTE
        Usuario docente = usuarioRepository.findById(datos.getDocenteId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado con id " + datos.getDocenteId()));
        if (!"DOCENTE".equalsIgnoreCase(docente.getRol().getNombre())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Solo un usuario con rol DOCENTE puede calificar");
        }

        // Debe ser el docente del curso de la actividad
        Long docenteDelCurso = entrega.getActividad().getUnidad().getCurso().getDocente().getId();
        if (!docenteDelCurso.equals(docente.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Solo el docente de este curso puede calificar sus entregas");
        }

        // La nota debe estar entre 0 y el puntaje máximo
        Double puntajeMaximo = entrega.getActividad().getPuntajeMaximo();
        if (datos.getNota() < 0 || datos.getNota() > puntajeMaximo) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "La nota debe estar entre 0 y " + puntajeMaximo);
        }

        entrega.setNota(datos.getNota());
        entrega.setRetroalimentacion(datos.getRetroalimentacion());
        entrega.setFechaCalificacion(LocalDate.now());
        entrega.setEstado("CALIFICADA");
        return entregaRepository.save(entrega);
    }

    private Actividad buscarActividad(Actividad actividad) {
        if (actividad.getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Debe indicar el id de la actividad");
        }
        return actividadRepository.findById(actividad.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Actividad no encontrada con id " + actividad.getId()));
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
                    HttpStatus.BAD_REQUEST, "Solo un usuario con rol ESTUDIANTE puede hacer entregas");
        }
        return usuario;
    }
}