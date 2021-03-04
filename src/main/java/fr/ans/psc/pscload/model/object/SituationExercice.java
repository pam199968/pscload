package fr.ans.psc.pscload.model.object;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Objects;

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

    public SituationExercice() {}

    public SituationExercice(String[] items) {
        this.modeCode = items[20];
        this.activitySectorCode = items[21];
        this.pharmacistTableSectionCode = items[22];
        this.roleCode = items[23];
        this.structure = new Structure(items);
    }

    public String getKey() {
        return Objects.toString(modeCode, "") +
                Objects.toString(structure.getKey(), "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SituationExercice)) return false;
        SituationExercice that = (SituationExercice) o;
        return Objects.equals(modeCode, that.modeCode) && Objects.equals(activitySectorCode, that.activitySectorCode) && Objects.equals(pharmacistTableSectionCode, that.pharmacistTableSectionCode) && Objects.equals(roleCode, that.roleCode) && Objects.equals(structure, that.structure);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modeCode, activitySectorCode, pharmacistTableSectionCode, roleCode, structure);
    }

    @Override
    public String toString() {
        return modeCode + '|' + activitySectorCode + '|' + pharmacistTableSectionCode + '|' + roleCode + '|' + structure;
    }
}
