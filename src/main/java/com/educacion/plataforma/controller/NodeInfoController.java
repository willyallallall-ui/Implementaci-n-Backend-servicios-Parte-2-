/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.educacion.plataforma.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Willi
 */

@RestController
@RequestMapping("/api/nodeinfo")
public class NodeInfoController {

    /** Puerto en el que corre esta instancia (por defecto 8080). */
    private final String puerto;

    public NodeInfoController(@Value("${server.port:8080}") String puerto) {
        this.puerto = puerto;
    }

    /**
     * GET /api/nodeinfo
     * @return JSON con host, ip, puerto y hora del servidor que respondio.
     */
    @GetMapping

    public Map<String, Object> info() {
        String host;
        String ip;
        try {
            InetAddress local = InetAddress.getLocalHost();
            host = local.getHostName();
            ip = local.getHostAddress();
        } catch (UnknownHostException e) {
            host = "desconocido";
            ip = "desconocida";
        }
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("host", host);
        respuesta.put("ip", ip);
        respuesta.put("puerto", puerto);
        respuesta.put("timestamp", System.currentTimeMillis());
        return respuesta;
    }
}