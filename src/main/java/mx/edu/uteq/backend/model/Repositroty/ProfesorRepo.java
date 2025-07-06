package mx.edu.uteq.backend.model.Repositroty;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.edu.uteq.backend.model.Entity.Profesor;

public interface ProfesorRepo extends JpaRepository <Profesor, Integer> {
    List<Profesor> findByNombreContainingIgnoreCaseAndActivo(String nombre, boolean activo);

    List<Profesor> findByCorreoContainingIgnoreCaseAndActivo(String correo, boolean activo);
}
