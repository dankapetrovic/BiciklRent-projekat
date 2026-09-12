package com.projekat.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecenzijaDto {
    private Long id;
    private Integer ocena;
    private String komentar;
    private LocalDate datumKreiranja;
    private Long biciklId;
    private Long klijentId;
    private String klijentIme;
    private String klijentPrezime;
}