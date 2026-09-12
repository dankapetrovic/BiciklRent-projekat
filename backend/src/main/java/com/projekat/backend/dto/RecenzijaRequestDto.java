package com.projekat.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RecenzijaRequestDto {

    @NotNull
    @Min(1)
    @Max(5)
    private Integer ocena;

    @Size(max = 1000)
    private String komentar;
}