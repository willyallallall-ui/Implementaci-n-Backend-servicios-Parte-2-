/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.educacion.plataforma.controller;

import com.educacion.plataforma.entity.Unidad;
import com.educacion.plataforma.service.UnidadService;
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
@RequestMapping("/api/unidades")
public class UnidadController {

    private final UnidadService unidadService;

    public UnidadController(UnidadService unidadService) {
        this.unidadService = unidadService;
    }

    @GetMapping
    public List<Unidad> listar(@RequestParam(required = false) Long cursoId) {
        if (cursoId != null) {
            return unidadService.listarPorCurso(cursoId);
        }
        return unidadService.listar();
    }

    @GetMapping("/{id}")
    public Unidad buscar(@PathVariable Long id) {
        return unidadService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Unidad crear(@Valid @RequestBody Unidad unidad) {
        return unidadService.crear(unidad);
    }

    @PutMapping("/{id}")
    public Unidad actualizar(@PathVariable Long id, @Valid @RequestBody Unidad unidad) {
        return unidadService.actualizar(id, unidad);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        unidadService.eliminar(id);
    }
}