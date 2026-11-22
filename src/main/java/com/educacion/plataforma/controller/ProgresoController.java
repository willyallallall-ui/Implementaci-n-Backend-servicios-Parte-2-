/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.educacion.plataforma.controller;

import com.educacion.plataforma.dto.ProgresoResponse;
import com.educacion.plataforma.service.ProgresoService;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 *
 * @author Willi
 */


@RestController
@RequestMapping("/api/progreso")
public class ProgresoController {

    private final ProgresoService progresoService;

    public ProgresoController(ProgresoService progresoService) {
        this.progresoService = progresoService;
    }

    @GetMapping("/curso/{cursoId}/estudiante/{estudianteId}")
    public ProgresoResponse progreso(@PathVariable Long cursoId,
                                     @PathVariable Long estudianteId) {
        return progresoService.calcular(cursoId, estudianteId);
    }
    
    @GetMapping("/curso/{cursoId}/estudiante/{estudianteId}/notas")
    public Map<String, Object> notas(@PathVariable Long cursoId,
                                     @PathVariable Long estudianteId) {
        return progresoService.detalleNotas(cursoId, estudianteId);
    }    
}