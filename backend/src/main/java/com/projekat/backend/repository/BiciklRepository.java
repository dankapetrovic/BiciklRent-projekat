package com.projekat.backend.repository;

import com.projekat.backend.entity.Bicikl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BiciklRepository extends JpaRepository<Bicikl, Long> {
    long countByDostupanTrue();
    long countByDostupanFalse();
}
