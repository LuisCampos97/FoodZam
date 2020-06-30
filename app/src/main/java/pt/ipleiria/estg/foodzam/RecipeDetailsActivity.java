package pt.ipleiria.estg.foodzam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bumptech.glide.Glide;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
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

public class RecipeDetailsActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    Retrofit retrofit;
    SpoonacularAPI spoonacularAPI;

    private TextView textViewTitle, textViewTime, textViewPeople, textViewIngredientsListTitle, textViewStepsListTitle;
    private ImageView imageViewRecipe;
    private Button favoriteButton, addToCalendarButton;
    private ListView ingredientListView, stepsListView;

    private FirebaseFirestore db;

    final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
    GoogleAccountCredential credential;
    private static final String[] SCOPES = { CalendarScopes.CALENDAR_READONLY };
    com.google.api.services.calendar.Calendar mService;
    private static final String PREF_ACCOUNT_NAME = "primary";

    String date, time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        credential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));

        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Google Calendar API Android Quickstart")
                .build();

        retrofit = new Retrofit.Builder().baseUrl("https://api.spoonacular.com/")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        spoonacularAPI = retrofit.create(SpoonacularAPI.class);

        db = FirebaseFirestore.getInstance();

        //region Views Declaration
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
        //endregion

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

                    //region Favorite Button
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
                    //endregion

                    //region Ingredients List
                    if (!recipe.getExtendedIngredients().isEmpty()) {
                        IngredientsAdapter ingredientsListAdapter;
                        ingredientsListAdapter = new IngredientsAdapter(getBaseContext(), R.layout.row_ingredient_list, recipe.getExtendedIngredients());
                        ingredientListView.setAdapter(ingredientsListAdapter);
                        textViewIngredientsListTitle.setVisibility(View.VISIBLE);
                    }
                    //endregion

                    //region Steps List
                    if (!recipe.getAnalyzedInstructions().isEmpty()) {
                        StepsAdapter stepsAdapter;
                        stepsAdapter = new StepsAdapter(getBaseContext(), R.layout.row_step_list, recipe.getAnalyzedInstructions().get(0).getSteps());
                        stepsListView.setAdapter(stepsAdapter);
                        textViewStepsListTitle.setVisibility(View.VISIBLE);
                    }
                    //endregion

                    addToCalendarButton.setOnClickListener(v -> openDialog(recipe));
                }
            }

            @Override
            public void onFailure(Call<Recipe> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

    EditText editTextEventTitle, editTextDate, editTextDescription, editTextTime;

    private void openDialog(Recipe recipe) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_to_calendar, null);

        editTextEventTitle = dialogView.findViewById(R.id.editTextEventTitle);
        editTextDate = dialogView.findViewById(R.id.editTextDate);
        editTextDescription = dialogView.findViewById(R.id.editTextDescription);
        editTextTime = dialogView.findViewById(R.id.editTextTime);

        editTextEventTitle.setText(recipe.getTitle());

        Button saveButton = dialogView.findViewById(R.id.saveButton);;
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        editTextDate.setOnClickListener(view -> {
            Calendar cal = Calendar.getInstance();

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    this,
                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

            datePickerDialog.show();
        });

        editTextTime.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    this,
                    this,
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        });

        alertDialog.setView(dialogView);
        AlertDialog dialog = alertDialog.create();

        saveButton.setOnClickListener(v -> {
            Event event = new Event()
                    .setSummary(editTextEventTitle.getText().toString())
                    .setDescription(editTextDescription.getText().toString());

            DateTime dateTime = new DateTime(date+"T"+time);
            EventDateTime start = new EventDateTime()
                    .setDateTime(dateTime)
                    .setTimeZone("GMT+1:00");
            event.setStart(start);

            String calendarId = "primary";
            try {
                event = mService.events().insert(calendarId, event).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //System.out.printf("Event created: %s\n", event.getHtmlLink());

            dialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return true;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        date = dayOfMonth+"-"+month+"-"+year;
        editTextDate.setText(date);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        time = hourOfDay+":"+minute;
        editTextTime.setText(time);
    }
}