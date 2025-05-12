package com.example.smartgarden.ui.machinery;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartgarden.databinding.ListItemMachineryUsageLogBinding;
import com.example.smartgarden.model.MachineryUsageLog;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;
public class MachineryUsageLogAdapter extends ListAdapter<MachineryUsageLog, MachineryUsageLogAdapter.ViewHolder> {
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
    public MachineryUsageLogAdapter() {
        super(DIFF_CALLBACK);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ListItemMachineryUsageLogBinding binding = ListItemMachineryUsageLogBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MachineryUsageLog item = getItem(position);
        if (item != null) {
            holder.bind(item);
        }
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ListItemMachineryUsageLogBinding binding;
        public ViewHolder(@NonNull ListItemMachineryUsageLogBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        void bind(final MachineryUsageLog log) {
            binding.textViewUsageTask.setText(log.getTaskDescription() != null ? log.getTaskDescription() : "");
            if (log.getStartDate() != null) {
                binding.textViewUsageStartDatetime.setText("Початок: " + DATE_TIME_FORMAT.format(log.getStartDate().toDate())); // *** .toDate() ***
                binding.textViewUsageStartDatetime.setVisibility(View.VISIBLE);
            } else {
                binding.textViewUsageStartDatetime.setVisibility(View.GONE);
            }
            if (log.getEndDate() != null) {
                binding.textViewUsageEndDatetime.setText("Кінець: " + DATE_TIME_FORMAT.format(log.getEndDate().toDate())); // *** .toDate() ***
                binding.textViewUsageEndDatetime.setVisibility(View.VISIBLE);
            } else {
                binding.textViewUsageEndDatetime.setVisibility(View.GONE);
            }
            if (log.getNotes() != null && !log.getNotes().trim().isEmpty()) {
                binding.textViewUsageNotes.setText("Нотатки: " + log.getNotes());
                binding.textViewUsageNotes.setVisibility(View.VISIBLE);
            } else {
                binding.textViewUsageNotes.setVisibility(View.GONE);
            }
        }
    }
    private static final DiffUtil.ItemCallback<MachineryUsageLog> DIFF_CALLBACK = new DiffUtil.ItemCallback<MachineryUsageLog>() {
        @Override
        public boolean areItemsTheSame(@NonNull MachineryUsageLog oldItem, @NonNull MachineryUsageLog newItem) {
            return oldItem.getLogId() != null && oldItem.getLogId().equals(newItem.getLogId());
        }
        @Override
        public boolean areContentsTheSame(@NonNull MachineryUsageLog oldItem, @NonNull MachineryUsageLog newItem) {
            return Objects.equals(oldItem.getTaskDescription(), newItem.getTaskDescription()) &&
                    Objects.equals(oldItem.getStartDate(), newItem.getStartDate()) && // Timestamp порівнюються напряму
                    Objects.equals(oldItem.getEndDate(), newItem.getEndDate()) &&
                    Objects.equals(oldItem.getNotes(), newItem.getNotes());
        }
    };
}