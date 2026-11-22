/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.educacion.plataforma.repository;

import com.educacion.plataforma.entity.Actividad;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
/**
 *
 * @author Willi
 */


public interface ActividadRepository extends JpaRepository<Actividad, Long> {

    List<Actividad> findByUnidadId(Long unidadId);

    List<Actividad> findByUnidadCursoId(Long cursoId);
}