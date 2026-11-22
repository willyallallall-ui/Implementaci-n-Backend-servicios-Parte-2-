/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.educacion.plataforma.controller;

import com.educacion.plataforma.dto.RespuestaReclamoRequest;
import com.educacion.plataforma.entity.Reclamo;
import com.educacion.plataforma.service.ReclamoService;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
/**
 *
 * @author Willi
 */


@RestController
@RequestMapping("/api/reclamos")
public class ReclamoController {

    private final ReclamoService reclamoService;

    public ReclamoController(ReclamoService reclamoService) {
        this.reclamoService = reclamoService;
    }

    @GetMapping
    public List<Reclamo> listar(@RequestParam(required = false) Long estudianteId,
                                @RequestParam(required = false) Long docenteId) {
        if (estudianteId != null) {
            return reclamoService.listarPorEstudiante(estudianteId);
        }
        if (docenteId != null) {
            return reclamoService.listarPorDocente(docenteId);
        }
        return reclamoService.listar();
    }

    @GetMapping("/{id}")
    public Reclamo buscar(@PathVariable Long id) {
        return reclamoService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Reclamo crear(@Valid @RequestBody Reclamo reclamo) {
        return reclamoService.crear(reclamo);
    }

    @PutMapping("/{id}/responder")
    public Reclamo responder(@PathVariable Long id,
                             @Valid @RequestBody RespuestaReclamoRequest datos) {
        return reclamoService.responder(id, datos);
    }
}