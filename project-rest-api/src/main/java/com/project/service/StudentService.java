package com.project.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.project.model.Student;

public interface StudentService {
    Optional<Student> getStudentByNrIndeksu(String nrIndeksu);
    Page<Student> getStudenciByNrIndeksu(String nrIndeksu, Pageable pageable);
    Page<Student> getStudenciByNazwisko(String nazwisko, Pageable pageable);
    Student saveStudent(Student student);
    void deleteStudent(Integer studentId);
}