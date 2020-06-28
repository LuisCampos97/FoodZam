package pt.ipleiria.estg.foodzam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import iammert.com.expandablelib.ExpandableLayout;
import iammert.com.expandablelib.Section;
import pt.ipleiria.estg.foodzam.helpers.SpoonacularAPI;
import pt.ipleiria.estg.foodzam.model.Ingredient;
import pt.ipleiria.estg.foodzam.model.Recipe;
import pt.ipleiria.estg.foodzam.model.Step;
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
    private Button favoriteButton;
    private ExpandableLayout expandableLayout, expandableLayout2;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        retrofit = new Retrofit.Builder().baseUrl("https://api.spoonacular.com/")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        spoonacularAPI = retrofit.create(SpoonacularAPI.class);

        db = FirebaseFirestore.getInstance();

        textViewTitle = findViewById(R.id.textViewTitle);
        textViewTime = findViewById(R.id.textViewTime);
        textViewPeople = findViewById(R.id.textViewPeople);
        imageViewRecipe = findViewById(R.id.imageViewRecipe);
        favoriteButton = findViewById(R.id.favoriteButton);
        expandableLayout = findViewById(R.id.expandable_layout);
        expandableLayout2 = findViewById(R.id.expandable_layout);

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
                AtomicBoolean isFavorite = new AtomicBoolean(false);
                AtomicReference<String> documentId = new AtomicReference<>("");

                if(recipe != null) {
                    textViewTitle.setText(recipe.getTitle());
                    textViewTime.setText(recipe.getReadyInMinutes() + " Minutes");
                    textViewPeople.setText(recipe.getServings() + " Servings");
                    Glide.with(RecipeDetailsActivity.this).load(recipe.getImage()).into(imageViewRecipe);

                    favoriteButton.setOnClickListener(v -> {
                        db.collection("favorites")
                                .whereEqualTo("id", recipe.getId())
                                .get()
                                .addOnCompleteListener(task -> {
                                    if(task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            if((Long) document.getData().get("id") == recipe.getId()) {
                                                favoriteButton.setBackgroundResource(R.drawable.ic_favorite_red);
                                                isFavorite.set(true);
                                                documentId.set(document.getId());
                                            } else {
                                                favoriteButton.setBackgroundResource(R.drawable.ic_favorite_shadow);
                                                isFavorite.set(false);
                                            }
                                        }

                                        if(!isFavorite.get()) {
                                            Map<String, Object> recipeMap = new HashMap();
                                            recipeMap.put("id", recipe.getId());

                                            db.collection("favorites")
                                                    .add(recipeMap)
                                                    .addOnSuccessListener(documentReference -> {
                                                        favoriteButton.setBackgroundResource(R.drawable.ic_favorite_red);
                                                        isFavorite.set(true);
                                                    })
                                                    .addOnFailureListener(e -> {
                                                    });
                                        } else {
                                            db.collection("favorites")
                                                    .document(String.valueOf(documentId))
                                                    .delete()
                                                    .addOnSuccessListener(aVoid -> {
                                                        favoriteButton.setBackgroundResource(R.drawable.ic_favorite_shadow);
                                                        isFavorite.set(false);
                                                    });
                                        }
                                    }
                                });
                    });

                    expandableLayout.setRenderer(new ExpandableLayout.Renderer<String, Ingredient>() {
                        @Override
                        public void renderParent(View view, String title, boolean isExpanded, int parentPosition) {
                            ((TextView) view.findViewById(R.id.expandable_layout_parent_name)).setText(title);
                            view.findViewById(R.id.arrow).setBackgroundResource(isExpanded?R.drawable.ic_arrow_up:R.drawable.ic_arrow_down);
                        }

                        @Override
                        public void renderChild(View view, Ingredient recipeModel, int parentPosition, int childPosition) {
                            System.out.println("POSICAO CHILD: " + childPosition);
                            if(parentPosition == 0) {
                                //Ingredient List
                                ((TextView) view.findViewById(R.id.expandable_layout_child_name))
                                        .setText(recipeModel.getOriginal());
                            } else if(parentPosition == 1) {
                                //Step by Step
                            }
                        }
                    });

                    //TODO: Os steps não estão a ver na API

                    /* expandableLayout2.setRenderer(new ExpandableLayout.Renderer<String, Step>() {
                        @Override
                        public void renderParent(View view, String title, boolean isExpanded, int parentPosition) {
                            ((TextView) view.findViewById(R.id.expandable_layout_parent_name)).setText(title);
                            view.findViewById(R.id.arrow).setBackgroundResource(isExpanded?R.drawable.ic_arrow_up:R.drawable.ic_arrow_down);
                        }

                        @Override
                        public void renderChild(View view, Step step, int parentPosition, int childPosition) {
                            ((TextView) view.findViewById(R.id.expandable_layout_child_name))
                                    .setText(step.getStep());
                        }
                    }); */

                    expandableLayout.addSection(getSection(recipe.getExtendedIngredients(), "Ingredient List"));
                    //expandableLayout2.addSection(getSectionStep(recipe.getAnalyzedInstructions().get(0).getSteps(), "Step by Step"));
                }
            }

            @Override
            public void onFailure(Call<Recipe> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

    private Section<String, Ingredient> getSection(List<Ingredient> ingredients, String parentTitle) {
        Section<String, Ingredient> section = new Section<>();

        section.children.addAll(ingredients);
        section.parent = parentTitle;
        return section;
    }

    private Section<String, Step> getSectionStep(List<Step> steps, String parentTitle) {
        Section<String, Step> section = new Section<>();

        section.children.addAll(steps);
        section.parent = parentTitle;
        return section;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return true;
    }
}