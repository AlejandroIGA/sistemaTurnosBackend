package mx.edu.uteq.backend.model.Repositroty;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.edu.uteq.backend.model.Entity.Profesor;

public interface ProfesorRepo extends JpaRepository <Profesor, Integer> {
    List<Profesor> findByNombreContainingIgnoreCaseAndActivo(String nombre, boolean activo);
    List<Profesor> findByCorreoContainingIgnoreCaseAndActivo(String correo, boolean activo);
    

    // Métodos para verificar unicidad
    boolean existsByCorreo(String correo);
    boolean existsByCubiculo(String cubiculo);
    
    // Métodos para encontrar por correo y cubículo
    Optional<Profesor> findByCorreo(String correo);
    Optional<Profesor> findByCubiculo(String cubiculo);
    
    // Para validaciones en actualizaciones (excluir el propio registro)
    boolean existsByCorreoAndIdNot(String correo, int id);
    boolean existsByCubiculoAndIdNot(String cubiculo, int id);

    Optional<Profesor> findByIdUsuario(Integer idUsuario);
}
