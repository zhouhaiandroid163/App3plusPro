package com.zjw.apps3pluspro.module.device.weather.openweather;

import java.util.ArrayList;

/**
 * Created by android
 * on 2021/6/1
 */
public class WeatherFind {
    public String message;
    public String cod;
    public String count;
    public ArrayList<WeatherFindItem> list;

    public class WeatherFindItem {
        public String id;
        public String name;
        public WeatherCoord coord;
        public WeatherMain main;
        public String dt;
        public WeatherWind wind;
        public WeatherSys sys;
        public WeatherRain rain;
        public WeatherClouds clouds;
        public ArrayList<WeatherItem> weather;
    }

    /*
    * {
    "message": "accurate",
    "cod": "200",
    "count": 3,
    "list": [
        {
            "id": 1795565,
            "name": "Shenzhen",
            "coord": {
                "lat": 22.5455,
                "lon": 114.0683
            },
            "main": {
                "temp": 25.42,
                "feels_like": 26.47,
                "temp_min": 22.78,
                "temp_max": 26.67,
                "pressure": 1006,
                "humidity": 94
            },
            "dt": 1622506157,
            "wind": {
                "speed": 0.89,
                "deg": 115
            },
            "sys": {
                "country": "CN"
            },
            "rain": {
                "1h": 1.33
            },
            "snow": null,
            "clouds": {
                "all": 100
            },
            "weather": [
                {
                    "id": 501,
                    "main": "Rain",
                    "description": "moderate rain",
                    "icon": "10d"
                }
            ]
        },
        {
            "id": 1795563,
            "name": "Shenzhen",
            "coord": {
                "lat": 22.6177,
                "lon": 114.1259
            },
            "main": {
                "temp": 25.41,
                "feels_like": 26.46,
                "temp_min": 22.78,
                "temp_max": 26.67,
                "pressure": 1006,
                "humidity": 94
            },
            "dt": 1622505945,
            "wind": {
                "speed": 0.89,
                "deg": 115
            },
            "sys": {
                "country": "CN"
            },
            "rain": {
                "1h": 0.49
            },
            "snow": null,
            "clouds": {
                "all": 100
            },
            "weather": [
                {
                    "id": 500,
                    "main": "Rain",
                    "description": "light rain",
                    "icon": "10d"
                }
            ]
        },
        {
            "id": 1795564,
            "name": "Shenzhen",
            "coord": {
                "lat": 29.4159,
                "lon": 121.3397
            },
            "main": {
                "temp": 23.26,
                "feels_like": 23.81,
                "temp_min": 22.22,
                "temp_max": 24,
                "pressure": 1009,
                "humidity": 83
            },
            "dt": 1622505945,
            "wind": {
                "speed": 3,
                "deg": 160
            },
            "sys": {
                "country": "CN"
            },
            "rain": null,
            "snow": null,
            "clouds": {
                "all": 40
            },
            "weather": [
                {
                    "id": 802,
                    "main": "Clouds",
                    "description": "scattered clouds",
                    "icon": "03d"
                }
            ]
        }
    ]
}
    * */
}
