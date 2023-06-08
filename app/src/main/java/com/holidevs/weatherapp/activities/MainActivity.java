package com.holidevs.weatherapp.activities;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.holidevs.weatherapp.R;
import com.holidevs.weatherapp.adapters.AdapterDataRVDays;
import com.holidevs.weatherapp.Models.Days;
import com.holidevs.weatherapp.database.DatabaseManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdapterDataRVDays adapter;
    private ArrayList<Days> daysList;

    private Button btnPlansInYourCity;

    private AppCompatButton btnGoogleMaps;

    private TextView city;

    private ImageView weatherImg;

    private TextView currentTemp;

    private TextView maxTemp;

    private TextView minTemp;

    private TextView descriptionWeather;

    private long lastRequestTimeDatos = 0;

    private long lastRequestTimePronostico = 0;
    private static final long MINIMUM_REQUEST_INTERVAL = 60000;

   // final String openWeahterApiKey = "YOUR_OPENWEATHER_API_KEY";

    private LocationManager locationManager;

    private LocationListener locationListener;

    private double latitude;

    private double longitude;

    private DatabaseManager databaseManager;

    private View preLoaderView;

    private boolean isLocationEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initPreLoader();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
            if (isLocationEnabled) {
                if (latitude == 0 && longitude == 0) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    Log.i("onActivityResultCheck", "latitude On Location OK IF" + latitude);
                    isLocationEnabled = false;
                    // Detén las actualizaciones de ubicación
                    locationManager.removeUpdates(locationListener);
                }

            }

                obtenerDatosTiempo(latitude, longitude);
                obtenerPronosticoExtendido(latitude, longitude);
                Log.i("onActivityResultCheck", "latitude On Location KO IF" + latitude);


            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };

        // Verificar si ya tienes los permisos de ubicación
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Verificar si es la primera vez que se obtiene la ubicación
            if (isLocationEnabled) {
                // Realizar la geolocalización inicial
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        } else {
            // Solicitar permisos de ubicación
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }


        initItems();
        initRecyclerView();
        setOnclickListeners();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onActivityResultCheck", "OK");
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            double receivedLatitude = data.getDoubleExtra("latitude", 0);
            double receivedLongitude = data.getDoubleExtra("longitude", 0);
            initPreLoader();
            // Verificar si se recibieron las coordenadas extras
            if (receivedLatitude != 0 && receivedLongitude != 0) {
                // Usar las coordenadas extras en lugar de las obtenidas del GPS
                Log.i("onActivityResultCheck", "latitude" + latitude);
                latitude = receivedLatitude;
                longitude = receivedLongitude;
                lastRequestTimeDatos = 0;
                lastRequestTimePronostico = 0;
                obtenerDatosTiempo(latitude, longitude);
                obtenerPronosticoExtendido(latitude, longitude);
                Log.i("onActivityResultCheck", "latitude" + latitude);

            }
        }
    }

    private void initPreLoader() {
        preLoaderView = findViewById(R.id.pre_loader);
        preLoaderView.setVisibility(View.VISIBLE);
    }

    private void handleApiDataLoaded() {
        preLoaderView.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        // Cerrar la conexión a la base de datos antes de destruir la actividad
        super.onDestroy();
        if (databaseManager != null) {
            databaseManager.close();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
            }
        }
    }

    private void initItems() {
        recyclerView = findViewById(R.id.recyclerID);
        daysList = new ArrayList<>(); // Inicializa la lista

        city = findViewById(R.id.txt_city);
        currentTemp = findViewById(R.id.txt_temperature);
        maxTemp = findViewById(R.id.txt_maxTemp);
        minTemp = findViewById(R.id.txt_minTemp);
        descriptionWeather = findViewById(R.id.descriptionWeather);
        weatherImg = findViewById(R.id.weatherImg);
        btnPlansInYourCity = findViewById(R.id.btnPlansInYourCity);
        btnGoogleMaps = findViewById(R.id.btnGoogleMaps);

    }

    private void initRecyclerView() {
        // Configura el RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new AdapterDataRVDays(daysList);
        recyclerView.setAdapter(adapter);

        adapter.setDaysList(daysList); // Pasa la lista de días al adaptador
    }

    private void setOnclickListeners() {
        btnPlansInYourCity.setOnClickListener(v -> {

            String city2 = city.getText().toString();
            String descriptionWeather2 = descriptionWeather.getText().toString();

            Intent intent = new Intent(getApplicationContext(), PlansInYourCity.class);
            intent.putExtra("city", city2);
            intent.putExtra("descriptionWeather", descriptionWeather2);
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            startActivity(intent);
        });

        btnGoogleMaps.setOnClickListener(v ->{
            Intent intentGM = new Intent(getApplicationContext(), MapsActivity.class);
            intentGM.putExtra("latitude", latitude);
            intentGM.putExtra("longitude", longitude);
            //finish();
            startActivityForResult(intentGM, 1);
        });
    }

    private void obtenerDatosTiempo(double latitud, double longitud) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastRequestTimeDatos >= MINIMUM_REQUEST_INTERVAL) {
            String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitud + "&lon=" + longitud + "&appid=" + openWeahterApiKey;

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("response", response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                        JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);

                        JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                        JSONObject jsonObjectSys = jsonResponse.getJSONObject("sys");

                        int temp = (int) (jsonObjectMain.getDouble("temp") - 273.15);
                        int temp_min = (int) (jsonObjectMain.getDouble("temp_min") - 273.15);
                        int temp_max = (int) (jsonObjectMain.getDouble("temp_max") - 273.15);

                        String description = jsonObjectWeather.getString("main");
                        String countryName = jsonObjectSys.getString("country");
                        String cityName = jsonResponse.getString("name");
                        String icon = jsonObjectWeather.getString("icon");

                        runOnUiThread(() -> setCurrentWeather(temp, temp_max, temp_min, countryName, cityName, description, icon));

                        String setBtn = "Planes para hoy en " + cityName;
                        btnPlansInYourCity.setText(setBtn);
                        Log.i("onActivityResultCheck", "llamas a OpenApi OK" + latitude);

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }, error -> Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show());

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);

            // Actualizar el tiempo de la última solicitud
            lastRequestTimeDatos = currentTime;
        }
    }
    private void obtenerPronosticoExtendido(double latitud, double longitud) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastRequestTimePronostico >= MINIMUM_REQUEST_INTERVAL) {
                String url = "https://api.openweathermap.org/data/2.5/forecast?lat=" + latitud + "&lon=" + longitud + "&appid=" + openWeahterApiKey;

                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
                    Log.d("response", response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray jsonArray = jsonResponse.getJSONArray("list");

                        // Parsea los datos de los próximos 5 días
                        ArrayList<Days> nextFiveDays = parseNextFiveDays(jsonArray);

                        // Pasa los datos al adaptador del RecyclerView
                        runOnUiThread(() -> adapter.setDaysList(nextFiveDays));

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }, error -> Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show());

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);

                // Actualizar el tiempo de la última solicitud
                lastRequestTimePronostico = currentTime;
            }

        }

    private ArrayList<Days> parseNextFiveDays(JSONArray jsonArray) throws JSONException {
        ArrayList<Days> nextFiveDays = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        for (int i = 8; i < jsonArray.length(); i+=8) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            JSONArray weatherArray = jsonObject.getJSONArray("weather");
            JSONObject weatherObject = weatherArray.getJSONObject(0);
            long timestamp = jsonObject.getLong("dt") * 1000;
            calendar.setTimeInMillis(timestamp);
            
            int temp = (int) (jsonObject.getJSONObject("main").getDouble("temp") - 273.15);
            int min_temp = (int) (jsonObject.getJSONObject("main").getDouble("temp_min") - 273.15);
            int max_temp = (int) (jsonObject.getJSONObject("main").getDouble("temp_max") - 273.15);
            String date = jsonObject.getString("dt_txt");

            String icon = weatherObject.getString("icon");

            nextFiveDays.add(new Days(date,icon, temp, min_temp, max_temp));
        }
        handleApiDataLoaded();
        return nextFiveDays;

    }

    private void setCurrentWeather(int temp, int temp_max, int temp_min, String countryName, String cityName, String description, String icon) {

        String setCityCountry =  cityName + ", " + countryName;
        String tempString = String.valueOf(temp);
        String tempMaxStr = temp_max + "º";
        String tempMinStr = temp_min + "º";
        int codigoImagen = obtenerCodigoImagen(icon);

        city.setText(setCityCountry);
        currentTemp.setText(tempString);
        maxTemp.setText(tempMaxStr);
        minTemp.setText(tempMinStr);
        descriptionWeather.setText(traducirMain(description));
        weatherImg.setImageResource(codigoImagen);

    }

    public static String traducirMain(String main) {
        String traduccion = "";

        switch (main) {
            case "Clear":
                traduccion = "Despejado";
                break;
            case "Clouds":
                traduccion = "Nublado";
                break;
            case "Rain":
            case "Drizzle":
            case "Shower Rain":
                traduccion = "Lluvia";
                break;
            case "Snow":
                traduccion = "Nieve";
                break;
            case "Mist":
            case "Fog":
            case "Haze":
                traduccion = "Niebla";
                break;
            case "Dust":
            case "Sand":
            case "Ash":
                traduccion = "Polvo";
                break;
            case "Squall":
            case "Tornado":
                traduccion = "Tormenta";
                break;
            default:
                traduccion = "Desconocido";
                break;
        }
        return traduccion;
    }

    public static int obtenerCodigoImagen(String icon) {
        int codigoImagen = 0;

        // Mapear el código de imagen según el valor del campo "icon"
        switch (icon) {
            case "01d":
                codigoImagen = R.drawable.ic_clear_day;
                break;
            case "02d":
                codigoImagen = R.drawable.ic_few_clouds;
                break;
            case "03d":
                codigoImagen = R.drawable.ic_cloudy_weather;
                break;
            case "04d":
                codigoImagen = R.drawable.ic_cloudy_weather;
                break;
            case "09d":
                codigoImagen = R.drawable.ic_rainy_weather;
                break;
            case "10d":
                codigoImagen = R.drawable.ic_rainy_weather;
                break;
            case "11d":
                codigoImagen = R.drawable.ic_storm_weather;
                break;
            case "13d":
                codigoImagen = R.drawable.ic_snow_weather;
                break;
            default:
                //codigoImagen = R.drawable.ic_default_weather; // Imagen predeterminada para casos no disponibles
                break;
        }

        return codigoImagen;
    }

}