package com.holidevs.weatherapp.Models;

public class Days {

    private String day;

    private String icon;

    private int currentTemp;

    private int maxTemp;

    private int minTemp;


    public Days(String day, String icon, int currentTemp, int maxTemp, int minTemp) {
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
                //", icon=" + icon +
                ", currentTemp='" + currentTemp + '\'' +
                ", maxTemp='" + maxTemp + '\'' +
                ", minTemp='" + minTemp + '\'' +
                '}';
    }

    public String getDay() {
        return day;
    }


    public String getIcon() {
        return icon;
    }

    public int getCurrentTemp() {
        return currentTemp;
    }

    public int getMaxTemp() {
        return maxTemp;
    }

    public int getMinTemp() {
        return minTemp;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
    public void setCurrentTemp(int currentTemp) {
        this.currentTemp = currentTemp;
    }

    public void setMaxTemp(int maxTemp) {
        this.maxTemp = maxTemp;
    }

    public void setMinTemp(int minTemp) {
        this.minTemp = minTemp;
    }


}
