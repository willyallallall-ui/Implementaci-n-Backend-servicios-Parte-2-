/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.educacion.plataforma.service;

import com.educacion.plataforma.dto.EstadisticaCurso;
import com.educacion.plataforma.entity.Curso;
import com.educacion.plataforma.entity.Usuario;
import com.educacion.plataforma.repository.CertificadoRepository;
import com.educacion.plataforma.repository.CursoRepository;
import com.educacion.plataforma.repository.EntregaRepository;
import com.educacion.plataforma.repository.InscripcionRepository;
import com.educacion.plataforma.repository.OfertaRepository;
import com.educacion.plataforma.repository.ReclamoRepository;
import com.educacion.plataforma.repository.UsuarioRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
/**
 *
 * @author Willi
 */


@Service
public class ReporteService {

    private final UsuarioRepository usuarioRepository;
    private final OfertaRepository ofertaRepository;
    private final CursoRepository cursoRepository;
    private final InscripcionRepository inscripcionRepository;
    private final EntregaRepository entregaRepository;
    private final ReclamoRepository reclamoRepository;
    private final CertificadoRepository certificadoRepository;
    private final EstadisticaService estadisticaService;

    public ReporteService(UsuarioRepository usuarioRepository,
                          OfertaRepository ofertaRepository,
                          CursoRepository cursoRepository,
                          InscripcionRepository inscripcionRepository,
                          EntregaRepository entregaRepository,
                          ReclamoRepository reclamoRepository,
                          CertificadoRepository certificadoRepository,
                          EstadisticaService estadisticaService) {
        this.usuarioRepository = usuarioRepository;
        this.ofertaRepository = ofertaRepository;
        this.cursoRepository = cursoRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.entregaRepository = entregaRepository;
        this.reclamoRepository = reclamoRepository;
        this.certificadoRepository = certificadoRepository;
        this.estadisticaService = estadisticaService;
    }

    public Map<String, Object> plataforma(Long coordinadorId) {
        Usuario u = usuarioRepository.findById(coordinadorId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado con id " + coordinadorId));
        if (!"COORDINADOR".equalsIgnoreCase(u.getRol().getNombre())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Solo un usuario con rol COORDINADOR puede ver los reportes de la plataforma");
        }

        // Bloque 1: totales
        Map<String, Object> totales = new LinkedHashMap<String, Object>();
        totales.put("docentes", usuarioRepository.countByRolNombre("DOCENTE"));
        totales.put("estudiantes", usuarioRepository.countByRolNombre("ESTUDIANTE"));
        totales.put("coordinadores", usuarioRepository.countByRolNombre("COORDINADOR"));
        totales.put("ofertas", ofertaRepository.count());
        totales.put("cursos", cursoRepository.count());
        totales.put("cursosPublicados", cursoRepository.countByPublicacion("PUBLICADO"));
        totales.put("inscripcionesActivas", inscripcionRepository.countByEstado("ACTIVA"));
        totales.put("inscripcionesRetiradas", inscripcionRepository.countByEstado("RETIRADA"));
        totales.put("entregas", entregaRepository.count());
        totales.put("entregasCalificadas", entregaRepository.countByEstado("CALIFICADA"));
        totales.put("reclamosAbiertos", reclamoRepository.countByEstado("ABIERTO"));
        totales.put("reclamosResueltos", reclamoRepository.countByEstado("RESUELTO"));
        totales.put("reclamosRechazados", reclamoRepository.countByEstado("RECHAZADO"));
        totales.put("certificadosEmitidos", certificadoRepository.count());

        // Bloque 2 y 3: detalle por curso y aprobación general
        List<Map<String, Object>> cursos = new ArrayList<Map<String, Object>>();
        int inscritosTotal = 0;
        int aprobadosTotal = 0;
        for (Curso c : cursoRepository.findAll()) {
            EstadisticaCurso e = estadisticaService.calcular(c.getId());
            inscritosTotal += e.getTotalInscritos();
            aprobadosTotal += e.getAprobados();

            Map<String, Object> fila = new LinkedHashMap<String, Object>();
            fila.put("cursoId", c.getId());
            fila.put("curso", c.getNombre());
            fila.put("docente", c.getDocente().getNombre());
            fila.put("publicacion", c.getPublicacion());
            fila.put("cupos", c.getCupos());
            fila.put("inscritosActivos", e.getTotalInscritos());
            fila.put("promedioGeneralSobre5", e.getPromedioGeneralSobre5());
            fila.put("aprobados", e.getAprobados());
            fila.put("reprobados", e.getReprobados());
            fila.put("porcentajeAprobacion", e.getPorcentajeAprobacion());
            fila.put("calificacionPromedioCurso", e.getCalificacionPromedioCurso());
            cursos.add(fila);
        }

        Map<String, Object> aprobacion = new LinkedHashMap<String, Object>();
        aprobacion.put("totalInscritos", inscritosTotal);
        aprobacion.put("aprobados", aprobadosTotal);
        aprobacion.put("porcentajeAprobacionGeneral", inscritosTotal == 0
                ? 0.0 : Math.round(aprobadosTotal * 10000.0 / inscritosTotal) / 100.0);

        Map<String, Object> reporte = new LinkedHashMap<String, Object>();
        reporte.put("totales", totales);
        reporte.put("aprobacionGeneral", aprobacion);
        reporte.put("cursos", cursos);
        return reporte;
    }
}