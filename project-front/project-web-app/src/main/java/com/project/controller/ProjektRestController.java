package com.project.controller;

import java.net.URI;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.project.model.Projekt;
import com.project.service.ProjektService;

@RestController
@RequestMapping("/api")
public class ProjektRestController {
    private final ProjektService projektService;

    @Autowired
    public ProjektRestController(ProjektService projektService) {
        this.projektService = projektService;
    }

    @GetMapping("/projekty/{projektId}")
    ResponseEntity<Projekt> getProjekt(@PathVariable("projektId") Integer projektId) {
        return ResponseEntity.of(projektService.getProjekt(projektId));
    }

    @PostMapping("/projekty")
    ResponseEntity<Void> createProjekt(@Valid @RequestBody Projekt projekt) {
        Projekt createdProjekt = projektService.setProjekt(projekt);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{projektId}")
                .buildAndExpand(createdProjekt.getProjektId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/projekty/{projektId}")
    public ResponseEntity<Void> updateProjekt(@Valid @RequestBody Projekt projekt,
            @PathVariable("projektId") Integer projektId) {
        if (!projektId.equals(projekt.getProjektId())) {
            return ResponseEntity.badRequest().build();
        }
        projektService.setProjekt(projekt);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/projekty/{projektId}")
    public ResponseEntity<Void> deleteProjekt(@PathVariable("projektId") Integer projektId) {
        projektService.deleteProjekt(projektId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/projekty")
    Page<Projekt> getProjekty(Pageable pageable) {
        return projektService.getProjekty(pageable);
    }

    @GetMapping(value = "/projekty", params = "nazwa")
    public Page<Projekt> getProjektyByNazwa(@RequestParam String nazwa, Pageable pageable) {
        return projektService.searchByNazwa(nazwa, pageable);
    }
}