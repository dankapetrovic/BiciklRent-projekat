package com.projekat.backend.dto;

public class RentalStatsDto {
    private String nazivBicikla;
    private Long brojIznajmljivanja;
    private Double procenat;

    public RentalStatsDto() {
    }

    public RentalStatsDto(String nazivBicikla, Long brojIznajmljivanja, Double procenat) {
        this.nazivBicikla = nazivBicikla;
        this.brojIznajmljivanja = brojIznajmljivanja;
        this.procenat = procenat;
    }

    public String getNazivBicikla() {
        return nazivBicikla;
    }

    public void setNazivBicikla(String nazivBicikla) {
        this.nazivBicikla = nazivBicikla;
    }

    public Long getBrojIznajmljivanja() {
        return brojIznajmljivanja;
    }

    public void setBrojIznajmljivanja(Long brojIznajmljivanja) {
        this.brojIznajmljivanja = brojIznajmljivanja;
    }

    public Double getProcenat() {
        return procenat;
    }

    public void setProcenat(Double procenat) {
        this.procenat = procenat;
    }
}
