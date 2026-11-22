/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.educacion.plataforma.config;


import com.educacion.plataforma.entity.Actividad;
import com.educacion.plataforma.entity.Curso;
import com.educacion.plataforma.entity.Oferta;
import com.educacion.plataforma.entity.Rol;
import com.educacion.plataforma.entity.Unidad;
import com.educacion.plataforma.entity.Usuario;
import com.educacion.plataforma.repository.ActividadRepository;
import com.educacion.plataforma.repository.CursoRepository;
import com.educacion.plataforma.repository.OfertaRepository;
import com.educacion.plataforma.repository.RolRepository;
import com.educacion.plataforma.repository.UnidadRepository;
import com.educacion.plataforma.repository.UsuarioRepository;
import java.time.LocalDate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.educacion.plataforma.entity.Entrega;
import com.educacion.plataforma.entity.Inscripcion;
import com.educacion.plataforma.repository.EntregaRepository;
import com.educacion.plataforma.repository.InscripcionRepository;
/**
 *
 * @author Willi
 */



@Component
public class CargadorDatos implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final OfertaRepository ofertaRepository;
    private final CursoRepository cursoRepository;
    private final UnidadRepository unidadRepository;
    private final ActividadRepository actividadRepository;
    private final InscripcionRepository inscripcionRepository;
    private final EntregaRepository entregaRepository;

    public CargadorDatos(RolRepository rolRepository,
            UsuarioRepository usuarioRepository,
            OfertaRepository ofertaRepository,
            CursoRepository cursoRepository,
            UnidadRepository unidadRepository,
            ActividadRepository actividadRepository,
            InscripcionRepository inscripcionRepository,
            EntregaRepository entregaRepository) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.ofertaRepository = ofertaRepository;
        this.cursoRepository = cursoRepository;
        this.unidadRepository = unidadRepository;
        this.actividadRepository = actividadRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.entregaRepository = entregaRepository;
    }
        
    @Override
    public void run(String... args) {
        // Si ya hay roles, no se carga nada (evita duplicados)
        if (rolRepository.count() > 0) {
            return;
        }

        // Roles
        Rol docente = new Rol();
        docente.setNombre("DOCENTE");
        docente.setDescripcion("Crea y gestiona cursos");
        docente = rolRepository.save(docente);

        Rol estudiante = new Rol();
        estudiante.setNombre("ESTUDIANTE");
        estudiante.setDescripcion("Toma cursos y realiza actividades");
        estudiante = rolRepository.save(estudiante);
        
        Rol coordinadorRol = new Rol();
        coordinadorRol.setNombre("COORDINADOR");
        coordinadorRol.setDescripcion("Publica cursos, asigna docentes y genera reportes");
        coordinadorRol = rolRepository.save(coordinadorRol);

        // Usuarios
        Usuario carlos = new Usuario();
        carlos.setNombre("Carlos Pérez");
        carlos.setCorreo("carlos@correo.com");

        carlos.setRol(docente);
        carlos = usuarioRepository.save(carlos);
        
        Usuario elena = crearEstudiante("Elena Mora", "elena@correo.com", coordinadorRol);
        Usuario ana = crearEstudiante("Ana Gómez", "ana@correo.com", estudiante);
        Usuario luis = crearEstudiante("Luis Torres", "luis@correo.com", estudiante);
        Usuario marta = crearEstudiante("Marta Díaz", "marta@correo.com", estudiante);
        Usuario pedro = crearEstudiante("Pedro Ruiz", "pedro@correo.com", estudiante);
        crearEstudiante("Sofía Vega", "sofia@correo.com", estudiante); // sin inscribir

        // Oferta
        Oferta oferta = new Oferta();
        oferta.setNombre("Diplomado en Programación");
        oferta.setDescripcion("Programa de formación en desarrollo de software");
        oferta.setFechaInicio(LocalDate.of(2026, 11, 1));
        oferta.setFechaFin(LocalDate.of(2027, 3, 30));
        oferta.setEstado("ACTIVA");
        oferta = ofertaRepository.save(oferta);

        // Curso
        Curso curso = new Curso();
        curso.setNombre("Java Básico");
        curso.setDescripcion("Fundamentos de programación en Java");
        curso.setEstado("ACTIVO");
        curso.setOferta(oferta);
        curso.setDocente(carlos);
        curso.setObjetivos("Comprender los fundamentos de Java y escribir programas básicos");
        curso.setResultadosAprendizaje("El estudiante declara variables, usa condicionales y ciclos");
        curso.setCupos(4);
        curso.setFechaInicio(LocalDate.of(2026, 11, 1));
        curso.setFechaFin(LocalDate.of(2027, 3, 30));
        curso.setPublicacion("PUBLICADO");        
        curso = cursoRepository.save(curso);

        // Unidad
        Unidad unidad = new Unidad();
        unidad.setTitulo("Variables y tipos de datos");
        unidad.setDescripcion("Introducción a variables en Java");
        unidad.setOrden(1);

        unidad.setCurso(curso);
        unidad = unidadRepository.save(unidad);

        Actividad taller = new Actividad();
        taller.setTitulo("Taller 1: ejercicios de variables");
        taller.setDescripcion("Resolver 10 ejercicios de declaración de variables");
        taller.setTipo("TALLER");
        taller.setPuntajeMaximo(5.0);
        taller.setPorcentaje(60.0);        
        taller.setFechaLimite(LocalDate.of(2026, 12, 15));
        taller.setUnidad(unidad);
        taller = actividadRepository.save(taller);

        Actividad quiz = new Actividad();
        quiz.setTitulo("Quiz de tipos de datos");
        quiz.setTipo("QUIZ");
        quiz.setPuntajeMaximo(3.0);
        quiz.setPorcentaje(40.0);          
        quiz.setUnidad(unidad);
        quiz = actividadRepository.save(quiz);

        // Inscripciones (Sofía queda sin inscribir)
        inscribir(curso, ana);
        inscribir(curso, luis);
        inscribir(curso, marta);
        inscribir(curso, pedro);

        // Entregas calificadas por Carlos (Pedro no entrega nada)
        entregar(taller, ana, 4.5, "Buen trabajo, revisa el ejercicio 7");
        entregar(quiz, ana, 3.0, "Perfecto");
        entregar(taller, luis, 3.0, "Faltan ejercicios por resolver");
        entregar(quiz, luis, 2.0, "Repasa los tipos primitivos");
        entregar(taller, marta, 5.0, "Excelente");

        System.out.println(">>> Datos iniciales cargados correctamente");
    }
    
        private Usuario crearEstudiante(String nombre, String correo, Rol rol) {
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setCorreo(correo);
        u.setRol(rol);
        return usuarioRepository.save(u);
    }

    private void inscribir(Curso curso, Usuario estudiante) {
        Inscripcion i = new Inscripcion();
        i.setCurso(curso);
        i.setEstudiante(estudiante);
        i.setFechaInscripcion(LocalDate.now());
        i.setEstado("ACTIVA");
        inscripcionRepository.save(i);
    }

    private void entregar(Actividad actividad, Usuario estudiante, double nota, String comentario) {
        Entrega e = new Entrega();
        e.setActividad(actividad);
        e.setEstudiante(estudiante);
        e.setContenido("Entrega de " + estudiante.getNombre());
        e.setFechaEntrega(LocalDate.now());
        e.setEstado("CALIFICADA");
        e.setNota(nota);
        e.setRetroalimentacion(comentario);
        e.setFechaCalificacion(LocalDate.now());
        entregaRepository.save(e);
    }
}