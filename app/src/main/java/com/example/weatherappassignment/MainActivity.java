package com.example.weatherappassignment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    // --- YOUR API KEY IS SET HERE ---
    String API_KEY = "bbf8afe696e44fda9e0201806253011";
    // --------------------------------

    EditText etCity;
    Button btnSearch;
    ProgressBar progressBar;
    LinearLayout weatherLayout;
    TextView tvCityName, tvTemperature, tvCondition, tvHumidity, tvWind;
    ImageView imgWeatherIcon;
    TextView tvForecast1, tvForecast2, tvForecast3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Initialize UI Elements
        etCity = findViewById(R.id.etCity);
        btnSearch = findViewById(R.id.btnSearch);
        progressBar = findViewById(R.id.progressBar);
        weatherLayout = findViewById(R.id.weatherLayout);
        tvCityName = findViewById(R.id.tvCityName);
        tvTemperature = findViewById(R.id.tvTemperature);
        tvCondition = findViewById(R.id.tvCondition);
        tvHumidity = findViewById(R.id.tvHumidity);
        tvWind = findViewById(R.id.tvWind);
        imgWeatherIcon = findViewById(R.id.imgWeatherIcon);
        tvForecast1 = findViewById(R.id.tvForecast1);
        tvForecast2 = findViewById(R.id.tvForecast2);
        tvForecast3 = findViewById(R.id.tvForecast3);

        // 2. Check for last searched city in Storage (Requirement)
        SharedPreferences prefs = getSharedPreferences("WeatherApp", MODE_PRIVATE);
        String lastCity = prefs.getString("lastCity", "");
        if (!lastCity.isEmpty()) {
            getWeatherData(lastCity);
            etCity.setText(lastCity);
        }

        // 3. Set Search Button Click Listener
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = etCity.getText().toString().trim();
                if (!city.isEmpty()) {
                    getWeatherData(city);
                    // Hide keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a city", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getWeatherData(String city) {
        // Show loading
        progressBar.setVisibility(View.VISIBLE);
        weatherLayout.setVisibility(View.GONE);

        // Construct the API URL
        String url = "https://api.weatherapi.com/v1/forecast.json?key=" + API_KEY + "&q=" + city + "&days=3&aqi=no&alerts=no";

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.GONE);
                        weatherLayout.setVisibility(View.VISIBLE);
                        try {
                            // --- PARSE CURRENT WEATHER ---
                            String name = response.getJSONObject("location").getString("name");
                            JSONObject current = response.getJSONObject("current");
                            String temp = current.getString("temp_c");
                            String humidity = current.getString("humidity");
                            String wind = current.getString("wind_kph");
                            String condition = current.getJSONObject("condition").getString("text");
                            String iconUrl = "https:" + current.getJSONObject("condition").getString("icon");

                            tvCityName.setText(name);
                            tvTemperature.setText(temp + "°C");
                            tvCondition.setText(condition);
                            tvHumidity.setText("Humidity: " + humidity + "%");
                            tvWind.setText("Wind: " + wind + " km/h");

                            // Load image using Picasso library
                            Picasso.get().load(iconUrl).into(imgWeatherIcon);

                            // --- PARSE 3-DAY FORECAST ---
                            JSONArray forecastDays = response.getJSONObject("forecast").getJSONArray("forecastday");

                            // Helper function to format forecast string
                            updateForecastView(tvForecast1, forecastDays.getJSONObject(0)); // Today
                            updateForecastView(tvForecast2, forecastDays.getJSONObject(1)); // Tomorrow
                            updateForecastView(tvForecast3, forecastDays.getJSONObject(2)); // Day after

                            // --- SAVE CITY LOCALLY ---
                            SharedPreferences prefs = getSharedPreferences("WeatherApp", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("lastCity", city);
                            editor.apply();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "City not found or No Internet", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(jsonObjectRequest);
    }

    private void updateForecastView(TextView view, JSONObject dayObj) throws JSONException {
        String date = dayObj.getString("date");
        JSONObject dayData = dayObj.getJSONObject("day");
        String maxTemp = dayData.getString("maxtemp_c");
        String minTemp = dayData.getString("mintemp_c");
        String desc = dayData.getJSONObject("condition").getString("text");

        view.setText(date + "\n" + desc + " | Max: " + maxTemp + "°C / Min: " + minTemp + "°C");
    }
}