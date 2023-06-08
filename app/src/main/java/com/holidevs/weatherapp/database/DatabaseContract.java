package com.holidevs.weatherapp.database;


import android.provider.BaseColumns;

public final class DatabaseContract {
    private DatabaseContract() {}

    public static class PlanEntry implements BaseColumns {
        public static final String TABLE_NAME = "plans";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_PLAN = "plan"; // Asegúrate de que esta línea esté presente
    }
}