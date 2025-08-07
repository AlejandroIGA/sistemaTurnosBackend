package mx.edu.uteq.backend.model.Repositroty;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.edu.uteq.backend.model.Entity.ProfesorGrupo;

public interface ProfesorGrupoRepo extends JpaRepository<ProfesorGrupo,Integer>{
    void deleteByGrupoId(int id);
    boolean existsByGrupoId(int id);
    List<ProfesorGrupo> findByGrupoId(Integer grupoId);
}
