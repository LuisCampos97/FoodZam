package pt.ipleiria.estg.foodzam;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.StrictMode;

import java.util.List;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.request.model.PredictRequest;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.model.Model;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ClarifaiClient client = new ClarifaiBuilder(getString(R.string.clarifai_api_key)).buildSync();

        Model<Concept> generalModel = client.getDefaultModels().generalModel();

        PredictRequest<Concept> request = generalModel.predict().withInputs(
                ClarifaiInput.forImage("https://upload.wikimedia.org/wikipedia/commons/8/8a/Banana-Single.jpg")
        );

        List<ClarifaiOutput<Concept>> results = request.executeSync().get();

        for(int i = 0; i < results.get(0).data().size(); i++) {
            float prob = (results.get(0).data().get(0).value()) * 100;
            System.out.println(results.get(0).data().get(i).name() + " - " + prob +"%");
        }
    }
}