package com.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

@Entity
@Table(name = "zadanie")
public class Zadanie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "zadanie_id")
    private Integer zadanieId;

    @NotBlank(message = "Nazwa zadania nie może być pusta!")
    @Column(nullable = false, length = 50)
    private String nazwa;

    @NotNull(message = "Kolejność nie może być pusta!")
    @Column(nullable = false)
    private Integer kolejnosc;

    @Column(length = 1000)
    private String opis;

    @Column(name = "dataczas_dodania", nullable = false)
    private LocalDateTime dataczasDodania = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "projekt_id", nullable = false)
    @JsonIgnoreProperties({"zadania", "studenci"})
    private Projekt projekt;

    public Zadanie() {}

    public Zadanie(String nazwa, String opis, Integer kolejnosc) {
        this.nazwa = nazwa;
        this.opis = opis;
        this.kolejnosc = kolejnosc;
    }

    // Gettery i settery
    public Integer getZadanieId() { return zadanieId; }
    public void setZadanieId(Integer zadanieId) { this.zadanieId = zadanieId; }
    public String getNazwa() { return nazwa; }
    public void setNazwa(String nazwa) { this.nazwa = nazwa; }
    public Integer getKolejnosc() { return kolejnosc; }
    public void setKolejnosc(Integer kolejnosc) { this.kolejnosc = kolejnosc; }
    public String getOpis() { return opis; }
    public void setOpis(String opis) { this.opis = opis; }
    public LocalDateTime getDataczasDodania() { return dataczasDodania; }
    public void setDataczasDodania(LocalDateTime dataczasDodania) { this.dataczasDodania = dataczasDodania; }
    public Projekt getProjekt() { return projekt; }
    public void setProjekt(Projekt projekt) { this.projekt = projekt; }
}