package com.projekat.backend.service;

import com.projekat.backend.dto.RecenzijaDto;
import com.projekat.backend.dto.RecenzijaRequestDto;
import com.projekat.backend.entity.Bicikl;
import com.projekat.backend.entity.Klijent;
import com.projekat.backend.entity.Recenzija;
import com.projekat.backend.repository.BiciklRepository;
import com.projekat.backend.repository.KlijentRepository;
import com.projekat.backend.repository.RecenzijaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecenzijaService {

    private final RecenzijaRepository recenzijaRepository;
    private final BiciklRepository biciklRepository;
    private final KlijentRepository klijentRepository;

    public List<RecenzijaDto> getRecenzijeZaBicikl(Long biciklId) {
        return recenzijaRepository.findByBiciklIdOrderByDatumKreiranjaDesc(biciklId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public Double getProsecnaOcena(Long biciklId) {
        Double prosek = recenzijaRepository.findProsecnaOcenaByBiciklId(biciklId);
        return prosek != null ? Math.round(prosek * 10) / 10.0 : null;
    }

    @Transactional
    public RecenzijaDto createRecenzija(Long biciklId, Long klijentId, RecenzijaRequestDto requestDto) {
        Bicikl bicikl = biciklRepository.findById(biciklId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bicikl nije pronadjen"));
        Klijent klijent = klijentRepository.findById(klijentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Klijent nije pronadjen"));

        Recenzija recenzija = new Recenzija();
        recenzija.setOcena(requestDto.getOcena());
        recenzija.setKomentar(requestDto.getKomentar());
        recenzija.setBicikl(bicikl);
        recenzija.setKlijent(klijent);

        return toDto(recenzijaRepository.save(recenzija));
    }

    @Transactional
    public void deleteRecenzija(Long id, Long klijentId) {
        Recenzija recenzija = recenzijaRepository.findByIdAndKlijentId(id, klijentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Ne mozete obrisati tudju recenziju"));
        recenzijaRepository.delete(recenzija);
    }

    private RecenzijaDto toDto(Recenzija r) {
        return RecenzijaDto.builder()
                .id(r.getId())
                .ocena(r.getOcena())
                .komentar(r.getKomentar())
                .datumKreiranja(r.getDatumKreiranja())
                .biciklId(r.getBicikl().getId())
                .klijentId(r.getKlijent().getId())
                .klijentIme(r.getKlijent().getIme())
                .klijentPrezime(r.getKlijent().getPrezime())
                .build();
    }
}