package com.project.service;

import java.net.URI;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import com.project.model.Student;
import org.springframework.core.ParameterizedTypeReference;

@Service
public class StudentServiceImpl implements StudentService {
    private final RestClient restClient;

    public StudentServiceImpl(RestClient restClient) {
        this.restClient = restClient;
    }

    private String getResourcePath() {
        return "/api/studenci";
    }

    private String getResourcePath(String nrIndeksu) {
        return String.format("%s/%s", getResourcePath(), nrIndeksu);
    }

    @Override
    public Optional<Student> getStudentByNrIndeksu(String nrIndeksu) {
        String resourcePath = getResourcePath(nrIndeksu);
        Student student = restClient.get()
            .uri(resourcePath)
            .retrieve()
            .body(Student.class);
        return Optional.ofNullable(student);
    }

    @Override
    public Page<Student> getStudenciByNrIndeksu(String nrIndeksu, Pageable pageable) {
        URI uri = ServiceUtil.getURI(getResourcePath() + "/szukaj/" + nrIndeksu, pageable);
        return ServiceUtil.getPage(uri, restClient, 
            new ParameterizedTypeReference<RestResponsePage<Student>>() {});
    }

    @Override
    public Page<Student> getStudenciByNazwisko(String nazwisko, Pageable pageable) {
        URI uri = ServiceUtil.getURI(getResourcePath() + "/szukaj/nazwisko/" + nazwisko, pageable);
        return ServiceUtil.getPage(uri, restClient, 
            new ParameterizedTypeReference<RestResponsePage<Student>>() {});
    }

    @Override
    public Student saveStudent(Student student) {
        if (student.getStudentId() != null) {
            return restClient.put()
                .uri(getResourcePath() + "/" + student.getStudentId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(student)
                .retrieve()
                .body(Student.class);
        } else {
            return restClient.post()
                .uri(getResourcePath())
                .contentType(MediaType.APPLICATION_JSON)
                .body(student)
                .retrieve()
                .body(Student.class);
        }
    }

    @Override
    public void deleteStudent(Integer studentId) {
        restClient.delete()
            .uri(getResourcePath() + "/" + studentId)
            .retrieve()
            .toBodilessEntity();
    }
}