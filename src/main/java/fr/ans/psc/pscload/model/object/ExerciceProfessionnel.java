package fr.ans.psc.pscload.model.object;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

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
    private SavoirFaire[] expertises;

    @JsonProperty("workSituations")
    private SituationExercice[] workSituations;

    public ExerciceProfessionnel(String[] items) {
        this.code = items[13];
        this.categoryCode = items[14];
        this.salutationCode = items[15];
        this.lastName = items[16];
        this.firstName = items[17];
        this.expertises = new SavoirFaire[1];
        this.expertises[0] = new SavoirFaire(items);
        this.workSituations = new SituationExercice[1];
        this.workSituations[0] = new SituationExercice(items);
    }

}
