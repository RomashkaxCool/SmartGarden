package com.example.smartgarden.ui.machinery;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.example.smartgarden.R;
import com.example.smartgarden.databinding.FragmentLogMachineryUsageBinding;
import com.example.smartgarden.model.MachineryUsageLog;
import com.google.firebase.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
public class LogMachineryUsageFragment extends Fragment {
    private FragmentLogMachineryUsageBinding binding;
    private MachineryViewModel machineryViewModel;
    private String machineryId;
    private String machineryName;
    private Calendar startCalendar;
    private Calendar endCalendar;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        machineryViewModel = new ViewModelProvider(requireActivity()).get(MachineryViewModel.class);
        if (getArguments() != null) {
            machineryId = getArguments().getString("machineryId");
            machineryName = getArguments().getString("machineryName", "");
        }
        startCalendar = Calendar.getInstance();
        endCalendar = null;
        dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLogMachineryUsageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.textViewLogMachineryName.setText("Техніка: " + (machineryName != null ? machineryName : "ID " + machineryId));
        updateDateTimeEditText(binding.editTextUsageStartDate, startCalendar, dateFormat);
        updateDateTimeEditText(binding.editTextUsageStartTime, startCalendar, timeFormat);
        setupDateTimePickers();
        setupSaveButton();
    }
    private void setupDateTimePickers() {
        binding.editTextUsageStartDate.setOnClickListener(v -> showDatePickerDialog(startCalendar, binding.editTextUsageStartDate));
        binding.textInputLayoutUsageStartDate.setEndIconOnClickListener(v -> showDatePickerDialog(startCalendar, binding.editTextUsageStartDate));
        binding.editTextUsageStartTime.setOnClickListener(v -> showTimePickerDialog(startCalendar, binding.editTextUsageStartTime));
        binding.textInputLayoutUsageStartTime.setEndIconOnClickListener(v -> showTimePickerDialog(startCalendar, binding.editTextUsageStartTime));
        binding.editTextUsageEndDate.setOnClickListener(v -> {
            if (endCalendar == null) endCalendar = (Calendar) startCalendar.clone();
            showDatePickerDialog(endCalendar, binding.editTextUsageEndDate);
        });
        binding.textInputLayoutUsageEndDate.setEndIconOnClickListener(v -> {
            if (endCalendar == null) endCalendar = (Calendar) startCalendar.clone();
            showDatePickerDialog(endCalendar, binding.editTextUsageEndDate);
        });
        binding.editTextUsageEndTime.setOnClickListener(v -> {
            if (endCalendar == null) endCalendar = (Calendar) startCalendar.clone();
            showTimePickerDialog(endCalendar, binding.editTextUsageEndTime);
        });
        binding.textInputLayoutUsageEndTime.setEndIconOnClickListener(v -> {
            if (endCalendar == null) endCalendar = (Calendar) startCalendar.clone();
            showTimePickerDialog(endCalendar, binding.editTextUsageEndTime);
        });
    }
    private void showDatePickerDialog(Calendar calendarToSet, com.google.android.material.textfield.TextInputEditText targetEditText) {
        if (!isAdded() || getContext() == null) return;
        DatePickerDialog.OnDateSetListener dateSetListener = (dpView, year, month, dayOfMonth) -> {
            calendarToSet.set(Calendar.YEAR, year);
            calendarToSet.set(Calendar.MONTH, month);
            calendarToSet.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateTimeEditText(targetEditText, calendarToSet, dateFormat);
        };
        new DatePickerDialog(requireContext(), dateSetListener,
                calendarToSet.get(Calendar.YEAR), calendarToSet.get(Calendar.MONTH), calendarToSet.get(Calendar.DAY_OF_MONTH)).show();
    }
    private void showTimePickerDialog(Calendar calendarToSet, com.google.android.material.textfield.TextInputEditText targetEditText) {
        if (!isAdded() || getContext() == null) return;
        TimePickerDialog.OnTimeSetListener timeSetListener = (tpView, hourOfDay, minute) -> {
            calendarToSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendarToSet.set(Calendar.MINUTE, minute);
            updateDateTimeEditText(targetEditText, calendarToSet, timeFormat);
        };
        new TimePickerDialog(requireContext(), timeSetListener,
                calendarToSet.get(Calendar.HOUR_OF_DAY), calendarToSet.get(Calendar.MINUTE), true).show();
    }
    private void updateDateTimeEditText(com.google.android.material.textfield.TextInputEditText editText, Calendar calendar, SimpleDateFormat sdf) {
        if (calendar != null && editText != null && sdf != null) { editText.setText(sdf.format(calendar.getTime())); }
    }
    private void setupSaveButton() {
        binding.buttonSaveUsageLog.setOnClickListener(v -> saveUsageLog());
    }
    private void saveUsageLog() {
        Timestamp startDateValue = new Timestamp(startCalendar.getTime()); // *** Створюємо Timestamp ***
        Timestamp endDateValue = (endCalendar != null) ? new Timestamp(endCalendar.getTime()) : null; // *** Створюємо Timestamp ***
        Double duration = null;
        String task = binding.editTextUsageTask.getText() != null ? binding.editTextUsageTask.getText().toString().trim() : "";
        String notes = binding.editTextUsageNotes.getText() != null ? binding.editTextUsageNotes.getText().toString().trim() : "";
        boolean valid = true;
        if (TextUtils.isEmpty(task)) {
            binding.textInputLayoutUsageTask.setError("Вкажіть виконану роботу"); valid = false;
        } else { binding.textInputLayoutUsageTask.setError(null); }
        if (endDateValue != null && startDateValue != null && endDateValue.toDate().before(startDateValue.toDate())) {
            binding.textInputLayoutUsageEndDate.setError("Дата кінця не може бути раніше дати початку");
            binding.textInputLayoutUsageEndTime.setError("Час кінця не може бути раніше часу початку");
            valid = false;
        } else {
            binding.textInputLayoutUsageEndDate.setError(null);
            binding.textInputLayoutUsageEndTime.setError(null);
        }
        if (!valid) return;
        if (machineryId == null) { Toast.makeText(getContext(), "Помилка: Немає ID техніки", Toast.LENGTH_SHORT).show(); return; }
        MachineryUsageLog newLog = new MachineryUsageLog(machineryId, machineryName, startDateValue, endDateValue, duration, task, notes);
        machineryViewModel.addMachineryUsageLog(newLog);
        Toast.makeText(getContext(), "Запис використання збережено", Toast.LENGTH_SHORT).show();
        NavHostFragment.findNavController(this).popBackStack();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}