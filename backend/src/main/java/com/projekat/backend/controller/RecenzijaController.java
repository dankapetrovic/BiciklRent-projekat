package com.projekat.backend.controller;

// Prilagodi import na tacnu lokaciju tvog JwtUtil-a
import com.projekat.backend.security.JwtUtil;
import com.projekat.backend.dto.RecenzijaDto;
import com.projekat.backend.dto.RecenzijaRequestDto;
import com.projekat.backend.service.RecenzijaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RecenzijaController {

    private final RecenzijaService recenzijaService;
    private final JwtUtil jwtUtil;

    @GetMapping("/bicikli/{biciklId}/recenzije")
    public List<RecenzijaDto> getRecenzije(@PathVariable Long biciklId) {
        return recenzijaService.getRecenzijeZaBicikl(biciklId);
    }

    @GetMapping("/bicikli/{biciklId}/recenzije/prosek")
    public Double getProsecnaOcena(@PathVariable Long biciklId) {
        return recenzijaService.getProsecnaOcena(biciklId);
    }

    @PostMapping("/bicikli/{biciklId}/recenzije")
    public ResponseEntity<RecenzijaDto> createRecenzija(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long biciklId,
            @Valid @RequestBody RecenzijaRequestDto requestDto) {
        Long klijentId = jwtUtil.requireUserId(authHeader, "KLIJENT");
        return new ResponseEntity<>(recenzijaService.createRecenzija(biciklId, klijentId, requestDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/recenzije/{id}")
    public ResponseEntity<Void> deleteRecenzija(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        Long klijentId = jwtUtil.requireUserId(authHeader, "KLIJENT");
        recenzijaService.deleteRecenzija(id, klijentId);
        return ResponseEntity.noContent().build();
    }
}