/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.educacion.plataforma.controller;
import com.educacion.plataforma.entity.Actividad;
import com.educacion.plataforma.service.ActividadService;
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
@RequestMapping("/api/actividades")
public class ActividadController {

    private final ActividadService actividadService;

    public ActividadController(ActividadService actividadService) {
        this.actividadService = actividadService;
    }

    @GetMapping
    public List<Actividad> listar(@RequestParam(required = false) Long unidadId,
                                  @RequestParam(required = false) Long cursoId) {
        if (unidadId != null) {
            return actividadService.listarPorUnidad(unidadId);
        }
        if (cursoId != null) {
            return actividadService.listarPorCurso(cursoId);
        }
        return actividadService.listar();
    }

    @GetMapping("/{id}")
    public Actividad buscar(@PathVariable Long id) {
        return actividadService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Actividad crear(@Valid @RequestBody Actividad actividad) {
        return actividadService.crear(actividad);
    }

    @PutMapping("/{id}")
    public Actividad actualizar(@PathVariable Long id, @Valid @RequestBody Actividad actividad) {
        return actividadService.actualizar(id, actividad);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        actividadService.eliminar(id);
    }
}