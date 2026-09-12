package com.projekat.backend.dto;

import java.time.LocalDate;

public class IznajmljivanjeRequestDto {
    private Long biciklId;
    private LocalDate datumOd;
    private LocalDate datumDo;
    private String brojKartice;
    private String datumIstekaKartice;
    private String cvc;

    public Long getBiciklId() {
        return biciklId;
    }

    public void setBiciklId(Long biciklId) {
        this.biciklId = biciklId;
    }

    public LocalDate getDatumOd() {
        return datumOd;
    }

    public void setDatumOd(LocalDate datumOd) {
        this.datumOd = datumOd;
    }

    public LocalDate getDatumDo() {
        return datumDo;
    }

    public void setDatumDo(LocalDate datumDo) {
        this.datumDo = datumDo;
    }

    public String getBrojKartice() {
        return brojKartice;
    }

    public void setBrojKartice(String brojKartice) {
        this.brojKartice = brojKartice;
    }

    public String getDatumIstekaKartice() {
        return datumIstekaKartice;
    }

    public void setDatumIstekaKartice(String datumIstekaKartice) {
        this.datumIstekaKartice = datumIstekaKartice;
    }

    public String getCvc() {
        return cvc;
    }

    public void setCvc(String cvc) {
        this.cvc = cvc;
    }
}
