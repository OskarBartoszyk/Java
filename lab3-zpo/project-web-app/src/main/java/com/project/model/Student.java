package com.project.model;

import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Set;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Student {
    private Integer studentId;

    @NotBlank(message = "Imię nie może być puste!")
    private String imie;

    @NotBlank(message = "Nazwisko nie może być puste!")
    private String nazwisko;

    @NotBlank(message = "Numer indeksu nie może być pusty!")
    private String nrIndeksu;

    @Email(message = "Niepoprawny format adresu email!")
    private String email;

    @NotNull(message = "Pole stacjonarny nie może być puste!")
    private Boolean stacjonarny;

    @JsonIgnoreProperties({"studenci"})
    private Set<Projekt> projekty;

    public Student() {}

    public Student(String imie, String nazwisko, String nrIndeksu, Boolean stacjonarny) {
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.nrIndeksu = nrIndeksu;
        this.stacjonarny = stacjonarny;
    }

    public Student(String imie, String nazwisko, String nrIndeksu, String email, Boolean stacjonarny) {
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.nrIndeksu = nrIndeksu;
        this.email = email;
        this.stacjonarny = stacjonarny;
    }

    // Gettery i settery
    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }
    public String getImie() { return imie; }
    public void setImie(String imie) { this.imie = imie; }
    public String getNazwisko() { return nazwisko; }
    public void setNazwisko(String nazwisko) { this.nazwisko = nazwisko; }
    public String getNrIndeksu() { return nrIndeksu; }
    public void setNrIndeksu(String nrIndeksu) { this.nrIndeksu = nrIndeksu; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Boolean getStacjonarny() { return stacjonarny; }
    public void setStacjonarny(Boolean stacjonarny) { this.stacjonarny = stacjonarny; }
    public Set<Projekt> getProjekty() { return projekty; }
    public void setProjekty(Set<Projekt> projekty) { this.projekty = projekty; }
}