/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.educacion.plataforma.repository;

import com.educacion.plataforma.entity.Certificado;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
/**
 *
 * @author Willi
 */


public interface CertificadoRepository extends JpaRepository<Certificado, Long> {

    boolean existsByCursoIdAndEstudianteId(Long cursoId, Long estudianteId);

    List<Certificado> findByEstudianteId(Long estudianteId);

    Optional<Certificado> findByCodigo(String codigo);
}