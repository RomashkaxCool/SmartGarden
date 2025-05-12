package com.example.smartgarden.ui.trees;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.example.smartgarden.R;
import com.example.smartgarden.databinding.FragmentAddEditPlantBinding;
import com.example.smartgarden.model.Plant;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
public class AddEditPlantFragment extends Fragment {
    private FragmentAddEditPlantBinding binding;
    private PlantsViewModel plantsViewModel;
    private String plantIdToEdit = null;
    private Plant currentPlant = null;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        plantsViewModel = new ViewModelProvider(requireActivity()).get(PlantsViewModel.class);

        if (getArguments() != null) {
            plantIdToEdit = getArguments().getString("plantId");
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddEditPlantBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupSaveButton();
        if (plantIdToEdit != null) {
            binding.textViewAddEditPlantTitle.setText("Редагувати Рослину");
            observePlantDetails();
            plantsViewModel.loadPlantForEdit(plantIdToEdit);
        } else {
            binding.textViewAddEditPlantTitle.setText("Додати Рослину");
            binding.editTextQuantity.setText("1");
            plantsViewModel.clearSelectedPlant();
        }

    }
    private void observePlantDetails() {
        plantsViewModel.getSelectedPlant().observe(getViewLifecycleOwner(), plant -> {
            if (plant != null && Objects.equals(plant.getId(), plantIdToEdit)) {
                currentPlant = plant;
                populateUI(plant);
            } else if (plant == null && plantIdToEdit != null) {
                // Handle error or cleared state
            }
        });
    }
    private void populateUI(Plant plant) {
        if (plant.getType() == Plant.PlantType.TREE) {
            binding.radioButtonTree.setChecked(true);
        } else {
            binding.radioButtonBerry.setChecked(true);
        }
        binding.editTextCulture.setText(plant.getCulture());
        binding.editTextVariety.setText(plant.getVariety());
        binding.editTextAge.setText(String.valueOf(plant.getAge()));
        binding.editTextQuantity.setText(String.valueOf(plant.getQuantity()));
        binding.editTextRow.setText(plant.getGardenRow());
        binding.editTextStatus.setText(plant.getStatus());
    }
    private void setupSaveButton() {
        binding.buttonSavePlant.setOnClickListener(v -> savePlant());
    }
    private void savePlant() {
        String culture = binding.editTextCulture.getText() != null ? binding.editTextCulture.getText().toString().trim() : "";
        String variety = binding.editTextVariety.getText() != null ? binding.editTextVariety.getText().toString().trim() : "";
        String ageStr = binding.editTextAge.getText() != null ? binding.editTextAge.getText().toString().trim() : "";
        String quantityStr = binding.editTextQuantity.getText() != null ? binding.editTextQuantity.getText().toString().trim() : "";
        String row = binding.editTextRow.getText() != null ? binding.editTextRow.getText().toString().trim() : "";
        String status = binding.editTextStatus.getText() != null ? binding.editTextStatus.getText().toString().trim() : "";
        String imageUrl = (currentPlant != null) ? currentPlant.getImageUrl() : null;
        Plant.PlantType type = binding.radioButtonTree.isChecked() ? Plant.PlantType.TREE : Plant.PlantType.BERRY;
        if (TextUtils.isEmpty(culture)) {
            binding.textInputLayoutCulture.setError("Вкажіть культуру"); return;
        } else { binding.textInputLayoutCulture.setError(null); }
        if (TextUtils.isEmpty(variety)) {
            binding.textInputLayoutVariety.setError("Вкажіть сорт"); return;
        } else { binding.textInputLayoutVariety.setError(null); }
        int age = 0;
        if (TextUtils.isEmpty(ageStr)) {
            binding.textInputLayoutAge.setError("Вкажіть вік"); return;
        } else {
            try {
                age = Integer.parseInt(ageStr);
                if (age <= 0) {
                    binding.textInputLayoutAge.setError("Некоректний вік"); return;
                }
                binding.textInputLayoutAge.setError(null);
            } catch (NumberFormatException e) {
                binding.textInputLayoutAge.setError("Введіть число"); return;
            }
        }
        int quantity = 1;
        if (TextUtils.isEmpty(quantityStr)) {
            binding.textInputLayoutQuantity.setError("Вкажіть кількість"); return;
        } else {
            try {
                quantity = Integer.parseInt(quantityStr);
                if (quantity <= 0) {
                    binding.textInputLayoutQuantity.setError("Кількість має бути > 0"); return;
                }
                binding.textInputLayoutQuantity.setError(null);
            } catch (NumberFormatException e) {
                binding.textInputLayoutQuantity.setError("Введіть число"); return;
            }
        }
        if (plantIdToEdit != null) {
            Plant updatedPlant = new Plant(plantIdToEdit, type, culture, variety, age, row, status, imageUrl, quantity);
            plantsViewModel.updatePlant(updatedPlant);
            Toast.makeText(getContext(), "Рослину оновлено", Toast.LENGTH_SHORT).show();
        } else {
            Plant newPlant = new Plant(null, type, culture, variety, age, row, status, imageUrl, quantity);
            plantsViewModel.addPlant(newPlant);
            Toast.makeText(getContext(), "Рослину додано", Toast.LENGTH_SHORT).show();
        }
        NavController navController = NavHostFragment.findNavController(this);
        navController.popBackStack();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (plantIdToEdit != null) {
            plantsViewModel.clearSelectedPlant();
        }
        binding = null;
    }
}