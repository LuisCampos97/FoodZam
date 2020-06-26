package pt.ipleiria.estg.foodzam.model;

import com.google.gson.annotations.SerializedName;

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
}
