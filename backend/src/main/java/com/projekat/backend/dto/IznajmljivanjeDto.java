package com.projekat.backend.dto;

import java.time.LocalDate;

public class IznajmljivanjeDto {
    private Long id;
    private LocalDate datumPocetka;
    private LocalDate datumKraja;
    private Double cena;
    private Long biciklId;
    private String proizvodjacNaziv;
    private String kategorijaNaziv;
    private String rezolucija;
    private String opis;

    public IznajmljivanjeDto() {
    }

    public IznajmljivanjeDto(Long id, LocalDate datumPocetka, LocalDate datumKraja, Double cena, Long biciklId,
                              String proizvodjacNaziv, String kategorijaNaziv, String rezolucija, String opis) {
        this.id = id;
        this.datumPocetka = datumPocetka;
        this.datumKraja = datumKraja;
        this.cena = cena;
        this.biciklId = biciklId;
        this.proizvodjacNaziv = proizvodjacNaziv;
        this.kategorijaNaziv = kategorijaNaziv;
        this.rezolucija = rezolucija;
        this.opis = opis;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDatumPocetka() {
        return datumPocetka;
    }

    public void setDatumPocetka(LocalDate datumPocetka) {
        this.datumPocetka = datumPocetka;
    }

    public LocalDate getDatumKraja() {
        return datumKraja;
    }

    public void setDatumKraja(LocalDate datumKraja) {
        this.datumKraja = datumKraja;
    }

    public Double getCena() {
        return cena;
    }

    public void setCena(Double cena) {
        this.cena = cena;
    }

    public Long getBiciklId() {
        return biciklId;
    }

    public void setBiciklId(Long biciklId) {
        this.biciklId = biciklId;
    }

    public String getProizvodjacNaziv() {
        return proizvodjacNaziv;
    }

    public void setProizvodjacNaziv(String proizvodjacNaziv) {
        this.proizvodjacNaziv = proizvodjacNaziv;
    }

    public String getKategorijaNaziv() {
        return kategorijaNaziv;
    }

    public void setKategorijaNaziv(String kategorijaNaziv) {
        this.kategorijaNaziv = kategorijaNaziv;
    }

    public String getRezolucija() {
        return rezolucija;
    }

    public void setRezolucija(String rezolucija) {
        this.rezolucija = rezolucija;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }
}
