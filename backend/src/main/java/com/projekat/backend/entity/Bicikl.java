package com.projekat.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Bicikl {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate datumKupovine;
    private String napomena;
    private Boolean dostupan;

    @ManyToOne
    @JoinColumn(name = "proizvodjac_id")
    private Proizvodjac proizvodjac;

    @ManyToOne
    @JoinColumn(name = "kategorija_id")
    private Kategorija kategorija;

    private String tipBicikle;
    private String velicinaRama;
    private String brojBrzina;
    private String tipKocnica;
    private String precnikTocka;
    private String tezina;

    @Column(length = 1000)
    private String opis;

    @OneToMany(mappedBy = "bicikl")
    private List<Iznajmljivanje> iznajmljivanja = new ArrayList<>();

    public Bicikl() {
    }
}