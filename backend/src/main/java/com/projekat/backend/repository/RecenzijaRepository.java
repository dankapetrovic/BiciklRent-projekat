package com.projekat.backend.repository;

import com.projekat.backend.entity.Recenzija;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RecenzijaRepository extends JpaRepository<Recenzija, Long> {

    List<Recenzija> findByBiciklIdOrderByDatumKreiranjaDesc(Long biciklId);

    Optional<Recenzija> findByIdAndKlijentId(Long id, Long klijentId);

    @Query("select avg(r.ocena) from Recenzija r where r.bicikl.id = :biciklId")
    Double findProsecnaOcenaByBiciklId(@Param("biciklId") Long biciklId);
}