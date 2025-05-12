package com.example.smartgarden.ui.harvest;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
// Видалено імпорт ArrayAdapter, AutoCompleteTextView, якщо вони більше не потрібні
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.smartgarden.R;
import com.example.smartgarden.databinding.FragmentLogHarvestBinding;
import com.example.smartgarden.model.HarvestLog;
import com.example.smartgarden.ui.trees.PlantsViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class LogHarvestFragment extends Fragment {
    private FragmentLogHarvestBinding binding;
    private PlantsViewModel plantsViewModel;
    private String plantId;
    private String plantInfo;
    private Calendar selectedDateCalendar;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        plantsViewModel = new ViewModelProvider(requireActivity()).get(PlantsViewModel.class);
        if (getArguments() != null) {
            plantId = getArguments().getString("plantId");
            plantInfo = getArguments().getString("plantInfo", "");
        }
        selectedDateCalendar = Calendar.getInstance();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLogHarvestBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (plantInfo != null && !plantInfo.isEmpty()) {
            binding.textViewLogPlantInfo.setText("Рослина: " + plantInfo);
        } else {
            binding.textViewLogPlantInfo.setText("Рослина: ID " + plantId);
        }
        setupDatePicker();
        setupSaveButton();
        updateDateEditText();
    }
    private void setupDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            selectedDateCalendar.set(Calendar.YEAR, year);
            selectedDateCalendar.set(Calendar.MONTH, month);
            selectedDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateEditText();
        };

        View.OnClickListener dateClickListener = v -> {
            if (isAdded() && getContext() != null) {
                new DatePickerDialog(requireContext(), dateSetListener,
                        selectedDateCalendar.get(Calendar.YEAR),
                        selectedDateCalendar.get(Calendar.MONTH),
                        selectedDateCalendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        };
        binding.editTextHarvestDate.setOnClickListener(dateClickListener);
        binding.textInputLayoutHarvestDate.setEndIconOnClickListener(dateClickListener);
    }
    private void updateDateEditText() {
        String myFormat = "dd.MM.yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        binding.editTextHarvestDate.setText(sdf.format(selectedDateCalendar.getTime()));
    }
    private void setupSaveButton() {
        binding.buttonSaveHarvestLog.setOnClickListener(v -> saveHarvestLog());
    }
    private void saveHarvestLog() {
        String quantityStr = binding.editTextHarvestQuantity.getText() != null ? binding.editTextHarvestQuantity.getText().toString().trim() : "";
        String unit = "кг";
        String notes = binding.editTextHarvestNotes.getText() != null ? binding.editTextHarvestNotes.getText().toString().trim() : "";
        Date harvestDate = selectedDateCalendar.getTime();
        double quantity = 0;
        boolean valid = true;
        if (TextUtils.isEmpty(quantityStr)) {
            binding.textInputLayoutHarvestQuantity.setError("Вкажіть кількість");
            valid = false;
        } else {
            try {
                quantity = Double.parseDouble(quantityStr);
                if (quantity <= 0) {
                    binding.textInputLayoutHarvestQuantity.setError("Кількість має бути > 0");
                    valid = false;
                } else {
                    binding.textInputLayoutHarvestQuantity.setError(null);
                }
            } catch (NumberFormatException e) {
                binding.textInputLayoutHarvestQuantity.setError("Введіть число");
                valid = false;
            }
        }
        if (!valid) {
            return;
        }
        if (plantId == null) {
            Toast.makeText(getContext(), "Помилка: Немає ID рослини", Toast.LENGTH_SHORT).show();
            return;
        }
        HarvestLog newLog = new HarvestLog(plantId, plantInfo, null, quantity, unit, notes);
        plantsViewModel.addHarvestLog(newLog);
        Toast.makeText(getContext(), "Запис про врожай збережено", Toast.LENGTH_SHORT).show();
        NavController navController = NavHostFragment.findNavController(this);
        navController.popBackStack();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}