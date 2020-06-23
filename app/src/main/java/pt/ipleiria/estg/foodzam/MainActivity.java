package pt.ipleiria.estg.foodzam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import pt.ipleiria.estg.foodzam.fragments.FavoritesFragment;
import pt.ipleiria.estg.foodzam.fragments.HomeFragment;
import pt.ipleiria.estg.foodzam.fragments.ProfileFragment;
import pt.ipleiria.estg.foodzam.fragments.RecipeFragment;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navbar);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null;

            switch  (menuItem.getItemId()){
                case R.id.predictFragment:
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.recipesFragment:
                    selectedFragment = new RecipeFragment();
                    break;
                case R.id.profileFragment:
                    selectedFragment = new ProfileFragment();
                    break;
                case R.id.favoritesFragment:
                    selectedFragment = new FavoritesFragment();
                    break;
                default:
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

            return true;
        }
    };
}