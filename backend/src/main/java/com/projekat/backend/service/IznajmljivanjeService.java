package com.projekat.backend.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.projekat.backend.dto.EmployeeDashboardStatsDto;
import com.projekat.backend.dto.IznajmljivanjeDto;
import com.projekat.backend.dto.IznajmljivanjeRequestDto;
import com.projekat.backend.dto.RentalStatsDto;
import com.projekat.backend.entity.Bicikl;
import com.projekat.backend.entity.Iznajmljivanje;
import com.projekat.backend.entity.Klijent;
import com.projekat.backend.exception.ValidationException;
import com.projekat.backend.repository.BiciklRepository;
import com.projekat.backend.repository.IznajmljivanjeRepository;
import com.projekat.backend.repository.KlijentRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IznajmljivanjeService {

    private final IznajmljivanjeRepository iznajmljivanjeRepository;
    private final BiciklRepository biciklRepository;
    private final KlijentRepository klijentRepository;
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username:}")
    private String mailSenderAddress;

    @Value("${app.name}")
    private String appName;

    private static final DateTimeFormatter PDF_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy.");

    @Transactional
    public IznajmljivanjeDto createIznajmljivanje(Long klijentId, IznajmljivanjeRequestDto requestDto) {
        validatePayment(requestDto);

        Bicikl bicikl = biciklRepository.findById(requestDto.getBiciklId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bicikl nije pronađen"));

        if (!Boolean.TRUE.equals(bicikl.getDostupan())) {
            throwValidationError("biciklId", "Bicikl trenutno nije u ponudi");
        }

        boolean zauzet = iznajmljivanjeRepository.existsByBiciklIdAndDatumPocetkaLessThanEqualAndDatumKrajaGreaterThanEqual(
                bicikl.getId(), requestDto.getDatumDo(), requestDto.getDatumOd());
        if (zauzet) {
            throwValidationError("datumOd", "Izabrani period više nije slobodan za ovaj bicikl");
        }

        Klijent klijent = klijentRepository.findById(klijentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Klijent nije pronađen"));

        Iznajmljivanje iznajmljivanje = new Iznajmljivanje();
        iznajmljivanje.setDatumPocetka(requestDto.getDatumOd());
        iznajmljivanje.setDatumKraja(requestDto.getDatumDo());
        iznajmljivanje.setBicikl(bicikl);
        iznajmljivanje.setKlijent(klijent);
        iznajmljivanje = iznajmljivanjeRepository.save(iznajmljivanje);

        sendConfirmation(klijent, bicikl, iznajmljivanje);

        return toDto(iznajmljivanje, bicikl);
    }

    @Transactional(readOnly = true)
    public List<IznajmljivanjeDto> getMojaIznajmljivanja(Long klijentId) {
        return iznajmljivanjeRepository.findByKlijentId(klijentId)
                .stream()
                .map(iznajmljivanje -> toDto(iznajmljivanje, iznajmljivanje.getBicikl()))
                .toList();
    }

    @Transactional(readOnly = true)
    public EmployeeDashboardStatsDto getStatistika(LocalDate datumOd, LocalDate datumDo) {
        List<Iznajmljivanje> sveIznajmljivanja = iznajmljivanjeRepository.findAll();

        Map<Long, Long> brojPoBiciklId = new LinkedHashMap<>();
        Map<Long, Bicikl> biciklPoId = new LinkedHashMap<>();
        for (Iznajmljivanje iznajmljivanje : sveIznajmljivanja) {
            Bicikl bicikl = iznajmljivanje.getBicikl();
            brojPoBiciklId.merge(bicikl.getId(), 1L, Long::sum);
            biciklPoId.putIfAbsent(bicikl.getId(), bicikl);
        }

        long ukupnoIznajmljivanja = sveIznajmljivanja.size();
        List<RentalStatsDto> statistikaPoBiciklu = brojPoBiciklId.entrySet()
                .stream()
                .map(entry -> new RentalStatsDto(
                        nazivZaBicikl(biciklPoId.get(entry.getKey())),
                        entry.getValue(),
                        ukupnoIznajmljivanja == 0 ? 0.0 : Math.round(entry.getValue() * 10000.0 / ukupnoIznajmljivanja) / 100.0
                ))
                .sorted(Comparator.comparing(RentalStatsDto::getBrojIznajmljivanja).reversed())
                .toList();

        String najpopularniji = statistikaPoBiciklu.isEmpty() ? null : statistikaPoBiciklu.get(0).getNazivBicikla();
        long brojDostupnih;
        long brojNedostupnih;
        if (datumOd != null && datumDo != null) {
            Set<Long> zauzetiIds = iznajmljivanjeRepository
                    .findByDatumPocetkaLessThanEqualAndDatumKrajaGreaterThanEqual(datumDo, datumOd)
                    .stream()
                    .map(iznajmljivanje -> iznajmljivanje.getBicikl().getId())
                    .collect(Collectors.toSet());
            long ukupnoDostupnihUPonudi = biciklRepository.countByDostupanTrue();
            brojDostupnih = biciklRepository.findAll().stream()
                    .filter(bicikl -> Boolean.TRUE.equals(bicikl.getDostupan()) && !zauzetiIds.contains(bicikl.getId()))
                    .count();
            brojNedostupnih = biciklRepository.count() - brojDostupnih;
            if (brojDostupnih > ukupnoDostupnihUPonudi) {
                brojDostupnih = ukupnoDostupnihUPonudi;
            }
        } else {
            brojDostupnih = biciklRepository.countByDostupanTrue();
            brojNedostupnih = biciklRepository.countByDostupanFalse();
        }

        return new EmployeeDashboardStatsDto(
                ukupnoIznajmljivanja,
                klijentRepository.count(),
                biciklRepository.count(),
                brojDostupnih,
                brojNedostupnih,
                najpopularniji,
                statistikaPoBiciklu
        );
    }

    private String nazivZaBicikl(Bicikl bicikl) {
        if (bicikl == null) {
            return "Nepoznat bicikl";
        }
        String proizvodjacNaziv = bicikl.getProizvodjac() == null ? "Bicikl" : bicikl.getProizvodjac().getName();
        return bicikl.getTipBicikle() != null
                ? proizvodjacNaziv + " · " + bicikl.getTipBicikle()
                : proizvodjacNaziv;
    }

    private void validatePayment(IznajmljivanjeRequestDto requestDto) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();

        if (requestDto.getBiciklId() == null) {
            fieldErrors.put("biciklId", "Bicikl je obavezan");
        }
        if (requestDto.getDatumOd() == null || requestDto.getDatumDo() == null) {
            fieldErrors.put("datumOd", "Period iznajmljivanja je obavezan");
        } else if (!requestDto.getDatumDo().isAfter(requestDto.getDatumOd())) {
            fieldErrors.put("datumDo", "Datum završetka mora biti posle datuma početka");
        }

        String brojKartice = requestDto.getBrojKartice() == null ? "" : requestDto.getBrojKartice().replaceAll("\\s+", "");
        if (brojKartice.isBlank() || !brojKartice.matches("\\d{12,19}")) {
            fieldErrors.put("brojKartice", "Broj kartice nije validan");
        }

        if (requestDto.getDatumIstekaKartice() == null || requestDto.getDatumIstekaKartice().isBlank()) {
            fieldErrors.put("datumIstekaKartice", "Datum isteka kartice je obavezan");
        }

        String cvc = requestDto.getCvc() == null ? "" : requestDto.getCvc().trim();
        if (!cvc.matches("\\d{3,4}")) {
            fieldErrors.put("cvc", "CVC nije validan");
        }

        if (!fieldErrors.isEmpty()) {
            throw new ValidationException(fieldErrors);
        }
    }

    private void sendConfirmation(Klijent klijent, Bicikl bicikl, Iznajmljivanje iznajmljivanje) {
        if (klijent.getEmail() == null || klijent.getEmail().isBlank()) {
            return;
        }

        try {
            byte[] pdf = generateRentalConfirmationPdf(klijent, bicikl, iznajmljivanje);
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(mailSenderAddress);
            helper.setTo(klijent.getEmail());
            helper.setSubject("Potvrda iznajmljivanja");
            helper.setText("""
                    <div style="font-family: Arial, sans-serif; color: #1e1b4b; line-height: 1.6; padding: 20px;">
                        <h2 style="margin: 0 0 12px;">Iznajmljivanje je uspešno potvrđeno</h2>
                        <p style="margin: 0;">PDF potvrda vašeg iznajmljivanja je u prilogu ovog email-a.</p>
                    </div>
                    """, true);
            helper.addAttachment("potvrda-iznajmljivanja.pdf", () -> new ByteArrayInputStream(pdf), "application/pdf");
            javaMailSender.send(message);
        } catch (MessagingException | MailException | DocumentException exception) {
        }
    }
    private byte[] generateRentalConfirmationPdf(Klijent klijent, Bicikl bicikl, Iznajmljivanje iznajmljivanje) throws DocumentException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 48, 48, 54, 48);
        PdfWriter.getInstance(document, outputStream);
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 11);

        String proizvodjacNaziv = bicikl.getProizvodjac() == null ? "Bicikl" : bicikl.getProizvodjac().getName();
        String modelNaziv = proizvodjacNaziv + (bicikl.getTipBicikle() != null ? " · " + bicikl.getTipBicikle() : "");

        Paragraph title = new Paragraph(appName, titleFont);
        title.setSpacingAfter(4);
        document.add(title);

        Paragraph subtitle = new Paragraph("Potvrda iznajmljivanja bicikla", sectionFont);
        subtitle.setSpacingAfter(18);
        document.add(subtitle);

        document.add(detailLine("Klijent", klijent.getIme() + " " + klijent.getPrezime(), textFont));
        document.add(detailLine("Email", klijent.getEmail(), textFont));
        document.add(detailLine("Bicikl", modelNaziv, textFont));
        document.add(detailLine("Period iznajmljivanja", formatPdfDate(iznajmljivanje.getDatumPocetka()) + " - " + formatPdfDate(iznajmljivanje.getDatumKraja()), textFont));
        if (iznajmljivanje.getCena() != null) {
            document.add(detailLine("Cena", iznajmljivanje.getCena() + " EUR", textFont));
        }
        document.add(detailLine("Datum kreiranja potvrde", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm")), textFont));

        Paragraph footer = new Paragraph("Vaše iznajmljivanje je uspešno potvrđeno. Hvala što ste izabrali " + appName + "!", textFont);
        footer.setSpacingBefore(20);
        document.add(footer);

        document.close();
        return outputStream.toByteArray();
    }

    private Paragraph detailLine(String label, String value, Font textFont) {
        Paragraph paragraph = new Paragraph();
        paragraph.setSpacingBefore(7);
        paragraph.add(new Phrase(label + ": ", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11)));
        paragraph.add(new Phrase(value == null || value.isBlank() ? "-" : value, textFont));
        return paragraph;
    }

    private String formatPdfDate(LocalDate date) {
        return date == null ? "-" : date.format(PDF_DATE_FORMATTER);
    }

    private void throwValidationError(String field, String message) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        fieldErrors.put(field, message);
        throw new ValidationException(fieldErrors);
    }

    private IznajmljivanjeDto toDto(Iznajmljivanje iznajmljivanje, Bicikl bicikl) {
        return new IznajmljivanjeDto(
                iznajmljivanje.getId(),
                iznajmljivanje.getDatumPocetka(),
                iznajmljivanje.getDatumKraja(),
                iznajmljivanje.getCena(),
                bicikl.getId(),
                bicikl.getProizvodjac() == null ? null : bicikl.getProizvodjac().getName(),
                bicikl.getKategorija() == null ? null : bicikl.getKategorija().getNaziv(),
                bicikl.getTipBicikle(),
                bicikl.getOpis()
        );
    }
}