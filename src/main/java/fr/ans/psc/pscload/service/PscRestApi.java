package fr.ans.psc.pscload.service;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import fr.ans.psc.pscload.component.JsonFormatter;
import fr.ans.psc.pscload.model.*;
import fr.ans.psc.pscload.model.response.PsListResponse;
import fr.ans.psc.pscload.model.response.PsResponse;
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

    @Value("${api.base.url}")
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
        /*ForkJoinPool customThreadPool = new ForkJoinPool(numOfThreads-2);
        try {
            customThreadPool.submit(
                    () -> psSet.parallelStream().forEach(ps -> put(getPsUrl(), jsonFormatter.jsonFromObject(ps))));
        } finally {
            customThreadPool.shutdown();
        }*/
        psSet.parallelStream().forEach(ps -> put(getPsUrl(), jsonFormatter.jsonFromObject(ps)));
    }

    /**
     * Upload structure map.
     *
     * @param structureMap the structure map
     */
    public void uploadStructureMap(Map<String, Structure> structureMap) {
        HashSet<Structure> structureSet = new HashSet<>(structureMap.values());
        /*ForkJoinPool customThreadPool = new ForkJoinPool(2);
        try {
            customThreadPool.submit(
                    () -> structureSet.parallelStream().forEach(structure -> put(getStructureUrl(), jsonFormatter.jsonFromObject(structure))));
        } finally {
            customThreadPool.shutdown();
        }*/
        structureSet.parallelStream().forEach(structure -> put(getStructureUrl(), jsonFormatter.jsonFromObject(structure)));
    }

    /**
     * Diff structure maps.
     *
     * @param original the original
     * @param revised  the revised
     */
    public void diffStructureMaps(Map<String, Structure> original, Map<String, Structure> revised) {
        MapDifference<String, Structure> diff = Maps.difference(original, revised);
        diff.entriesOnlyOnLeft().forEach((k, v) ->
                delete(getStructureUrl() + '/' + URLEncoder.encode(v.getStructureId(), StandardCharsets.UTF_8)));
        diff.entriesOnlyOnRight().forEach((k, v) -> post(getStructureUrl(), jsonFormatter.jsonFromObject(v)));
        diff.entriesDiffering().forEach((k, v) -> put(getStructureUrl(v.leftValue().getStructureId()), jsonFormatter.jsonFromObject(v.rightValue())));
    }

    /**
     * Diff PS maps.
     *
     * @param original OG PS map
     * @param revised  the revised PS map
     */
    public void diffPsMaps(Map<String, Professionnel> original, Map<String, Professionnel> revised) {
        MapDifference<String, Professionnel> diff = Maps.difference(original, revised);
        diff.entriesOnlyOnLeft().forEach((k, v) -> delete(getPsUrl(v.getNationalId())));
        diff.entriesOnlyOnRight().forEach((k, v) -> post(getPsUrl(), jsonFormatter.jsonFromObject(v)));
        diff.entriesDiffering().forEach((k, v) -> diffUpdatePs(v.leftValue(), v.rightValue()));
    }

    /**
     * Diff update ps.
     *
     * @param left  the left
     * @param right the right
     */
    public void diffUpdatePs(Professionnel left, Professionnel right) {
        String psUrl = getPsUrl(left.getNationalId());

        if (left.nakedHash() != right.nakedHash()) {
            // update Ps basic attributes
            put(psUrl, jsonFormatter.nakedPsFromObject(right));
        }

        // diff professions
        Map<String, ExerciceProfessionnel> leftExPro = Maps
                .uniqueIndex(left.getProfessions(), ExerciceProfessionnel::getProfessionId);
        Map<String, ExerciceProfessionnel> rightExPro = Maps
                .uniqueIndex(right.getProfessions(), ExerciceProfessionnel::getProfessionId);
        MapDifference<String, ExerciceProfessionnel> diff = Maps.difference(leftExPro, rightExPro);

        diff.entriesOnlyOnLeft().forEach((k, v) -> delete(getExProUrl(psUrl, v.getProfessionId())));
        diff.entriesOnlyOnRight().forEach((k, v) -> post(getExProUrl(psUrl), jsonFormatter.jsonFromObject(v)));
        diff.entriesDiffering().forEach((k, v) -> diffUpdateExPro(v.leftValue(), v.rightValue(), psUrl));
    }

    private void diffUpdateExPro(ExerciceProfessionnel leftExPro, ExerciceProfessionnel rightExPro, String psUrl) {
        String exProUrl = getExProUrl(psUrl, leftExPro.getProfessionId());

        if (leftExPro.nakedHash() != rightExPro.nakedHash()) {
            // update ExPro basic attributes
            put(exProUrl, jsonFormatter.nakedExProFromObject(rightExPro));
        }

        // diff expertises
        Map<String, SavoirFaire> leftExpertises = Maps
                .uniqueIndex(leftExPro.getExpertises(), SavoirFaire::getExpertiseId);
        Map<String, SavoirFaire> rightExpertises = Maps
                .uniqueIndex(rightExPro.getExpertises(), SavoirFaire::getExpertiseId);
        MapDifference<String, SavoirFaire> expertiseDiff = Maps.difference(leftExpertises, rightExpertises);

        expertiseDiff.entriesOnlyOnLeft().forEach((k, v) -> delete(getExpertiseUrl(exProUrl, v.getExpertiseId())));
        expertiseDiff.entriesOnlyOnRight().forEach((k, v) -> post(getExpertiseUrl(exProUrl), jsonFormatter.jsonFromObject(v)));
        expertiseDiff.entriesDiffering().forEach((k, v) ->
                put(getExpertiseUrl(exProUrl, v.rightValue().getExpertiseId()), jsonFormatter.jsonFromObject(v.rightValue())));

        // diff situations
        Map<String, SituationExercice> leftSituations = Maps
                .uniqueIndex(leftExPro.getWorkSituations(), SituationExercice::getSituationId);
        Map<String, SituationExercice> rightSituations = Maps
                .uniqueIndex(rightExPro.getWorkSituations(), SituationExercice::getSituationId);
        MapDifference<String, SituationExercice> situationDiff = Maps.difference(leftSituations, rightSituations);

        situationDiff.entriesOnlyOnLeft().forEach((k, v) -> delete(getSituationUrl(exProUrl, v.getSituationId())));
        situationDiff.entriesOnlyOnRight().forEach((k, v) -> post(getSituationUrl(exProUrl), jsonFormatter.jsonFromObject(v)));
        situationDiff.entriesDiffering().forEach((k, v) ->
                put(getSituationUrl(exProUrl, v.rightValue().getSituationId()), jsonFormatter.jsonFromObject(v.rightValue())));
    }

    /**
     * Gets ps url.
     *
     * @return the ps url
     */
    public String getPsUrl() {
        return apiBaseUrl + "/ps";
    }

    /**
     * Gets ps url.
     *
     * @param id the id
     * @return the ps url
     */
    public String getPsUrl(String id) {
        return getPsUrl() + "/" + URLEncoder.encode(id, StandardCharsets.UTF_8);
    }

    /**
     * Gets ex pro url.
     *
     * @param psUrl the ps url
     * @return the ex pro url
     */
    public String getExProUrl(String psUrl) {
        return psUrl + "/professions";
    }

    /**
     * Gets ex pro url.
     *
     * @param psUrl the ps url
     * @param id    the id
     * @return the ex pro url
     */
    public String getExProUrl(String psUrl, String id) {
        return getExProUrl(psUrl) + '/' + URLEncoder.encode(id, StandardCharsets.UTF_8);
    }

    /**
     * Gets expertise url.
     *
     * @param exProUrl the ex pro url
     * @return the expertise url
     */
    public String getExpertiseUrl(String exProUrl) {
        return  exProUrl + "/expertises";
    }

    /**
     * Gets expertise url.
     *
     * @param exProUrl the ex pro url
     * @param id       the id
     * @return the expertise url
     */
    public String getExpertiseUrl(String exProUrl, String id) {
        return  getExpertiseUrl(exProUrl) + '/' + URLEncoder.encode(id, StandardCharsets.UTF_8);
    }

    /**
     * Gets situation url.
     *
     * @param exProUrl the ex pro url
     * @return the situation url
     */
    public String getSituationUrl(String exProUrl) {
        return  exProUrl + "/situations";
    }

    /**
     * Gets situation url.
     *
     * @param exProUrl the ex pro url
     * @param id       the id
     * @return the situation url
     */
    public String getSituationUrl(String exProUrl, String id) {
        return  getSituationUrl(exProUrl) + '/' + URLEncoder.encode(id, StandardCharsets.UTF_8);
    }

    /**
     * Gets structure url.
     *
     * @return the structure url
     */
    public String getStructureUrl() {
        return apiBaseUrl + "/structures";
    }

    /**
     * Gets structure url.
     *
     * @param id the id
     * @return the structure url
     */
    public String getStructureUrl(String id) {
        return getStructureUrl() + '/' + URLEncoder.encode(id, StandardCharsets.UTF_8);
    }

}
