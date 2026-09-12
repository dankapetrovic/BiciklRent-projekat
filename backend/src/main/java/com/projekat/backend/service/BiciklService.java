package com.projekat.backend.service;

import com.projekat.backend.dto.BiciklDto;
import com.projekat.backend.dto.BiciklRequestDto;
import com.projekat.backend.dto.ZauzetostDto;
import com.projekat.backend.entity.Bicikl;
import com.projekat.backend.entity.Iznajmljivanje;
import com.projekat.backend.entity.Kategorija;
import com.projekat.backend.entity.Proizvodjac;
import com.projekat.backend.exception.ValidationException;
import com.projekat.backend.repository.BiciklRepository;
import com.projekat.backend.repository.IznajmljivanjeRepository;
import com.projekat.backend.repository.KategorijaRepository;
import com.projekat.backend.repository.ProizvodjacRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BiciklService {

    private final BiciklRepository biciklRepository;
    private final IznajmljivanjeRepository iznajmljivanjeRepository;
    private final KategorijaRepository kategorijaRepository;
    private final ProizvodjacRepository proizvodjacRepository;

    @Transactional(readOnly = true)
    public List<BiciklDto> getBicikli(LocalDate datumOd, LocalDate datumDo) {
        LocalDate periodOd = datumOd != null ? datumOd : LocalDate.now();
        LocalDate periodDo = datumDo != null ? datumDo : periodOd.plusDays(1);

        Set<Long> zauzetiBiciklIds = iznajmljivanjeRepository
                .findByDatumPocetkaLessThanEqualAndDatumKrajaGreaterThanEqual(periodDo, periodOd)
                .stream()
                .map(Iznajmljivanje::getBicikl)
                .map(Bicikl::getId)
                .collect(Collectors.toSet());

        return biciklRepository.findAll()
                .stream()
                .map(bicikl -> toDto(bicikl, zauzetiBiciklIds))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ZauzetostDto> getZauzetPeriodi(Long biciklId) {
        if (!biciklRepository.existsById(biciklId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Bicikl nije pronađen");
        }

        return iznajmljivanjeRepository.findByBiciklId(biciklId)
                .stream()
                .map(iznajmljivanje -> new ZauzetostDto(iznajmljivanje.getDatumPocetka(), iznajmljivanje.getDatumKraja()))
                .toList();
    }

    @Transactional
    public BiciklDto createBicikl(BiciklRequestDto requestDto) {
        validateBicikl(requestDto);

        Kategorija kategorija = kategorijaRepository.findById(requestDto.getKategorijaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kategorija nije pronađena"));
        Proizvodjac proizvodjac = resolveProizvodjac(requestDto.getProizvodjacNaziv());

        Bicikl bicikl = new Bicikl();
        applyFields(bicikl, requestDto, proizvodjac, kategorija);

        bicikl = biciklRepository.save(bicikl);
        return toDto(bicikl, Set.of());
    }

    @Transactional
    public BiciklDto updateBicikl(Long id, BiciklRequestDto requestDto) {
        validateBicikl(requestDto);

        Bicikl bicikl = biciklRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bicikl nije pronađen"));
        Kategorija kategorija = kategorijaRepository.findById(requestDto.getKategorijaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kategorija nije pronađena"));
        Proizvodjac proizvodjac = resolveProizvodjac(requestDto.getProizvodjacNaziv());

        applyFields(bicikl, requestDto, proizvodjac, kategorija);

        bicikl = biciklRepository.save(bicikl);
        return toDto(bicikl, Set.of());
    }

    @Transactional
    public void deleteBicikl(Long id) {
        Bicikl bicikl = biciklRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bicikl nije pronađen"));

        boolean imaIznajmljivanja = !iznajmljivanjeRepository.findByBiciklId(id).isEmpty();
        if (imaIznajmljivanja) {
            bicikl.setDostupan(false);
            biciklRepository.save(bicikl);
        } else {
            biciklRepository.delete(bicikl);
        }
    }

    private void applyFields(Bicikl bicikl, BiciklRequestDto requestDto, Proizvodjac proizvodjac, Kategorija kategorija) {
        bicikl.setDatumKupovine(requestDto.getDatumKupovine());
        bicikl.setNapomena(requestDto.getNapomena());
        bicikl.setDostupan(requestDto.getDostupan() != null ? requestDto.getDostupan() : true);
        bicikl.setProizvodjac(proizvodjac);
        bicikl.setKategorija(kategorija);
        bicikl.setTipBicikle(requestDto.getTipBicikle());
        bicikl.setVelicinaRama(requestDto.getVelicinaRama());
        bicikl.setBrojBrzina(requestDto.getBrojBrzina());
        bicikl.setTipKocnica(requestDto.getTipKocnica());
        bicikl.setPrecnikTocka(requestDto.getPrecnikTocka());
        bicikl.setTezina(requestDto.getTezina());
        bicikl.setOpis(requestDto.getOpis());
    }

    private Proizvodjac resolveProizvodjac(String naziv) {
        String trimmedNaziv = naziv == null ? "" : naziv.trim();
        return proizvodjacRepository.findByNameIgnoreCase(trimmedNaziv)
                .orElseGet(() -> {
                    Proizvodjac proizvodjac = new Proizvodjac();
                    proizvodjac.setName(trimmedNaziv);
                    return proizvodjacRepository.save(proizvodjac);
                });
    }

    private void validateBicikl(BiciklRequestDto requestDto) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();

        if (requestDto.getProizvodjacNaziv() == null || requestDto.getProizvodjacNaziv().isBlank()) {
            fieldErrors.put("proizvodjacNaziv", "Proizvođač je obavezan");
        }
        if (requestDto.getKategorijaId() == null) {
            fieldErrors.put("kategorijaId", "Kategorija je obavezna");
        }

        if (!fieldErrors.isEmpty()) {
            throw new ValidationException(fieldErrors);
        }
    }

    private BiciklDto toDto(Bicikl bicikl, Set<Long> zauzetiBiciklIds) {
        boolean dostupanZaPeriod = Boolean.TRUE.equals(bicikl.getDostupan()) && !zauzetiBiciklIds.contains(bicikl.getId());

        return new BiciklDto(
                bicikl.getId(),
                bicikl.getDatumKupovine(),
                bicikl.getNapomena(),
                bicikl.getDostupan(),
                bicikl.getProizvodjac() == null ? null : bicikl.getProizvodjac().getName(),
                bicikl.getKategorija() == null ? null : bicikl.getKategorija().getId(),
                bicikl.getKategorija() == null ? null : bicikl.getKategorija().getNaziv(),
                bicikl.getTipBicikle(),
                bicikl.getVelicinaRama(),
                bicikl.getBrojBrzina(),
                bicikl.getTipKocnica(),
                bicikl.getPrecnikTocka(),
                bicikl.getTezina(),
                bicikl.getOpis(),
                dostupanZaPeriod
        );
    }
}