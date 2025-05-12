package com.example.smartgarden.ui.reporting;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.smartgarden.model.HarvestLog;
import com.example.smartgarden.model.Machinery;
import com.example.smartgarden.model.Plant;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
public class ReportingViewModel extends ViewModel {
    private static final String TAG = "ReportingViewModel";
    private final MutableLiveData<Long> treeCount = new MutableLiveData<>(0L);
    private final MutableLiveData<Long> berryCount = new MutableLiveData<>(0L);
    private final MutableLiveData<Long> totalPlantCount = new MutableLiveData<>(0L);
    public LiveData<Long> getTreeCount() { return treeCount; }
    public LiveData<Long> getBerryCount() { return berryCount; }
    public LiveData<Long> getTotalPlantCount() { return totalPlantCount; }
    private final MutableLiveData<Long> statusReadyHarvestCount = new MutableLiveData<>(0L);
    private final MutableLiveData<Long> statusHarvestedCount = new MutableLiveData<>(0L);
    private final MutableLiveData<Long> statusPrunedCount = new MutableLiveData<>(0L);
    private final MutableLiveData<Long> statusSprayedCount = new MutableLiveData<>(0L);
    private final MutableLiveData<Long> statusOtherCount = new MutableLiveData<>(0L);
    public LiveData<Long> getStatusReadyHarvestCount() { return statusReadyHarvestCount; }
    public LiveData<Long> getStatusHarvestedCount() { return statusHarvestedCount; }
    public LiveData<Long> getStatusPrunedCount() { return statusPrunedCount; }
    public LiveData<Long> getStatusSprayedCount() { return statusSprayedCount; }
    public LiveData<Long> getStatusOtherCount() { return statusOtherCount; }
    private final MutableLiveData<Long> ageGroup1_3Count = new MutableLiveData<>(0L);
    private final MutableLiveData<Long> ageGroup4_6Count = new MutableLiveData<>(0L);
    private final MutableLiveData<Long> ageGroup7plusCount = new MutableLiveData<>(0L);
    public LiveData<Long> getAgeGroup1_3Count() { return ageGroup1_3Count; }
    public LiveData<Long> getAgeGroup4_6Count() { return ageGroup4_6Count; }
    public LiveData<Long> getAgeGroup7plusCount() { return ageGroup7plusCount; }
    private final MutableLiveData<Long> totalMachineryCount = new MutableLiveData<>(0L);
    private final MutableLiveData<Long> conditionOkCount = new MutableLiveData<>(0L);
    private final MutableLiveData<Long> conditionRepairCount = new MutableLiveData<>(0L);
    private final MutableLiveData<Long> conditionServiceCount = new MutableLiveData<>(0L);
    public LiveData<Long> getTotalMachineryCount() { return totalMachineryCount; }
    public LiveData<Long> getConditionOkCount() { return conditionOkCount; }
    public LiveData<Long> getConditionRepairCount() { return conditionRepairCount; }
    public LiveData<Long> getConditionServiceCount() { return conditionServiceCount; }
    private final MutableLiveData<Long> workingStatusActiveCount = new MutableLiveData<>(0L);
    private final MutableLiveData<Long> workingStatusIdleCount = new MutableLiveData<>(0L);
    private final MutableLiveData<Long> workingStatusOtherCount = new MutableLiveData<>(0L);
    public LiveData<Long> getWorkingStatusActiveCount() { return workingStatusActiveCount; }
    public LiveData<Long> getWorkingStatusIdleCount() { return workingStatusIdleCount; }
    public LiveData<Long> getWorkingStatusOtherCount() { return workingStatusOtherCount; }

    private final MutableLiveData<Double> totalHarvestKgCurrentYear = new MutableLiveData<>(0.0);
    private final MutableLiveData<String> lastHarvestDateString = new MutableLiveData<>("-");
    public LiveData<Double> getTotalHarvestKgCurrentYear() { return totalHarvestKgCurrentYear; }
    public LiveData<String> getLastHarvestDateString() { return lastHarvestDateString; }
    private FirebaseFirestore db;
    private CollectionReference plantsRef;
    private CollectionReference machineryRef;
    private CollectionReference harvestLogsRef;
    private ListenerRegistration plantsListenerRegistration;
    private ListenerRegistration machineryListenerRegistration;
    private ListenerRegistration harvestListenerRegistration;
    public ReportingViewModel() {
        db = FirebaseFirestore.getInstance();
        plantsRef = db.collection("plants");
        machineryRef = db.collection("machinery");
        harvestLogsRef = db.collection("harvestLogs");
        loadPlantData();
        loadMachineryStats();
        listenForHarvestLogs();
    }
    private void loadPlantData() {
        if (plantsListenerRegistration != null) { plantsListenerRegistration.remove(); }
        plantsListenerRegistration = plantsRef.addSnapshotListener((snapshots, error) -> {
            long treeC=0, berryC=0, readyH=0, harvested=0, pruned=0, sprayed=0, otherS=0, age1=0, age4=0, age7=0;
            if (error != null) { Log.e(TAG, "Plant listen failed:", error); }
            else if (snapshots != null) {
                List<Plant> plants = snapshots.toObjects(Plant.class);
                for (Plant plant : plants) {
                    if (plant.getType() == Plant.PlantType.TREE) treeC++; else if (plant.getType() == Plant.PlantType.BERRY) berryC++;
                    String status = plant.getStatus();
                    if (status != null) {
                        if ("Готово до збору".equalsIgnoreCase(status)) readyH++;
                        else if ("Зібрано".equalsIgnoreCase(status)) harvested++;
                        else if ("Обрізано".equalsIgnoreCase(status)) pruned++;
                        else if ("Обприскано".equalsIgnoreCase(status)) sprayed++;
                        else if (!status.trim().isEmpty()) otherS++; else otherS++;
                    } else otherS++;
                    int age = plant.getAge();
                    if (age >= 1 && age <= 3) age1++; else if (age >= 4 && age <= 6) age4++; else if (age >= 7) age7++;
                }
            }
            treeCount.postValue(treeC); berryCount.postValue(berryC); totalPlantCount.postValue(treeC + berryC);
            statusReadyHarvestCount.postValue(readyH); statusHarvestedCount.postValue(harvested);
            statusPrunedCount.postValue(pruned); statusSprayedCount.postValue(sprayed); statusOtherCount.postValue(otherS);
            ageGroup1_3Count.postValue(age1); ageGroup4_6Count.postValue(age4); ageGroup7plusCount.postValue(age7);
        });
    }
    private void loadMachineryStats() {
        if (machineryListenerRegistration != null) { machineryListenerRegistration.remove(); }
        machineryListenerRegistration = machineryRef.addSnapshotListener((snapshots, error) -> {
            long totalC=0, okC=0, repairC=0, serviceC=0, activeC=0, idleC=0, otherW=0;
            if (error != null) { Log.e(TAG, "Machinery listen failed:", error); }
            else if (snapshots != null) {
                totalC = snapshots.size();
                List<Machinery> list = snapshots.toObjects(Machinery.class);
                for (Machinery item : list) {
                    String condition = item.getTechnicalCondition();
                    if (condition != null) {
                        if ("Справний".equalsIgnoreCase(condition)||"Справна".equalsIgnoreCase(condition)) okC++;
                        else if ("Ремонт".equalsIgnoreCase(condition)||"Потребує ремонту".equalsIgnoreCase(condition)) repairC++;
                        else if ("ТО".equalsIgnoreCase(condition)||"На ТО".equalsIgnoreCase(condition)||"На обслуговуванні".equalsIgnoreCase(condition)) serviceC++;
                    }
                    String workStatus = item.getWorkingStatus();
                    if (workStatus != null) {
                        if ("Стоїть".equalsIgnoreCase(workStatus)||"Готова до роботи".equalsIgnoreCase(workStatus)) idleC++;
                        else if (!workStatus.trim().isEmpty()) activeC++;
                        else otherW++;
                    } else otherW++;
                }
            }
            totalMachineryCount.postValue(totalC); conditionOkCount.postValue(okC);
            conditionRepairCount.postValue(repairC); conditionServiceCount.postValue(serviceC);
            workingStatusActiveCount.postValue(activeC); workingStatusIdleCount.postValue(idleC);
            workingStatusOtherCount.postValue(otherW);
        });
    }
    private void listenForHarvestLogs() {
        if (harvestListenerRegistration != null) {
            harvestListenerRegistration.remove();
        }

        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        cal.set(currentYear, Calendar.JANUARY, 1, 0, 0, 0);
        Date startOfYear = cal.getTime();
        cal.set(currentYear, Calendar.DECEMBER, 31, 23, 59, 59);
        Date endOfYear = cal.getTime();
        Query query = harvestLogsRef
                .whereGreaterThanOrEqualTo("date", startOfYear)
                .whereLessThanOrEqualTo("date", endOfYear)
                .orderBy("date", Query.Direction.DESCENDING);
        harvestListenerRegistration = query.addSnapshotListener((snapshots, error) -> {
            double totalKg = 0.0;
            Date lastDate = null;
            if (error != null) { }
            else if (snapshots != null && !snapshots.isEmpty()) {
                List<HarvestLog> logs = snapshots.toObjects(HarvestLog.class);
                for (HarvestLog log : logs) {
                    if (log.getDate() != null) {
                        if (lastDate == null || log.getDate().after(lastDate)) {
                            lastDate = log.getDate();
                        }
                    }
                    if ("кг".equalsIgnoreCase(log.getUnit())) {
                        totalKg += log.getQuantity();
                    }
                }
            }
            totalHarvestKgCurrentYear.postValue(totalKg);
            if (lastDate != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                lastHarvestDateString.postValue(sdf.format(lastDate));
            } else {
                lastHarvestDateString.postValue("-");
            }
        });
    }
    @Override
    protected void onCleared() {
        super.onCleared();
        if (plantsListenerRegistration != null) { plantsListenerRegistration.remove(); }
        if (machineryListenerRegistration != null) { machineryListenerRegistration.remove(); }
        if (harvestListenerRegistration != null) { harvestListenerRegistration.remove(); }
    }
}