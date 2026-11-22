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
import java.util.List;
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
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Curso no encontrado con id " + cursoId));
        Usuario estudiante = usuarioRepository.findById(estudianteId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado con id " + estudianteId));

        if (!inscripcionRepository.existsByCursoIdAndEstudianteId(cursoId, estudianteId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El estudiante no está inscrito en este curso");
        }

        List<Actividad> actividades = actividadRepository.findByUnidadCursoId(cursoId);
        List<Entrega> entregas =
                entregaRepository.findByEstudianteIdAndActividadUnidadCursoId(estudianteId, cursoId);

        double puntajeMaximoCurso = 0;
        for (Actividad a : actividades) {
            puntajeMaximoCurso += a.getPuntajeMaximo();
        }

        int calificadas = 0;
        double puntajeObtenido = 0;
        for (Entrega e : entregas) {
            if ("CALIFICADA".equals(e.getEstado()) && e.getNota() != null) {
                calificadas++;
                puntajeObtenido += e.getNota();
            }
        }

        int total = actividades.size();
        double porcentaje = total == 0 ? 0.0 : redondear(entregas.size() * 100.0 / total);
        double promedio = puntajeMaximoCurso == 0
                ? 0.0 : redondear(puntajeObtenido / puntajeMaximoCurso * 5);

        ProgresoResponse r = new ProgresoResponse();
        r.setCursoId(curso.getId());
        r.setCursoNombre(curso.getNombre());
        r.setEstudianteId(estudiante.getId());
        r.setEstudianteNombre(estudiante.getNombre());
        r.setTotalActividades(total);
        r.setActividadesEntregadas(entregas.size());
        r.setActividadesCalificadas(calificadas);
        r.setPorcentajeAvance(porcentaje);
        r.setPuntajeObtenido(puntajeObtenido);
        r.setPuntajeMaximoCurso(puntajeMaximoCurso);
        r.setPromedioSobre5(promedio);
        return r;
    }

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}