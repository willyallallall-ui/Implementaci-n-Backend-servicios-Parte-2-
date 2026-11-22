/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.educacion.plataforma.controller;

import com.educacion.plataforma.entity.Inscripcion;
import com.educacion.plataforma.service.InscripcionService;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api/inscripciones")
public class InscripcionController {

    private final InscripcionService inscripcionService;

    public InscripcionController(InscripcionService inscripcionService) {
        this.inscripcionService = inscripcionService;
    }

    @GetMapping
    public List<Inscripcion> listar(@RequestParam(required = false) Long cursoId,
                                    @RequestParam(required = false) Long estudianteId) {
        if (cursoId != null) {
            return inscripcionService.listarPorCurso(cursoId);
        }
        if (estudianteId != null) {
            return inscripcionService.listarPorEstudiante(estudianteId);
        }
        return inscripcionService.listar();
    }

    @GetMapping("/{id}")
    public Inscripcion buscar(@PathVariable Long id) {
        return inscripcionService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Inscripcion crear(@Valid @RequestBody Inscripcion inscripcion) {
        return inscripcionService.crear(inscripcion);
    }

    @PutMapping("/{id}/cancelar")
    public Inscripcion cancelar(@PathVariable Long id) {
        return inscripcionService.cancelar(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        inscripcionService.eliminar(id);
    }
}