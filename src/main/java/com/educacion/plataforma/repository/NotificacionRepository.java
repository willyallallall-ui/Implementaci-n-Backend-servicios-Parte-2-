/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.educacion.plataforma.repository;

import com.educacion.plataforma.entity.Notificacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
/**
 *
 * @author Willi
 */



public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByDestinatarioIdOrderByFechaDesc(Long destinatarioId);

    List<Notificacion> findByDestinatarioIdAndLeidaFalseOrderByFechaDesc(Long destinatarioId);

    long countByDestinatarioIdAndLeidaFalse(Long destinatarioId);
}