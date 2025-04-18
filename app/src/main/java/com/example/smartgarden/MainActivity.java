package com.example.smartgarden;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationBarView;
import com.example.smartgarden.databinding.ActivityMainBinding;

import com.example.smartgarden.ui.home.HomeFragment;
import com.example.smartgarden.ui.machinery.MachineryFragment;
import com.example.smartgarden.ui.trees.TreesFragment;
import com.example.smartgarden.ui.reporting.ReportingFragment;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.navigation_home) {
                    selectedFragment = new HomeFragment();
                } else if (itemId == R.id.navigation_machinery) {
                    selectedFragment = new MachineryFragment();
                } else if (itemId == R.id.navigation_trees) {
                    selectedFragment = new TreesFragment();
                }

                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                    return true;
                }
                return false;
            }
        });

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            binding.bottomNavigation.setSelectedItemId(R.id.navigation_home);
        }

        binding.btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGardenMapDialog();
            }
        });

        binding.btnReporting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Завантажуємо фрагмент звітності в той самий контейнер
                loadFragment(new ReportingFragment());
                // Опціонально: можна візуально "скинути" вибір
                // на нижній панелі, якщо звітність - це окремий потік
                // Наприклад, можна спробувати встановити неіснуючий ID або 0,
                // але це може не спрацювати або виглядати дивно.
                // Або просто не змінювати виділення на нижній панелі.
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.fragment_container, fragment);


        transaction.commit();
    }

    private void showGardenMapDialog() {
        Dialog dialog = new Dialog(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_map, null);

        ImageView imageViewMap = dialogView.findViewById(R.id.imageViewMap);
        imageViewMap.setImageResource(R.drawable.garden_map); // drawable/garden_map

        dialog.setContentView(dialogView);
        dialog.show();
    }
}