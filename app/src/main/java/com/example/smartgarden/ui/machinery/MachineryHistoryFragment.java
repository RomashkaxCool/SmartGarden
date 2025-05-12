package com.example.smartgarden.ui.machinery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartgarden.databinding.FragmentMachineryHistoryBinding;
import com.example.smartgarden.model.MachineryUsageLog;
import java.util.ArrayList;
import java.util.List;
public class MachineryHistoryFragment extends Fragment {
    private FragmentMachineryHistoryBinding binding;
    private MachineryViewModel machineryViewModel;
    private MachineryUsageLogAdapter adapter;
    private String machineryId;
    private String machineryName;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        machineryViewModel = new ViewModelProvider(requireActivity()).get(MachineryViewModel.class);
        if (getArguments() != null) {
            machineryId = getArguments().getString("machineryId");
            machineryName = getArguments().getString("machineryName", "Історія");
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMachineryHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        setupTitle();
        observeViewModel();
        if (machineryId != null) {
            machineryViewModel.loadUsageHistoryFor(machineryId);
        } else {
            binding.textViewNoHistory.setText("Помилка: ID техніки не знайдено.");
            binding.textViewNoHistory.setVisibility(View.VISIBLE);
            binding.recyclerViewMachineryHistory.setVisibility(View.GONE);
        }
    }
    private void setupTitle() {
        binding.textViewHistoryTitle.setText("Історія: " + (machineryName != null ? machineryName : ""));
    }
    private void setupRecyclerView() {
        adapter = new MachineryUsageLogAdapter();
        binding.recyclerViewMachineryHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewMachineryHistory.setAdapter(adapter);
        binding.recyclerViewMachineryHistory.setHasFixedSize(true);
    }
    private void observeViewModel() {
        machineryViewModel.getMachineryHistoryList().observe(getViewLifecycleOwner(), historyLogs -> {
            if (historyLogs != null && !historyLogs.isEmpty()) {
                adapter.submitList(historyLogs);
                binding.recyclerViewMachineryHistory.setVisibility(View.VISIBLE);
                binding.textViewNoHistory.setVisibility(View.GONE);
            } else {
                adapter.submitList(new ArrayList<>());
                binding.recyclerViewMachineryHistory.setVisibility(View.GONE);
                binding.textViewNoHistory.setText("Історія використання порожня.");
                binding.textViewNoHistory.setVisibility(View.VISIBLE);
            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (machineryViewModel != null) {
            machineryViewModel.clearMachineryHistoryListener();
        }
        if (binding != null && binding.recyclerViewMachineryHistory != null) {
            binding.recyclerViewMachineryHistory.setAdapter(null);
        }
        binding = null;
        adapter = null;
    }
}