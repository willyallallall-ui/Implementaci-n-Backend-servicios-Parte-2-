/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.educacion.plataforma.repository;

import com.educacion.plataforma.entity.Entrega;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
/**
 *
 * @author Willi
 */


public interface EntregaRepository extends JpaRepository<Entrega, Long> {

    List<Entrega> findByActividadId(Long actividadId);

    List<Entrega> findByEstudianteId(Long estudianteId);

    boolean existsByActividadIdAndEstudianteId(Long actividadId, Long estudianteId);
    long countByEstado(String estado);    

    List<Entrega> findByEstudianteIdAndActividadUnidadCursoId(Long estudianteId, Long cursoId);

    List<Entrega> findByActividadUnidadCursoId(Long cursoId);
}