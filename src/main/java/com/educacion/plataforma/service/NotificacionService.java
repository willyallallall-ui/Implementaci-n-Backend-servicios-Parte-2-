/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.educacion.plataforma.service;

import com.educacion.plataforma.dto.NotificacionRequest;
import com.educacion.plataforma.entity.Curso;
import com.educacion.plataforma.entity.Inscripcion;
import com.educacion.plataforma.entity.Notificacion;
import com.educacion.plataforma.entity.Usuario;
import com.educacion.plataforma.repository.CursoRepository;
import com.educacion.plataforma.repository.InscripcionRepository;
import com.educacion.plataforma.repository.NotificacionRepository;
import com.educacion.plataforma.repository.UsuarioRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author Willi
 */

@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final CursoRepository cursoRepository;
    private final InscripcionRepository inscripcionRepository;

    public NotificacionService(NotificacionRepository notificacionRepository,
                               UsuarioRepository usuarioRepository,
                               CursoRepository cursoRepository,
                               InscripcionRepository inscripcionRepository) {
        this.notificacionRepository = notificacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.cursoRepository = cursoRepository;
        this.inscripcionRepository = inscripcionRepository;
    }

    // ---------- Consultas ----------

    public List<Notificacion> listarDeUsuario(Long usuarioId, boolean soloNoLeidas) {
        verificarUsuario(usuarioId);
        if (soloNoLeidas) {
            return notificacionRepository.findByDestinatarioIdAndLeidaFalseOrderByFechaDesc(usuarioId);
        }
        return notificacionRepository.findByDestinatarioIdOrderByFechaDesc(usuarioId);
    }

    public long contarNoLeidas(Long usuarioId) {
        verificarUsuario(usuarioId);
        return notificacionRepository.countByDestinatarioIdAndLeidaFalse(usuarioId);
    }

    public Notificacion marcarLeida(Long id) {
        Notificacion n = notificacionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Notificación no encontrada con id " + id));
        n.setLeida(true);
        return notificacionRepository.save(n);
    }

    // ---------- Envío manual (coordinador / docente) ----------

    public List<Notificacion> enviar(NotificacionRequest req) {
        Usuario remitente = verificarUsuario(req.getRemitenteId());
        String rol = remitente.getRol().getNombre();
        if (!"COORDINADOR".equalsIgnoreCase(rol) && !"DOCENTE".equalsIgnoreCase(rol)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Solo un COORDINADOR o un DOCENTE puede enviar notificaciones");
        }

        int destinos = 0;
        if (req.getDestinatarioId() != null) destinos++;
        if (req.getCursoId() != null) destinos++;
        if (req.getParaRol() != null) destinos++;
        if (destinos != 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Indique exactamente un destino: destinatarioId, cursoId o paraRol");
        }

        List<Usuario> destinatarios = new ArrayList<Usuario>();

        if (req.getDestinatarioId() != null) {
            if (!"COORDINADOR".equalsIgnoreCase(rol)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Un DOCENTE solo puede notificar a los inscritos de sus cursos");
            }
            destinatarios.add(verificarUsuario(req.getDestinatarioId()));

        } else if (req.getCursoId() != null) {
            Curso curso = cursoRepository.findById(req.getCursoId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Curso no encontrado con id " + req.getCursoId()));
            if ("DOCENTE".equalsIgnoreCase(rol)
                    && !curso.getDocente().getId().equals(remitente.getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Un DOCENTE solo puede notificar a los inscritos de sus propios cursos");
            }
            for (Inscripcion i : inscripcionRepository.findByCursoId(curso.getId())) {
                if ("ACTIVA".equals(i.getEstado())) {
                    destinatarios.add(i.getEstudiante());
                }
            }

        } else {
            if (!"COORDINADOR".equalsIgnoreCase(rol)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Solo el COORDINADOR puede notificar por rol");
            }
            destinatarios.addAll(usuarioRepository.findByRolNombre(req.getParaRol().toUpperCase()));
        }

        if (destinatarios.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No hay destinatarios para esta notificación");
        }

        List<Notificacion> creadas = new ArrayList<Notificacion>();
        for (Usuario d : destinatarios) {
            creadas.add(crear(d, remitente, "GENERAL", req.getMensaje()));
        }
        return creadas;
    }

    // ---------- Uso interno (lo llaman otros servicios) ----------

    public Notificacion crear(Usuario destinatario, Usuario remitente, String tipo, String mensaje) {
        Notificacion n = new Notificacion();
        n.setDestinatario(destinatario);
        n.setRemitente(remitente);
        n.setTipo(tipo);
        n.setMensaje(mensaje);
        n.setFecha(LocalDateTime.now());
        n.setLeida(false);
        return notificacionRepository.save(n);
    }

    private Usuario verificarUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado con id " + id));
    }
}