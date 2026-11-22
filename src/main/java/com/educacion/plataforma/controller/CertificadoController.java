/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.educacion.plataforma.controller;

import com.educacion.plataforma.entity.Certificado;
import com.educacion.plataforma.service.CertificadoService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Willi
 */

@RestController
@RequestMapping("/api/certificados")
public class CertificadoController {

    private final CertificadoService certificadoService;

    public CertificadoController(CertificadoService certificadoService) {
        this.certificadoService = certificadoService;
    }

    @PostMapping("/solicitar")
    @ResponseStatus(HttpStatus.CREATED)
    public Certificado solicitar(@RequestParam Long cursoId, @RequestParam Long estudianteId) {
        return certificadoService.solicitar(cursoId, estudianteId);
    }

    @GetMapping("/estudiante/{estudianteId}")
    public List<Certificado> porEstudiante(@PathVariable Long estudianteId) {
        return certificadoService.listarPorEstudiante(estudianteId);
    }

    @GetMapping("/codigo/{codigo}")
    public Certificado porCodigo(@PathVariable String codigo) {
        return certificadoService.buscarPorCodigo(codigo);
    }
}