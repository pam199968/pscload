package fr.ans.psc.pscload.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.ans.psc.pscload.model.object.Professionnel;
import fr.ans.psc.pscload.model.object.response.PsListResponse;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.*;

@Service
public class PscRestApi {

    private final RestTemplate restTemplate;

    public PscRestApi(RestTemplateBuilder restTemplateBuilder) {
        // set connection and read timeouts
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(500))
                .setReadTimeout(Duration.ofSeconds(500))
                .build();
    }

    public PscRestApi() {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        this.restTemplate = restTemplateBuilder.build();
    }

    public PsListResponse getPsAsResponse(String url) {
        return this.restTemplate.getForObject(url, PsListResponse.class);
    }

    public void putPs(String url, String message) {

        String[] items = message.split("\\|", -1);

        Professionnel professionnel = new Professionnel(items);

        GsonBuilder builder = new GsonBuilder();
        builder.disableHtmlEscaping();
        Gson gson = builder.create();

        String json = gson.toJson(professionnel);

        // create headers
        HttpHeaders headers = new HttpHeaders();
        // set `content-type` header
        headers.setContentType(MediaType.APPLICATION_JSON);
        // set `accept` header
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // build the request
        HttpEntity<String> request = new HttpEntity<>(json, headers);

        // send POST request
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
        // check the response, e.g. Location header,  Status, and body
        response.getHeaders().getLocation();
        response.getStatusCode();
        String responseBody = response.getBody();

        // return response message
        System.out.println(responseBody);
    }

}
