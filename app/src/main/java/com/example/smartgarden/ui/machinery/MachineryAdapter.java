package com.example.smartgarden.ui.machinery;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartgarden.R;
import com.example.smartgarden.databinding.ListItemMachineryBinding;
import com.example.smartgarden.model.Machinery;
import java.util.Objects;
public class MachineryAdapter extends ListAdapter<Machinery, MachineryAdapter.ViewHolder> {
    private final MachineryClickListener clickListener;
    public interface MachineryClickListener {
        void onEditClick(Machinery machinery);
        void onDeleteClick(Machinery machinery);
        void onItemClick(Machinery machinery);
        void onLogUsageClick(Machinery machinery);
        void onHistoryClick(Machinery machinery);
    }
    public MachineryAdapter(MachineryClickListener listener) {
        super(DIFF_CALLBACK);
        this.clickListener = listener;
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
        Machinery currentMachinery = getItem(position);
        if (currentMachinery != null) {
            holder.bind(currentMachinery, clickListener);
        }
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ListItemMachineryBinding binding;
        public ViewHolder(@NonNull ListItemMachineryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        public void bind(final Machinery machinery, final MachineryClickListener listener) {
            binding.textViewName.setText(machinery.getName());
            binding.textViewStatus.setText("Стан роботи: " + machinery.getWorkingStatus());
            binding.textViewCondition.setText("Технічний стан: " + machinery.getTechnicalCondition());
            binding.imageViewMachinery.setImageResource(R.drawable.ic_tractor);

            binding.buttonEdit.setOnClickListener(v -> { if (listener != null) listener.onEditClick(machinery); });
            binding.buttonDelete.setOnClickListener(v -> { if (listener != null) listener.onDeleteClick(machinery); });
            itemView.setOnClickListener(v -> { if (listener != null) listener.onItemClick(machinery); });
            binding.buttonLogUsage.setOnClickListener(v -> { if (listener != null) listener.onLogUsageClick(machinery); });
            binding.buttonShowHistory.setOnClickListener(v -> { if (listener != null) listener.onHistoryClick(machinery); });
        }
    }
    private static final DiffUtil.ItemCallback<Machinery> DIFF_CALLBACK = new DiffUtil.ItemCallback<Machinery>() {
        @Override
        public boolean areItemsTheSame(@NonNull Machinery oldItem, @NonNull Machinery newItem) {
            return oldItem.getId() != null && oldItem.getId().equals(newItem.getId());
        }
        @Override
        public boolean areContentsTheSame(@NonNull Machinery oldItem, @NonNull Machinery newItem) {
            return Objects.equals(oldItem.getName(), newItem.getName()) &&
                    Objects.equals(oldItem.getWorkingStatus(), newItem.getWorkingStatus()) &&
                    Objects.equals(oldItem.getTechnicalCondition(), newItem.getTechnicalCondition());
        }
    };
}