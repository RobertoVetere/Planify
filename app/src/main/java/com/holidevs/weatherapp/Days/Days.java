package com.holidevs.weatherapp.Days;

import android.widget.ImageView;

public class Days {

    private String day;

    private ImageView icon;

    private String currentTemp;

    private String maxTemp;

    private String minTemp;

    public Days(String day, ImageView icon, String currentTemp, String maxTemp, String minTemp) {
        this.day = day;
        this.icon = icon;
        this.currentTemp = currentTemp;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
    }

    @Override
    public String toString() {
        return "Days{" +
                "day='" + day + '\'' +
                ", icon=" + icon +
                ", currentTemp='" + currentTemp + '\'' +
                ", maxTemp='" + maxTemp + '\'' +
                ", minTemp='" + minTemp + '\'' +
                '}';
    }

    public String getDay() {
        return day;
    }

    public ImageView getIcon() {
        return icon;
    }

    public String getCurrentTemp() {
        return currentTemp;
    }

    public String getMaxTemp() {
        return maxTemp;
    }

    public String getMinTemp() {
        return minTemp;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setIcon(ImageView icon) {
        this.icon = icon;
    }

    public void setCurrentTemp(String currentTemp) {
        this.currentTemp = currentTemp;
    }

    public void setMaxTemp(String maxTemp) {
        this.maxTemp = maxTemp;
    }

    public void setMinTemp(String minTemp) {
        this.minTemp = minTemp;
    }
}
