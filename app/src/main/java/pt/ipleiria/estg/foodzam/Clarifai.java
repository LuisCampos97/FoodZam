package pt.ipleiria.estg.foodzam;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.request.model.PredictRequest;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.model.Model;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;

public class Clarifai extends AppCompatActivity {

    private ClarifaiClient client;
    private static Model<Concept> generalModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        client = new ClarifaiBuilder(getString(R.string.clarifai_api_key)).buildSync();
        generalModel = client.getDefaultModels().generalModel();
    }

    public static List<Concept> predictImage(byte[] image) {
        PredictRequest<Concept> request = generalModel.predict().withInputs(
                ClarifaiInput.forImage(image)
        );

        List<ClarifaiOutput<Concept>> results = request.executeSync().get();
        System.out.println(results);
        return results.get(0).data();
    }
}
