package com.example.smartgarden.model;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;
import java.util.Objects;
public class MachineryUsageLog {
    @Exclude
    private String logId;
    private String machineryId;
    private String machineryName;
    @ServerTimestamp
    private Date logTimestamp;
    private Timestamp startDate;
    private Timestamp endDate;
    private Double durationHours;
    private String taskDescription;
    private String notes;
    public MachineryUsageLog() {}
    public MachineryUsageLog(String machineryId, String machineryName, Timestamp startDate, Timestamp endDate, Double durationHours, String taskDescription, String notes) {
        this.machineryId = machineryId;
        this.machineryName = machineryName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.durationHours = durationHours;
        this.taskDescription = taskDescription;
        this.notes = notes;
    }
    @Exclude
    public String getLogId() { return logId; }
    public void setLogId(String logId) { this.logId = logId; }
    public String getMachineryId() { return machineryId; }
    public void setMachineryId(String machineryId) { this.machineryId = machineryId; }
    public String getMachineryName() { return machineryName; }
    public void setMachineryName(String machineryName) { this.machineryName = machineryName; }
    public Date getLogTimestamp() { return logTimestamp; }
    public void setLogTimestamp(Date logTimestamp) { this.logTimestamp = logTimestamp; }
    public Timestamp getStartDate() { return startDate; } // Тип Timestamp
    public void setStartDate(Timestamp startDate) { this.startDate = startDate; } // Тип Timestamp
    public Timestamp getEndDate() { return endDate; } // Тип Timestamp
    public void setEndDate(Timestamp endDate) { this.endDate = endDate; } // Тип Timestamp
    public Double getDurationHours() { return durationHours; }
    public void setDurationHours(Double durationHours) { this.durationHours = durationHours; }
    public String getTaskDescription() { return taskDescription; }
    public void setTaskDescription(String taskDescription) { this.taskDescription = taskDescription; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MachineryUsageLog that = (MachineryUsageLog) o;
        return Objects.equals(logId, that.logId);
    }
    @Override
    public int hashCode() {
        return Objects.hash(logId);
    }
    @Override
    public String toString() {
        return "MachineryUsageLog{" +
                "logId='" + logId + '\'' +
                ", machineryName='" + machineryName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", startDate=" + (startDate != null ? startDate.toDate() : "null") +
                ", endDate=" + (endDate != null ? endDate.toDate() : "null") +
                '}';
    }
}