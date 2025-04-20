package com.example.smartgarden.ui.machinery;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartgarden.R;
import com.example.smartgarden.databinding.ListItemMachineryBinding;
import com.example.smartgarden.model.Machinery;

import java.util.ArrayList;
import java.util.List;

public class MachineryAdapter extends RecyclerView.Adapter<MachineryAdapter.ViewHolder> {

    private List<Machinery> machineryList = new ArrayList<>();
    private MachineryClickListener clickListener;

    public MachineryAdapter(MachineryClickListener listener) {
        this.clickListener = listener;
    }

    public interface MachineryClickListener {
        void onEditClick(Machinery machinery);
        void onDeleteClick(Machinery machinery);
        void onHistoryClick(Machinery machinery);
        void onItemClick(Machinery machinery);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ListItemMachineryBinding binding = ListItemMachineryBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Machinery currentMachinery = machineryList.get(position);
        holder.bind(currentMachinery, clickListener);
    }

    @Override
    public int getItemCount() {
        return machineryList.size();
    }

    public void setData(List<Machinery> newMachineryList) {
        this.machineryList.clear();
        if (newMachineryList != null) {
            this.machineryList.addAll(newMachineryList);
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ListItemMachineryBinding binding; // Binding для доступу до View

        public ViewHolder(@NonNull ListItemMachineryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final Machinery machinery, final MachineryClickListener listener) {
            binding.textViewName.setText(machinery.getName());
            binding.textViewStatus.setText("Стан роботи: " + machinery.getWorkingStatus());
            binding.textViewCondition.setText("Технічний стан: " + machinery.getTechnicalCondition());

            binding.imageViewMachinery.setImageResource(R.drawable.ic_tractor);


            binding.buttonEdit.setOnClickListener(v -> listener.onEditClick(machinery));
            binding.buttonDelete.setOnClickListener(v -> listener.onDeleteClick(machinery));
            binding.buttonHistory.setOnClickListener(v -> listener.onHistoryClick(machinery));

            itemView.setOnClickListener(v -> listener.onItemClick(machinery));
        }
    }
}