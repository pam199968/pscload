package fr.ans.psc.pscload.model.object;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.*;

public class ExerciceProfessionnel implements Serializable {

    @JsonProperty("code")
    private String code;

    @JsonProperty("categoryCode")
    private String categoryCode;

    @JsonProperty("salutationCode")
    private String salutationCode;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("expertises")
    private Map<String, SavoirFaire> expertises = new HashMap<>();

    @JsonProperty("workSituations")
    private Map<String, SituationExercice> workSituations = new HashMap<>();

    public ExerciceProfessionnel() {}

    public ExerciceProfessionnel(String[] items) {
        this.code = items[13];
        this.categoryCode = items[14];
        this.salutationCode = items[15];
        this.lastName = items[16];
        this.firstName = items[17];
        Map.Entry<String, SavoirFaire> expertise = new SavoirFaire(items).getEntry();
        this.expertises.put(expertise.getKey(), expertise.getValue());
        Map.Entry<String, SituationExercice> situation = new SituationExercice(items).getEntry();
        this.workSituations.put(situation.getKey(), situation.getValue());
    }

    public Map.Entry<String, ExerciceProfessionnel> getEntry() {
        String exProKey = Objects.toString(code + categoryCode, "");
        return new AbstractMap.SimpleEntry<>(exProKey, this);
    }

    public Map<String, SavoirFaire> getExpertises() {
        return expertises;
    }

    public void setExpertises(Map<String, SavoirFaire> expertises) {
        this.expertises = expertises;
    }

    public Map<String, SituationExercice> getWorkSituations() {
        return workSituations;
    }

    public void setWorkSituations(Map<String, SituationExercice> workSituations) {
        this.workSituations = workSituations;
    }

    public int naked() {
        return Objects.hash(code, categoryCode, salutationCode, lastName, firstName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExerciceProfessionnel)) return false;
        ExerciceProfessionnel that = (ExerciceProfessionnel) o;
        return Objects.equals(code, that.code) && Objects.equals(categoryCode, that.categoryCode) && Objects.equals(salutationCode, that.salutationCode) && Objects.equals(lastName, that.lastName) && Objects.equals(firstName, that.firstName) && Objects.equals(getExpertises(), that.getExpertises()) && Objects.equals(getWorkSituations(), that.getWorkSituations());
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, categoryCode, salutationCode, lastName, firstName, getExpertises(), getWorkSituations());
    }

    @Override
    public String toString() {
        return code + '|' + categoryCode + '|' + salutationCode + '|' + lastName + '|' + firstName + '|' +
                expertises + '|' + workSituations;
    }
}
