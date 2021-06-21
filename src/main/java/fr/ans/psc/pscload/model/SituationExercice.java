package fr.ans.psc.pscload.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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

    @JsonProperty("structures")
    private final List<StructureRef> structures = new ArrayList<>();

    public SituationExercice() {}

    public SituationExercice(String[] items) {
        this.modeCode = items[20];
        this.activitySectorCode = items[21];
        this.pharmacistTableSectionCode = items[22];
        this.roleCode = items[23];
        this.structures.add(new StructureRef(items[28]));  // structureTechnicalId
    }

    public List<StructureRef> getStructures() {
        return structures;
    }

    public String getSituationId() {
        String key = Objects.toString(modeCode, "") +
                Objects.toString(activitySectorCode, "") +
                Objects.toString(pharmacistTableSectionCode, "") +
                Objects.toString(roleCode, "");
        if ("".equals(key)) {
            return "ND";
        }
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SituationExercice)) return false;
        SituationExercice that = (SituationExercice) o;
        return Objects.equals(modeCode, that.modeCode) && Objects.equals(activitySectorCode, that.activitySectorCode) &&
                Objects.equals(pharmacistTableSectionCode, that.pharmacistTableSectionCode) && Objects.equals(roleCode, that.roleCode) &&
                Objects.equals(structures.stream().map(s -> Objects.hash(s.getStructureId())).reduce(0, Integer::sum),
                        that.structures.stream().map(s -> Objects.hash(s.getStructureId())).reduce(0, Integer::sum));
    }

    @Override
    public int hashCode() {
        return Objects.hash(modeCode, activitySectorCode, pharmacistTableSectionCode, roleCode,
                getStructures().stream().map(s -> Objects.hash(s.getStructureId())).reduce(0, Integer::sum));
    }

    @Override
    public String toString() {
        return modeCode + '|' + activitySectorCode + '|' + pharmacistTableSectionCode + '|' + roleCode;
    }
}
