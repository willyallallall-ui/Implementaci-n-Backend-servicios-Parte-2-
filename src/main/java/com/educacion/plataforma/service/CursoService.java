/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.educacion.plataforma.service;

import com.educacion.plataforma.entity.Curso;
import com.educacion.plataforma.entity.Oferta;
import com.educacion.plataforma.entity.Usuario;
import com.educacion.plataforma.repository.CursoRepository;
import com.educacion.plataforma.repository.OfertaRepository;
import com.educacion.plataforma.repository.UsuarioRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.educacion.plataforma.service.NotificacionService;
/**
 *
 * @author Willi
 */
@Service
public class CursoService {

    private final CursoRepository cursoRepository;
    private final OfertaRepository ofertaRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionService notificacionService;

    public CursoService(CursoRepository cursoRepository,
                        OfertaRepository ofertaRepository,
                        UsuarioRepository usuarioRepository,
                        NotificacionService notificacionService) {
        this.cursoRepository = cursoRepository;
        this.ofertaRepository = ofertaRepository;
        this.usuarioRepository = usuarioRepository;
        this.notificacionService = notificacionService;
    }

    public List<Curso> listar() {
        return cursoRepository.findAll();
    }

    public List<Curso> listarPorOferta(Long ofertaId) {
        return cursoRepository.findByOfertaId(ofertaId);
    }

    public List<Curso> listarPorDocente(Long docenteId) {
        return cursoRepository.findByDocenteId(docenteId);
    }

    public Curso buscarPorId(Long id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Curso no encontrado con id " + id));
    }

    public Curso crear(Curso curso) {
        curso.setOferta(buscarOferta(curso.getOferta()));
        curso.setDocente(buscarDocente(curso.getDocente()));
        normalizarEstado(curso);
        validarFechasYCupos(curso);
        curso.setPublicacion("BORRADOR");
        Curso guardado = cursoRepository.save(curso);
        notificacionService.crear(guardado.getDocente(), null, "ASIGNACION_CURSO",
                "Se le asignó el curso \"" + guardado.getNombre() + "\"");
        return guardado;
    }

    public Curso actualizar(Long id, Curso datos) {
        Curso curso = buscarPorId(id);
        Long docenteAnterior = curso.getDocente().getId();   // 1) recordar el docente de antes

        curso.setNombre(datos.getNombre());
        curso.setDescripcion(datos.getDescripcion());
        curso.setEstado(datos.getEstado());
        curso.setObjetivos(datos.getObjetivos());
        curso.setResultadosAprendizaje(datos.getResultadosAprendizaje());
        curso.setCupos(datos.getCupos());
        curso.setFechaInicio(datos.getFechaInicio());
        curso.setFechaFin(datos.getFechaFin());
        curso.setOferta(buscarOferta(datos.getOferta()));
        curso.setDocente(buscarDocente(datos.getDocente()));
        normalizarEstado(curso);
        validarFechasYCupos(curso);

        Curso guardado = cursoRepository.save(curso);

        // 2) si el docente cambió, avisar al nuevo
        if (!guardado.getDocente().getId().equals(docenteAnterior)) {
            notificacionService.crear(guardado.getDocente(), null, "ASIGNACION_CURSO",
                    "Se le asignó el curso \"" + guardado.getNombre() + "\"");
        }
        return guardado;
    }

    public Curso publicar(Long id, Long coordinadorId) {
        verificarCoordinador(coordinadorId);
        Curso curso = buscarPorId(id);
        if (curso.getCupos() == null || curso.getFechaInicio() == null || curso.getFechaFin() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Para publicar, el curso debe tener cupos, fecha de inicio y fecha de fin");
        }
        if (curso.getObjetivos() == null || curso.getObjetivos().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Para publicar, el curso debe tener objetivos");
        }
        curso.setPublicacion("PUBLICADO");
        return cursoRepository.save(curso);
    }

    public Curso despublicar(Long id, Long coordinadorId) {
        verificarCoordinador(coordinadorId);
        Curso curso = buscarPorId(id);
        curso.setPublicacion("BORRADOR");
        return cursoRepository.save(curso);
    }

    public List<Curso> listarPublicados() {
        return cursoRepository.findByPublicacion("PUBLICADO");
    }

    public void eliminar(Long id) {
        Curso curso = buscarPorId(id);
        cursoRepository.delete(curso);
    }

    // Verifica que la oferta enviada exista de verdad
    private Oferta buscarOferta(Oferta oferta) {
        if (oferta.getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Debe indicar el id de la oferta");
        }
        return ofertaRepository.findById(oferta.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Oferta no encontrada con id " + oferta.getId()));
    }

    // Verifica que el docente exista Y que tenga el rol DOCENTE
    private Usuario buscarDocente(Usuario docente) {
        if (docente.getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Debe indicar el id del docente");
        }
        Usuario usuario = usuarioRepository.findById(docente.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado con id " + docente.getId()));
        if (!"DOCENTE".equalsIgnoreCase(usuario.getRol().getNombre())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Solo un usuario con rol DOCENTE puede dictar cursos");
        }
        return usuario;
    }

    private void normalizarEstado(Curso curso) {
        if (curso.getEstado() == null || curso.getEstado().trim().isEmpty()) {
            curso.setEstado("ACTIVO");
        }
        curso.setEstado(curso.getEstado().toUpperCase());
        if (!curso.getEstado().equals("ACTIVO") && !curso.getEstado().equals("INACTIVO")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El estado debe ser ACTIVO o INACTIVO");
        }
    }
  
    private void validarFechasYCupos(Curso curso) {
        if (curso.getCupos() != null && curso.getCupos() < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Los cupos deben ser 1 o más");
        }
        if (curso.getFechaInicio() != null && curso.getFechaFin() != null
                && curso.getFechaFin().isBefore(curso.getFechaInicio())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La fecha de fin no puede ser anterior a la fecha de inicio");
        }
    }

    private void verificarCoordinador(Long coordinadorId) {
        Usuario u = usuarioRepository.findById(coordinadorId)
                .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Usuario no encontrado con id " + coordinadorId));
        if (!"COORDINADOR".equalsIgnoreCase(u.getRol().getNombre())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Solo un usuario con rol COORDINADOR puede publicar cursos");
        }
    }
}  