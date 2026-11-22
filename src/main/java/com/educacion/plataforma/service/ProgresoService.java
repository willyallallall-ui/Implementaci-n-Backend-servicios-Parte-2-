/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.educacion.plataforma.service;


import com.educacion.plataforma.dto.ProgresoResponse;
import com.educacion.plataforma.entity.Actividad;
import com.educacion.plataforma.entity.Curso;
import com.educacion.plataforma.entity.Entrega;
import com.educacion.plataforma.entity.Usuario;
import com.educacion.plataforma.repository.ActividadRepository;
import com.educacion.plataforma.repository.CursoRepository;
import com.educacion.plataforma.repository.EntregaRepository;
import com.educacion.plataforma.repository.InscripcionRepository;
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
public class ProgresoService {

    private final CursoRepository cursoRepository;
    private final UsuarioRepository usuarioRepository;
    private final InscripcionRepository inscripcionRepository;
    private final ActividadRepository actividadRepository;
    private final EntregaRepository entregaRepository;

    public ProgresoService(CursoRepository cursoRepository,
                           UsuarioRepository usuarioRepository,
                           InscripcionRepository inscripcionRepository,
                           ActividadRepository actividadRepository,
                           EntregaRepository entregaRepository) {
        this.cursoRepository = cursoRepository;
        this.usuarioRepository = usuarioRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.actividadRepository = actividadRepository;
        this.entregaRepository = entregaRepository;
    }

    public ProgresoResponse calcular(Long cursoId, Long estudianteId) {
        Curso curso = buscarCurso(cursoId);
        Usuario estudiante = buscarEstudianteInscrito(cursoId, estudianteId);

        List<Actividad> actividades = actividadRepository.findByUnidadCursoId(cursoId);
        List<Entrega> entregas =
                entregaRepository.findByEstudianteIdAndActividadUnidadCursoId(estudianteId, cursoId);

        double puntajeMaximoCurso = 0;
        double porcentajeAsignado = 0;
        for (Actividad a : actividades) {
            puntajeMaximoCurso += a.getPuntajeMaximo();
            porcentajeAsignado += a.getPorcentaje();
        }

        int calificadas = 0;
        double puntajeObtenido = 0;
        double porcentajeEvaluado = 0;
        double definitiva = 0;
        for (Entrega e : entregas) {
            if ("CALIFICADA".equals(e.getEstado()) && e.getNota() != null) {
                calificadas++;
                puntajeObtenido += e.getNota();
                porcentajeEvaluado += e.getActividad().getPorcentaje();
                definitiva += aporte(e.getActividad(), e.getNota());
            }
        }

        int total = actividades.size();
        ProgresoResponse r = new ProgresoResponse();
        r.setCursoId(curso.getId());
        r.setCursoNombre(curso.getNombre());
        r.setEstudianteId(estudiante.getId());
        r.setEstudianteNombre(estudiante.getNombre());
        r.setTotalActividades(total);
        r.setActividadesEntregadas(entregas.size());
        r.setActividadesCalificadas(calificadas);
        r.setPorcentajeAvance(total == 0 ? 0.0 : redondear(entregas.size() * 100.0 / total));
        r.setPuntajeObtenido(puntajeObtenido);
        r.setPuntajeMaximoCurso(puntajeMaximoCurso);
        r.setPorcentajeAsignado(porcentajeAsignado);
        r.setPorcentajeEvaluado(porcentajeEvaluado);
        r.setPromedioSobre5(redondear(definitiva));
        return r;
    }

    // Detalle por actividad: porcentaje, nota y aporte a la nota definitiva
    public Map<String, Object> detalleNotas(Long cursoId, Long estudianteId) {
        Curso curso = buscarCurso(cursoId);
        Usuario estudiante = buscarEstudianteInscrito(cursoId, estudianteId);

        List<Actividad> actividades = actividadRepository.findByUnidadCursoId(cursoId);
        List<Entrega> entregas =
                entregaRepository.findByEstudianteIdAndActividadUnidadCursoId(estudianteId, cursoId);

        double definitiva = 0;
        double porcentajeAsignado = 0;
        List<Map<String, Object>> detalle = new ArrayList<Map<String, Object>>();
        for (Actividad a : actividades) {
            porcentajeAsignado += a.getPorcentaje();
            Entrega entrega = null;
            for (Entrega e : entregas) {
                if (e.getActividad().getId().equals(a.getId())) {
                    entrega = e;
                }
            }
            Map<String, Object> fila = new LinkedHashMap<String, Object>();
            fila.put("actividadId", a.getId());
            fila.put("titulo", a.getTitulo());
            fila.put("tipo", a.getTipo());
            fila.put("porcentaje", a.getPorcentaje());
            fila.put("puntajeMaximo", a.getPuntajeMaximo());
            if (entrega == null) {
                fila.put("estado", "SIN_ENTREGAR");
                fila.put("nota", null);
                fila.put("aporteSobre5", 0.0);
            } else {
                fila.put("estado", entrega.getEstado());
                fila.put("nota", entrega.getNota());
                double ap = entrega.getNota() == null ? 0.0 : aporte(a, entrega.getNota());
                fila.put("aporteSobre5", redondear(ap));
                fila.put("retroalimentacion", entrega.getRetroalimentacion());
                definitiva += ap;
            }
            detalle.add(fila);
        }

        Map<String, Object> r = new LinkedHashMap<String, Object>();
        r.put("cursoId", curso.getId());
        r.put("cursoNombre", curso.getNombre());
        r.put("estudianteId", estudiante.getId());
        r.put("estudianteNombre", estudiante.getNombre());
        r.put("porcentajeAsignado", porcentajeAsignado);
        r.put("notaDefinitivaSobre5", redondear(definitiva));
        r.put("actividades", detalle);
        return r;
    }

    // Aporte de una actividad a la nota definitiva (escala 0 a 5)
    private double aporte(Actividad actividad, double nota) {
        return nota / actividad.getPuntajeMaximo() * 5 * actividad.getPorcentaje() / 100.0;
    }

    private Curso buscarCurso(Long cursoId) {
        return cursoRepository.findById(cursoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Curso no encontrado con id " + cursoId));
    }

    private Usuario buscarEstudianteInscrito(Long cursoId, Long estudianteId) {
        Usuario estudiante = usuarioRepository.findById(estudianteId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado con id " + estudianteId));
        if (!inscripcionRepository.existsByCursoIdAndEstudianteId(cursoId, estudianteId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El estudiante no está inscrito en este curso");
        }
        return estudiante;
    }

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}