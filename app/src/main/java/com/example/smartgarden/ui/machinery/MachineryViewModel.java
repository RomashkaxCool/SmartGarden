package com.example.smartgarden.ui.machinery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.smartgarden.model.ActivityLogEntry;
import com.example.smartgarden.model.Machinery;
import com.example.smartgarden.model.MachineryUsageLog;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MachineryViewModel extends ViewModel {

    private final MutableLiveData<List<Machinery>> machineryListLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Machinery> selectedMachinery = new MutableLiveData<>();
    private final MutableLiveData<List<MachineryUsageLog>> machineryHistoryList = new MutableLiveData<>();

    public LiveData<List<Machinery>> getMachineryList() { return machineryListLiveData; }
    public LiveData<Machinery> getSelectedMachinery() { return selectedMachinery; }
    public LiveData<List<MachineryUsageLog>> getMachineryHistoryList() { return machineryHistoryList; }

    private FirebaseFirestore db;
    private CollectionReference machineryRef;
    private CollectionReference activityLogRef;
    private CollectionReference machineryUsageLogsRef;
    private ListenerRegistration machineryListenerRegistration;
    private ListenerRegistration machineryHistoryListenerRegistration;

    public MachineryViewModel() {
        db = FirebaseFirestore.getInstance();
        machineryRef = db.collection("machinery");
        activityLogRef = db.collection("activityLog");
        machineryUsageLogsRef = db.collection("machineryUsageLogs");
        listenForMachineryUpdates();
    }

    private void listenForMachineryUpdates() {
        if (machineryListenerRegistration != null) { machineryListenerRegistration.remove(); }
        machineryListenerRegistration = machineryRef.addSnapshotListener((snapshots, error) -> {
            List<Machinery> machineryList = new ArrayList<>();
            if (error != null) { }
            else if (snapshots != null) {
                snapshots.forEach(doc -> {
                    Machinery machinery = doc.toObject(Machinery.class);
                    if (machinery != null) { machinery.setId(doc.getId()); machineryList.add(machinery); }
                });
            }
            machineryListLiveData.postValue(machineryList);
        });
    }

    public void loadMachineryForEdit(String machineryId) {
        if (machineryId == null || machineryId.isEmpty()) { selectedMachinery.postValue(null); return; }
        machineryRef.document(machineryId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Machinery machinery = documentSnapshot.toObject(Machinery.class);
                        if (machinery != null) { machinery.setId(documentSnapshot.getId()); selectedMachinery.postValue(machinery); }
                        else { selectedMachinery.postValue(null); }
                    } else { selectedMachinery.postValue(null); }
                })
                .addOnFailureListener(e -> { selectedMachinery.postValue(null); });
    }

    public void clearSelectedMachinery() { selectedMachinery.setValue(null); }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (machineryListenerRegistration != null) { machineryListenerRegistration.remove(); }
        if (machineryHistoryListenerRegistration != null) { machineryHistoryListenerRegistration.remove(); }
    }

    public void addMachinery(Machinery machinery) {
        machineryRef.add(machinery)
                .addOnSuccessListener(documentReference -> { logActivity(ActivityLogEntry.ActionType.ADDED, ActivityLogEntry.ObjectType.MACHINERY, machinery.getName(), documentReference.getId(), null); })
                .addOnFailureListener(e -> { });
    }

    public void updateMachinery(Machinery machinery) {
        if (machinery.getId() == null || machinery.getId().isEmpty()) return;
        machineryRef.document(machinery.getId()).set(machinery)
                .addOnSuccessListener(aVoid -> { logActivity(ActivityLogEntry.ActionType.EDITED, ActivityLogEntry.ObjectType.MACHINERY, machinery.getName(), machinery.getId(), null); })
                .addOnFailureListener(e -> { });
    }

    public void deleteMachinery(Machinery machinery) {
        if (machinery.getId() == null || machinery.getId().isEmpty()) return;
        String name = machinery.getName(); String deletedId = machinery.getId();
        machineryRef.document(machinery.getId()).delete()
                .addOnSuccessListener(aVoid -> { logActivity(ActivityLogEntry.ActionType.DELETED, ActivityLogEntry.ObjectType.MACHINERY, name, deletedId, null); })
                .addOnFailureListener(e -> { });
    }

    public void addMachineryUsageLog(MachineryUsageLog logEntry) {
        if (machineryUsageLogsRef == null) return;
        machineryUsageLogsRef.add(logEntry)
                .addOnSuccessListener(documentReference -> {
                    String details = "Завдання: " + (logEntry.getTaskDescription() != null ? logEntry.getTaskDescription() : "N/A");
                    logActivity(ActivityLogEntry.ActionType.USAGE_LOGGED, ActivityLogEntry.ObjectType.MACHINERY_USAGE, logEntry.getMachineryName(), logEntry.getMachineryId(), details);
                })
                .addOnFailureListener(e -> { });
    }

    public void loadUsageHistoryFor(String machineryId) {
        if (machineryHistoryListenerRegistration != null) {
            machineryHistoryListenerRegistration.remove();
            machineryHistoryListenerRegistration = null;
        }
        machineryHistoryList.setValue(new ArrayList<>());

        if (machineryId == null || machineryId.isEmpty()) {
            return;
        }

        Query query = machineryUsageLogsRef
                .whereEqualTo("machineryId", machineryId)
                .orderBy("startDate", Query.Direction.DESCENDING);

        machineryHistoryListenerRegistration = query.addSnapshotListener((snapshots, error) -> {
            List<MachineryUsageLog> logs = new ArrayList<>();
            if (error != null) {
            } else if (snapshots != null) {
                for (DocumentSnapshot doc : snapshots.getDocuments()) {
                    MachineryUsageLog log = null;
                    try {
                        log = doc.toObject(MachineryUsageLog.class);
                        if (log != null) {
                            log.setLogId(doc.getId());
                            logs.add(log);
                        } else { }
                    } catch (Exception e) { }
                }
            } else {
            }
            machineryHistoryList.postValue(logs);
        });
    }

    public void clearMachineryHistoryListener() {
        if (machineryHistoryListenerRegistration != null) {
            machineryHistoryListenerRegistration.remove();
            machineryHistoryListenerRegistration = null;
        }
        machineryHistoryList.postValue(new ArrayList<>());
    }

    private void logActivity(ActivityLogEntry.ActionType actionType, ActivityLogEntry.ObjectType objectType, String objectName, String objectId, String details) {
        if (activityLogRef == null) return;
        ActivityLogEntry log = new ActivityLogEntry(actionType, objectType, objectName, objectId, details);
        activityLogRef.add(log).addOnFailureListener(e -> { });
    }
}