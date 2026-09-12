
package com.projekat.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Recenzija {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(1)
    @Max(5)
    private Integer ocena;

    @Column(length = 1000)
    private String komentar;

    private LocalDate datumKreiranja;

    @ManyToOne
    @JoinColumn(name = "bicikl_id")
    private Bicikl bicikl;

    @ManyToOne
    @JoinColumn(name = "klijent_id")
    private Klijent klijent;

    public Recenzija() {
    }

    @PrePersist
    public void prePersist() {
        if (datumKreiranja == null) {
            datumKreiranja = LocalDate.now();
        }
    }
}
