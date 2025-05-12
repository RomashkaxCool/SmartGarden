package com.example.smartgarden.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.smartgarden.model.ActivityLogEntry;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
public class HomeViewModel extends ViewModel {
    private static final int RECENT_ACTIVITIES_LIMIT = 20;
    private final MutableLiveData<List<ActivityLogEntry>> recentActivitiesLiveData = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<ActivityLogEntry>> getRecentActivities() {
        return recentActivitiesLiveData;
    }
    private FirebaseFirestore db;
    private CollectionReference activityLogRef;
    private ListenerRegistration activityListenerRegistration;
    public HomeViewModel() {
        db = FirebaseFirestore.getInstance();
        activityLogRef = db.collection("activityLog");
        listenForRecentActivities();
    }
    private void listenForRecentActivities() {
        if (activityListenerRegistration != null) {
            activityListenerRegistration.remove();
        }
        activityListenerRegistration = activityLogRef
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(RECENT_ACTIVITIES_LIMIT)
                .addSnapshotListener((snapshots, error) -> {
                    List<ActivityLogEntry> logs = new ArrayList<>();
                    if (error != null) {
                    } else if (snapshots != null) {
                        snapshots.forEach(doc -> {
                            ActivityLogEntry logEntry = doc.toObject(ActivityLogEntry.class);
                            if (logEntry != null) {
                                logEntry.setLogId(doc.getId());
                                logs.add(logEntry);
                            }
                        });
                    }
                    recentActivitiesLiveData.postValue(logs);
                });
    }
    @Override
    protected void onCleared() {
        super.onCleared();
        if (activityListenerRegistration != null) {
            activityListenerRegistration.remove();
        }
    }
}