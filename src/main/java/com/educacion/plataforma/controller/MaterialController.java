/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.educacion.plataforma.controller;

import com.educacion.plataforma.entity.Material;
import com.educacion.plataforma.service.MaterialService;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
/**
 *
 * @author Willi
 */


@RestController
@RequestMapping("/api/materiales")
public class MaterialController {

    private final MaterialService materialService;

    public MaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }

    @GetMapping
    public List<Material> listar(@RequestParam(required = false) Long unidadId,
                                 @RequestParam(required = false) Long cursoId) {
        if (unidadId != null) {
            return materialService.listarPorUnidad(unidadId);
        }
        if (cursoId != null) {
            return materialService.listarPorCurso(cursoId);
        }
        throw new org.springframework.web.server.ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Indique unidadId o cursoId");
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Material publicar(@RequestParam Long unidadId,
                             @RequestParam Long docenteId,
                             @RequestParam String titulo,
                             @RequestParam(required = false) String descripcion,
                             @RequestParam("archivo") MultipartFile archivo) {
        return materialService.publicar(unidadId, docenteId, titulo, descripcion, archivo);
    }

    @GetMapping("/{id}/descargar")
    public ResponseEntity<Resource> descargar(@PathVariable Long id,
                                              @RequestParam Long usuarioId) {
        Material m = materialService.buscarPorId(id);
        Resource recurso = materialService.descargar(id, usuarioId);
        MediaType tipo = MediaType.APPLICATION_OCTET_STREAM;
        if (m.getTipoContenido() != null) {
            try {
                tipo = MediaType.parseMediaType(m.getTipoContenido());
            } catch (Exception e) {
                // se queda con octet-stream
            }
        }
        return ResponseEntity.ok()
                .contentType(tipo)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + m.getNombreOriginal() + "\"")
                .body(recurso);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id, @RequestParam Long docenteId) {
        materialService.eliminar(id, docenteId);
    }
}