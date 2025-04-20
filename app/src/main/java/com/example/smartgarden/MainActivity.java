package com.example.smartgarden;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.smartgarden.databinding.ActivityMainBinding;
import com.example.smartgarden.R;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private AppBarConfiguration appBarConfiguration;
    private NavController navController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = binding.bottomNavigation;
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);

        Set<Integer> topLevelDestinations = new HashSet<>();
        topLevelDestinations.add(R.id.navigation_home);
        topLevelDestinations.add(R.id.navigation_machinery);
        topLevelDestinations.add(R.id.navigation_trees);
        appBarConfiguration = new AppBarConfiguration.Builder(topLevelDestinations).build();

        binding.btnMap.setOnClickListener(v -> showGardenMapDialog());
        binding.btnReporting.setOnClickListener(v -> {
            try {
                navController.navigate(R.id.navigation_reporting);
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, "Не вдалося відкрити звітність.", Toast.LENGTH_SHORT).show();
                System.err.println("Помилка навігації до reporting: " + e.getMessage());
            }
        });
    }

    private void showGardenMapDialog() {
        final Dialog dialog = new Dialog(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_map, null);
        ImageView imageViewMap = dialogView.findViewById(R.id.imageViewMap);

        try {
            imageViewMap.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.garden_map));
        } catch (Exception e) {
            Toast.makeText(this, "Не вдалося завантажити мапу саду", Toast.LENGTH_SHORT).show();
            imageViewMap.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray));
            System.err.println("Помилка завантаження ресурсу 'garden_map': " + e.getMessage());
        }

        dialog.setContentView(dialogView);
        dialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}