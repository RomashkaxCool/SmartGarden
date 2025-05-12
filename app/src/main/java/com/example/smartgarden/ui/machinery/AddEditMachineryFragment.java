package com.example.smartgarden.ui.machinery;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.example.smartgarden.R;
import com.example.smartgarden.databinding.FragmentAddEditMachineryBinding;
import com.example.smartgarden.model.Machinery;
import java.util.Objects;
import java.util.UUID;
public class AddEditMachineryFragment extends Fragment {
    private FragmentAddEditMachineryBinding binding;
    private MachineryViewModel machineryViewModel;
    private String machineryIdToEdit = null;
    private Machinery currentMachinery = null;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        machineryViewModel = new ViewModelProvider(requireActivity()).get(MachineryViewModel.class);
        if (getArguments() != null) {
            machineryIdToEdit = getArguments().getString("machineryId");
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddEditMachineryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupSaveButton();
        if (machineryIdToEdit != null) {
            binding.textViewAddEditTitle.setText("Редагувати Техніку");
            observeMachineryDetails();
            machineryViewModel.loadMachineryForEdit(machineryIdToEdit);
        } else {
            binding.textViewAddEditTitle.setText("Додати Техніку");
            machineryViewModel.clearSelectedMachinery();
        }

    }
    private void observeMachineryDetails() {
        machineryViewModel.getSelectedMachinery().observe(getViewLifecycleOwner(), machinery -> {
            if (machinery != null && Objects.equals(machinery.getId(), machineryIdToEdit)) {
                currentMachinery = machinery;
                populateMachineryUI(machinery);
            } else if (machinery == null && machineryIdToEdit != null) {
            }
        });
    }
    private void populateMachineryUI(Machinery machinery) {
        binding.editTextName.setText(machinery.getName());
        binding.editTextStatus.setText(machinery.getWorkingStatus());
        binding.editTextCondition.setText(machinery.getTechnicalCondition());
    }
    private void setupSaveButton() {
        binding.buttonSave.setOnClickListener(v -> {
            saveMachinery();
        });
    }
    private void saveMachinery() {
        String name = binding.editTextName.getText() != null ? binding.editTextName.getText().toString().trim() : "";
        String status = binding.editTextStatus.getText() != null ? binding.editTextStatus.getText().toString().trim() : "";
        String condition = binding.editTextCondition.getText() != null ? binding.editTextCondition.getText().toString().trim() : "";
        String imageUrl = (currentMachinery != null) ? currentMachinery.getImageUrl() : null;
        if (TextUtils.isEmpty(name)) {
            binding.textInputLayoutName.setError("Назва не може бути порожньою");
            return;
        } else {
            binding.textInputLayoutName.setError(null);
        }
        if (machineryIdToEdit != null) {
            Machinery updatedMachinery = new Machinery(machineryIdToEdit, name, imageUrl, status, condition);
            machineryViewModel.updateMachinery(updatedMachinery);
            Toast.makeText(getContext(), "Техніку оновлено", Toast.LENGTH_SHORT).show();
        } else {
            Machinery newMachinery = new Machinery(null, name, imageUrl, status, condition);
            machineryViewModel.addMachinery(newMachinery);
            Toast.makeText(getContext(), "Техніку додано", Toast.LENGTH_SHORT).show();
        }
        NavController navController = NavHostFragment.findNavController(this);
        navController.popBackStack();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (machineryIdToEdit != null) {
            machineryViewModel.clearSelectedMachinery();
        }
        binding = null;
    }
}