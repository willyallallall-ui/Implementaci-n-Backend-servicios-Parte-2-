/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.educacion.plataforma.repository;

import com.educacion.plataforma.entity.Reclamo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Willi
 */

public interface ReclamoRepository extends JpaRepository<Reclamo, Long> {

    List<Reclamo> findByEstudianteId(Long estudianteId);

    List<Reclamo> findByEntregaActividadUnidadCursoDocenteId(Long docenteId);

    boolean existsByEntregaIdAndEstado(Long entregaId, String estado);
    long countByEstado(String estado);    
}