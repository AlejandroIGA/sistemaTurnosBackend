package mx.edu.uteq.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.edu.uteq.backend.client.GrupoRest;
import mx.edu.uteq.backend.model.Entity.Profesor;
import mx.edu.uteq.backend.model.Entity.ProfesorGrupo;
import mx.edu.uteq.backend.model.Repositroty.ProfesorGrupoRepo;
import mx.edu.uteq.backend.model.Repositroty.ProfesorRepo;
import mx.edu.uteq.backend.model.dto.Grupo;

@Service
public class ProfesorService {
    @Autowired
    private ProfesorRepo repo;

    @Autowired
    private GrupoRest grupoClient;

    @Autowired
    private ProfesorGrupoRepo profesorGrupoRepo;


    @Transactional(readOnly = true)
    public List<Profesor> getAll(){
        
        List<Profesor> profesores = repo.findAll();

        for (Profesor profesor : profesores) {
            List<Grupo> grupos = new ArrayList<>();
            for (ProfesorGrupo profesorGrupo : profesor.getProfesoresGrupos()) {
                grupos.add(grupoClient.getById(profesorGrupo.getGrupoId()));
            }
            profesor.setGrupos(grupos);
        }

        return profesores;
    }

    @Transactional(readOnly = true)
    public Optional<Profesor> getById(int id){
        Optional<Profesor> opt =  repo.findById(id);
        if(opt.isPresent()){
            Profesor profesor = opt.get();
            List <Grupo> grupos =  new ArrayList<>();

            for (ProfesorGrupo profesorGrupo : profesor.getProfesoresGrupos()) {
                grupos.add(grupoClient.getById(profesorGrupo.getGrupoId()));
            }
            profesor.setGrupos(grupos);
            return Optional.of(profesor);
        }
        return opt;
    }

    @Transactional(readOnly = true)
    public Optional<Profesor> getByIdUsuario(int id){
        return repo.findByIdUsuario(id);
    }

    @Transactional(readOnly = true)
    public List<Profesor> getByName(String nombre){
        List<Profesor> profesores =  repo.findByNombreContainingIgnoreCaseAndActivo(nombre, true);
        for (Profesor profesor : profesores) {
            List<Grupo> grupos = new ArrayList<>();
            for (ProfesorGrupo profesorGrupo : profesor.getProfesoresGrupos()) {
                grupos.add(grupoClient.getById(profesorGrupo.getGrupoId()));
            }
            profesor.setGrupos(grupos);
        }

        return profesores;
    }

    @Transactional(readOnly = true)
    public List<Profesor> getByEmail(String correo){
        List<Profesor> profesores = repo.findByCorreoContainingIgnoreCaseAndActivo(correo, true);
        for (Profesor profesor : profesores) {
            List<Grupo> grupos = new ArrayList<>();
            for (ProfesorGrupo profesorGrupo : profesor.getProfesoresGrupos()) {
                grupos.add(grupoClient.getById(profesorGrupo.getGrupoId()));
            }
            profesor.setGrupos(grupos);
        }

        return profesores;
    }

    @Transactional
    public Profesor save(Profesor profesor) throws RuntimeException {
        // Validar que correo y cubículo no estén vacíos
        if (profesor.getCorreo() == null || profesor.getCorreo().trim().isEmpty()) {
            throw new RuntimeException("El correo es obligatorio");
        }
        if (profesor.getCubiculo() == null || profesor.getCubiculo().trim().isEmpty()) {
            throw new RuntimeException("El cubículo es obligatorio");
        }
        
        // Validar unicidad del correo
        if (repo.existsByCorreo(profesor.getCorreo())) {
            throw new RuntimeException("Ya existe un profesor con ese correo: " + profesor.getCorreo());
        }
        
        // Validar unicidad del cubículo
        if (repo.existsByCubiculo(profesor.getCubiculo())) {
            throw new RuntimeException("Ya existe un profesor con ese cubículo: " + profesor.getCubiculo());
        }
        
        return repo.save(profesor);
}

    @Transactional
    public Profesor update(int id, Profesor profesor) throws RuntimeException {
        Optional<Profesor> existingOpt = repo.findById(id);
        if (existingOpt.isEmpty()) {
            throw new RuntimeException("Profesor no encontrado");
        }
        
        // Validar que correo y cubículo no estén vacíos
        if (profesor.getCorreo() == null || profesor.getCorreo().trim().isEmpty()) {
            throw new RuntimeException("El correo es obligatorio");
        }
        if (profesor.getCubiculo() == null || profesor.getCubiculo().trim().isEmpty()) {
            throw new RuntimeException("El cubículo es obligatorio");
        }
        
        // Validar unicidad excluyendo el registro actual
        if (repo.existsByCorreoAndIdNot(profesor.getCorreo(), id)) {
            throw new RuntimeException("Ya existe otro profesor con ese correo: " + profesor.getCorreo());
        }
        
        if (repo.existsByCubiculoAndIdNot(profesor.getCubiculo(), id)) {
            throw new RuntimeException("Ya existe otro profesor con ese cubículo: " + profesor.getCubiculo());
        }
        
        profesor.setId(id);
        return repo.save(profesor);
    }

    @Transactional
    public void deleteById(int id){
        Optional<Profesor> opt = repo.findById(id);
        if (opt.isPresent()){
            Profesor profesor = opt.get();
            profesor.setActivo(false);
            repo.save(profesor);
        }
    }

    @Transactional
    public boolean addProfesorGrupo (int profesorId, int grupoid){

        Optional<Profesor> opt = repo.findById(profesorId);//¿es necesario?
        if(opt.isPresent()){
            //Se busca al profesor para saber que Id de profesor corresponde al grupo ingresado
            Profesor profesor = opt.get();
            //Se busca al grupo para saber si todavía existe, al ser un sistema distribuido puede eliminarse el regstro sin ser notificados
            Grupo grupo = grupoClient.getById(grupoid);
            if(grupo != null){
                ProfesorGrupo profesorGrupo = new ProfesorGrupo();
                profesorGrupo.setGrupoId(grupoid);

                profesor.addProfesoresGrupos(profesorGrupo);

                repo.save(profesor);

                return true;
            }
        }
        return false;
    }

    @Transactional
    public boolean removeProfesorGrupo (int profesorId, int grupoId){

        Optional<Profesor> opt = repo.findById(profesorId);//¿es necesario?
        if(opt.isPresent()){
            //Se busca al profesor para saber que Id de profesor corresponde al grupo ingresado
            Profesor profesor = opt.get();
            //Se busca al grupo para saber si todavía existe, al ser un sistema distribuido puede eliminarse el regstro sin ser notificados
            Grupo grupo = grupoClient.getById(grupoId);
            if(grupo != null){
                boolean removed = profesor.getProfesoresGrupos().removeIf(
                pg -> pg.getGrupoId() == grupoId
            );
            
            if (removed) {
                repo.save(profesor);
                return true;
            }
            }
        }
        return false;
    }

    @Transactional
public boolean addProfesoresGrupos(int profesorId, List<Grupo> grupos) {
    // Validaciones iniciales
    if (grupos == null || grupos.isEmpty()) {
        return false;
    }
    
    Optional<Profesor> opt = repo.findById(profesorId);
    if (opt.isEmpty()) {
        return false;
    }
    
    Profesor profesor = opt.get();
    List<ProfesorGrupo> profesoresGrupos = new ArrayList<>();
    
    // Validar existencia y preparar objetos en una sola pasada
    for (Grupo grupoInput : grupos) {
        Grupo grupo = grupoClient.getById(grupoInput.getId());
        if (grupo == null) {
            return false; // Transacción se revierte automáticamente
        }
        
        // Preparar el objeto ProfesorGrupo
        ProfesorGrupo profesorGrupo = new ProfesorGrupo();
        profesorGrupo.setGrupoId(grupo.getId());
        profesoresGrupos.add(profesorGrupo);
    }
    
    // Si llegamos aquí, todos existen, agregar todos de una vez
    for (ProfesorGrupo profesorGrupo : profesoresGrupos) {
        profesor.addProfesoresGrupos(profesorGrupo);
    }
    
    repo.save(profesor);
    return true;
}

    @Transactional
    public boolean removeProfesoresGrupos (int profesorId, List<Grupo> grupos){
        boolean allExist = false;
        Optional<Profesor> opt = repo.findById(profesorId);
        if(opt.isPresent()){
            Profesor profesor = opt.get();
            for (int i=0; i<grupos.size(); i++){
                Grupo grupo = grupoClient.getById(grupos.get(i).getId());

                if(grupo == null){
                    allExist = false;
                    break;
                }
                else{
                    allExist = true;
                    ProfesorGrupo profesorGrupo = new ProfesorGrupo();
                    profesorGrupo.setGrupoId(grupo.getId());

                    profesor.removeProfesoresGrupos(profesorGrupo);
                }
            }
            if(allExist){
                repo.save(profesor);
                return true;
            }
        }
        return false;
    }

    @Transactional
    public boolean removeGrupo (int grupoId){
        boolean status = profesorGrupoRepo.existsByGrupoId(grupoId);
        if(status){
            profesorGrupoRepo.deleteByGrupoId(grupoId);
            return true;
        }
        return false;
    }

    public List<Profesor> findByGrupoId(Integer grupoId) {
        List<Profesor> todosLosProfesores = repo.findAll();

        List<Profesor> profesoresDelGrupo = todosLosProfesores.stream()
            .filter(profesor -> {
                return profesor.getProfesoresGrupos().stream()
                    .anyMatch(pg -> pg.getGrupoId() == grupoId);
            })
            .collect(Collectors.toList());

        return profesoresDelGrupo;
    }

    public Optional<Profesor> findByIdUsuario(Integer idUsuario) {
        return repo.findByIdUsuario(idUsuario);
    }

}
