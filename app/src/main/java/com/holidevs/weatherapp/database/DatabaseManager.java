package com.holidevs.weatherapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.holidevs.weatherapp.Models.Plans;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private Context context;
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public DatabaseManager(Context context) {
        this.context = context;
    }

    public void open() {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insertPlan(Plans plan) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.PlanEntry.COLUMN_PLAN, plan.getPlan());
        return database.insert(DatabaseContract.PlanEntry.TABLE_NAME, null, values);
    }

    public Plans getPlanById(int id) {
        String[] projection = {
                DatabaseContract.PlanEntry.COLUMN_ID,
                DatabaseContract.PlanEntry.COLUMN_PLAN
        };

        String selection = DatabaseContract.PlanEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = database.query(
                DatabaseContract.PlanEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            int planId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.PlanEntry.COLUMN_ID));
            String planText = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.PlanEntry.COLUMN_PLAN));
            cursor.close();
            return new Plans(planId, planText);
        }

        return null;
    }

    public List<Plans> getAllPlans() {
        List<Plans> planList = new ArrayList<>();

        String[] projection = {
                DatabaseContract.PlanEntry.COLUMN_ID,
                DatabaseContract.PlanEntry.COLUMN_PLAN
        };

        Cursor cursor = database.query(
                DatabaseContract.PlanEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int planId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.PlanEntry.COLUMN_ID));
                String planText = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.PlanEntry.COLUMN_PLAN));
                Plans plan = new Plans(planId, planText);
                planList.add(plan);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return planList;
    }

    public int updatePlan(Plans plan) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.PlanEntry.COLUMN_PLAN, plan.getPlan());

        String selection = DatabaseContract.PlanEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(plan.getId())};

        return database.update(
                DatabaseContract.PlanEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
    }

    public int deletePlan(Plans plan) {
        String selection = DatabaseContract.PlanEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(plan.getId())};

        return database.delete(
                DatabaseContract.PlanEntry.TABLE_NAME,
                selection,
                selectionArgs
        );
    }

    public boolean isDatabaseOpen() {
        return database != null && database.isOpen();
    }

}