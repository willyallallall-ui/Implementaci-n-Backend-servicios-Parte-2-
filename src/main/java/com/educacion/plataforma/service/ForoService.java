/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.educacion.plataforma.service;

import com.educacion.plataforma.dto.ForoRequest;
import com.educacion.plataforma.dto.MensajeRequest;
import com.educacion.plataforma.entity.Curso;
import com.educacion.plataforma.entity.Foro;
import com.educacion.plataforma.entity.Inscripcion;
import com.educacion.plataforma.entity.MensajeForo;
import com.educacion.plataforma.entity.Usuario;
import com.educacion.plataforma.repository.CursoRepository;
import com.educacion.plataforma.repository.ForoRepository;
import com.educacion.plataforma.repository.InscripcionRepository;
import com.educacion.plataforma.repository.MensajeForoRepository;
import com.educacion.plataforma.repository.UsuarioRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
/**
 *
 * @author Willi
 */


@Service
public class ForoService {

    private final ForoRepository foroRepository;
    private final MensajeForoRepository mensajeRepository;
    private final CursoRepository cursoRepository;
    private final UsuarioRepository usuarioRepository;
    private final InscripcionRepository inscripcionRepository;
    private final NotificacionService notificacionService;

    public ForoService(ForoRepository foroRepository,
                       MensajeForoRepository mensajeRepository,
                       CursoRepository cursoRepository,
                       UsuarioRepository usuarioRepository,
                       InscripcionRepository inscripcionRepository,
                       NotificacionService notificacionService) {
        this.foroRepository = foroRepository;
        this.mensajeRepository = mensajeRepository;
        this.cursoRepository = cursoRepository;
        this.usuarioRepository = usuarioRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.notificacionService = notificacionService;
    }

    public List<Foro> listarPorCurso(Long cursoId) {
        return foroRepository.findByCursoId(cursoId);
    }

    public Foro buscarPorId(Long id) {
        return foroRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Foro no encontrado con id " + id));
    }

    public List<MensajeForo> listarMensajes(Long foroId) {
        buscarPorId(foroId);
        return mensajeRepository.findByForoIdOrderByFechaAsc(foroId);
    }

    public Foro abrir(ForoRequest req) {
        Curso curso = cursoRepository.findById(req.getCursoId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Curso no encontrado con id " + req.getCursoId()));
        Usuario creador = buscarUsuario(req.getCreadorId());
        boolean esDocente = curso.getDocente().getId().equals(creador.getId());
        boolean esCoordinador = "COORDINADOR".equalsIgnoreCase(creador.getRol().getNombre());
        if (!esDocente && !esCoordinador) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Solo el docente del curso o un coordinador puede abrir un foro");
        }

        Foro f = new Foro();
        f.setCurso(curso);
        f.setCreador(creador);
        f.setTitulo(req.getTitulo());
        f.setDescripcion(req.getDescripcion());
        f.setEstado("ABIERTO");
        f.setFechaCreacion(LocalDateTime.now());
        Foro guardado = foroRepository.save(f);

        for (Inscripcion i : inscripcionRepository.findByCursoId(curso.getId())) {
            if ("ACTIVA".equals(i.getEstado())) {
                notificacionService.crear(i.getEstudiante(), creador, "FORO",
                        "Nuevo foro en \"" + curso.getNombre() + "\": " + guardado.getTitulo());
            }
        }
        return guardado;
    }

    public Foro cambiarEstado(Long foroId, Long usuarioId, String estado) {
        Foro f = buscarPorId(foroId);
        if (!f.getCurso().getDocente().getId().equals(usuarioId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Solo el docente del curso puede cerrar o reabrir el foro");
        }
        f.setEstado(estado);
        return foroRepository.save(f);
    }

    public MensajeForo publicar(Long foroId, MensajeRequest req) {
        Foro f = buscarPorId(foroId);
        if (!"ABIERTO".equals(f.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El foro está cerrado");
        }
        Usuario autor = buscarUsuario(req.getAutorId());
        Long cursoId = f.getCurso().getId();

        boolean esDocente = f.getCurso().getDocente().getId().equals(autor.getId());
        boolean inscritoActivo = inscripcionRepository
                .findByCursoIdAndEstudianteId(cursoId, autor.getId())
                .map(i -> "ACTIVA".equals(i.getEstado()))
                .orElse(false);
        if (!esDocente && !inscritoActivo) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Solo el docente o los estudiantes inscritos pueden escribir en este foro");
        }

        MensajeForo m = new MensajeForo();
        m.setForo(f);
        m.setAutor(autor);
        m.setTexto(req.getTexto());
        m.setFecha(LocalDateTime.now());
        return mensajeRepository.save(m);
    }

    private Usuario buscarUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado con id " + id));
    }
}