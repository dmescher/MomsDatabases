package org.golfballdm.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FreeResident {

    @JsonProperty("PersonID")
    private int personID;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("FamilyID")
    private int familyID;

    @JsonProperty("Page")
    private int page;

    @JsonProperty("Age")
    private int age;

    @JsonProperty("Sex")
    private char sex;

    @JsonProperty("Color")
    private char color;

    @JsonProperty("Profession")
    private String profession;

    @JsonProperty("Married")
    private char married;

    @JsonProperty("Schooling")
    private char schooling;

    @JsonProperty("IlliterateOver20")
    private char illiterateOver20;
}
