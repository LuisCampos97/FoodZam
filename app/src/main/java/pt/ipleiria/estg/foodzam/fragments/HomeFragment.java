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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private ImageView imageView;
    private Button chooseButton, btnOpenDialog;

    ArrayList<String> ingredients = null;
    ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment_view = inflater.inflate(R.layout.fragment_home, container, false);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        imageView = fragment_view.findViewById(R.id.imageView);
        chooseButton = fragment_view.findViewById(R.id.chooseButton);
        btnOpenDialog = fragment_view.findViewById(R.id.openDialogButton);

        chooseButton.setOnClickListener(this);
        btnOpenDialog.setOnClickListener(this);
        btnOpenDialog.setVisibility(View.INVISIBLE);

        ingredients = new ArrayList<>();

        return fragment_view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ingredients.clear();

        ClarifaiClient client = new ClarifaiBuilder(getString(R.string.clarifai_api_key)).buildSync();
        Model<Concept> generalModel = client.getDefaultModels().generalModel();
        PredictRequest<Concept> request = null;

        if(resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                        imageView.setImageBitmap(imageBitmap);

                        if(imageBitmap != null) {
                            request = generalModel.predict().withInputs(
                                    ClarifaiInput.forImage(bitmapToByteArray(imageBitmap))
                            );
                        }
                    }
                    break;

                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri imageData = data.getData();
                        imageView.setImageURI(imageData);

                        if(imageData != null) {
                            request = generalModel.predict().withInputs(
                                    ClarifaiInput.forImage(convertImageToByte(imageData))
                            );
                        }
                    }
                    break;
            }
        }

        if(request != null) {
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
            dialog.dismiss();
            String ingredient = ingredients.get(position);

            //TODO: Mover para o RecipeFragment e procurar lá por receitas do alimentos
            Bundle bundle = new Bundle();
            bundle.putString("ingredient", ingredient);

            RecipeFragment frag = new RecipeFragment();
            frag.setArguments(bundle);

            FragmentManager fragmentManager = getParentFragmentManager();

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, frag);
            fragmentTransaction.commit();

        });

        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chooseButton:
                final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setTitle("Choose an image");

                builder.setItems(options, (dialog, item) -> {

                    if (options[item].equals("Take Photo")) {
                        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(takePicture, 0);

                    } else if (options[item].equals("Choose from Gallery")) {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto , 1);

                    } else if (options[item].equals("Cancel")) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                break;

            case R.id.openDialogButton:
                openDialog();
                break;
        }
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