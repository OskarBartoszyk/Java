package com.project.service;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import com.project.model.Zadanie;
import com.project.exception.HttpException;
import org.springframework.http.HttpStatusCode;

@Service
public class ZadanieServiceImpl implements ZadanieService {
    private final RestClient restClient;

    public ZadanieServiceImpl(RestClient restClient) {
        this.restClient = restClient;
    }

    private String getResourcePath() {
        return "/api/zadania";
    }

    private String getResourcePath(Integer id) {
        return String.format("%s/%d", getResourcePath(), id);
    }

    @Override
    public Optional<Zadanie> getZadanie(Integer zadanieId) {
        String resourcePath = getResourcePath(zadanieId);
        Zadanie zadanie = restClient.get()
            .uri(resourcePath)
            .retrieve()
            .onStatus(HttpStatusCode::isError, (req, res) -> {
                throw new HttpException(res.getStatusCode(), res.getHeaders());
            })
            .body(Zadanie.class);
        return Optional.ofNullable(zadanie);
    }

    @Override
    public Zadanie saveZadanie(Zadanie zadanie) {
        if (zadanie.getZadanieId() != null) {
            String resourcePath = getResourcePath(zadanie.getZadanieId());
            return restClient.put()
                .uri(resourcePath)
                .contentType(MediaType.APPLICATION_JSON)
                .body(zadanie)
                .retrieve()
                .body(Zadanie.class);
        } else {
            return restClient.post()
                .uri(getResourcePath())
                .contentType(MediaType.APPLICATION_JSON)
                .body(zadanie)
                .retrieve()
                .body(Zadanie.class);
        }
    }

    @Override
    public void deleteZadanie(Integer zadanieId) {
        String resourcePath = getResourcePath(zadanieId);
        restClient.delete()
            .uri(resourcePath)
            .retrieve()
            .toBodilessEntity();
    }

    @Override
    public List<Zadanie> getZadaniaProjektu(Integer projektId) {
        String resourcePath = String.format("/api/projekty/%d/zadania", projektId);
        return restClient.get()
            .uri(resourcePath)
            .retrieve()
            .body(new ParameterizedTypeReference<List<Zadanie>>() {});
    }

    @Override
    public Page<Zadanie> getZadaniaProjektu(Integer projektId, Pageable pageable) {
        String resourcePath = String.format("/api/projekty/%d/zadania", projektId);
        URI uri = ServiceUtil.getURI(resourcePath, pageable);
        return ServiceUtil.getPage(uri, restClient, 
            new ParameterizedTypeReference<RestResponsePage<Zadanie>>() {});
    }
}
