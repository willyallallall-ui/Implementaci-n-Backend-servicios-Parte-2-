/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.educacion.plataforma.controller;

import com.educacion.plataforma.dto.ForoRequest;
import com.educacion.plataforma.dto.MensajeRequest;
import com.educacion.plataforma.entity.Foro;
import com.educacion.plataforma.entity.MensajeForo;
import com.educacion.plataforma.service.ForoService;
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
@RequestMapping("/api/foros")
public class ForoController {

    private final ForoService foroService;

    public ForoController(ForoService foroService) {
        this.foroService = foroService;
    }

    @GetMapping
    public List<Foro> listar(@RequestParam Long cursoId) {
        return foroService.listarPorCurso(cursoId);
    }

    @GetMapping("/{id}")
    public Foro buscar(@PathVariable Long id) {
        return foroService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Foro abrir(@Valid @RequestBody ForoRequest req) {
        return foroService.abrir(req);
    }

    @PutMapping("/{id}/cerrar")
    public Foro cerrar(@PathVariable Long id, @RequestParam Long usuarioId) {
        return foroService.cambiarEstado(id, usuarioId, "CERRADO");
    }

    @PutMapping("/{id}/reabrir")
    public Foro reabrir(@PathVariable Long id, @RequestParam Long usuarioId) {
        return foroService.cambiarEstado(id, usuarioId, "ABIERTO");
    }

    @GetMapping("/{id}/mensajes")
    public List<MensajeForo> mensajes(@PathVariable Long id) {
        return foroService.listarMensajes(id);
    }

    @PostMapping("/{id}/mensajes")
    @ResponseStatus(HttpStatus.CREATED)
    public MensajeForo publicar(@PathVariable Long id, @Valid @RequestBody MensajeRequest req) {
        return foroService.publicar(id, req);
    }
}