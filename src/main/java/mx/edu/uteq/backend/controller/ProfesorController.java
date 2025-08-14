package mx.edu.uteq.backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mx.edu.uteq.backend.model.Entity.Profesor;
import mx.edu.uteq.backend.model.dto.Grupo;
import mx.edu.uteq.backend.service.ProfesorService;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;



@CrossOrigin("http://localhost:5173/")
@RestController
@RequestMapping("/api/profesor")
public class ProfesorController {
    @Autowired
    private ProfesorService profesorService;

    @GetMapping
    public List<Profesor> getAll(@RequestParam boolean activos) {
        if(activos){
            return profesorService.getAll().stream().filter(Profesor::isActivo).toList();
        }
        return profesorService.getAll();
    }

    @GetMapping("/my/usuario/{id}")
    public ResponseEntity<?> getByIdUsuario(@PathVariable int id) {
        return profesorService.getByIdUsuario(id)
        .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        return profesorService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/nombre/{nombre}")
    public List<Profesor> getByName(@PathVariable String nombre) {
        return profesorService.getByName(nombre);
    }
    
    @GetMapping("/correo/{correo}")
    public List<Profesor> getByEmail(@PathVariable String correo) {
        return profesorService.getByEmail(correo);
    }


    @PostMapping
    public ResponseEntity<?> create(@RequestBody Profesor profesor) {
        try {
            Profesor profesorDB = profesorService.save(profesor);
            return ResponseEntity.status(HttpStatus.CREATED).body(profesorDB);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error interno del servidor");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editProfesor (@PathVariable int id, @RequestBody Profesor profesor) {
        try {
        Profesor profesorDB = profesorService.update(id, profesor);
        return ResponseEntity.ok(profesorDB);
    } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Error interno del servidor");
    }
}
    
    @PostMapping("/{idProfesor}/agregar/grupos")
    public ResponseEntity<?> addProfesorGrupo(@PathVariable int idProfesor, @RequestBody List<Grupo> grupos) {        
        //List<Grupo> grupos = request.get("grupos");
        boolean status = profesorService.addProfesoresGrupos(idProfesor, grupos);
        if(status){
            return ResponseEntity.ok("Grupos asignados exitosamente.");
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Algunos grupos no pudieron ser agregados o no existen.");
        }
    }
    
    @PutMapping("/{idProfesor}/eliminar/grupos")
    public ResponseEntity<?> removeProfesorGrupo(@PathVariable int idProfesor, @RequestBody List<Grupo> grupos) {
        boolean status = profesorService.removeProfesoresGrupos(idProfesor, grupos);
        if(status){
            return ResponseEntity.ok("Grupos eliminados exitosamente.");
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Algunos grupos no pudieron ser eliminados o no existen.");
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> borrar(@PathVariable int id) {
        Optional<Profesor> opt = profesorService.getById(id);
        if (opt.isPresent()) {
            profesorService.deleteById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/eliminar/grupos/{id}")
    public ResponseEntity<?> borrarGrupo(@PathVariable int id) {
        boolean status = profesorService.removeGrupo(id);
        if(status){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/eliminar/profesor/{profesorId}/grupo/{grupoId}")
    public ResponseEntity<?> borrarGrupoProfesor(@PathVariable int profesorId, @PathVariable int grupoId){
        boolean status = profesorService.removeProfesorGrupo(profesorId, grupoId);
        if(status){
            return ResponseEntity.ok("Grupo eliminado exitosamente del profesor.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body("No se pudo eliminar el grupo o no existe la relación.");
    }
    
    // Conexión con solicitudes para obtener profesores de un grupo
    @GetMapping("/grupo/{grupoId}")
    public ResponseEntity<List<Profesor>> getProfesoresByGrupo(@PathVariable int grupoId) {
        try {
            List<Profesor> profesores = profesorService.findByGrupoId(grupoId);

            if (profesores != null && !profesores.isEmpty()) {
                return ResponseEntity.ok(profesores);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("Error al obtener los profesores del grupo: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<Map<String, Integer>> getIdsByUsuarioId(@PathVariable("idUsuario") Integer idUsuario) {
        Optional<Profesor> optionalProfesor = profesorService.findByIdUsuario(idUsuario);

        if (optionalProfesor.isPresent()) {
            Profesor profesor = optionalProfesor.get();
            Map<String, Integer> response = new HashMap<>();
            response.put("idProfesor", profesor.getId());
            response.put("idUsuario", profesor.getIdUsuario());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
