/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.educacion.plataforma.service;

import com.educacion.plataforma.dto.ProgresoResponse;
import com.educacion.plataforma.entity.Certificado;
import com.educacion.plataforma.entity.Curso;
import com.educacion.plataforma.entity.Inscripcion;
import com.educacion.plataforma.entity.Usuario;
import com.educacion.plataforma.repository.CertificadoRepository;
import com.educacion.plataforma.repository.CursoRepository;
import com.educacion.plataforma.repository.InscripcionRepository;
import com.educacion.plataforma.repository.UsuarioRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author Willi
 */

@Service
public class CertificadoService {

    private static final double NOTA_APROBACION = 3.0;

    private final CertificadoRepository certificadoRepository;
    private final CursoRepository cursoRepository;
    private final UsuarioRepository usuarioRepository;
    private final InscripcionRepository inscripcionRepository;
    private final ProgresoService progresoService;
    private final NotificacionService notificacionService;

    public CertificadoService(CertificadoRepository certificadoRepository,
                              CursoRepository cursoRepository,
                              UsuarioRepository usuarioRepository,
                              InscripcionRepository inscripcionRepository,
                              ProgresoService progresoService,
                              NotificacionService notificacionService) {
        this.certificadoRepository = certificadoRepository;
        this.cursoRepository = cursoRepository;
        this.usuarioRepository = usuarioRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.progresoService = progresoService;
        this.notificacionService = notificacionService;
    }

    public List<Certificado> listarPorEstudiante(Long estudianteId) {
        return certificadoRepository.findByEstudianteId(estudianteId);
    }

    public Certificado buscarPorCodigo(String codigo) {
        return certificadoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Certificado no encontrado con código " + codigo));
    }

    public Certificado solicitar(Long cursoId, Long estudianteId) {
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Curso no encontrado con id " + cursoId));
        Usuario estudiante = usuarioRepository.findById(estudianteId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado con id " + estudianteId));

        if (!"ESTUDIANTE".equalsIgnoreCase(estudiante.getRol().getNombre())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Solo un ESTUDIANTE puede solicitar certificados");
        }

        Inscripcion inscripcion = inscripcionRepository
                .findByCursoIdAndEstudianteId(cursoId, estudianteId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "El estudiante no está inscrito en este curso"));
        if (!"ACTIVA".equals(inscripcion.getEstado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La inscripción del estudiante no está activa");
        }

        if (certificadoRepository.existsByCursoIdAndEstudianteId(cursoId, estudianteId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Este estudiante ya tiene certificado de este curso");
        }

        ProgresoResponse p = progresoService.calcular(cursoId, estudianteId);
        if (p.getTotalActividades() == 0
                || p.getActividadesEntregadas() < p.getTotalActividades()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Debe entregar todas las actividades del curso para solicitar el certificado");
        }
        if (p.getActividadesCalificadas() < p.getTotalActividades()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Aún hay entregas pendientes de calificar");
        }
        if (p.getPromedioSobre5() < NOTA_APROBACION) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El promedio (" + p.getPromedioSobre5() + ") es menor a " + NOTA_APROBACION
                    + ", no se puede emitir el certificado");
        }

        Certificado c = new Certificado();
        c.setCurso(curso);
        c.setEstudiante(estudiante);
        c.setFechaEmision(LocalDate.now());
        c.setPromedioFinal(p.getPromedioSobre5());
        c.setCodigo("CERT-" + cursoId + "-" + estudianteId + "-"
                + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        Certificado guardado = certificadoRepository.save(c);

        notificacionService.crear(estudiante, null, "CERTIFICADO",
                "Su certificado del curso \"" + curso.getNombre() + "\" fue emitido: "
                + guardado.getCodigo());
        return guardado;
    }
}