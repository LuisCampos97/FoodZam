package pt.ipleiria.estg.foodzam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import iammert.com.expandablelib.ExpandableLayout;
import iammert.com.expandablelib.Section;
import pt.ipleiria.estg.foodzam.helpers.SpoonacularAPI;
import pt.ipleiria.estg.foodzam.model.Ingredient;
import pt.ipleiria.estg.foodzam.model.Recipe;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RecipeDetailsActivity extends AppCompatActivity {

    Retrofit retrofit;
    SpoonacularAPI spoonacularAPI;

    private TextView textViewTitle, textViewTime, textViewPeople;
    private ImageView imageViewRecipe;
    private ExpandableLayout expandableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        retrofit = new Retrofit.Builder().baseUrl("https://api.spoonacular.com/")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        spoonacularAPI = retrofit.create(SpoonacularAPI.class);

        textViewTitle = findViewById(R.id.textViewTitle);
        textViewTime = findViewById(R.id.textViewTime);
        textViewPeople = findViewById(R.id.textViewPeople);
        imageViewRecipe = findViewById(R.id.imageViewRecipe);
        expandableLayout = findViewById(R.id.expandable_layout);

        Bundle bundle = getIntent().getExtras();
        int recipeId = bundle.getInt("recipeId");

        Call<Recipe> listCall = spoonacularAPI.getRecipeInformation(recipeId, getString(R.string.spoonacular_api_key));

        listCall.enqueue(new Callback<Recipe>() {
            @Override
            public void onResponse(Call<Recipe> call, Response<Recipe> response) {
                if (!response.isSuccessful()) {
                    System.out.println("Code: " + response.code());
                    return;
                }

                Recipe recipe = response.body();
                if(recipe != null) {
                    textViewTitle.setText(recipe.getTitle());
                    textViewTime.setText(recipe.getReadyInMinutes() + " Minutes");
                    textViewPeople.setText(recipe.getServings() + " Servings");
                    Glide.with(RecipeDetailsActivity.this).load(recipe.getImage()).into(imageViewRecipe);

                    expandableLayout.setRenderer(new ExpandableLayout.Renderer<String, Recipe>() {
                        @Override
                        public void renderParent(View view, String title, boolean isExpanded, int parentPosition) {
                            ((TextView) view.findViewById(R.id.expandable_layout_parent_name)).setText(title);
                            view.findViewById(R.id.arrow).setBackgroundResource(isExpanded?R.drawable.ic_arrow_up:R.drawable.ic_arrow_down);
                        }

                        @Override
                        public void renderChild(View view, Recipe recipeModel, int parentPosition, int childPosition) {
                            System.out.println("POSICAO CHILD: " + childPosition);
                            if(parentPosition == 0) {
                                //Ingredient List
                                ((TextView) view.findViewById(R.id.expandable_layout_child_name))
                                        .setText(recipeModel.getExtendedIngredients().get(0).getRecipeLine());
                            } else if(parentPosition == 1) {
                                //Step by Step
                            }
                        }
                    });
                }

                expandableLayout.addSection(getSection(recipe, "Ingredient List"));
                expandableLayout.addSection(getSection(recipe, "Step by Step"));

            }

            @Override
            public void onFailure(Call<Recipe> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

    private Section<String, Recipe> getSection(Recipe recipe, String parentTitle) {
        Section<String, Recipe> section = new Section<>();

        section.children.add(recipe);
        section.parent = parentTitle;
        return section;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return true;
    }
}