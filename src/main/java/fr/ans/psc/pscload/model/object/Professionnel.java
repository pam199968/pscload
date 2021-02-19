package fr.ans.psc.pscload.model.object;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * The type Professionnel.
 */
public class Professionnel implements Serializable {

    @JsonProperty("idType")
    private String idType;

    @JsonProperty("id")
    private String id;

    @JsonProperty("nationalId")
    private String nationalId;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("dateOfBirth")
    private String dateOfBirth;

    @JsonProperty("birthAddressCode")
    private String birthAddressCode;

    @JsonProperty("birthCountryCode")
    private String birthCountryCode;

    @JsonProperty("birthAddress")
    private String birthAddress;

    @JsonProperty("genderCode")
    private String genderCode;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("email")
    private String email;

    @JsonProperty("salutationCode")
    private String salutationCode;

    @JsonProperty("professions")
    private ExerciceProfessionnel[] professions;

    public Professionnel() {}

    public Professionnel(String[] items) {
        this.idType = items[0];
        this.id = items[1];
        this.nationalId = items[2];
        this.lastName = items[3];
        this.firstName = items[4];
        this.dateOfBirth = items[5];
        this.birthAddressCode = items[6];
        this.birthCountryCode = items[7];
        this.birthAddress = items[8];
        this.genderCode = items[9];
        this.phone = items[10];
        this.email = items[11];
        this.salutationCode = items[12];
        this.professions = new ExerciceProfessionnel[1];
        this.professions[0] = new ExerciceProfessionnel(items);
    }

}
