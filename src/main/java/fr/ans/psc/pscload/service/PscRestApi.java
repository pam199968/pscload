package fr.ans.psc.pscload.service;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import fr.ans.psc.pscload.component.JsonFormatter;
import fr.ans.psc.pscload.model.object.ExerciceProfessionnel;
import fr.ans.psc.pscload.model.object.Professionnel;
import fr.ans.psc.pscload.model.object.response.PsListResponse;
import fr.ans.psc.pscload.model.object.response.PsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The type Psc rest api.
 */
@Service
public class PscRestApi {

    private final RestTemplate restTemplate;

    @Autowired
    private final JsonFormatter jsonFormatter;

    @Value("${ps.api.base.url}")
    private String apiBaseUrl;

    /**
     * Instantiates a new Psc rest api.
     *  @param restTemplateBuilder the rest template builder
     * @param gson                gson
     * @param jsonFormatter
     */
    public PscRestApi(RestTemplateBuilder restTemplateBuilder, Gson gson, JsonFormatter jsonFormatter) {
        // set connection and read timeouts
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(500))
                .setReadTimeout(Duration.ofSeconds(500))
                .build();
        this.jsonFormatter = jsonFormatter;
    }

    /**
     * Instantiates a new Psc rest api.
     */
    public PscRestApi() {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        this.restTemplate = restTemplateBuilder.build();

        this.jsonFormatter = new JsonFormatter();
    }

    /**
     * Gets ps list.
     *
     * @param url the url
     * @return the ps list
     */
    public PsListResponse getPsList(String url) {
        return this.restTemplate.getForObject(url, PsListResponse.class);
    }

    /**
     * Gets ps.
     *
     * @param url the url
     * @return the ps
     */
    public PsResponse getPs(String url) {
        return this.restTemplate.getForObject(url, PsResponse.class);
    }

    /**
     * Put.
     *
     * @param url           the url
     * @param json        json request
     */
    public void put(String url, String json) {
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

    /**
     * Delete.
     *
     * @param url  the url
     */
    public void delete(String url) {
        // send DELETE request
        restTemplate.delete(url);

        System.out.println("deleted " + url);
    }

    public void diffUpdate(Professionnel left, Professionnel right) {

        if (left.nakedHash() == right.nakedHash()) {
            MapDifference<String, ExerciceProfessionnel> exProDiff = Maps.difference(left.getProfessions(), right.getProfessions());
            System.out.println(exProDiff);
        } else {
            // update Ps basic attributes
            put(apiBaseUrl + '/' + left.getNationalId(), jsonFormatter.nakedPsFromObject(right));
        }

    }
}
