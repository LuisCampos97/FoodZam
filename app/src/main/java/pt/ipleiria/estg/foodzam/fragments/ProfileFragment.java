package pt.ipleiria.estg.foodzam.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import pt.ipleiria.estg.foodzam.LoginActivity;
import pt.ipleiria.estg.foodzam.R;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    GoogleSignInClient mGoogleSignInClient;
    Button sign_out;
    TextView nameTextView;
    TextView emailTextView;
    ImageView photoImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragment_view = inflater.inflate(R.layout.fragment_profile, container, false);

        sign_out = fragment_view.findViewById(R.id.sign_out);
        sign_out.setOnClickListener(this);

        nameTextView = fragment_view.findViewById(R.id.name);
        emailTextView = fragment_view.findViewById(R.id.email);
        photoImageView = fragment_view.findViewById(R.id.photo);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(requireActivity());

        if (acct != null) {
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            Uri personPhoto = acct.getPhotoUrl();

            nameTextView.setText(personName);
            emailTextView.setText(personEmail);
            Glide.with(this).load(personPhoto).into(photoImageView);
        }

        return fragment_view;
    }

    private void sign_out(){
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(requireActivity(), "Sucessfully signed out", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(requireActivity(), LoginActivity.class));
                        requireActivity().finish();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sign_out) {
            sign_out();
        }
    }
}