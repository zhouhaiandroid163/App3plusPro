package com.zjw.apps3pluspro.module.device.weather.openweather;

import java.util.ArrayList;

/**
 * Created by android
 * on 2021/5/31
 */
public class CurrentWeather {
    public WeatherCoord coord;
    public ArrayList<WeatherItem> weather;
    public String base;
    public WeatherMain main;
    public String visibility;
    public WeatherWind wind;
    public WeatherRain rain;
    public WeatherClouds clouds;
    public String dt;
    public String timezone;
    public String id;
    public String name;
    public String cod;
/*
* {
    "coord": {
        "lon": 114.0859,
        "lat": 22.547
    },
    "weather": [
        {
            "id": 501,
            "main": "Rain",
            "description": "moderate rain",
            "icon": "10d"
        }
    ],
    "base": "stations",
    "main": {
        "temp": 29.99,
        "feels_like": 41.01,
        "temp_min": 28.76,
        "temp_max": 30.99,
        "pressure": 1005,
        "humidity": 91,
        "sea_level": 1005,
        "grnd_level": 1003
    },
    "visibility": 10000,
    "wind": {
        "speed": 4.59,
        "deg": 199,
        "gust": 6.29
    },
    "rain": {
        "1h": 2.05
    },
    "clouds": {
        "all": 100
    },
    "dt": 1622425228,
    "sys": {
        "type": 2,
        "id": 2031340,
        "country": "CN",
        "sunrise": 1622410747,
        "sunset": 1622459031
    },
    "timezone": 28800,
    "id": 1795565,
    "name": "Shenzhen",
    "cod": 200
}
* */

}
