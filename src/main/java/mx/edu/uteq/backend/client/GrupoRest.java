package mx.edu.uteq.backend.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import mx.edu.uteq.backend.model.Entity.Profesor;
import mx.edu.uteq.backend.model.dto.Grupo;

@FeignClient(name = "grupos", url = "localhost:8081") //Modificar después
public interface GrupoRest {
    @GetMapping("/api/grupo/{id}")
    Grupo getById(@PathVariable int id);
}
