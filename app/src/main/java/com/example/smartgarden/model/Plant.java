package com.example.smartgarden.model;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;
import java.util.Objects;

public class Plant {

    @Exclude
    private String id;
    private PlantType type;
    private String culture;
    private String variety;
    private int age;
    private String gardenRow;
    private String status;
    private String imageUrl;
    private int quantity = 1;

    public enum PlantType {
        TREE, BERRY
    }

    public Plant() { }

    public Plant(String id, PlantType type, String culture, String variety, int age, String gardenRow, String status, String imageUrl, int quantity) {
        this.id = id;
        this.type = type;
        this.culture = culture;
        this.variety = variety;
        this.age = age;
        this.gardenRow = gardenRow;
        this.status = status;
        this.imageUrl = imageUrl;
        this.quantity = Math.max(1, quantity);
    }

    @Exclude
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public PlantType getType() { return type; }
    public void setType(PlantType type) { this.type = type; }

    public String getCulture() { return culture; }
    public void setCulture(String culture) { this.culture = culture; }

    public String getVariety() { return variety; }
    public void setVariety(String variety) { this.variety = variety; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getGardenRow() { return gardenRow; }
    public void setGardenRow(String gardenRow) { this.gardenRow = gardenRow; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }


    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = Math.max(1, quantity); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Plant plant = (Plant) o;
        return Objects.equals(id, plant.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Plant{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", culture='" + culture + '\'' +
                ", variety='" + variety + '\'' +
                ", age=" + age +
                ", quantity=" + quantity +
                '}';
    }
}