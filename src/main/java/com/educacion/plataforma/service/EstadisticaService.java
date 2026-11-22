/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.educacion.plataforma.service;

import com.educacion.plataforma.dto.EstadisticaActividad;
import com.educacion.plataforma.dto.EstadisticaCurso;
import com.educacion.plataforma.entity.Actividad;
import com.educacion.plataforma.entity.Curso;
import com.educacion.plataforma.entity.Entrega;
import com.educacion.plataforma.entity.Inscripcion;
import com.educacion.plataforma.repository.ActividadRepository;
import com.educacion.plataforma.repository.CursoRepository;
import com.educacion.plataforma.repository.EntregaRepository;
import com.educacion.plataforma.repository.EvaluacionCursoRepository;
import com.educacion.plataforma.repository.InscripcionRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
/**
 *
 * @author Willi
 */


@Service
public class EstadisticaService {

    private static final double NOTA_APROBACION = 3.0;

    private final CursoRepository cursoRepository;
    private final InscripcionRepository inscripcionRepository;
    private final ActividadRepository actividadRepository;
    private final EntregaRepository entregaRepository;
    private final EvaluacionCursoRepository evaluacionRepository;

    public EstadisticaService(CursoRepository cursoRepository,
                              InscripcionRepository inscripcionRepository,
                              ActividadRepository actividadRepository,
                              EntregaRepository entregaRepository,
                              EvaluacionCursoRepository evaluacionRepository) {
        this.cursoRepository = cursoRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.actividadRepository = actividadRepository;
        this.entregaRepository = entregaRepository;
        this.evaluacionRepository = evaluacionRepository;
    }

    public EstadisticaCurso calcular(Long cursoId) {
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Curso no encontrado con id " + cursoId));

        List<Inscripcion> inscripciones = inscripcionRepository.findByCursoId(cursoId);
        List<Actividad> actividades = actividadRepository.findByUnidadCursoId(cursoId);
        List<Entrega> entregas = entregaRepository.findByActividadUnidadCursoId(cursoId);

        // Puntaje máximo total del curso
        double puntajeMaximoCurso = 0;
        for (Actividad a : actividades) {
            puntajeMaximoCurso += a.getPuntajeMaximo();
        }

        // Estudiantes con inscripción ACTIVA
        Set<Long> inscritos = new HashSet<Long>();
        for (Inscripcion i : inscripciones) {
            if ("ACTIVA".equals(i.getEstado())) {
                inscritos.add(i.getEstudiante().getId());
            }
        }

        // Puntaje de cada estudiante inscrito (suma de sus notas) y datos globales
        Map<Long, Double> puntajePorEstudiante = new HashMap<Long, Double>();
        for (Long id : inscritos) {
            puntajePorEstudiante.put(id, 0.0);
        }
        Set<Long> conEntregas = new HashSet<Long>();
        int calificadas = 0;
        for (Entrega e : entregas) {
            Long estudianteId = e.getEstudiante().getId();
            if (!inscritos.contains(estudianteId)) {
                continue;
            }
            conEntregas.add(estudianteId);
            if ("CALIFICADA".equals(e.getEstado()) && e.getNota() != null) {
                calificadas++;
                Actividad act = e.getActividad();
                double aporte = e.getNota() / act.getPuntajeMaximo() * 5 * act.getPorcentaje() / 100.0;
                puntajePorEstudiante.put(estudianteId,
                        puntajePorEstudiante.get(estudianteId) + aporte);
            }
        }

        // Aprobados, reprobados y promedio general
        int aprobados = 0;
        int reprobados = 0;
        double sumaPromedios = 0;
        for (Double definitiva : puntajePorEstudiante.values()) {
            double promedio = redondear(definitiva);
            sumaPromedios += promedio;
            if (promedio >= NOTA_APROBACION) {
                aprobados++;
            } else {
                reprobados++;
            }
        }
        int totalInscritos = inscritos.size();

        // Estadística por actividad
        List<EstadisticaActividad> porActividad = new ArrayList<EstadisticaActividad>();
        for (Actividad a : actividades) {
            porActividad.add(estadisticaDeActividad(a, entregas));
        }

        EstadisticaCurso r = new EstadisticaCurso();
        r.setCursoId(curso.getId());
        r.setCursoNombre(curso.getNombre());
        r.setTotalInscritos(totalInscritos);
        r.setEstudiantesConEntregas(conEntregas.size());
        r.setTotalActividades(actividades.size());
        r.setTotalEntregas(entregas.size());
        r.setEntregasCalificadas(calificadas);
        r.setPromedioGeneralSobre5(totalInscritos == 0 ? 0.0 : redondear(sumaPromedios / totalInscritos));
        r.setAprobados(aprobados);
        r.setReprobados(reprobados);
        r.setPorcentajeAprobacion(totalInscritos == 0 ? 0.0 : redondear(aprobados * 100.0 / totalInscritos));
        r.setCalificacionPromedioCurso(promedioCurso(cursoId));
        r.setActividades(porActividad);
        return r;
    }

    private EstadisticaActividad estadisticaDeActividad(Actividad actividad, List<Entrega> entregas) {
        int cantidad = 0;
        int conNota = 0;
        double suma = 0;
        double maxima = 0;
        double minima = 0;
        for (Entrega e : entregas) {
            if (!e.getActividad().getId().equals(actividad.getId())) {
                continue;
            }
            cantidad++;
            if (e.getNota() != null) {
                double nota = e.getNota();
                if (conNota == 0) {
                    maxima = nota;
                    minima = nota;
                } else {
                    maxima = Math.max(maxima, nota);
                    minima = Math.min(minima, nota);
                }
                suma += nota;
                conNota++;
            }
        }
        EstadisticaActividad ea = new EstadisticaActividad();
        ea.setActividadId(actividad.getId());
        ea.setTitulo(actividad.getTitulo());
        ea.setPuntajeMaximo(actividad.getPuntajeMaximo());
        ea.setEntregas(cantidad);
        ea.setNotaPromedio(conNota == 0 ? 0.0 : redondear(suma / conNota));
        ea.setNotaMaxima(maxima);
        ea.setNotaMinima(minima);
        return ea;
    }

    private Double promedioCurso(Long cursoId) {
        Double promedio = evaluacionRepository.promedioPorCurso(cursoId);
        return promedio == null ? null : redondear(promedio);
    }

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}