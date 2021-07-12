package fr.ans.psc.pscload.service;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import fr.ans.psc.pscload.component.JsonFormatter;
import fr.ans.psc.pscload.metrics.CustomMetrics;
import fr.ans.psc.pscload.model.*;
import fr.ans.psc.pscload.service.task.Create;
import fr.ans.psc.pscload.service.task.Delete;
import fr.ans.psc.pscload.service.task.Task;
import fr.ans.psc.pscload.service.task.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The type Psc rest api.
 */
@Service
public class PscRestApi {

    private final Set<Task> tasks = new HashSet<>();

    @Autowired
    private CustomMetrics customMetrics;

    @Autowired
    private JsonFormatter jsonFormatter;

    @Value("${api.base.url}")
    private String apiBaseUrl;

    @Value("${fixed.thread.pool}")
    private int nThreads;

    /**
     * Diff PS maps.
     *
     * @param original OG PS map
     * @param revised  the revised PS map
     * @return the map difference
     */
    public MapDifference<String, Professionnel> diffPsMaps(Map<String, Professionnel> original, Map<String, Professionnel> revised) {
        MapDifference<String, Professionnel> psDiff = Maps.difference(original, revised);

        customMetrics.getAppGauges().get(CustomMetrics.CustomMetric.PS_DELETE_SIZE).set(psDiff.entriesOnlyOnLeft().size());
        customMetrics.getAppGauges().get(CustomMetrics.CustomMetric.PS_CREATE_SIZE).set(psDiff.entriesOnlyOnRight().size());
        customMetrics.getAppGauges().get(CustomMetrics.CustomMetric.PS_UPDATE_SIZE).set(psDiff.entriesDiffering().size());

        return psDiff;
    }

    /**
     * Diff structure maps.
     *
     * @param original the original
     * @param revised  the revised
     * @return the map difference
     */
    public MapDifference<String, Structure> diffStructureMaps(Map<String, Structure> original, Map<String, Structure> revised) {
        MapDifference<String, Structure> structureDiff = Maps.difference(original, revised);

        customMetrics.getAppGauges().get(CustomMetrics.CustomMetric.STRUCTURE_DELETE_SIZE).set(structureDiff.entriesOnlyOnLeft().size());
        customMetrics.getAppGauges().get(CustomMetrics.CustomMetric.STRUCTURE_CREATE_SIZE).set(structureDiff.entriesOnlyOnRight().size());
        customMetrics.getAppGauges().get(CustomMetrics.CustomMetric.STRUCTURE_UPDATE_SIZE).set(structureDiff.entriesDiffering().size());

        return structureDiff;
    }

    /**
     * Upload changes.
     *
     * @param psDiff        the ps diff
     * @param structureDiff the structure diff
     * @throws InterruptedException the interrupted exception
     */
    public void uploadChanges(MapDifference<String, Professionnel> psDiff,
                              MapDifference<String, Structure> structureDiff) throws InterruptedException {
        injectPsDiffTasks(psDiff);
        injectStructuresDiffTasks(structureDiff);
        // run all tasks in parallel blocking all other processes until all tasks complete
        final ExecutorService execService = Executors.newFixedThreadPool(nThreads);
        execService.invokeAll(tasks);
        execService.shutdown();
        tasks.clear();
    }

    private void injectPsDiffTasks(MapDifference<String, Professionnel> diff) {
        customMetrics.getAppGauges().get(CustomMetrics.CustomMetric.PS_DELETE_PROGRESSION).set(0);
        customMetrics.getAppGauges().get(CustomMetrics.CustomMetric.PS_CREATE_PROGRESSION).set(0);
        customMetrics.getAppGauges().get(CustomMetrics.CustomMetric.PS_UPDATE_PROGRESSION).set(0);

        diff.entriesOnlyOnLeft().values().forEach(ps -> tasks.add(new Delete(getPsUrl(ps.getNationalId()))));
        diff.entriesOnlyOnRight().values().forEach(ps -> tasks.add(new Create(getPsUrl(), jsonFormatter.jsonFromObject(ps))));
        diff.entriesDiffering().values().forEach(v -> injectPsUpdateTasks(v.leftValue(), v.rightValue()));
    }

    private void injectStructuresDiffTasks(MapDifference<String, Structure> diff) {
        customMetrics.getAppGauges().get(CustomMetrics.CustomMetric.STRUCTURE_DELETE_PROGRESSION).set(0);
        customMetrics.getAppGauges().get(CustomMetrics.CustomMetric.STRUCTURE_CREATE_PROGRESSION).set(0);
        customMetrics.getAppGauges().get(CustomMetrics.CustomMetric.STRUCTURE_UPDATE_PROGRESSION).set(0);

        diff.entriesOnlyOnLeft().values().forEach(structure -> tasks.add(new Delete(getStructureUrl(structure.getStructureId()))));
        diff.entriesOnlyOnRight().values().forEach(structure -> tasks.add(new Create(getStructureUrl(), jsonFormatter.jsonFromObject(structure))));
        diff.entriesDiffering().values().forEach(v -> tasks.add(new Update(
                getStructureUrl(v.leftValue().getStructureId()), jsonFormatter.jsonFromObject(v.rightValue()))));
    }

    private void injectPsUpdateTasks(Professionnel left, Professionnel right) {
        String psUrl = getPsUrl(left.getNationalId());

        if (left.nakedHash() != right.nakedHash()) {
            // update Ps basic attributes
            tasks.add(new Update(psUrl, jsonFormatter.nakedPsFromObject(right)));
        }

        // diff professions
        Map<String, ExerciceProfessionnel> leftExPro = Maps
                .uniqueIndex(left.getProfessions(), ExerciceProfessionnel::getProfessionId);
        Map<String, ExerciceProfessionnel> rightExPro = Maps
                .uniqueIndex(right.getProfessions(), ExerciceProfessionnel::getProfessionId);
        MapDifference<String, ExerciceProfessionnel> exProDiff = Maps.difference(leftExPro, rightExPro);

        exProDiff.entriesOnlyOnLeft().forEach((k, v) -> tasks.add(new Delete(getExProUrl(psUrl, v.getProfessionId()))));
        exProDiff.entriesOnlyOnRight().forEach((k, v) -> tasks.add(new Create(getExProUrl(psUrl), jsonFormatter.jsonFromObject(v))));
        exProDiff.entriesDiffering().forEach((k, v) -> injectExProUpdateTasks(v.leftValue(), v.rightValue(), psUrl));
    }

    private void injectExProUpdateTasks(ExerciceProfessionnel leftExPro, ExerciceProfessionnel rightExPro, String psUrl) {
        String exProUrl = getExProUrl(psUrl, leftExPro.getProfessionId());

        if (leftExPro.nakedHash() != rightExPro.nakedHash()) {
            // update ExPro basic attributes
            tasks.add(new Update(exProUrl, jsonFormatter.nakedExProFromObject(rightExPro)));
        }

        // diff expertises
        Map<String, SavoirFaire> leftExpertises = Maps
                .uniqueIndex(leftExPro.getExpertises(), SavoirFaire::getExpertiseId);
        Map<String, SavoirFaire> rightExpertises = Maps
                .uniqueIndex(rightExPro.getExpertises(), SavoirFaire::getExpertiseId);
        MapDifference<String, SavoirFaire> expertiseDiff = Maps.difference(leftExpertises, rightExpertises);

        expertiseDiff.entriesOnlyOnLeft().forEach((k, v) -> tasks.add(new Delete(getExpertiseUrl(exProUrl, v.getExpertiseId()))));
        expertiseDiff.entriesOnlyOnRight().forEach((k, v) -> tasks.add(new Create(getExpertiseUrl(exProUrl), jsonFormatter.jsonFromObject(v))));
        expertiseDiff.entriesDiffering().forEach((k, v) -> tasks.add(new Update(
                getExpertiseUrl(exProUrl, v.rightValue().getExpertiseId()), jsonFormatter.jsonFromObject(v.rightValue()))));

        // diff situations
        Map<String, SituationExercice> leftSituations = Maps
                .uniqueIndex(leftExPro.getWorkSituations(), SituationExercice::getSituationId);
        Map<String, SituationExercice> rightSituations = Maps
                .uniqueIndex(rightExPro.getWorkSituations(), SituationExercice::getSituationId);
        MapDifference<String, SituationExercice> situationDiff = Maps.difference(leftSituations, rightSituations);

        situationDiff.entriesOnlyOnLeft().forEach((k, v) -> tasks.add(new Delete(getSituationUrl(exProUrl, v.getSituationId()))));
        situationDiff.entriesOnlyOnRight().forEach((k, v) -> tasks.add(new Create(getSituationUrl(exProUrl), jsonFormatter.jsonFromObject(v))));
        situationDiff.entriesDiffering().forEach((k, v) ->
                tasks.add(new Update(getSituationUrl(exProUrl, v.rightValue().getSituationId()), jsonFormatter.jsonFromObject(v.rightValue()))));
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
