package com.example.Backend.controller;

import com.example.Backend.model.Poste;
import com.example.Backend.repository.PosteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/postes")
public class PosteController  
{
    private  PosteRepository posteRepository;
    
    @Autowired
    public PosteController(PosteRepository posteRepository){
        
        this.posteRepository = posteRepository;
    
    }

    @GetMapping
    public ResponseEntity<List<Poste>> findAll()
    {
        
            try {
                List<Poste> liste = posteRepository.findAll();
                if (!liste.isEmpty()) {
                    return ResponseEntity.ok().body(liste);
                } else {
                    return ResponseEntity.status(404).body(liste);
                }
            }catch (Exception e) {
               return ResponseEntity.status(500).body(null);
            }
    
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Poste> findOne(@PathVariable Integer id)
    {
        
        try{
            Poste one = posteRepository.findById(id).orElse(null);
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
    public ResponseEntity<Poste> insert(@RequestBody Poste poste)
    {
        
        try{
            Poste creation = posteRepository.save(poste);
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
    public ResponseEntity<Poste> update(@PathVariable Integer id, @RequestBody Poste poste)
    {
        
        try{
            Optional<Poste> opt = posteRepository.findById(id);
            if(opt.isPresent()){
                Poste update = opt.get();
                poste.setId(update.getId());
                posteRepository.save(poste);
                if(poste != null){
                    return ResponseEntity.ok().body(poste);
                }else{
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body( poste);
                }
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body( poste);
            }
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id)
    {
        
        posteRepository.deleteById(id);
    
    }

    

    

}

