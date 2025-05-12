package com.example.smartgarden.ui.trees;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.smartgarden.R;
import com.example.smartgarden.databinding.FragmentTreesBinding;
import com.example.smartgarden.model.Plant;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
public class TreesFragment extends Fragment implements PlantAdapter.PlantClickListener {
    private FragmentTreesBinding binding;
    private PlantsViewModel plantsViewModel;
    private PlantAdapter adapter;
    private ArrayAdapter<String> cultureAdapter;
    private ArrayAdapter<String> varietyAdapter;
    private ArrayAdapter<String> ageAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTreesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        plantsViewModel = new ViewModelProvider(requireActivity()).get(PlantsViewModel.class);
        setupTabLayout();
        setupRecyclerView();
        setupFilters();
        setupFab();
        observeViewModel();
    }
    @Override
    public void onResume() {
        super.onResume();
        if (plantsViewModel != null) {
            plantsViewModel.resetFilters();
            clearFilterTextFieldsUi();
        }
    }
    private void clearFilterTextFieldsUi() {
        AutoCompleteTextView cultureTextView = (AutoCompleteTextView) binding.menuCultureLayout.getEditText();
        AutoCompleteTextView varietyTextView = (AutoCompleteTextView) binding.menuVarietyLayout.getEditText();
        AutoCompleteTextView ageTextView = (AutoCompleteTextView) binding.menuAgeLayout.getEditText();
        String allCultures = "Всі культури";
        String allVarieties = "Всі сорти";
        String anyAge = "Будь-який вік";
        if(cultureTextView != null) {
            if(cultureAdapter != null && cultureAdapter.getPosition(allCultures) >= 0) {
                cultureTextView.setText(cultureAdapter.getItem(cultureAdapter.getPosition(allCultures)), false);
            } else {
                cultureTextView.setText(null, false);
            }
        }
        if(varietyTextView != null) {
            if(varietyAdapter != null && varietyAdapter.getPosition(allVarieties) >= 0) {
                varietyTextView.setText(varietyAdapter.getItem(varietyAdapter.getPosition(allVarieties)), false);
            } else {
                varietyTextView.setText(null, false);
            }
        }
        if(ageTextView != null) {
            if(ageAdapter != null && ageAdapter.getPosition(anyAge) >= 0) {
                ageTextView.setText(ageAdapter.getItem(ageAdapter.getPosition(anyAge)), false);
            } else {
                ageTextView.setText(null, false);
            }
        }
    }
    private void setupTabLayout() {
        if (binding.tabLayoutPlantType.getTabCount() == 0) {
            binding.tabLayoutPlantType.addTab(binding.tabLayoutPlantType.newTab().setText("Дерева"));
            binding.tabLayoutPlantType.addTab(binding.tabLayoutPlantType.newTab().setText("Ягоди"));
        }
        Plant.PlantType currentType = plantsViewModel.getPlantTypeFilter().getValue();
        int initialTabPosition = (currentType == Plant.PlantType.BERRY) ? 1 : 0;
        binding.tabLayoutPlantType.post(() -> {
            if (binding.tabLayoutPlantType.getTabCount() > initialTabPosition) {
                TabLayout.Tab tab = binding.tabLayoutPlantType.getTabAt(initialTabPosition);
                if (tab != null && !tab.isSelected()) {
                    tab.select();
                }
            }
        });
        binding.tabLayoutPlantType.clearOnTabSelectedListeners();
        binding.tabLayoutPlantType.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                plantsViewModel.setPlantTypeFilter(tab.getPosition() == 0 ? Plant.PlantType.TREE : Plant.PlantType.BERRY);
                clearFilterTextFieldsUi();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) { }
            @Override public void onTabReselected(TabLayout.Tab tab) { }
        });
    }
    private void setupRecyclerView() {
        adapter = new PlantAdapter(this);
        binding.recyclerViewPlants.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewPlants.setAdapter(adapter);
        binding.recyclerViewPlants.setHasFixedSize(true);
    }
    private void setupFilters() {
        if (getContext() == null) return;
        cultureAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        varietyAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        ageAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        AutoCompleteTextView cultureTextView = binding.autocompleteCulture;
        AutoCompleteTextView varietyTextView = binding.autocompleteVariety;
        AutoCompleteTextView ageTextView = binding.autocompleteAge;
        cultureTextView.setAdapter(cultureAdapter);
        cultureTextView.setOnItemClickListener((parent, v, position, id) -> {
            String selectedCulture = cultureAdapter.getItem(position);
            plantsViewModel.setCultureFilter(selectedCulture);
            if (varietyTextView != null) {
                varietyTextView.setText(null, false);
            }
        });
        varietyTextView.setAdapter(varietyAdapter);
        varietyTextView.setOnItemClickListener((parent, v, position, id) -> {
            String selectedVariety = varietyAdapter.getItem(position);
            plantsViewModel.setVarietyFilter(selectedVariety);
        });
        ageTextView.setAdapter(ageAdapter);
        ageTextView.setOnItemClickListener((parent, v, position, id) -> {
            String selectedAge = ageAdapter.getItem(position);
            plantsViewModel.setAgeFilter(selectedAge);
        });
    }
    private void setupFab() {
        binding.fabAddPlant.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(this);
            try {
                navController.navigate(R.id.action_trees_to_add_edit);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Навігація додавання рослини ще не налаштована", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void observeViewModel() {
        plantsViewModel.getFilteredPlants().observe(getViewLifecycleOwner(), plants -> {
            if (adapter != null) {
                adapter.submitList(plants);
            }
        });
        plantsViewModel.getCultureOptions().observe(getViewLifecycleOwner(), cultures -> {
            if (cultureAdapter != null) {
                cultureAdapter.clear();
                if (cultures != null) {
                    cultureAdapter.addAll(cultures);
                }
            }
        });
        plantsViewModel.getVarietyOptions().observe(getViewLifecycleOwner(), varieties -> {
            if (varietyAdapter != null) {
                varietyAdapter.clear();
                if (varieties != null) {
                    varietyAdapter.addAll(varieties);
                }
            }
        });
        plantsViewModel.getAgeOptions().observe(getViewLifecycleOwner(), ages -> {
            if (ageAdapter != null) {
                ageAdapter.clear();
                if (ages != null) {
                    ageAdapter.addAll(ages);
                }
            }
        });
    }
    @Override
    public void onPlantClick(Plant plant) {
        Toast.makeText(getContext(), "Натиснуто: " + plant.getCulture() + " " + plant.getVariety(), Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onPlantEditClick(Plant plant) {
        if (plant.getId() == null) {
            Toast.makeText(getContext(), "Помилка: Неможливо редагувати рослину без ID", Toast.LENGTH_SHORT).show();
            return;
        }
        NavController navController = NavHostFragment.findNavController(this);
        Bundle args = new Bundle();
        args.putString("plantId", plant.getId());
        try {
            navController.navigate(R.id.action_trees_to_add_edit, args);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Помилка навігації до редагування рослини", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onPlantDeleteClick(Plant plant) {
        if (!isAdded() || getContext() == null) {
            return;
        }
        new AlertDialog.Builder(requireContext())
                .setTitle("Видалити Рослину?")
                .setMessage("Ви впевнені, що хочете видалити '" + plant.getCulture() + " " + plant.getVariety() + "'?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    plantsViewModel.deletePlant(plant);
                    Toast.makeText(getContext(), "'" + plant.getCulture() + "' видалено", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    @Override
    public void onLogHarvestClick(Plant plant) {
        if (plant.getId() == null) {
            Toast.makeText(getContext(), "Помилка: Неможливо додати врожай для рослини без ID", Toast.LENGTH_SHORT).show();
            return;
        }
        NavController navController = NavHostFragment.findNavController(this);
        Bundle args = new Bundle();
        args.putString("plantId", plant.getId());
        String plantInfo = (plant.getCulture() != null ? plant.getCulture() : "") +
                (plant.getVariety() != null ? " " + plant.getVariety() : "");
        args.putString("plantInfo", plantInfo.trim());
        try {
            navController.navigate(R.id.action_trees_to_log_harvest, args);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Навігація для логування врожаю ще не налаштована", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adapter = null;
        cultureAdapter = null;
        varietyAdapter = null;
        ageAdapter = null;
        binding = null;
    }
}