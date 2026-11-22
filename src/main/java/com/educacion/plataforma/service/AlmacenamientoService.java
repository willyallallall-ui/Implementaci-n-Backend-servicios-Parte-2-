/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.educacion.plataforma.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
/**
 *
 * @author Willi
 */


@Service
public class AlmacenamientoService {

    private final Path carpeta;

    public AlmacenamientoService(@Value("${app.uploads.dir}") String dir) {
        this.carpeta = Paths.get(dir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.carpeta);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo crear la carpeta de archivos", e);
        }
    }

    // Guarda el archivo con nombre único y devuelve ese nombre
    public String guardar(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe adjuntar un archivo");
        }
        String original = archivo.getOriginalFilename() == null ? "archivo" : archivo.getOriginalFilename();
        // Limpia el nombre para evitar rutas maliciosas (../)
        String limpio = original.replaceAll("[^a-zA-Z0-9._-]", "_");
        String nombreGuardado = UUID.randomUUID().toString().substring(0, 8) + "_" + limpio;
        try (InputStream in = archivo.getInputStream()) {
            Files.copy(in, carpeta.resolve(nombreGuardado), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "No se pudo guardar el archivo");
        }
        return nombreGuardado;
    }

    public Resource cargar(String nombreGuardado) {
        try {
            Path ruta = carpeta.resolve(nombreGuardado).normalize();
            Resource recurso = new UrlResource(ruta.toUri());
            if (!recurso.exists() || !ruta.startsWith(carpeta)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "El archivo ya no está disponible en el servidor");
            }
            return recurso;
        } catch (java.net.MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Archivo no encontrado");
        }
    }
}