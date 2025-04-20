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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.smartgarden.R;
import com.example.smartgarden.databinding.FragmentMachineryBinding;
import com.example.smartgarden.model.Machinery;

import java.util.ArrayList;
import java.util.List;

public class MachineryFragment extends Fragment implements MachineryAdapter.MachineryClickListener {

    private FragmentMachineryBinding binding;
    private MachineryAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMachineryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupFab();
        loadTestData();
    }

    private void setupRecyclerView() {
        adapter = new MachineryAdapter(this);
        binding.recyclerViewMachinery.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewMachinery.setAdapter(adapter);
    }

    private void setupFab() {
        binding.fabAddMachinery.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(this);
            Toast.makeText(getContext(), "Перехід на екран додавання", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadTestData() {
        List<Machinery> testData = new ArrayList<>();
        testData.add(new Machinery("1", "Трактор John Deere 8R", null, "Стоїть", "Справний"));
        testData.add(new Machinery("2", "Комбайн Claas Lexion", null, "Жнива", "Потребує ТО"));
        testData.add(new Machinery("3", "Обприскувач Amazone", null, "Обприскує", "Справний"));
        adapter.setData(testData);
    }

    @Override
    public void onEditClick(Machinery machinery) {
        NavController navController = NavHostFragment.findNavController(this);
        Toast.makeText(getContext(), "Редагувати: " + machinery.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(Machinery machinery) {
        new AlertDialog.Builder(getContext())
                .setTitle("Видалити техніку?")
                .setMessage("Ви впевнені, що хочете видалити '" + machinery.getName() + "'?")
                .setPositiveButton("Так", (dialog, which) -> {
                    Toast.makeText(getContext(), "Видалено: " + machinery.getName(), Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Ні", null)
                .show();
    }

    @Override
    public void onHistoryClick(Machinery machinery) {
        NavController navController = NavHostFragment.findNavController(this);
        Toast.makeText(getContext(), "Історія: " + machinery.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(Machinery machinery) {
        Toast.makeText(getContext(), "Натиснуто: " + machinery.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}