package com.example.smartgarden.ui.home;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartgarden.databinding.ListItemActivityLogBinding; // Binding для елемента
import com.example.smartgarden.model.ActivityLogEntry;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;
public class ActivityLogAdapter extends ListAdapter<ActivityLogEntry, ActivityLogAdapter.ViewHolder> {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
    public ActivityLogAdapter() {
        super(DIFF_CALLBACK);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ListItemActivityLogBinding binding = ListItemActivityLogBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ActivityLogEntry item = getItem(position);
        if (item != null) {
            holder.bind(item);
        }
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ListItemActivityLogBinding binding;
        public ViewHolder(@NonNull ListItemActivityLogBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        void bind(ActivityLogEntry logEntry) {
            String description = buildDescription(logEntry);
            binding.textViewLogDescription.setText(description);

            if (logEntry.getTimestamp() != null) {
                binding.textViewLogTimestamp.setText(DATE_FORMAT.format(logEntry.getTimestamp()));
            } else {
                binding.textViewLogTimestamp.setText("");
            }
        }
        private String buildDescription(ActivityLogEntry logEntry) {
            String actionStr = "";
            String objectTypeStr = "";
            if (logEntry.getActionType() != null) {
                switch (logEntry.getActionType()) {
                    case ADDED: actionStr = "Додано"; break;
                    case EDITED: actionStr = "Змінено"; break;
                    case DELETED: actionStr = "Видалено"; break;
                    case HARVEST_LOGGED: actionStr = "Запис врожаю"; break;
                    default: actionStr = "Дія"; break;
                }
            }
            if (logEntry.getObjectType() != null) {
                switch (logEntry.getObjectType()) {
                    case PLANT: objectTypeStr = "Рослина"; break;
                    case MACHINERY: objectTypeStr = "Техніка"; break;
                    case HARVEST: objectTypeStr = "Врожай"; break; // Можливо не використовується як тип об'єкта
                    default: objectTypeStr = "Об'єкт"; break;
                }
            }
            StringBuilder sb = new StringBuilder();
            sb.append(actionStr);
            if (!objectTypeStr.isEmpty()) {
                sb.append(" ").append(objectTypeStr);
            }
            if (logEntry.getObjectName() != null && !logEntry.getObjectName().isEmpty()) {
                sb.append(": ").append(logEntry.getObjectName());
            }
            if (logEntry.getDetails() != null && !logEntry.getDetails().isEmpty()) {
                sb.append(" (").append(logEntry.getDetails()).append(")");
            }
            return sb.toString();
        }
    }
    private static final DiffUtil.ItemCallback<ActivityLogEntry> DIFF_CALLBACK = new DiffUtil.ItemCallback<ActivityLogEntry>() {
        @Override
        public boolean areItemsTheSame(@NonNull ActivityLogEntry oldItem, @NonNull ActivityLogEntry newItem) {
            return oldItem.getLogId() != null && oldItem.getLogId().equals(newItem.getLogId());
        }
        @Override
        public boolean areContentsTheSame(@NonNull ActivityLogEntry oldItem, @NonNull ActivityLogEntry newItem) {
            return Objects.equals(oldItem.getTimestamp(), newItem.getTimestamp()) &&
                    Objects.equals(oldItem.getObjectName(), newItem.getObjectName()) &&
                    Objects.equals(oldItem.getActionType(), newItem.getActionType());
        }
    };
}