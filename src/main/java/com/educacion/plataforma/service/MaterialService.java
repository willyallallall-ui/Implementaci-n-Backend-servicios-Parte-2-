/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.educacion.plataforma.service;

import com.educacion.plataforma.entity.Material;
import com.educacion.plataforma.entity.Unidad;
import com.educacion.plataforma.entity.Usuario;
import com.educacion.plataforma.repository.InscripcionRepository;
import com.educacion.plataforma.repository.MaterialRepository;
import com.educacion.plataforma.repository.UnidadRepository;
import com.educacion.plataforma.repository.UsuarioRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author Willi
 */

@Service
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final UnidadRepository unidadRepository;
    private final UsuarioRepository usuarioRepository;
    private final InscripcionRepository inscripcionRepository;
    private final AlmacenamientoService almacenamiento;

    public MaterialService(MaterialRepository materialRepository,
                           UnidadRepository unidadRepository,
                           UsuarioRepository usuarioRepository,
                           InscripcionRepository inscripcionRepository,
                           AlmacenamientoService almacenamiento) {
        this.materialRepository = materialRepository;
        this.unidadRepository = unidadRepository;
        this.usuarioRepository = usuarioRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.almacenamiento = almacenamiento;
    }

    public List<Material> listarPorUnidad(Long unidadId) {
        return materialRepository.findByUnidadId(unidadId);
    }

    public List<Material> listarPorCurso(Long cursoId) {
        return materialRepository.findByUnidadCursoId(cursoId);
    }

    public Material buscarPorId(Long id) {
        return materialRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Material no encontrado con id " + id));
    }

    public Material publicar(Long unidadId, Long docenteId, String titulo,
                             String descripcion, MultipartFile archivo) {
        Unidad unidad = unidadRepository.findById(unidadId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Unidad no encontrada con id " + unidadId));
        Usuario docente = usuarioRepository.findById(docenteId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado con id " + docenteId));
        if (!"DOCENTE".equalsIgnoreCase(docente.getRol().getNombre())
                || !unidad.getCurso().getDocente().getId().equals(docente.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Solo el docente del curso puede publicar materiales");
        }
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El título es obligatorio");
        }

        String guardado = almacenamiento.guardar(archivo);

        Material m = new Material();
        m.setTitulo(titulo);
        m.setDescripcion(descripcion);
        m.setNombreOriginal(archivo.getOriginalFilename());
        m.setNombreGuardado(guardado);
        m.setTipoContenido(archivo.getContentType());
        m.setFechaPublicacion(LocalDate.now());
        m.setUnidad(unidad);
        return materialRepository.save(m);
    }

    // Devuelve el archivo si el usuario es el docente del curso o un estudiante inscrito ACTIVO
    public Resource descargar(Long materialId, Long usuarioId) {
        Material m = buscarPorId(materialId);
        Usuario u = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado con id " + usuarioId));
        Long cursoId = m.getUnidad().getCurso().getId();

        boolean esDocenteDelCurso = m.getUnidad().getCurso().getDocente().getId().equals(u.getId());
        boolean esInscritoActivo = inscripcionRepository
                .findByCursoIdAndEstudianteId(cursoId, u.getId())
                .map(i -> "ACTIVA".equals(i.getEstado()))
                .orElse(false);
        if (!esDocenteDelCurso && !esInscritoActivo) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Solo el docente o los estudiantes inscritos pueden descargar este material");
        }
        return almacenamiento.cargar(m.getNombreGuardado());
    }

    public void eliminar(Long id, Long docenteId) {
        Material m = buscarPorId(id);
        if (!m.getUnidad().getCurso().getDocente().getId().equals(docenteId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Solo el docente del curso puede eliminar materiales");
        }
        materialRepository.delete(m);
    }
}