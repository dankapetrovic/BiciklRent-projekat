package com.projekat.backend.dto;

import java.time.LocalDate;
public class BiciklDto {
    private Long id;
    private LocalDate datumKupovine;
    private String napomena;
    private Boolean dostupan;
    private String proizvodjacNaziv;
    private Long kategorijaId;
    private String kategorijaNaziv;
    private String tipBicikle;
    private String velicinaRama;
    private String brojBrzina;
    private String tipKocnica;
    private String precnikTocka;
    private String tezina;
    private String opis;
    private Boolean dostupanZaPeriod;

    public BiciklDto() {
    }

    public BiciklDto(Long id, LocalDate datumKupovine, String napomena, Boolean dostupan,
                     String proizvodjacNaziv, Long kategorijaId, String kategorijaNaziv, String tipBicikle,
                     String velicinaRama, String brojBrzina, String tipKocnica, String precnikTocka,
                     String tezina, String opis, Boolean dostupanZaPeriod) {
        this.id = id;
        this.datumKupovine = datumKupovine;
        this.napomena = napomena;
        this.dostupan = dostupan;
        this.proizvodjacNaziv = proizvodjacNaziv;
        this.kategorijaId = kategorijaId;
        this.kategorijaNaziv = kategorijaNaziv;
        this.tipBicikle = tipBicikle;
        this.velicinaRama = velicinaRama;
        this.brojBrzina = brojBrzina;
        this.tipKocnica = tipKocnica;
        this.precnikTocka = precnikTocka;
        this.tezina = tezina;
        this.opis = opis;
        this.dostupanZaPeriod = dostupanZaPeriod;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getKategorijaNaziv() {
        return kategorijaNaziv;
    }

    public void setKategorijaNaziv(String kategorijaNaziv) {
        this.kategorijaNaziv = kategorijaNaziv;
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

    public Boolean getDostupanZaPeriod() {
        return dostupanZaPeriod;
    }

    public void setDostupanZaPeriod(Boolean dostupanZaPeriod) {
        this.dostupanZaPeriod = dostupanZaPeriod;
    }
}
