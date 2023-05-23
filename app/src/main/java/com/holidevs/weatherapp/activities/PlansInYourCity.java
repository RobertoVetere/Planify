package com.holidevs.weatherapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.holidevs.weatherapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PlansInYourCity extends AppCompatActivity {

    private String apiKey = "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";

    private static final String URL = "https://api.openai.com/v1/chat/completions";

    private TextView textChatGptResponse;

    private RequestQueue requestQueue;

    private Handler messageHandler;
    private Runnable messageRunnable;

    private static final int DELAY_MILLIS = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plans_in_your_city);

        initComponents();
    }

    private void initComponents() {
        textChatGptResponse = findViewById(R.id.textChatGptResponse);
        requestQueue = Volley.newRequestQueue(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            String city = data.getStringExtra("city");
            String descriptionWeather = data.getStringExtra("descriptionWeather");

            makeAPIRequest(city, descriptionWeather);
            Log.i("onplans", "on plans");
        }
    }


    public void makeAPIRequest(String city, String descriptionWeather) {
        Log.i("onplans", "makeapiOK");
        Log.i("Pruebas GPT", "On api Request OK ");
        JSONArray messages = new JSONArray();
        JSONObject message1 = new JSONObject();

        try {
            message1.put("role", "user");
            message1.put("content",  "Dame tres planes para un día " + descriptionWeather + " en " + city + ": uno secreto, uno social y uno tranquilo (con nombres y direcciones, si es posible).");
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
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray choices = response.getJSONArray("choices");
                            JSONObject completion = choices.getJSONObject(0);
                            String generatedText = completion.getJSONObject("message").getString("content");

                            Log.i("Pruebas GPT", "On response OK ");
                            Toast.makeText(PlansInYourCity.this, "Respuesta: " + generatedText, Toast.LENGTH_SHORT).show();
                            Log.i("Pruebas GPT", "Respuesta: " + generatedText);

                            showChatResponse(response);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                error -> Toast.makeText(PlansInYourCity.this, "Error al hacer la llamada a la API", Toast.LENGTH_SHORT).show()) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + apiKey);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(request);
    }

    public void showChatResponse(JSONObject response) {
        // Cancelar el mensaje anterior si hay uno en curso
        if (messageHandler != null && messageRunnable != null) {
            messageHandler.removeCallbacks(messageRunnable);
        }

        textChatGptResponse.setText(""); // Limpiar el texto actual del TextView

        try {
            JSONArray choices = response.getJSONArray("choices");
            JSONObject completion = choices.getJSONObject(0);
            String generatedText = completion.getJSONObject("message").getString("content");

            Log.i("Pruebas GPT", "On response OK ");
            Toast.makeText(PlansInYourCity.this, "Respuesta: " + generatedText, Toast.LENGTH_SHORT).show();
            Log.i("Pruebas GPT", "Respuesta: " + generatedText);

            // Mostrar el mensaje generado en el TextView
            textChatGptResponse.setText(generatedText);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}