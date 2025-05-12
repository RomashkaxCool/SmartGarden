package com.example.smartgarden.ui.machinery;

import android.app.AlertDialog;
import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartgarden.R;
import com.example.smartgarden.databinding.FragmentMachineryBinding;
import com.example.smartgarden.model.Machinery;
import java.util.List;
public class MachineryFragment extends Fragment implements MachineryAdapter.MachineryClickListener {
    private FragmentMachineryBinding binding;
    private MachineryAdapter adapter;
    private MachineryViewModel machineryViewModel;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMachineryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        machineryViewModel = new ViewModelProvider(requireActivity()).get(MachineryViewModel.class);
        setupRecyclerView();
        setupFab();
        observeViewModel();
    }
    private void setupRecyclerView() {
        adapter = new MachineryAdapter(this);
        binding.recyclerViewMachinery.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewMachinery.setAdapter(adapter);
    }
    private void setupFab() {
        binding.fabAddMachinery.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(this);
            try {
                navController.navigate(R.id.action_machinery_to_add_edit);
            } catch (IllegalArgumentException e) {
                Toast.makeText(getContext(), "Не вдалося відкрити екран додавання техніки", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void observeViewModel() {
        machineryViewModel.getMachineryList().observe(getViewLifecycleOwner(), machineryList -> {
            adapter.submitList(machineryList);
        });
    }
    @Override
    public void onEditClick(Machinery machinery) {
        if (machinery.getId() == null) {
            Toast.makeText(getContext(), "Помилка: Неможливо редагувати техніку без ID", Toast.LENGTH_SHORT).show();
            return;
        }
        NavController navController = NavHostFragment.findNavController(this);
        Bundle args = new Bundle();
        args.putString("machineryId", machinery.getId());
        try {
            navController.navigate(R.id.action_machinery_to_add_edit, args);
        } catch (IllegalArgumentException e) {
            Toast.makeText(getContext(), "Не вдалося відкрити екран редагування техніки", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onDeleteClick(Machinery machinery) {
        if (!isAdded() || getContext() == null) return;
        new AlertDialog.Builder(requireContext())
                .setTitle("Видалити техніку?")
                .setMessage("Ви впевнені, що хочете видалити '" + machinery.getName() + "'?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    machineryViewModel.deleteMachinery(machinery);
                    Toast.makeText(getContext(), "'" + machinery.getName() + "' видалено", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    @Override
    public void onItemClick(Machinery machinery) {
        Toast.makeText(getContext(), "Натиснуто на: " + machinery.getName(), Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onLogUsageClick(Machinery machinery) {
        if (machinery.getId() == null) {
            Toast.makeText(getContext(), "Помилка: Неможливо додати лог для техніки без ID", Toast.LENGTH_SHORT).show();
            return;
        }
        NavController navController = NavHostFragment.findNavController(this);
        Bundle args = new Bundle();
        args.putString("machineryId", machinery.getId());
        args.putString("machineryName", machinery.getName());
        try {
            navController.navigate(R.id.action_machinery_to_log_usage, args);
        } catch (Exception e) {
                Toast.makeText(getContext(), "Навігація для логування використання ще не налаштована", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onHistoryClick(Machinery machinery) {
        if (machinery.getId() == null) {
            Toast.makeText(getContext(), "Помилка: Неможливо переглянути історію для техніки без ID", Toast.LENGTH_SHORT).show();
            return;
        }
        NavController navController = NavHostFragment.findNavController(this);
        Bundle args = new Bundle();
        args.putString("machineryId", machinery.getId());
        args.putString("machineryName", machinery.getName());
        try {
            navController.navigate(R.id.action_machinery_to_history, args);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Помилка навігації до історії", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (binding != null && binding.recyclerViewMachinery != null) {
            binding.recyclerViewMachinery.setAdapter(null);
        }
        binding = null;
        adapter = null;
    }
}