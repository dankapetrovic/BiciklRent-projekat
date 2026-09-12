package com.projekat.backend.dto;

import java.time.LocalDate;

public class BiciklRequestDto {
    private String proizvodjacNaziv;
    private Long kategorijaId;
    private LocalDate datumKupovine;
    private String napomena;
    private Boolean dostupan;
    private String tipBicikle;
    private String velicinaRama;
    private String brojBrzina;
    private String tipKocnica;
    private String precnikTocka;
    private String tezina;
    private String opis;

    public String getProizvodjacNaziv() {
        return proizvodjacNaziv;
    }

    public void setProizvodjacNaziv(String proizvodjacNaziv) {
        this.proizvodjacNaziv = proizvodjacNaziv;
    }

    public Long getKategorijaId() {
        return kategorijaId;
    }

    public void setKategorijaId(Long kategorijaId) {
        this.kategorijaId = kategorijaId;
    }

    public LocalDate getDatumKupovine() {
        return datumKupovine;
    }

    public void setDatumKupovine(LocalDate datumKupovine) {
        this.datumKupovine = datumKupovine;
    }

    public String getNapomena() {
        return napomena;
    }

    public void setNapomena(String napomena) {
        this.napomena = napomena;
    }

    public Boolean getDostupan() {
        return dostupan;
    }

    public void setDostupan(Boolean dostupan) {
        this.dostupan = dostupan;
    }

    public String getTipBicikle() {
        return tipBicikle;
    }

    public void setTipBicikle(String tipBicikle) {
        this.tipBicikle = tipBicikle;
    }

    public String getVelicinaRama() {
        return velicinaRama;
    }

    public void setVelicinaRama(String velicinaRama) {
        this.velicinaRama = velicinaRama;
    }

    public String getBrojBrzina() {
        return brojBrzina;
    }

    public void setBrojBrzina(String brojBrzina) {
        this.brojBrzina = brojBrzina;
    }

    public String getTipKocnica() {
        return tipKocnica;
    }

    public void setTipKocnica(String tipKocnica) {
        this.tipKocnica = tipKocnica;
    }

    public String getPrecnikTocka() {
        return precnikTocka;
    }

    public void setPrecnikTocka(String precnikTocka) {
        this.precnikTocka = precnikTocka;
    }

    public String getTezina() {
        return tezina;
    }

    public void setTezina(String tezina) {
        this.tezina = tezina;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }
}
