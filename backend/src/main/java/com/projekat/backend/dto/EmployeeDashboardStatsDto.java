package com.projekat.backend.dto;

import java.util.List;

public class EmployeeDashboardStatsDto {
    private Long ukupanBrojIznajmljivanja;
    private Long ukupanBrojKlijenata;
    private Long ukupanBrojBicikala;
    private Long brojDostupnihBicikala;
    private Long brojNedostupnihBicikala;
    private String najpopularnijiBicikl;
    private List<RentalStatsDto> statistikaPoBiciklu;

    public EmployeeDashboardStatsDto() {
    }

    public EmployeeDashboardStatsDto(Long ukupanBrojIznajmljivanja, Long ukupanBrojKlijenata, Long ukupanBrojBicikala,
                                     Long brojDostupnihBicikala, Long brojNedostupnihBicikala,
                                     String najpopularnijiBicikl, List<RentalStatsDto> statistikaPoBiciklu) {
        this.ukupanBrojIznajmljivanja = ukupanBrojIznajmljivanja;
        this.ukupanBrojKlijenata = ukupanBrojKlijenata;
        this.ukupanBrojBicikala = ukupanBrojBicikala;
        this.brojDostupnihBicikala = brojDostupnihBicikala;
        this.brojNedostupnihBicikala = brojNedostupnihBicikala;
        this.najpopularnijiBicikl = najpopularnijiBicikl;
        this.statistikaPoBiciklu = statistikaPoBiciklu;
    }

    public Long getUkupanBrojIznajmljivanja() {
        return ukupanBrojIznajmljivanja;
    }

    public void setUkupanBrojIznajmljivanja(Long ukupanBrojIznajmljivanja) {
        this.ukupanBrojIznajmljivanja = ukupanBrojIznajmljivanja;
    }

    public Long getUkupanBrojKlijenata() {
        return ukupanBrojKlijenata;
    }

    public void setUkupanBrojKlijenata(Long ukupanBrojKlijenata) {
        this.ukupanBrojKlijenata = ukupanBrojKlijenata;
    }

    public Long getUkupanBrojBicikala() {
        return ukupanBrojBicikala;
    }

    public void setUkupanBrojBicikala(Long ukupanBrojBicikala) {
        this.ukupanBrojBicikala = ukupanBrojBicikala;
    }

    public Long getBrojDostupnihBicikala() {
        return brojDostupnihBicikala;
    }

    public void setBrojDostupnihBicikala(Long brojDostupnihBicikala) {
        this.brojDostupnihBicikala = brojDostupnihBicikala;
    }

    public Long getBrojNedostupnihBicikala() {
        return brojNedostupnihBicikala;
    }

    public void setBrojNedostupnihBicikala(Long brojNedostupnihBicikala) {
        this.brojNedostupnihBicikala = brojNedostupnihBicikala;
    }

    public String getNajpopularnijiBicikl() {
        return najpopularnijiBicikl;
    }

    public void setNajpopularnijiBicikl(String najpopularnijiBicikl) {
        this.najpopularnijiBicikl = najpopularnijiBicikl;
    }

    public List<RentalStatsDto> getStatistikaPoBiciklu() {
        return statistikaPoBiciklu;
    }

    public void setStatistikaPoBiciklu(List<RentalStatsDto> statistikaPoBiciklu) {
        this.statistikaPoBiciklu = statistikaPoBiciklu;
    }
}
