package pt.ipleiria.estg.foodzam;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.request.model.PredictRequest;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.model.Model;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;

public class MainActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;

    //Views
    private ImageView imageView;
    private Button btnGaleria, btnCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        btnGaleria = findViewById(R.id.galeriaButton);
        btnCamera = findViewById(R.id.cameraButton);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        btnGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Pick an image"), GALLERY_REQUEST_CODE);
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri imageData = data.getData();
            imageView.setImageURI(imageData);

            ClarifaiClient client = new ClarifaiBuilder(getString(R.string.clarifai_api_key)).buildSync();
            final Model<Concept> generalModel = client.getDefaultModels().generalModel();

            PredictRequest<Concept> request = generalModel.predict().withInputs(
                    ClarifaiInput.forImage(convertImageToByte(imageData))
            );

            List<ClarifaiOutput<Concept>> results = request.executeSync().get();
            List<Concept> resulstsData = results.get(0).data();

            for(int i = 0; i < resulstsData.size(); i++) {
                float prob = (resulstsData.get(i).value()) * 100;
                System.out.println(resulstsData.get(i).name() + " - " + prob +"%");
            }
        } else if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(imageBitmap);

            ClarifaiClient client = new ClarifaiBuilder(getString(R.string.clarifai_api_key)).buildSync();
            final Model<Concept> generalModel = client.getDefaultModels().generalModel();

            PredictRequest<Concept> request = generalModel.predict().withInputs(
                    ClarifaiInput.forImage(bitmapToByteArray(imageBitmap))
            );

            List<ClarifaiOutput<Concept>> results = request.executeSync().get();
            List<Concept> resulstsData = results.get(0).data();

            for(int i = 0; i < resulstsData.size(); i++) {
                float prob = (resulstsData.get(i).value()) * 100;
                System.out.println(resulstsData.get(i).name() + " - " + prob +"%");
            }
        }
    }

    public byte[] convertImageToByte(Uri uri){
        byte[] data = null;
        try {
            ContentResolver cr = getBaseContext().getContentResolver();
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