package com.project.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.project.model.Zadanie;
import com.project.repository.ZadanieRepository;

@Service
public class ZadanieServiceImpl implements ZadanieService {
    private final ZadanieRepository zadanieRepository;

    @Autowired
    public ZadanieServiceImpl(ZadanieRepository zadanieRepository) {
        this.zadanieRepository = zadanieRepository;
    }

    @Override
    public List<Zadanie> getZadaniaProjektu(Integer projektId) {
        return zadanieRepository.findZadaniaProjektu(projektId);
    }

    @Override
    public Page<Zadanie> getZadaniaProjektu(Integer projektId, Pageable pageable) {
        return zadanieRepository.findZadaniaProjektu(projektId, pageable);
    }

    @Override
    public Zadanie saveZadanie(Zadanie zadanie) {
        return zadanieRepository.save(zadanie);
    }

    @Override
    public void deleteZadanie(Integer zadanieId) {
        zadanieRepository.deleteById(zadanieId);
    }
}