package fr.ans.psc.pscload.service;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import fr.ans.psc.pscload.component.JsonFormatter;
import fr.ans.psc.pscload.model.object.ExerciceProfessionnel;
import fr.ans.psc.pscload.model.object.Professionnel;
import fr.ans.psc.pscload.model.object.SavoirFaire;
import fr.ans.psc.pscload.model.object.SituationExercice;
import fr.ans.psc.pscload.model.object.response.PsListResponse;
import fr.ans.psc.pscload.model.object.response.PsResponse;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ForkJoinPool;

/**
 * The type Psc rest api.
 */
@Service
public class PscRestApi {

    /**
     * The logger.
     */
    private static final Logger log = LoggerFactory.getLogger(PscRestApi.class);

    private final Request.Builder requestBuilder;

    private final OkHttpClient client;

    @Autowired
    private final JsonFormatter jsonFormatter;

    @Value("${ps.api.base.url}")
    private String apiBaseUrl;

    @Value("${custom.thread.count}")
    private int numOfThreads;

    /**
     * Instantiates a new Psc rest api.
     *
     * @param client         the client
     * @param requestBuilder the request builder
     * @param jsonFormatter  json formatter
     */
    public PscRestApi(OkHttpClient client, Request.Builder requestBuilder, JsonFormatter jsonFormatter) {
        // set connection and read timeouts
        this.client = client;
        this.requestBuilder = requestBuilder;
        this.jsonFormatter = jsonFormatter;
    }

    /**
     * Instantiates a new Psc rest api.
     */
    public PscRestApi() {
        this.client = new OkHttpClient();
        this.requestBuilder = new Request.Builder();
        this.jsonFormatter = new JsonFormatter();
    }

    /**
     * Gets ps list.
     *
     * @param url the url
     * @return the ps list
     */
    public PsListResponse getPsList(String url) {
        Request request = requestBuilder
                .url(url)
                .build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            String responseBody = Objects.requireNonNull(response.body()).string();
            log.info("response body: {}", responseBody);
            return jsonFormatter.psListFromJson(Objects.requireNonNull(response.body()).string());
        } catch (IOException e) {
            log.error("error: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Gets ps.
     *
     * @param url the url
     * @return the ps
     */
    public PsResponse getPs(String url) {
        Request request = requestBuilder
                .url(url)
                .build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            String responseBody = Objects.requireNonNull(response.body()).string();
            log.info("response body: {}", responseBody);
            return jsonFormatter.psFromJson(Objects.requireNonNull(response.body()).string());
        } catch (IOException e) {
            log.error("error: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Put.
     *
     * @param url  the url
     * @param json json request
     */
    public void put(String url, String json) {
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = requestBuilder
                .url(url)
                .put(body)
                .build();
        sendRequest(request);
    }

    /**
     * Post.
     *
     * @param url  the url
     * @param json json request
     */
    public void post(String url, String json) {
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = requestBuilder
                .url(url)
                .post(body)
                .build();
        sendRequest(request);
    }

    /**
     * Delete.
     *
     * @param url the url
     */
    public void delete(String url) {
        Request request = requestBuilder
                .url(url)
                .delete()
                .build();
        sendRequest(request);
    }

    private void sendRequest(Request request) {
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            String responseBody = Objects.requireNonNull(response.body()).string();
            log.info("response body: {}", responseBody);
            response.close();
        } catch (IOException e) {
            log.error("error: {}", e.getMessage());
        }
    }

    /**
     * Upload ps map.
     *
     * @param psMap the ps map
     */
    public void uploadPsMap(Map<String, Professionnel> psMap) {
        HashSet<Professionnel> psSet = new HashSet<>(psMap.values());
        ForkJoinPool customThreadPool = new ForkJoinPool(numOfThreads);
        try {
            customThreadPool.submit(
                    () -> psSet.parallelStream().forEach(ps -> put(apiBaseUrl, jsonFormatter.jsonFromObject(ps))));
        } finally {
            customThreadPool.shutdown();
        }
    }

    /**
     * Diff PS maps.
     *
     * @param original OG PS map
     * @param revised  the revised PS map
     */
    public void diffPsMaps(Map<String, Professionnel> original, Map<String, Professionnel> revised) {
        MapDifference<String, Professionnel> diff = Maps.difference(original, revised);
        diff.entriesOnlyOnLeft().forEach((k, v) ->
                delete(apiBaseUrl + '/' + URLEncoder.encode(v.getNationalId(), StandardCharsets.UTF_8)));
        diff.entriesOnlyOnRight().forEach((k, v) -> post(apiBaseUrl, jsonFormatter.jsonFromObject(v)));
        diff.entriesDiffering().forEach((k, v) -> diffUpdatePs(v.leftValue(), v.rightValue()));
    }

    /**
     * Diff update ps.
     *
     * @param left  the left
     * @param right the right
     */
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
