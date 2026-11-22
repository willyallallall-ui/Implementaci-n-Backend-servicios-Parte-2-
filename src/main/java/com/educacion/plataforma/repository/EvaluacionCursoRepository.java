/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.educacion.plataforma.repository;

import com.educacion.plataforma.entity.EvaluacionCurso;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Willi
 */

public interface EvaluacionCursoRepository extends JpaRepository<EvaluacionCurso, Long> {

    List<EvaluacionCurso> findByCursoId(Long cursoId);

    boolean existsByCursoIdAndEstudianteId(Long cursoId, Long estudianteId);

    @Query("SELECT AVG(e.calificacion) FROM EvaluacionCurso e WHERE e.curso.id = :cursoId")
    Double promedioPorCurso(@Param("cursoId") Long cursoId);
}