/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.educacion.plataforma.controller;

import com.educacion.plataforma.entity.Entrega;
import com.educacion.plataforma.service.EntregaService;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.educacion.plataforma.dto.CalificacionRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Willi
 */

@RestController
@RequestMapping("/api/entregas")
public class EntregaController {

    private final EntregaService entregaService;

    public EntregaController(EntregaService entregaService) {
        this.entregaService = entregaService;
    }

    @GetMapping
    public List<Entrega> listar(@RequestParam(required = false) Long actividadId,
                                @RequestParam(required = false) Long estudianteId) {
        if (actividadId != null) {
            return entregaService.listarPorActividad(actividadId);
        }
        if (estudianteId != null) {
            return entregaService.listarPorEstudiante(estudianteId);
        }
        return entregaService.listar();
    }

    @GetMapping("/{id}")
    public Entrega buscar(@PathVariable Long id) {
        return entregaService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Entrega crear(@Valid @RequestBody Entrega entrega) {
        return entregaService.crear(entrega);
    }
    
    @PutMapping("/{id}/calificar")
    public Entrega calificar(@PathVariable Long id,
            @Valid @RequestBody CalificacionRequest datos) {
        return entregaService.calificar(id, datos);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        entregaService.eliminar(id);
    }
    @PostMapping(value = "/archivo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Entrega crearConArchivo(@RequestParam Long actividadId,
                                   @RequestParam Long estudianteId,
                                   @RequestParam(required = false) String contenido,
                                   @RequestParam("archivo") MultipartFile archivo) {
        return entregaService.crearConArchivo(actividadId, estudianteId, contenido, archivo);
    }

    @GetMapping("/{id}/descargar")
    public ResponseEntity<Resource> descargar(@PathVariable Long id, @RequestParam Long usuarioId) {
        Entrega e = entregaService.buscarPorId(id);
        Resource recurso = entregaService.descargarArchivo(id, usuarioId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + e.getArchivoOriginal() + "\"")
                .body(recurso);
    }    
}