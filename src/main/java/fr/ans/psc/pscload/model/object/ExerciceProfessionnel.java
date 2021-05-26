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
    private List<SavoirFaire> expertises = new ArrayList<>();

    @JsonProperty("workSituations")
    private List<SituationExercice> workSituations = new ArrayList<>();

    public ExerciceProfessionnel() {}

    public ExerciceProfessionnel(String[] items) {
        this.code = items[13];
        this.categoryCode = items[14];
        this.salutationCode = items[15];
        this.lastName = items[16];
        this.firstName = items[17];
        this.expertises.add(new SavoirFaire(items));
        this.workSituations.add(new SituationExercice(items));
    }

    public ExerciceProfessionnel(ExerciceProfessionnel exPro) {
        this.code = exPro.code;
        this.categoryCode = exPro.categoryCode;
        this.salutationCode = exPro.salutationCode;
        this.lastName = exPro.lastName;
        this.firstName = exPro.firstName;
    }

    public String getCompositeId() {
        String key = Objects.toString(code, "") +
                Objects.toString(categoryCode, "");
        if ("".equals(key)) {
            return "ND";
        }
        return key;
    }

    public List<SavoirFaire> getExpertises() {
        return expertises;
    }

    public List<SituationExercice> getWorkSituations() {
        return workSituations;
    }

    public int nakedHash() {
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
