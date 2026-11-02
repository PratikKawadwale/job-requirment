package com.recruitment.model;

public class Location {
    private Integer locationId;
    private String cityName;
    private String stateName;
    private String countryName;
    private boolean active;

    public Location() {
        this.countryName = "India";
        this.active = true;
    }

    public Location(Integer locationId, String cityName, String stateName, String countryName, boolean active) {
        this.locationId = locationId;
        this.cityName = cityName;
        this.stateName = stateName;
        this.countryName = countryName;
        this.active = active;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s, %s, %s", locationId, cityName, stateName, countryName);
    }
}
