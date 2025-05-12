package com.example.smartgarden.model;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;
import java.util.Objects;

public class ActivityLogEntry {

    @Exclude
    private String logId;
    @ServerTimestamp
    private Date timestamp;
    private ActionType actionType;
    private ObjectType objectType;
    private String objectName;
    private String objectId;
    private String details;

    public enum ActionType {
        ADDED, EDITED, DELETED, HARVEST_LOGGED,
        USAGE_LOGGED
    }

    public enum ObjectType {
        PLANT, MACHINERY, HARVEST,
        MACHINERY_USAGE
    }

    public ActivityLogEntry() {}

    public ActivityLogEntry(ActionType actionType, ObjectType objectType, String objectName, String objectId, String details) {
        this.actionType = actionType;
        this.objectType = objectType;
        this.objectName = objectName;
        this.objectId = objectId;
        this.details = details;
    }

    @Exclude
    public String getLogId() { return logId; }
    public void setLogId(String logId) { this.logId = logId; }
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
    public ActionType getActionType() { return actionType; }
    public void setActionType(ActionType actionType) { this.actionType = actionType; }
    public ObjectType getObjectType() { return objectType; }
    public void setObjectType(ObjectType objectType) { this.objectType = objectType; }
    public String getObjectName() { return objectName; }
    public void setObjectName(String objectName) { this.objectName = objectName; }
    public String getObjectId() { return objectId; }
    public void setObjectId(String objectId) { this.objectId = objectId; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActivityLogEntry that = (ActivityLogEntry) o;
        return Objects.equals(logId, that.logId);
    }
    @Override
    public int hashCode() { return Objects.hash(logId); }
}