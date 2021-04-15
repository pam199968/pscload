package fr.ans.psc.pscload.model.object;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Objects;

public class Structure implements Serializable {

    @JsonProperty("siteSIRET")
    private String siteSIRET;

    @JsonProperty("siteSIREN")
    private String siteSIREN;

    @JsonProperty("siteFINESS")
    private String siteFINESS;

    @JsonProperty("legalEstablishmentFINESS")
    private String legalEstablishmentFINESS;

    @JsonProperty("structureTechnicalId")
    private String structureTechnicalId;

    @JsonProperty("legalCommercialName")
    private String legalCommercialName;

    @JsonProperty("publicCommercialName")
    private String publicCommercialName;

    @JsonProperty("recipientAdditionalInfo")
    private String recipientAdditionalInfo;

    @JsonProperty("geoLocationAdditionalInfo")
    private String geoLocationAdditionalInfo;

    @JsonProperty("streetNumber")
    private String streetNumber;

    @JsonProperty("streetNumberRepetitionIndex")
    private String streetNumberRepetitionIndex;

    @JsonProperty("streetCategoryCode")
    private String streetCategoryCode;

    @JsonProperty("streetLabel")
    private String streetLabel;

    @JsonProperty("distributionMention")
    private String distributionMention;

    @JsonProperty("cedexOffice")
    private String cedexOffice;

    @JsonProperty("postalCode")
    private String postalCode;

    @JsonProperty("communeCode")
    private String communeCode;

    @JsonProperty("countryCode")
    private String countryCode;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("phone2")
    private String phone2;

    @JsonProperty("fax")
    private String fax;

    @JsonProperty("email")
    private String email;

    @JsonProperty("departmentCode")
    private String departmentCode;

    @JsonProperty("oldStructureId")
    private String oldStructureId;

    @JsonProperty("registrationAuthority")
    private String registrationAuthority;

    public Structure() {}

    public Structure(String[] items) {
        this.siteSIRET = items[24];
        this.siteSIREN = items[25];
        this.siteFINESS = items[26];
        this.legalEstablishmentFINESS = items[27];
        this.structureTechnicalId = items[28];
        this.legalCommercialName = items[29];
        this.publicCommercialName = items[30];
        this.recipientAdditionalInfo = items[31];
        this.geoLocationAdditionalInfo = items[32];
        this.streetNumber = items[33];
        this.streetNumberRepetitionIndex = items[34];
        this.streetCategoryCode = items[35];
        this.streetLabel = items[36];
        this.distributionMention = items[37];
        this.cedexOffice = items[38];
        this.postalCode = items[39];
        this.communeCode = items[40];
        this.countryCode = items[41];
        this.phone = items[42];
        this.phone2 = items[43];
        this.fax = items[44];
        this.email = items[45];
        this.departmentCode = items[46];
        this.oldStructureId = items[47];
        this.registrationAuthority = items[48];
    }

    public String getKey() {
        return Objects.toString(siteSIRET, "") +
                Objects.toString(siteSIREN, "") +
                Objects.toString(siteFINESS, "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Structure)) return false;
        Structure structure = (Structure) o;
        return Objects.equals(siteSIRET, structure.siteSIRET) && Objects.equals(siteSIREN, structure.siteSIREN) && Objects.equals(siteFINESS, structure.siteFINESS) && Objects.equals(legalEstablishmentFINESS, structure.legalEstablishmentFINESS) && Objects.equals(structureTechnicalId, structure.structureTechnicalId) && Objects.equals(legalCommercialName, structure.legalCommercialName) && Objects.equals(publicCommercialName, structure.publicCommercialName) && Objects.equals(recipientAdditionalInfo, structure.recipientAdditionalInfo) && Objects.equals(geoLocationAdditionalInfo, structure.geoLocationAdditionalInfo) && Objects.equals(streetNumber, structure.streetNumber) && Objects.equals(streetNumberRepetitionIndex, structure.streetNumberRepetitionIndex) && Objects.equals(streetCategoryCode, structure.streetCategoryCode) && Objects.equals(streetLabel, structure.streetLabel) && Objects.equals(distributionMention, structure.distributionMention) && Objects.equals(cedexOffice, structure.cedexOffice) && Objects.equals(postalCode, structure.postalCode) && Objects.equals(communeCode, structure.communeCode) && Objects.equals(countryCode, structure.countryCode) && Objects.equals(phone, structure.phone) && Objects.equals(phone2, structure.phone2) && Objects.equals(fax, structure.fax) && Objects.equals(email, structure.email) && Objects.equals(departmentCode, structure.departmentCode) && Objects.equals(oldStructureId, structure.oldStructureId) && Objects.equals(registrationAuthority, structure.registrationAuthority);
    }

    @Override
    public int hashCode() {
        return Objects.hash(siteSIRET, siteSIREN, siteFINESS, legalEstablishmentFINESS, structureTechnicalId, legalCommercialName, publicCommercialName, recipientAdditionalInfo, geoLocationAdditionalInfo, streetNumber, streetNumberRepetitionIndex, streetCategoryCode, streetLabel, distributionMention, cedexOffice, postalCode, communeCode, countryCode, phone, phone2, fax, email, departmentCode, oldStructureId, registrationAuthority);
    }

    @Override
    public String toString() {
        return siteSIRET + '|' + siteSIREN + '|' + siteFINESS + '|' + legalEstablishmentFINESS + '|' + structureTechnicalId + '|' + legalCommercialName + '|' + publicCommercialName + '|' + recipientAdditionalInfo + '|' + geoLocationAdditionalInfo + '|' + streetNumber + '|' + streetNumberRepetitionIndex + '|' + streetCategoryCode + '|' + streetLabel + '|' + distributionMention + '|' + cedexOffice + '|' + postalCode + '|' + communeCode + '|' + countryCode + '|' + phone + '|' + phone2 + '|' + fax + '|' + email + '|' + departmentCode + '|' + oldStructureId + '|' + registrationAuthority;
    }
}
