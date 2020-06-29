package pt.ipleiria.estg.foodzam.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import pt.ipleiria.estg.foodzam.R;
import pt.ipleiria.estg.foodzam.model.Ingredient;
import pt.ipleiria.estg.foodzam.model.Step;

public class StepsAdapter extends ArrayAdapter<Step> {

    private Context mContext;
    private int mResource;

    private static class ViewHolder {
        TextView number;
        TextView stepLine;
    }

    public StepsAdapter(@NonNull Context context, int resource, List<Step> steps) {
        super(context, resource, steps);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        int number = getItem(position).getNumber();
        String stepLine = getItem(position).getStep();

        Step step = new Step(number, stepLine);

        StepsAdapter.ViewHolder holder;

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder= new StepsAdapter.ViewHolder();
            holder.number = convertView.findViewById(R.id.textViewStepNumber);
            holder.stepLine = convertView.findViewById(R.id.textViewStepLine);

            convertView.setTag(holder);
        }
        else{
            holder = (StepsAdapter.ViewHolder) convertView.getTag();
        }

        holder.number.setText(String.valueOf(step.getNumber()));
        holder.stepLine.setText(step.getStep());

        return convertView;
    }
}
