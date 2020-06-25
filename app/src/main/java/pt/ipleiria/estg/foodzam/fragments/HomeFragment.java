package pt.ipleiria.estg.foodzam.fragments;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.request.model.PredictRequest;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.model.Model;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;
import pt.ipleiria.estg.foodzam.R;
import pt.ipleiria.estg.foodzam.SpoonacularAPI;
import pt.ipleiria.estg.foodzam.model.Recipe;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private static final int GALLERY_REQUEST_CODE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;

    private ImageView imageView;
    private Button btnGallery, btnCamera, btnOpenDialog;

    ArrayList<String> ingredients = null;
    ArrayAdapter<String> adapter;

    Retrofit retrofit;
    SpoonacularAPI spoonacularAPI;

    View fragment_view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragment_view = inflater.inflate(R.layout.fragment_home, container, false);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        imageView = fragment_view.findViewById(R.id.imageView);
        btnGallery = fragment_view.findViewById(R.id.galeriaButton);
        btnCamera = fragment_view.findViewById(R.id.cameraButton);
        btnOpenDialog = fragment_view.findViewById(R.id.openDialogButton);

        btnGallery.setOnClickListener(this);
        btnCamera.setOnClickListener(this);
        btnOpenDialog.setOnClickListener(this);
        btnOpenDialog.setVisibility(View.INVISIBLE);

        ingredients = new ArrayList<>();

        retrofit = new Retrofit.Builder().baseUrl("https://api.spoonacular.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        spoonacularAPI = retrofit.create(SpoonacularAPI.class);

        return fragment_view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ingredients.clear();

        ClarifaiClient client = new ClarifaiBuilder(getString(R.string.clarifai_api_key)).buildSync();
        Model<Concept> generalModel = client.getDefaultModels().generalModel();
        PredictRequest<Concept> request = null;

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri imageData = data.getData();
            imageView.setImageURI(imageData);

            request = generalModel.predict().withInputs(
                    ClarifaiInput.forImage(convertImageToByte(imageData))
            );

        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(imageBitmap);

            request = generalModel.predict().withInputs(
                    ClarifaiInput.forImage(bitmapToByteArray(imageBitmap))
            );
        }

        btnOpenDialog.setVisibility(View.VISIBLE);

        List<ClarifaiOutput<Concept>> results = request.executeSync().get();
        List<Concept> resulstsData = results.get(0).data();

        for (int i = 0; i < resulstsData.size(); i++) {
            float prob = (resulstsData.get(i).value()) * 100;
            String ingredientName = resulstsData.get(i).name().substring(0, 1).toUpperCase() + resulstsData.get(i).name().substring(1);
            if(prob > 97)
                ingredients.add(ingredientName);
        }

        openDialog();
    }

    public void openDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireActivity());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_item, null);
        ListView listView = dialogView.findViewById(R.id.listView);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, ingredients);

        listView.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        alertDialog.setView(dialogView);
        AlertDialog dialog = alertDialog.create();

        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            apiCall(ingredients.get(position));
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.galeriaButton:
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "Pick an image"), GALLERY_REQUEST_CODE);
                break;

            case R.id.cameraButton:
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                break;

            case R.id.openDialogButton:
                openDialog();
                break;
        }
    }

    public void apiCall(String food) {
        Call<List<Recipe>> listCall = spoonacularAPI.getRecipesByIngredients(food, "1", getString(R.string.spoonacular_api_key));

        listCall.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (!response.isSuccessful()) {
                    System.out.println("Code: " + response.code());
                    return;
                }

                List<Recipe> recipes = response.body();

                for (Recipe recipe : recipes) {
                    System.out.println("ReceitaID: " + recipe.getId());
                    System.out.println("ReceitaTitle: " + recipe.getTitle());
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

    public byte[] convertImageToByte(Uri uri) {
        byte[] data = null;
        try {
            ContentResolver cr = requireActivity().getBaseContext().getContentResolver();
            InputStream inputStream = cr.openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            data = baos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static byte[] bitmapToByteArray(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        return baos.toByteArray();
    }
}