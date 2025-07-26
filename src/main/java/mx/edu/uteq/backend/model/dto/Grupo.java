package mx.edu.uteq.backend.model.dto;

import jakarta.persistence.Id;
import lombok.Data;

@Data
public class Grupo {
    @Id
    private int id;
    private String nombre;
    private String carrera;
    private boolean estado;
}
