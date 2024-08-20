package org.golfballdm.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class FreeResident {
    @JsonProperty("PersonID")
    int personID;

    @JsonProperty("Name")
    String name;

    @JsonProperty("FamilyID")
    int familyID;

    @JsonProperty("Page")
    int page;

    @JsonProperty("Age")
    int age;

    @JsonProperty("Sex")
    char sex;

    @JsonProperty("Color")
    char color;

    @JsonProperty("Profession")
    String profession;

    @JsonProperty("Married")
    char married;

    @JsonProperty("Schooling")
    char schooling;

    @JsonProperty("IlliterateOver20")
    char illiterate;
}
