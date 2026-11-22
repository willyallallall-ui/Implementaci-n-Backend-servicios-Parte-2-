/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.educacion.plataforma.repository;

import com.educacion.plataforma.entity.Inscripcion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
/**
 *
 * @author Willi
 */


public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {

    List<Inscripcion> findByCursoId(Long cursoId);

    List<Inscripcion> findByEstudianteId(Long estudianteId);

    boolean existsByCursoIdAndEstudianteId(Long cursoId, Long estudianteId);

    Optional<Inscripcion> findByCursoIdAndEstudianteId(Long cursoId, Long estudianteId);
}