package fr.ans.psc.pscload.service;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import fr.ans.psc.pscload.component.JsonFormatter;
import fr.ans.psc.pscload.model.object.ExerciceProfessionnel;
import fr.ans.psc.pscload.model.object.Professionnel;
import fr.ans.psc.pscload.model.object.SavoirFaire;
import fr.ans.psc.pscload.model.object.SituationExercice;
import fr.ans.psc.pscload.model.object.response.PsListResponse;
import fr.ans.psc.pscload.model.object.response.PsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;

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
     * @param url         the url
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

        // send PUT request
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
        // check the response, e.g. Location header,  Status, and body
        response.getHeaders().getLocation();
        response.getStatusCode();
        String responseBody = response.getBody();

        // return response message
        System.out.println(responseBody);
    }

    /**
     * Post.
     *
     * @param url         the url
     * @param json        json request
     */
    public void post(String url, String json) {
        // create headers
        HttpHeaders headers = new HttpHeaders();
        // set `content-type` header
        headers.setContentType(MediaType.APPLICATION_JSON);
        // set `accept` header
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // build the request
        HttpEntity<String> request = new HttpEntity<>(json, headers);

        // send POST request
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
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

    public void diffUpdatePs(Professionnel left, Professionnel right) {

        String psUrl = apiBaseUrl + '/' + URLEncoder.encode(left.getNationalId(), StandardCharsets.UTF_8);

        if (left.nakedHash() != right.nakedHash()) {
            // update Ps basic attributes
            put(psUrl, jsonFormatter.nakedPsFromObject(right));
        }

        // diff professions
        String professionsUrl = psUrl + "/professions";

        Map<String, ExerciceProfessionnel> leftExPro = Maps
                .uniqueIndex(left.getProfessions(), ExerciceProfessionnel::getKey);
        Map<String, ExerciceProfessionnel> rightExPro = Maps
                .uniqueIndex(right.getProfessions(), ExerciceProfessionnel::getKey);
        MapDifference<String, ExerciceProfessionnel> diff = Maps.difference(leftExPro, rightExPro);

        diff.entriesOnlyOnLeft().forEach((k, v) ->
                delete(professionsUrl + '/' + URLEncoder.encode(v.getKey(), StandardCharsets.UTF_8)));
        diff.entriesOnlyOnRight().forEach((k, v) ->
                post(professionsUrl, jsonFormatter.jsonFromObject(v)));
        diff.entriesDiffering().forEach((k, v) ->
                diffUpdateExPro(v.leftValue(), v.rightValue(), professionsUrl));
    }

    private void diffUpdateExPro(ExerciceProfessionnel leftExPro, ExerciceProfessionnel rightExPro, String professionsUrl) {

        String exProUrl = professionsUrl + '/' + URLEncoder.encode(leftExPro.getKey(), StandardCharsets.UTF_8);

        if (leftExPro.nakedHash() != rightExPro.nakedHash()) {
            // update ExPro basic attributes
            put(exProUrl, jsonFormatter.nakedExProFromObject(rightExPro));
        }

        // diff expertises
        String expertiseUrl = exProUrl + "/expertises";

        Map<String, SavoirFaire> leftExpertises = Maps
                .uniqueIndex(leftExPro.getExpertises(), SavoirFaire::getKey);
        Map<String, SavoirFaire> rightExpertises = Maps
                .uniqueIndex(rightExPro.getExpertises(), SavoirFaire::getKey);
        MapDifference<String, SavoirFaire> expertiseDiff = Maps.difference(leftExpertises, rightExpertises);

        expertiseDiff.entriesOnlyOnLeft().forEach((k, v) ->
                delete(expertiseUrl + '/' + URLEncoder.encode(v.getKey(), StandardCharsets.UTF_8)));
        expertiseDiff.entriesOnlyOnRight().forEach((k, v) ->
                post(expertiseUrl, jsonFormatter.jsonFromObject(v)));
        expertiseDiff.entriesDiffering().forEach((k, v) ->
                put(expertiseUrl + '/' + URLEncoder.encode(v.rightValue().getKey(), StandardCharsets.UTF_8), jsonFormatter.jsonFromObject(v.rightValue())));

        // diff situations
        String situationUrl = exProUrl + "/situations";

        Map<String, SituationExercice> leftSituations = Maps
                .uniqueIndex(leftExPro.getWorkSituations(), SituationExercice::getKey);
        Map<String, SituationExercice> rightSituations = Maps
                .uniqueIndex(rightExPro.getWorkSituations(), SituationExercice::getKey);
        MapDifference<String, SituationExercice> situationDiff = Maps.difference(leftSituations, rightSituations);

        situationDiff.entriesOnlyOnLeft().forEach((k, v) ->
                delete(situationUrl + '/' + URLEncoder.encode(v.getKey(), StandardCharsets.UTF_8)));
        situationDiff.entriesOnlyOnRight().forEach((k, v) ->
                post(situationUrl, jsonFormatter.jsonFromObject(v)));
        situationDiff.entriesDiffering().forEach((k, v) ->
                put(situationUrl + '/' + URLEncoder.encode(v.rightValue().getKey(), StandardCharsets.UTF_8), jsonFormatter.jsonFromObject(v.rightValue())));

    }
}
