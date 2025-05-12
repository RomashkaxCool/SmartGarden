package com.example.smartgarden.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer; // Імпорт Observer
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView; // Імпорт RecyclerView
import com.example.smartgarden.databinding.FragmentHomeBinding;
import com.example.smartgarden.model.ActivityLogEntry; // Імпорт моделі

import java.util.ArrayList;
import java.util.List; // Імпорт List

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private ActivityLogAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        setupRecyclerView();
        observeViewModel();
    }
    private void setupRecyclerView() {
        adapter = new ActivityLogAdapter();
        binding.recyclerViewRecentChanges.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewRecentChanges.setAdapter(adapter);
    }
    private void observeViewModel() {
        homeViewModel.getRecentActivities().observe(getViewLifecycleOwner(), activityLogEntries -> {
            if (activityLogEntries != null && !activityLogEntries.isEmpty()) {
                adapter.submitList(activityLogEntries);
                binding.recyclerViewRecentChanges.setVisibility(View.VISIBLE);
                binding.textViewNoRecentChanges.setVisibility(View.GONE);
            } else {
                adapter.submitList(new ArrayList<>());
                binding.recyclerViewRecentChanges.setVisibility(View.GONE);
                binding.textViewNoRecentChanges.setVisibility(View.VISIBLE);
            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.recyclerViewRecentChanges.setAdapter(null);
        binding = null;
        adapter = null;
    }
}