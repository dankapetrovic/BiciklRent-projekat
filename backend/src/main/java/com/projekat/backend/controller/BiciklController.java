package com.projekat.backend.controller;

import com.projekat.backend.dto.BiciklDto;
import com.projekat.backend.dto.BiciklRequestDto;
import com.projekat.backend.dto.ZauzetostDto;
import com.projekat.backend.security.JwtUtil;
import com.projekat.backend.service.BiciklService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BiciklController {

    private final BiciklService biciklService;
    private final JwtUtil jwtUtil;

    @GetMapping("/bicikli")
    public List<BiciklDto> getBicikli(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate datumOd,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate datumDo) {
        return biciklService.getBicikli(datumOd, datumDo);
    }

    @GetMapping("/bicikli/{id}/zauzetost")
    public List<ZauzetostDto> getZauzetost(@PathVariable Long id) {
        return biciklService.getZauzetPeriodi(id);
    }

    @PostMapping("/bicikli")
    public ResponseEntity<BiciklDto> createBicikl(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody BiciklRequestDto requestDto) {
        jwtUtil.requireUserId(authHeader, "ZAPOSLENI");
        return new ResponseEntity<>(biciklService.createBicikl(requestDto), HttpStatus.CREATED);
    }

    @PutMapping("/bicikli/{id}")
    public BiciklDto updateBicikl(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id,
            @RequestBody BiciklRequestDto requestDto) {
        jwtUtil.requireUserId(authHeader, "ZAPOSLENI");
        return biciklService.updateBicikl(id, requestDto);
    }

    @DeleteMapping("/bicikli/{id}")
    public ResponseEntity<Void> deleteBicikl(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        jwtUtil.requireUserId(authHeader, "ZAPOSLENI");
        biciklService.deleteBicikl(id);
        return ResponseEntity.noContent().build();
    }
}