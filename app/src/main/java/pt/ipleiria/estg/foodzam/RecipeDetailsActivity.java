package pt.ipleiria.estg.foodzam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import pt.ipleiria.estg.foodzam.helpers.IngredientsAdapter;
import pt.ipleiria.estg.foodzam.helpers.SpoonacularAPI;
import pt.ipleiria.estg.foodzam.helpers.StepsAdapter;
import pt.ipleiria.estg.foodzam.model.Recipe;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RecipeDetailsActivity extends AppCompatActivity {

    Retrofit retrofit;
    SpoonacularAPI spoonacularAPI;

    private TextView textViewTitle, textViewTime, textViewPeople, textViewIngredientsListTitle, textViewStepsListTitle;
    private ImageView imageViewRecipe;
    private Button favoriteButton, addToCalendarButton;
    private ListView ingredientListView, stepsListView;

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
        ingredientListView = findViewById(R.id.ingredientList);
        stepsListView = findViewById(R.id.stepsList);
        favoriteButton = findViewById(R.id.favoriteButton);
        textViewIngredientsListTitle = findViewById(R.id.ingredientListTitle);
        textViewStepsListTitle = findViewById(R.id.stepsListTitle);
        addToCalendarButton = findViewById(R.id.addToCalendarButton);

        textViewIngredientsListTitle.setVisibility(View.GONE);
        textViewStepsListTitle.setVisibility(View.GONE);

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

                if (recipe != null) {
                    textViewTitle.setText(recipe.getTitle());
                    textViewTime.setText(recipe.getReadyInMinutes() + " Minutes");
                    textViewPeople.setText(recipe.getServings() + " Servings");
                    Glide.with(RecipeDetailsActivity.this).load(recipe.getImage()).into(imageViewRecipe);

                    db.collection("favorites")
                            .whereEqualTo("id", recipe.getId())
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if ((Long) document.getData().get("id") == recipe.getId()) {
                                            favoriteButton.setBackgroundResource(R.drawable.ic_favorite_red);
                                            isFavorite.set(true);
                                            documentId.set(document.getId());
                                        } else {
                                            favoriteButton.setBackgroundResource(R.drawable.ic_favorite_shadow);
                                            isFavorite.set(false);
                                        }
                                    }

                                    favoriteButton.setOnClickListener(v -> {
                                        if (!isFavorite.get()) {
                                            Map<String, Integer> recipeMap = new HashMap();
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
                                    });
                                }
                            });

                    if(!recipe.getExtendedIngredients().isEmpty()) {
                        IngredientsAdapter ingredientsListAdapter;
                        ingredientsListAdapter = new IngredientsAdapter(getBaseContext(), R.layout.row_ingredient_list, recipe.getExtendedIngredients());
                        ingredientListView.setAdapter(ingredientsListAdapter);
                        textViewIngredientsListTitle.setVisibility(View.VISIBLE);
                    }

                    if (!recipe.getAnalyzedInstructions().isEmpty()) {
                        StepsAdapter stepsAdapter;
                        stepsAdapter = new StepsAdapter(getBaseContext(), R.layout.row_step_list, recipe.getAnalyzedInstructions().get(0).getSteps());
                        stepsListView.setAdapter(stepsAdapter);
                        textViewStepsListTitle.setVisibility(View.VISIBLE);
                    }

                    addToCalendarButton.setOnClickListener(v -> openDialog(recipe));
                }
            }

            @Override
            public void onFailure(Call<Recipe> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

    private void openDialog(Recipe recipe) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_to_calendar, null);

        alertDialog.setView(dialogView);
        AlertDialog dialog = alertDialog.create();

        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return true;
    }
}