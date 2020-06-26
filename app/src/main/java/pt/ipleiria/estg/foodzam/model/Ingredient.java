package pt.ipleiria.estg.foodzam.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Ingredient {

    private String name;

    @SerializedName("original")
    private String recipeLine;

    public String getName() {
        return name;
    }

    public String getRecipeLine() {
        return recipeLine;
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "name='" + name + '\'' +
                ", recipeLine='" + recipeLine + '\'' +
                '}';
    }
}
