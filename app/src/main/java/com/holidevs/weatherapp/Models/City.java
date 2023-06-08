package com.holidevs.weatherapp.Models;

import java.util.ArrayList;
import java.util.List;

public class City {

    private final long id;

    private final String cityName;

    private final double latitude;

    private final double longitude;

    private List<Plans> plansList;

    public City(long id, String cityName, double latitude, double longitude) {
        this.id = id;
        this.cityName = cityName;
        this.latitude = latitude;
        this.longitude =longitude;
        this.plansList = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public String cityName() {
        return cityName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void addPlanToList(Plans plan) {
        plansList.add(plan);
    }

    public List<Plans> getPlansList() {
        return plansList;
    }

    @Override
    public String toString() {
        return "City{" +
                "id=" + id +
                ", cityName='" + cityName + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", plansList=" + plansList +
                '}';
    }
}
