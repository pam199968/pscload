package fr.ans.psc.pscload.model.object;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class SituationExercice implements Serializable {

    @JsonProperty("modeCode")
    private String modeCode;

    @JsonProperty("activitySectorCode")
    private String activitySectorCode;

    @JsonProperty("pharmacistTableSectionCode")
    private String pharmacistTableSectionCode;

    @JsonProperty("roleCode")
    private String roleCode;

    // many to one
    private Structure structure;

    public SituationExercice(String[] items) {
        this.modeCode = items[20];
        this.activitySectorCode = items[21];
        this.pharmacistTableSectionCode = items[22];
        this.roleCode = items[23];
    }

}
