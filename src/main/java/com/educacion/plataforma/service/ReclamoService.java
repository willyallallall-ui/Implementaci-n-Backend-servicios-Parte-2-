/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.educacion.plataforma.service;

import com.educacion.plataforma.dto.RespuestaReclamoRequest;
import com.educacion.plataforma.entity.Entrega;
import com.educacion.plataforma.entity.Reclamo;
import com.educacion.plataforma.entity.Usuario;
import com.educacion.plataforma.repository.EntregaRepository;
import com.educacion.plataforma.repository.ReclamoRepository;
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
public class ReclamoService {

    private final ReclamoRepository reclamoRepository;
    private final EntregaRepository entregaRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionService notificacionService;

    public ReclamoService(ReclamoRepository reclamoRepository,
                          EntregaRepository entregaRepository,
                          UsuarioRepository usuarioRepository,
                          NotificacionService notificacionService) {
        this.reclamoRepository = reclamoRepository;
        this.entregaRepository = entregaRepository;
        this.usuarioRepository = usuarioRepository;
        this.notificacionService = notificacionService;
    }

    public List<Reclamo> listar() {
        return reclamoRepository.findAll();
    }

    public List<Reclamo> listarPorEstudiante(Long estudianteId) {
        return reclamoRepository.findByEstudianteId(estudianteId);
    }

    public List<Reclamo> listarPorDocente(Long docenteId) {
        return reclamoRepository.findByEntregaActividadUnidadCursoDocenteId(docenteId);
    }

    public Reclamo buscarPorId(Long id) {
        return reclamoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Reclamo no encontrado con id " + id));
    }

    public Reclamo crear(Reclamo reclamo) {
        if (reclamo.getEntrega().getId() == null || reclamo.getEstudiante().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Debe indicar el id de la entrega y del estudiante");
        }
        Entrega entrega = entregaRepository.findById(reclamo.getEntrega().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Entrega no encontrada con id " + reclamo.getEntrega().getId()));
        Usuario estudiante = usuarioRepository.findById(reclamo.getEstudiante().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado con id " + reclamo.getEstudiante().getId()));

        if (!entrega.getEstudiante().getId().equals(estudiante.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Solo el dueño de la entrega puede presentar el reclamo");
        }
        if (!"CALIFICADA".equals(entrega.getEstado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Solo se puede reclamar una entrega ya calificada");
        }
        if (reclamoRepository.existsByEntregaIdAndEstado(entrega.getId(), "ABIERTO")) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe un reclamo abierto para esta entrega");
        }

        reclamo.setEntrega(entrega);
        reclamo.setEstudiante(estudiante);
        reclamo.setEstado("ABIERTO");
        reclamo.setNotaAnterior(entrega.getNota());
        reclamo.setFechaCreacion(LocalDate.now());
        Reclamo guardado = reclamoRepository.save(reclamo);

        Usuario docente = entrega.getActividad().getUnidad().getCurso().getDocente();
        notificacionService.crear(docente, estudiante, "RECLAMO",
                estudiante.getNombre() + " presentó un reclamo sobre \""
                + entrega.getActividad().getTitulo() + "\"");
        return guardado;
    }

    public Reclamo responder(Long id, RespuestaReclamoRequest datos) {
        Reclamo reclamo = buscarPorId(id);
        if (!"ABIERTO".equals(reclamo.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El reclamo ya fue respondido");
        }

        Usuario docente = usuarioRepository.findById(datos.getDocenteId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado con id " + datos.getDocenteId()));
        Entrega entrega = reclamo.getEntrega();
        Long docenteDelCurso = entrega.getActividad().getUnidad().getCurso().getDocente().getId();
        if (!"DOCENTE".equalsIgnoreCase(docente.getRol().getNombre())
                || !docenteDelCurso.equals(docente.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Solo el docente del curso puede responder este reclamo");
        }

        if (Boolean.TRUE.equals(datos.getAceptado())) {
            if (datos.getNuevaNota() != null) {
                double max = entrega.getActividad().getPuntajeMaximo();
                if (datos.getNuevaNota() < 0 || datos.getNuevaNota() > max) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "La nota debe estar entre 0 y " + max);
                }
                entrega.setNota(datos.getNuevaNota());
                entrega.setFechaCalificacion(LocalDate.now());
                entregaRepository.save(entrega);
            }
            reclamo.setEstado("RESUELTO");
        } else {
            reclamo.setEstado("RECHAZADO");
        }
        reclamo.setRespuesta(datos.getRespuesta());
        reclamo.setFechaRespuesta(LocalDate.now());
        Reclamo guardado = reclamoRepository.save(reclamo);

        notificacionService.crear(reclamo.getEstudiante(), docente, "RECLAMO",
                "Su reclamo sobre \"" + entrega.getActividad().getTitulo()
                + "\" fue " + guardado.getEstado());
        return guardado;
    }
}