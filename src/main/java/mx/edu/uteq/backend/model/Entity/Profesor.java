package mx.edu.uteq.backend.model.Entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import mx.edu.uteq.backend.model.dto.Grupo;

@Entity
@Data
@Table(name = "profesor", uniqueConstraints = {
    @UniqueConstraint(columnNames = "correo"),
    @UniqueConstraint(columnNames = "cubiculo")
})
public class Profesor {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nombre;
    private String correo;
    private String cubiculo;
    private boolean activo;
    private Integer idUsuario;

    @Transient
    private List<Grupo> grupos;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "profesor_id")
    List<ProfesorGrupo> profesoresGrupos;

    public void addProfesoresGrupos (ProfesorGrupo pg){
        profesoresGrupos.add(pg);
    }

    public void removeProfesoresGrupos (ProfesorGrupo pg){
        profesoresGrupos.remove(pg);
    }
}
