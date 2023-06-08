package com.holidevs.weatherapp.Models;

public class Plans {

    private Integer id;
    private String plan;

    public Plans(Integer id, String plan) {
        this.id = id;
        this.plan = plan;
    }

    public Plans(String plan) {
        this.plan = plan;
    }

    public Integer getId() {
        return id;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    @Override
    public String toString() {
        return "Plans{" +
                "id=" + id +
                ", plan='" + plan + '\'' +
                '}';
    }
}
