/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.educacion.plataforma.controller;

import com.educacion.plataforma.entity.EvaluacionCurso;
import com.educacion.plataforma.service.EvaluacionCursoService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
@RequestMapping("/api/evaluaciones-curso")
public class EvaluacionCursoController {

    private final EvaluacionCursoService evaluacionService;

    public EvaluacionCursoController(EvaluacionCursoService evaluacionService) {
        this.evaluacionService = evaluacionService;
    }

    @GetMapping
    public List<EvaluacionCurso> listar(@RequestParam(required = false) Long cursoId) {
        if (cursoId != null) {
            return evaluacionService.listarPorCurso(cursoId);
        }
        return evaluacionService.listar();
    }

    @GetMapping("/{id}")
    public EvaluacionCurso buscar(@PathVariable Long id) {
        return evaluacionService.buscarPorId(id);
    }

    @GetMapping("/promedio/{cursoId}")
    public Map<String, Object> promedio(@PathVariable Long cursoId) {
        Map<String, Object> respuesta = new HashMap<String, Object>();
        respuesta.put("cursoId", cursoId);
        respuesta.put("promedio", evaluacionService.promedioCurso(cursoId));
        return respuesta;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EvaluacionCurso crear(@Valid @RequestBody EvaluacionCurso evaluacion) {
        return evaluacionService.crear(evaluacion);
    }

    @PutMapping("/{id}")
    public EvaluacionCurso actualizar(@PathVariable Long id,
                                      @Valid @RequestBody EvaluacionCurso evaluacion) {
        return evaluacionService.actualizar(id, evaluacion);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        evaluacionService.eliminar(id);
    }
}