package com.example.smartgarden.ui.trees;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartgarden.R;
import com.example.smartgarden.databinding.ListItemPlantBinding;
import com.example.smartgarden.model.Plant;
import java.util.Locale;
import java.util.Objects;
public class PlantAdapter extends ListAdapter<Plant, PlantAdapter.PlantViewHolder> {
    private final PlantClickListener clickListener;
    public interface PlantClickListener {
        void onPlantClick(Plant plant);
        void onPlantEditClick(Plant plant);
        void onPlantDeleteClick(Plant plant);
        void onLogHarvestClick(Plant plant);
    }
    public PlantAdapter(PlantClickListener listener) {
        super(DIFF_CALLBACK);
        this.clickListener = listener;
    }
    @NonNull
    @Override
    public PlantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ListItemPlantBinding binding = ListItemPlantBinding.inflate(inflater, parent, false);
        return new PlantViewHolder(binding);
    }
    @Override
    public void onBindViewHolder(@NonNull PlantViewHolder holder, int position) {
        Plant currentPlant = getItem(position);
        if (currentPlant != null) {
            holder.bind(currentPlant, clickListener);
        }
    }
    static class PlantViewHolder extends RecyclerView.ViewHolder {
        private final ListItemPlantBinding binding;
        public PlantViewHolder(@NonNull ListItemPlantBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        public void bind(final Plant plant, final PlantClickListener listener) {
            binding.textViewPlantName.setText(plant.getCulture());
            binding.textViewPlantVariety.setText("Сорт: " + plant.getVariety());
            binding.textViewPlantAge.setText(String.format(Locale.getDefault(), "Вік: %d р.", plant.getAge()));
            binding.textViewPlantQuantity.setText(String.format(Locale.getDefault(), "(%d шт.)", plant.getQuantity()));
            binding.textViewPlantRow.setText("Ряд: " + plant.getGardenRow());
            binding.textViewPlantStatus.setText("Стан: " + plant.getStatus());
            binding.imageViewPlant.setImageResource(plant.getType() == Plant.PlantType.TREE ? R.drawable.ic_tree : R.drawable.ic_berries_placeholder);
            itemView.setOnClickListener(v -> { if(listener != null) listener.onPlantClick(plant); });
            binding.buttonPlantEdit.setOnClickListener(v -> { if (listener != null) listener.onPlantEditClick(plant); });
            binding.buttonPlantDelete.setOnClickListener(v -> { if (listener != null) listener.onPlantDeleteClick(plant); });
            binding.buttonLogHarvest.setOnClickListener(v -> { if (listener != null) listener.onLogHarvestClick(plant); });
        }
    }
    private static final DiffUtil.ItemCallback<Plant> DIFF_CALLBACK = new DiffUtil.ItemCallback<Plant>() {
        @Override
        public boolean areItemsTheSame(@NonNull Plant oldItem, @NonNull Plant newItem) {
            return oldItem.getId() != null && oldItem.getId().equals(newItem.getId());
        }
        @Override
        public boolean areContentsTheSame(@NonNull Plant oldItem, @NonNull Plant newItem) {
            return Objects.equals(oldItem.getCulture(), newItem.getCulture()) &&
                    Objects.equals(oldItem.getVariety(), newItem.getVariety()) &&
                    oldItem.getAge() == newItem.getAge() &&
                    oldItem.getQuantity() == newItem.getQuantity() &&
                    Objects.equals(oldItem.getGardenRow(), newItem.getGardenRow()) &&
                    Objects.equals(oldItem.getStatus(), newItem.getStatus()) &&
                    Objects.equals(oldItem.getType(), newItem.getType());
        }
    };
}