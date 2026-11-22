/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.educacion.plataforma.controller;

import com.educacion.plataforma.dto.NotificacionRequest;
import com.educacion.plataforma.entity.Notificacion;
import com.educacion.plataforma.service.NotificacionService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Notificacion> listar(@PathVariable Long usuarioId,
                                     @RequestParam(defaultValue = "false") boolean soloNoLeidas) {
        return notificacionService.listarDeUsuario(usuarioId, soloNoLeidas);
    }

    @GetMapping("/usuario/{usuarioId}/no-leidas/cantidad")
    public Map<String, Object> cantidadNoLeidas(@PathVariable Long usuarioId) {
        Map<String, Object> r = new HashMap<String, Object>();
        r.put("usuarioId", usuarioId);
        r.put("noLeidas", notificacionService.contarNoLeidas(usuarioId));
        return r;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<Notificacion> enviar(@Valid @RequestBody NotificacionRequest req) {
        return notificacionService.enviar(req);
    }

    @PutMapping("/{id}/leer")
    public Notificacion marcarLeida(@PathVariable Long id) {
        return notificacionService.marcarLeida(id);
    }
}