package com.example.smartgarden.ui.reporting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.smartgarden.databinding.FragmentReportingBinding;

public class ReportingFragment extends Fragment {

    private FragmentReportingBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReportingBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // binding.textReporting.setText("Графіки звітності");
        // Ваша логіка тут...

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}