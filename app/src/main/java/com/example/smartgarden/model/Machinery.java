package com.example.smartgarden.model;

public class Machinery {
    private String id;
    private String name;
    private String imageUrl;
    private String workingStatus;
    private String technicalCondition;

    public Machinery() {
    }

    public Machinery(String id, String name, String imageUrl, String workingStatus, String technicalCondition) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.workingStatus = workingStatus;
        this.technicalCondition = technicalCondition;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
    public String getWorkingStatus() { return workingStatus; }
    public String getTechnicalCondition() { return technicalCondition; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setWorkingStatus(String workingStatus) { this.workingStatus = workingStatus; }
    public void setTechnicalCondition(String technicalCondition) { this.technicalCondition = technicalCondition; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Machinery machinery = (Machinery) o;
        return java.util.Objects.equals(id, machinery.id);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id);
    }
}