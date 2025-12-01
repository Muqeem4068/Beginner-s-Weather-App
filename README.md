# Weather App Assignment

**Mobile Computing - CLO 3**

## Description
This is a native Android weather application built with Java. It fetches real-time weather data and a 3-day forecast using the free WeatherAPI.

## Features
- Search for weather by City Name.
- Displays current temperature, condition, humidity, and wind speed.
- Shows a 3-day forecast (Today, Tomorrow, Day After).
- Handles errors (e.g., City not found, No Internet).
- **Offline Storage:** Remembers the last searched city.

## Setup Instructions
1. Clone this repository.
2. Open the project in Android Studio.
3. **IMPORTANT:** You must enter your own API Key.
   - Go to `MainActivity.java`
   - Find the line: `String API_KEY = "YOUR_API_KEY_HERE";`
   - Replace it with your valid key from [WeatherAPI.com](https://www.weatherapi.com).
4. Run the app on an Emulator or Physical Device.

## APIs Used
- [WeatherAPI.com](https://www.weatherapi.com) (Free Tier)
