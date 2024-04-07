package com.example.Backend.controller;
import com.example.Backend.model.Etudiant;
import com.example.Backend.repository.EtudiantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/etudiants")
public class EtudiantController  
{
    private  EtudiantRepository etudiantRepository;
    
    @Autowired
    public EtudiantController(EtudiantRepository etudiantRepository){
        
        this.etudiantRepository = etudiantRepository;
    
    }

    @GetMapping
    public ResponseEntity<Page<Etudiant>> findAll(Pageable pageable)
    {
        
            try {
                Page<Etudiant> page = etudiantRepository.findAll(pageable);
                if (page.hasContent()) {
                    return ResponseEntity.ok().body(page);
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                }
            }catch (Exception e) {
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
    
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Etudiant> findOne(@PathVariable Integer id)
    {
        
        try{
            Etudiant one = etudiantRepository.findById(id).orElse(null);
            if(one != null){
                return ResponseEntity.ok().body(one);
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(one);
            }
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    
    }

    @PostMapping
    public ResponseEntity<Etudiant> insert(@RequestBody Etudiant etudiant)
    {
        
        try{
            Etudiant creation = etudiantRepository.save(etudiant);
            if(creation != null){
               return ResponseEntity.status(HttpStatus.CREATED).body(creation);
            }else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(creation);
            }
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    
    }

    @PutMapping("/{id}")
    public ResponseEntity<Etudiant> update(@PathVariable Integer id, @RequestBody Etudiant etudiant)
    {
        
        try{
            Optional<Etudiant> opt = etudiantRepository.findById(id);
            if(opt.isPresent()){
                Etudiant update = opt.get();
                etudiant.setId(update.getId());
                etudiantRepository.save(etudiant);
                if(etudiant != null){
                    return ResponseEntity.ok().body(etudiant);
                }else{
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body( etudiant);
                }
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body( etudiant);
            }
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id)
    {
        
        etudiantRepository.deleteById(id);
    
    }

    

    

}

