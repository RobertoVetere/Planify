package com.holidevs.weatherapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.holidevs.weatherapp.Models.Plans;
import com.holidevs.weatherapp.R;
import com.holidevs.weatherapp.adapters.AdapterDataPlans;
import com.holidevs.weatherapp.database.DatabaseManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlansInYourCity extends AppCompatActivity {

    private String OPENAI_API_KEY = "YOUR_OPENAI_API_KEY";

    private static final String URL = "https://api.openai.com/v1/chat/completions";

    private TextView textChatGptResponse;

    private TextView txtTitlePlans;

    private RequestQueue requestQueue;

    private DatabaseManager databaseManager;

    private String city;

    private Handler messageHandler;

    private AppCompatButton btnOtroPlan;
    private Runnable messageRunnable;

    private AppCompatButton btnSavePlan;

    private FrameLayout map_container;

    private SupportMapFragment mapFragment;

    private String descriptionWeather;

    private static final int DELAY_MILLIS = 30;

    private LatLng location;

    private GoogleMap mMap;
    private OnMapReadyCallback mapReadyCallback;

    private AdapterDataPlans adapterDataPlans;

    private ArrayList<Plans> planList;

    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plans_in_your_city);

        databaseManager = new DatabaseManager(this);
        databaseManager.open();

        planList = (ArrayList<Plans>) databaseManager.getAllPlans();

        requestQueue = Volley.newRequestQueue(this);

        initComponents();

        setGoogleMaps();

        setListeners();

        Intent intent = getIntent();
        if (intent != null) {
            city = intent.getStringExtra("city");
            descriptionWeather = intent.getStringExtra("descriptionWeather");
            double latitude = intent.getDoubleExtra("latitude", 0.0);
            double longitude = intent.getDoubleExtra("longitude", 0.0);
            location = new LatLng(latitude, longitude);

            Log.i("Newcity", city);

            if (city != null && descriptionWeather != null) {
                String textCity = "Que hacer hoy en " + city;
                showTextLetterByLetter(textCity, txtTitlePlans);
                makeAPIRequest(city, descriptionWeather);
            }
        }

        initRecyclerView();

    }
    private void initComponents() {
        textChatGptResponse = findViewById(R.id.textChatGptResponse);
        txtTitlePlans = findViewById(R.id.txtTitlePlans);
        btnSavePlan = findViewById(R.id.btnSavePlan);
        map_container = findViewById(R.id.map);
        btnOtroPlan = findViewById(R.id.btnOtroPlan);
        adapterDataPlans = new AdapterDataPlans(planList);
        recyclerView = findViewById(R.id.recyclerID_plans);
    }
    private void setGoogleMaps() {

        ////maps

        mapReadyCallback = googleMap -> {
            mMap = googleMap;
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(false);
            //mMap.getUiSettings().setZoomGesturesEnabled(true);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            float zoomLevel = 12.0f;

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(location)
                    .zoom(zoomLevel)
                    .build();

           // mMap.setOnMapClickListener(this);


            mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        };

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_container);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.map_container, mapFragment)
                    .commit();
        }

        mapFragment.getMapAsync(mapReadyCallback);

        ///end maps
    }
    private void setListeners() {
        btnSavePlan.setOnClickListener(v -> {

            createNewPlan(textChatGptResponse.getText().toString());
            markPlanOnMap(location, city);
            btnSavePlan.setVisibility(android.view.View.GONE);
            btnOtroPlan.setVisibility(android.view.View.VISIBLE);
        });

        btnOtroPlan.setOnClickListener(v -> {
            makeAPIRequest(city, descriptionWeather);
            btnOtroPlan.setVisibility(View.GONE);
            btnSavePlan.setVisibility(android.view.View.VISIBLE);
        });
    }
    private void makeAPIRequest(String city, String descriptionWeather) {
        JSONArray messages = new JSONArray();
        JSONObject message1 = new JSONObject();
        try {
            message1.put("role", "user");
            message1.put("content", "Hola! necesito tu ayuda, dame un plan para hacer en un día " + descriptionWeather + " en " + city + "(respuesta muy corta por favor)");
            messages.put(message1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "gpt-3.5-turbo");
            jsonBody.put("messages", messages);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, jsonBody,
                response -> {
                    Log.i("Pruebas GPT", "Message OK" + jsonBody);
                    Log.i("Pruebas GPT", "Before try block");
                    try {
                        //Log.i("Pruebas GPT", "Inside try block");
                        JSONArray choices = response.getJSONArray("choices");
                        JSONObject completion = choices.getJSONObject(0);
                        String generatedText = completion.getJSONObject("message").getString("content");


                        //Log.d("Pruebas GPT response", "On response OK ");
                        //Toast.makeText(this, "Respuesta: " + generatedText, Toast.LENGTH_SHORT).show();
                        //Log.d("Pruebas GPT generatedText", "Respuesta: " + generatedText);
                        //Toast.makeText(getApplicationContext(), "Respuesta: " + generatedText, Toast.LENGTH_SHORT).show();

                        //textChatGptResponse.setText(generatedText);
                        //txtTitlePlans.setText(generatedText);
                        showChatResponse(response);

                        //Log.d("makeApiRequest", "Tienes Api Key: " + API_KEY);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(this, "Error al hacer la llamada a la API", Toast.LENGTH_SHORT).show();
                    Log.d("makeApiRequest", "No tienes Api Key: " + OPENAI_API_KEY);
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + OPENAI_API_KEY);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        requestQueue.add(request);
    }
    public void showChatResponse(JSONObject response) {

        try {
            JSONArray choices = response.getJSONArray("choices");
            JSONObject completion = choices.getJSONObject(0);
            String generatedText = completion.getJSONObject("message").getString("content");
            String smileyEmoji = "\uD83D\uDE0A";

            String responseToShow = generatedText + " " + smileyEmoji;

            showTextLetterByLetter(responseToShow, textChatGptResponse);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void showTextLetterByLetter(String text, TextView textView) {
        // Cancelar el mensaje anterior si hay uno en curso
        if (messageHandler != null && messageRunnable != null) {
            messageHandler.removeCallbacks(messageRunnable);
        }

        textView.setText(""); // Limpiar el texto actual del TextView

        // Mostrar los puntos suspensivos al comienzo
        final StringBuilder stringBuilder = new StringBuilder("");


        // Mostrar el mensaje letra a letra después de los puntos suspensivos
        messageHandler = new Handler();
        messageRunnable = new Runnable() {
            int index = 0;

            @Override
            public void run() {
                if (index < text.length()) {
                    stringBuilder.append(text.charAt(index++));
                    textView.setText(stringBuilder.toString());
                    messageHandler.postDelayed(this, DELAY_MILLIS);
                }
            }
        };

        // Iniciar la tarea de mostrar el mensaje
        messageHandler.postDelayed(messageRunnable, DELAY_MILLIS * 4); // Retraso de 4 veces el DELAY_MILLIS para los puntos suspensivos
    }
    private void createNewPlan(String textChatGptResponse) {

        if (databaseManager != null && !databaseManager.isDatabaseOpen()) {
            databaseManager.open();
        }

        // Crear un nuevo objeto de tipo Plans con el plan recibido
        Plans plan = new Plans(textChatGptResponse);

        // Insertar el plan en la base de datos
        long newPlanId = databaseManager.insertPlan(plan);

        if (newPlanId != -1) {

            // Obtener el plan insertado desde la base de datos usando el ID
            Plans insertedPlan = databaseManager.getPlanById((int) newPlanId);
            Log.d("v", "Plan insertado: " + insertedPlan);

            // Obtener todos los planes de la base de datos
            List<Plans> allPlans = databaseManager.getAllPlans();

            for (Plans savedPlan : allPlans) {
                Log.d("PlansActivityNewPlan", "Plan ID: " + savedPlan.getId() + ", Plan Name: " + savedPlan.getPlan());
            }

            Toast.makeText(this, "Plan guardado con éxito", Toast.LENGTH_SHORT).show();
        } else {
            // Si la inserción falla, mostrar un mensaje de error
            Log.d("PlansActivityNewPlan", "Error al insertar el plan en la base de datos");
            Toast.makeText(this, "Error al guardar el plan", Toast.LENGTH_SHORT).show();
        }

        adapterDataPlans.setPlansList(databaseManager.getAllPlans());
    }
    public void markPlanOnMap(LatLng location, String planTitle) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(location)
                .title(textChatGptResponse.getText().toString());

        mMap.addMarker(markerOptions);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Cerrar la base de datos al finalizar la actividad
        databaseManager.close();
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapterDataPlans = new AdapterDataPlans(planList);
        recyclerView.setAdapter(adapterDataPlans);

        adapterDataPlans.setPlansList(planList); // Pasa la lista de días al adaptador
    }

}

