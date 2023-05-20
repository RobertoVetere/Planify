package com.holidevs.weatherapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.holidevs.weatherapp.R;
import com.holidevs.weatherapp.adapters.AdapterDataRVDays;
import com.holidevs.weatherapp.Days.Days;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdapterDataRVDays adapter;
    private ArrayList<Days> daysList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar la lista de días (datos de ejemplo)
        daysList = new ArrayList<>();
        daysList.add(new Days("Lunes", null, "20º", "25º", "12º"));
        daysList.add(new Days("Martes", null, "20º", "25º", "12º"));
        daysList.add(new Days("Miercoles", null, "20º", "25º", "12º"));
        daysList.add(new Days("Jueves", null, "20º", "25º", "12º"));
        daysList.add(new Days("Viernes", null, "20º", "25º", "12º"));
        daysList.add(new Days("Sabado", null, "20º", "25º", "12º"));
        // Agrega más elementos a daysList según sea necesario

        // Configurar el RecyclerView
        recyclerView = findViewById(R.id.recyclerID);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));
        adapter = new AdapterDataRVDays(daysList);
        recyclerView.setAdapter(adapter);
    }
}