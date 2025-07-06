package mx.edu.uteq.backend.service;

import java.util.List;
import java.util.Optional;

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
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Profesor> getById(int id){
        return repo.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Profesor> getByName(String nombre){
        return repo.findByNombreContainingIgnoreCaseAndActivo(nombre, true);
    }

    @Transactional(readOnly = true)
    public List<Profesor> getByEmail(String correo){
        return repo.findByCorreoContainingIgnoreCaseAndActivo(correo, true);
    }

    @Transactional
    public Profesor save(Profesor profesor){
        return repo.save(profesor);
    }

    @Transactional
    public void deleteById(int id){
        repo.deleteById(id);
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
    public boolean removeProfesorGrupo (int profesorId, int grupoid){

        Optional<Profesor> opt = repo.findById(profesorId);//¿es necesario?
        if(opt.isPresent()){
            //Se busca al profesor para saber que Id de profesor corresponde al grupo ingresado
            Profesor profesor = opt.get();
            //Se busca al grupo para saber si todavía existe, al ser un sistema distribuido puede eliminarse el regstro sin ser notificados
            Grupo grupo = grupoClient.getById(grupoid);
            if(grupo != null){
                ProfesorGrupo profesorGrupo = new ProfesorGrupo();
                profesorGrupo.setGrupoId(grupoid);

                profesor.removeProfesoresGrupos(profesorGrupo);

                repo.save(profesor);

                return true;
            }
        }
        return false;
    }

    @Transactional
    public boolean addProfesoresGrupos (int profesorId, List<Grupo> grupos){
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

                    profesor.addProfesoresGrupos(profesorGrupo);
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

}
