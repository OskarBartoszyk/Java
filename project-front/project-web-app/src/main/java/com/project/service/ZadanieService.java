package com.project.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.project.model.Zadanie;

public interface ZadanieService {
    Optional<Zadanie> getZadanie(Integer zadanieId);
    Zadanie saveZadanie(Zadanie zadanie);
    void deleteZadanie(Integer zadanieId);
    List<Zadanie> getZadaniaProjektu(Integer projektId);
    Page<Zadanie> getZadaniaProjektu(Integer projektId, Pageable pageable);
}