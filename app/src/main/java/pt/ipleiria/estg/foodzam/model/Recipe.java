package pt.ipleiria.estg.foodzam.model;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

public class Recipe {

    private int id;
    private String title;
    private Uri image;

    @SerializedName("body")
    private String text;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Uri getImage() {
        return image;
    }

    public String getText() {
        return text;
    }
}
