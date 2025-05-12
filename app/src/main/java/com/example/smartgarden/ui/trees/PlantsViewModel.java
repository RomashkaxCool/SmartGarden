package com.example.smartgarden.ui.trees;

import static android.content.ContentValues.TAG;

import android.os.Build;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import com.example.smartgarden.model.ActivityLogEntry;
import com.example.smartgarden.model.HarvestLog;
import com.example.smartgarden.model.Plant;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
public class PlantsViewModel extends ViewModel {
    private final MutableLiveData<List<Plant>> _allPlants = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Plant.PlantType> _typeFilter = new MutableLiveData<>(Plant.PlantType.TREE);
    private final MutableLiveData<String> _cultureFilter = new MutableLiveData<>(null);
    private final MutableLiveData<String> _varietyFilter = new MutableLiveData<>(null);
    private final MutableLiveData<Integer> _ageFilter = new MutableLiveData<>(null);
    private final MutableLiveData<List<String>> _cultureOptions = new MutableLiveData<>();
    public LiveData<List<String>> getCultureOptions() { return _cultureOptions; }
    private final MutableLiveData<List<String>> _varietyOptions = new MutableLiveData<>();
    public LiveData<List<String>> getVarietyOptions() { return _varietyOptions; }
    private final MutableLiveData<List<String>> _ageOptions = new MutableLiveData<>();
    public LiveData<List<String>> getAgeOptions() { return _ageOptions; }
    private final MediatorLiveData<List<Plant>> filteredPlantsResult = new MediatorLiveData<>();
    public LiveData<List<Plant>> getFilteredPlants() { return filteredPlantsResult; }
    public LiveData<String> getCurrentCultureFilter() { return _cultureFilter; }
    public LiveData<String> getCurrentVarietyFilter() { return _varietyFilter; }
    public LiveData<Integer> getCurrentAgeFilter() { return _ageFilter; }
    public LiveData<Plant.PlantType> getPlantTypeFilter() { return _typeFilter; }
    private final MutableLiveData<Plant> selectedPlant = new MutableLiveData<>();
    public LiveData<Plant> getSelectedPlant() { return selectedPlant; }
    private FirebaseFirestore db;
    private CollectionReference plantsRef;
    private CollectionReference harvestLogsRef;
    private CollectionReference activityLogRef;
    private ListenerRegistration plantsListenerRegistration;
    public PlantsViewModel() {
        db = FirebaseFirestore.getInstance();
        plantsRef = db.collection("plants");
        harvestLogsRef = db.collection("harvestLogs");
        activityLogRef = db.collection("activityLog");
        setupFilteredPlantsMediator();
        listenForPlantUpdates();
    }
    private void setupFilteredPlantsMediator() {
        filteredPlantsResult.setValue(new ArrayList<>());
        Observer<Object> observer = source -> performFiltering();
        filteredPlantsResult.addSource(_allPlants, observer);
        filteredPlantsResult.addSource(_typeFilter, observer);
        filteredPlantsResult.addSource(_cultureFilter, observer);
        filteredPlantsResult.addSource(_varietyFilter, observer);
        filteredPlantsResult.addSource(_ageFilter, observer);
    }
    private void listenForPlantUpdates() {
        if (plantsListenerRegistration != null) {
            plantsListenerRegistration.remove();
        }
        plantsListenerRegistration = plantsRef.addSnapshotListener((snapshots, error) -> {
            List<Plant> plants = new ArrayList<>();
            if (error != null) { /* handle */ }
            else if (snapshots != null) {
                snapshots.forEach(doc -> {
                    Plant plant = doc.toObject(Plant.class);
                    if (plant != null) {
                        plant.setId(doc.getId());
                        plants.add(plant);
                    }
                });
            }
            _allPlants.postValue(plants);
            recalculateFilterOptions();
        });
    }
    @Override
    protected void onCleared() {
        super.onCleared();
        if (plantsListenerRegistration != null) {
            plantsListenerRegistration.remove();
        }
    }
    private void recalculateFilterOptions() {
        List<Plant> all = _allPlants.getValue();
        Plant.PlantType currentType = _typeFilter.getValue();
        String currentCulture = _cultureFilter.getValue();
        if (all == null) { all = new ArrayList<>(); }
        List<Plant> typeFilteredPlants;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            typeFilteredPlants = all.stream().filter(p -> currentType == null || p.getType() == currentType).collect(Collectors.toList());
        } else {
            typeFilteredPlants = new ArrayList<>();
            for(Plant p : all) { if(currentType == null || p.getType() == currentType) typeFilteredPlants.add(p); }
        }
        Set<String> cultures = new HashSet<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { cultures = typeFilteredPlants.stream().map(Plant::getCulture).filter(Objects::nonNull).collect(Collectors.toSet()); }
        else { for(Plant p : typeFilteredPlants) { if(p.getCulture() != null) cultures.add(p.getCulture()); } }
        List<String> cultureList = new ArrayList<>(cultures);
        cultureList.sort(String::compareToIgnoreCase);
        cultureList.add(0, "Всі культури");
        _cultureOptions.postValue(cultureList);
        Set<String> varieties = new HashSet<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            varieties = typeFilteredPlants.stream().filter(p -> currentCulture == null || currentCulture.equals(p.getCulture())).map(Plant::getVariety).filter(Objects::nonNull).collect(Collectors.toSet());
        } else {
            for (Plant p : typeFilteredPlants) { if (currentCulture == null || currentCulture.equals(p.getCulture())) { if(p.getVariety() != null) varieties.add(p.getVariety()); } }
        }
        List<String> varietyList = new ArrayList<>(varieties);
        varietyList.sort(String::compareToIgnoreCase);
        varietyList.add(0, "Всі сорти");
        _varietyOptions.postValue(varietyList);
        Set<Integer> ages = new HashSet<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { ages = typeFilteredPlants.stream().map(Plant::getAge).filter(age -> age > 0).collect(Collectors.toSet()); }
        else { for(Plant p : typeFilteredPlants) { if(p.getAge() > 0) ages.add(p.getAge()); } }
        List<String> ageList = new ArrayList<>();
        ageList.add("Будь-який вік");
        List<Integer> sortedAges = new ArrayList<>(ages);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { sortedAges.sort(Integer::compareTo); }
        for(Integer ageVal : sortedAges) { ageList.add(String.valueOf(ageVal)); }
        _ageOptions.postValue(ageList);
    }
    public void setPlantTypeFilter(Plant.PlantType type) {
        if (!Objects.equals(_typeFilter.getValue(), type)) {
            _typeFilter.setValue(type);
            resetCultureVarietyAgeFilters();
        }
    }
    public void setCultureFilter(String culture) {
        String actualFilter = ("Всі культури".equals(culture)) ? null : culture;
        if (!Objects.equals(_cultureFilter.getValue(), actualFilter)) {
            _cultureFilter.setValue(actualFilter);
            _varietyFilter.setValue(null);
            recalculateFilterOptions();
        }
    }
    public void setVarietyFilter(String variety) {
        String actualFilter = ("Всі сорти".equals(variety)) ? null : variety;
        if (!Objects.equals(_varietyFilter.getValue(), actualFilter)) { _varietyFilter.setValue(actualFilter); }
    }
    public void setAgeFilter(String ageString) {
        Integer actualFilter = null;
        if (ageString != null && !ageString.equals("Будь-який вік")) { try { actualFilter = Integer.parseInt(ageString); } catch (NumberFormatException e) { /* ignore */ } }
        if (!Objects.equals(_ageFilter.getValue(), actualFilter)) { _ageFilter.setValue(actualFilter); }
    }
    private void performFiltering() {
        List<Plant> all = _allPlants.getValue();
        if (all == null) { filteredPlantsResult.setValue(new ArrayList<>()); return; }
        Plant.PlantType type = _typeFilter.getValue();
        String culture = _cultureFilter.getValue();
        String variety = _varietyFilter.getValue();
        Integer age = _ageFilter.getValue();
        List<Plant> filteredList;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            filteredList = all.stream()
                    .filter(plant -> type == null || plant.getType() == type)
                    .filter(plant -> culture == null || Objects.equals(culture, plant.getCulture()))
                    .filter(plant -> variety == null || Objects.equals(variety, plant.getVariety()))
                    .filter(plant -> age == null || plant.getAge() == age)
                    .collect(Collectors.toList());
        } else {
            filteredList = new ArrayList<>();
            for (Plant plant : all) {
                boolean typeMatch = (type == null || plant.getType() == type);
                boolean cultureMatch = (culture == null || Objects.equals(culture, plant.getCulture()));
                boolean varietyMatch = (variety == null || Objects.equals(variety, plant.getVariety()));
                boolean ageMatch = (age == null || plant.getAge() == age);
                if (typeMatch && cultureMatch && varietyMatch && ageMatch) { filteredList.add(plant); }
            }
        }
        filteredPlantsResult.setValue(filteredList);
    }
    public void loadPlantForEdit(String plantId) {
        if (plantId == null || plantId.isEmpty()) { selectedPlant.postValue(null); return; }
        plantsRef.document(plantId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Plant plant = documentSnapshot.toObject(Plant.class);
                        if (plant != null) { plant.setId(documentSnapshot.getId()); selectedPlant.postValue(plant); }
                        else { selectedPlant.postValue(null); }
                    } else { selectedPlant.postValue(null); }
                })
                .addOnFailureListener(e -> { selectedPlant.postValue(null); });
    }
    public void clearSelectedPlant() { selectedPlant.setValue(null); }
    public void addPlant(Plant plant) {
        plantsRef.add(plant)
                .addOnSuccessListener(documentReference -> {
                    String name = (plant.getCulture() != null ? plant.getCulture() : "") + " " + (plant.getVariety() != null ? plant.getVariety() : "");
                    logActivity(ActivityLogEntry.ActionType.ADDED, ActivityLogEntry.ObjectType.PLANT, name.trim(), documentReference.getId(), null);
                });
    }
    public void updatePlant(Plant plant) {
        if (plant.getId() == null || plant.getId().isEmpty()) return;
        plantsRef.document(plant.getId()).set(plant)
                .addOnSuccessListener(aVoid -> {
                    String name = (plant.getCulture() != null ? plant.getCulture() : "") + " " + (plant.getVariety() != null ? plant.getVariety() : "");
                    logActivity(ActivityLogEntry.ActionType.EDITED, ActivityLogEntry.ObjectType.PLANT, name.trim(), plant.getId(), null);
                });
    }
    public void deletePlant(Plant plant) {
        if (plant.getId() == null || plant.getId().isEmpty()) return;
        String name = (plant.getCulture() != null ? plant.getCulture() : "") + " " + (plant.getVariety() != null ? plant.getVariety() : "");
        String deletedId = plant.getId();
        plantsRef.document(plant.getId()).delete()
                .addOnSuccessListener(aVoid -> {
                    logActivity(ActivityLogEntry.ActionType.DELETED, ActivityLogEntry.ObjectType.PLANT, name.trim(), deletedId, null);
                });
    }
    public void resetFilters() {
        boolean changed = false;
        if (_cultureFilter.getValue() != null) { _cultureFilter.setValue(null); changed = true; }
        if (_varietyFilter.getValue() != null) { _varietyFilter.setValue(null); changed = true; }
        if (_ageFilter.getValue() != null) { _ageFilter.setValue(null); changed = true; }
        if(changed) { recalculateFilterOptions(); }
        else { recalculateFilterOptions(); }
    }
    public void addHarvestLog(HarvestLog logEntry) {
        harvestLogsRef.add(logEntry)
                .addOnSuccessListener(documentReference -> {
                    String details = String.format(Locale.getDefault(), "%.1f %s", logEntry.getQuantity(), logEntry.getUnit());
                    logActivity(ActivityLogEntry.ActionType.HARVEST_LOGGED, ActivityLogEntry.ObjectType.HARVEST, logEntry.getPlantInfo(), logEntry.getPlantId(), details);
                });
    }
    private void logActivity(ActivityLogEntry.ActionType actionType, ActivityLogEntry.ObjectType objectType, String objectName, String objectId, String details) {
        if (activityLogRef == null) return;
        ActivityLogEntry log = new ActivityLogEntry(actionType, objectType, objectName, objectId, details);
        activityLogRef.add(log).addOnFailureListener(e -> {
        });
    }
    private void resetCultureVarietyAgeFilters() {
        _cultureFilter.setValue(null);
        _varietyFilter.setValue(null);
        _ageFilter.setValue(null);
        recalculateFilterOptions();
    }
}