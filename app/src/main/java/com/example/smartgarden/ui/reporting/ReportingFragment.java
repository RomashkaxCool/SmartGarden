package com.example.smartgarden.ui.reporting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.example.smartgarden.R;
import com.example.smartgarden.databinding.FragmentReportingBinding;
import java.util.Locale;
public class ReportingFragment extends Fragment {
    private FragmentReportingBinding binding;
    private ReportingViewModel reportingViewModel;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReportingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        reportingViewModel = new ViewModelProvider(this).get(ReportingViewModel.class);
        observeViewModel();
    }
    private void observeViewModel() {
        reportingViewModel.getTreeCount().observe(getViewLifecycleOwner(), count -> binding.textViewTreeCount.setText(String.format(Locale.getDefault(), "Дерева: %d", count != null ? count : 0)));
        reportingViewModel.getBerryCount().observe(getViewLifecycleOwner(), count -> binding.textViewBerryCount.setText(String.format(Locale.getDefault(), "Ягоди: %d", count != null ? count : 0)));
        reportingViewModel.getTotalPlantCount().observe(getViewLifecycleOwner(), count -> binding.textViewTotalPlantCount.setText(String.format(Locale.getDefault(), "Всього рослин: %d", count != null ? count : 0)));
        reportingViewModel.getStatusReadyHarvestCount().observe(getViewLifecycleOwner(), count -> binding.textViewPlantStatusReadyHarvest.setText(String.format(Locale.getDefault(), "Готово до збору: %d", count != null ? count : 0)));
        reportingViewModel.getStatusHarvestedCount().observe(getViewLifecycleOwner(), count -> binding.textViewPlantStatusHarvested.setText(String.format(Locale.getDefault(), "Зібрано: %d", count != null ? count : 0)));
        reportingViewModel.getStatusPrunedCount().observe(getViewLifecycleOwner(), count -> binding.textViewPlantStatusPruned.setText(String.format(Locale.getDefault(), "Обрізано: %d", count != null ? count : 0)));
        reportingViewModel.getStatusSprayedCount().observe(getViewLifecycleOwner(), count -> binding.textViewPlantStatusSprayed.setText(String.format(Locale.getDefault(), "Обприскано: %d", count != null ? count : 0)));
        reportingViewModel.getStatusOtherCount().observe(getViewLifecycleOwner(), count -> binding.textViewPlantStatusOther.setText(String.format(Locale.getDefault(), "Інший стан: %d", count != null ? count : 0)));
        reportingViewModel.getAgeGroup1_3Count().observe(getViewLifecycleOwner(), count -> binding.textViewPlantAge13.setText(String.format(Locale.getDefault(), "1-3 роки: %d", count != null ? count : 0)));
        reportingViewModel.getAgeGroup4_6Count().observe(getViewLifecycleOwner(), count -> binding.textViewPlantAge46.setText(String.format(Locale.getDefault(), "4-6 років: %d", count != null ? count : 0)));
        reportingViewModel.getAgeGroup7plusCount().observe(getViewLifecycleOwner(), count -> binding.textViewPlantAge7Plus.setText(String.format(Locale.getDefault(), "7+ років: %d", count != null ? count : 0)));
        reportingViewModel.getTotalMachineryCount().observe(getViewLifecycleOwner(), count -> binding.textViewTotalMachineryCount.setText(String.format(Locale.getDefault(), "Всього техніки: %d", count != null ? count : 0)));
        reportingViewModel.getConditionOkCount().observe(getViewLifecycleOwner(), count -> binding.textViewMachineryConditionOk.setText(String.format(Locale.getDefault(), "Справний/а: %d", count != null ? count : 0)));
        reportingViewModel.getConditionRepairCount().observe(getViewLifecycleOwner(), count -> binding.textViewMachineryConditionRepair.setText(String.format(Locale.getDefault(), "Потребує ремонту: %d", count != null ? count : 0)));
        reportingViewModel.getConditionServiceCount().observe(getViewLifecycleOwner(), count -> binding.textViewMachineryConditionService.setText(String.format(Locale.getDefault(), "На ТО/Обслуговуванні: %d", count != null ? count : 0)));
        reportingViewModel.getWorkingStatusActiveCount().observe(getViewLifecycleOwner(), count -> binding.textViewMachineryWorkingStatusActive.setText(String.format(Locale.getDefault(), "Працює: %d", count != null ? count : 0)));
        reportingViewModel.getWorkingStatusIdleCount().observe(getViewLifecycleOwner(), count -> binding.textViewMachineryWorkingStatusIdle.setText(String.format(Locale.getDefault(), "Стоїть/Готова: %d", count != null ? count : 0)));
        reportingViewModel.getWorkingStatusOtherCount().observe(getViewLifecycleOwner(), count -> binding.textViewMachineryWorkingStatusOther.setText(String.format(Locale.getDefault(), "Інший стан/Невідомо: %d", count != null ? count : 0)));
        reportingViewModel.getTotalHarvestKgCurrentYear().observe(getViewLifecycleOwner(), totalKg -> binding.textViewHarvestTotalKg.setText(String.format(Locale.getDefault(), "Всього зібрано (кг): %.1f", totalKg != null ? totalKg : 0.0)));
        reportingViewModel.getLastHarvestDateString().observe(getViewLifecycleOwner(), dateStr -> binding.textViewLastHarvestDate.setText("Останній збір: " + (dateStr != null ? dateStr : "-")));

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}