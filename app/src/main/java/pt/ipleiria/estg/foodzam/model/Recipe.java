package pt.ipleiria.estg.foodzam.model;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Recipe {

    private int id;
    private String title;
    private String image;

    @SerializedName("extendedIngredients")
    private List<Ingredient> ingredients;

    @SerializedName("instructions")
    private String steps;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public String getSteps() {
        return steps;
    }

    @Override
    public String toString() {
        return title;
    }
}
