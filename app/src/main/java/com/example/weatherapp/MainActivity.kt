package com.example.weatherapp

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapp.databinding.ActivityMainBinding
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val apiKey = "8c5b2f1c326e3f885da2f9d70e9b3e1a" // Replace with your API key

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        setupSearchFunctionality()
    }

    private fun setupSearchFunctionality() {
        binding.searchButton.setOnClickListener {
            searchWeather()
        }

        binding.citySearchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchWeather()
                true
            } else {
                false
            }
        }
    }

    private fun searchWeather() {
        val city = binding.citySearchEditText.text.toString().trim()
        if (city.isEmpty()) {
            Toast.makeText(this, "Please enter a city name", Toast.LENGTH_SHORT).show()
            return
        }

        // Hide keyboard
        val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.hideSoftInputFromWindow(binding.citySearchEditText.windowToken, 0)

        fetchWeatherData(city)
    }

    private fun fetchWeatherData(city: String) {
        binding.progressBar.visibility = android.view.View.VISIBLE
        binding.weatherCard.visibility = android.view.View.GONE
        binding.errorTextView.visibility = android.view.View.GONE

        val url = "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$apiKey&units=metric"

        val queue = Volley.newRequestQueue(this)
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                parseWeatherData(response)
                binding.progressBar.visibility = android.view.View.GONE
                binding.weatherCard.visibility = android.view.View.VISIBLE
            },
            { error ->
                binding.progressBar.visibility = android.view.View.GONE
                binding.errorTextView.visibility = android.view.View.VISIBLE
                binding.errorTextView.text = "Error: ${error.message ?: "City not found"}"
            }
        )

        queue.add(jsonObjectRequest)
    }

    private fun parseWeatherData(response: JSONObject) {
        try {
            // Parse main weather data
            val main = response.getJSONObject("main")
            val temperature = main.getDouble("temp")
            val humidity = main.getInt("humidity")
            val pressure = main.getInt("pressure")

            // Parse weather description
            val weatherArray = response.getJSONArray("weather")
            val weatherObject = weatherArray.getJSONObject(0)
            val description = weatherObject.getString("description")
            val icon = weatherObject.getString("icon")

            // Parse wind data
            val wind = response.getJSONObject("wind")
            val windSpeed = wind.getDouble("speed")

            // Parse city name
            val cityName = response.getString("name")

            // Parse country
            val sys = response.getJSONObject("sys")
            val country = sys.getString("country")

            // Update UI
            binding.cityNameTextView.text = "$cityName, $country"
            binding.temperatureTextView.text = "${temperature.toInt()}°C"
            binding.weatherDescriptionTextView.text = description.capitalize()
            binding.humidityTextView.text = "$humidity%"
            binding.pressureTextView.text = "${pressure}hPa"
            binding.windSpeedTextView.text = "${windSpeed}m/s"

            // Load weather icon (you would need to implement this)
            loadWeatherIcon(icon)

        } catch (e: Exception) {
            binding.errorTextView.visibility = android.view.View.VISIBLE
            binding.errorTextView.text = "Error parsing weather data"
        }
    }

    private fun loadWeatherIcon(iconCode: String) {
        // You can implement this to load weather icons
        // For now, we'll just log the icon code
        println("Weather icon code: $iconCode")
        // Typically you would use Glide or Picasso to load an icon from:
        // "https://openweathermap.org/img/wn/${iconCode}@2x.png"
    }
}