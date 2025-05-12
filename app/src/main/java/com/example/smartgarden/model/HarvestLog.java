package com.example.smartgarden.model;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;
import java.util.Objects;

public class HarvestLog {

    @Exclude
    private String logId;

    private String plantId;
    private String plantInfo;

    @ServerTimestamp
    private Date date;

    private double quantity;
    private String unit;
    private String notes;

    public HarvestLog() { }

    public HarvestLog(String plantId, String plantInfo, Date date, double quantity, String unit, String notes) {
        this.plantId = plantId;
        this.plantInfo = plantInfo;
        this.date = date;
        this.quantity = quantity;
        this.unit = unit;
        this.notes = notes;
    }

    @Exclude
    public String getLogId() { return logId; }
    public void setLogId(String logId) { this.logId = logId; }

    public String getPlantId() { return plantId; }
    public void setPlantId(String plantId) { this.plantId = plantId; }

    public String getPlantInfo() { return plantInfo; }
    public void setPlantInfo(String plantInfo) { this.plantInfo = plantInfo; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HarvestLog that = (HarvestLog) o;
        return Objects.equals(logId, that.logId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(logId);
    }
}