package com.zjw.apps3pluspro.module.device.weather.openweather;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android
 * on 2021/5/31
 */
public class WeatherDays {

    public WeatherCity city;
    public String cod;
    public String message;
    public String cnt;
    public ArrayList<WeatherDay> list;

    public class WeatherDay {
        public String dt;
        public String sunrise;
        public String sunset;
        public WeatherTemp temp;
        public WeatherFeelsLike feels_like;
        public String pressure;
        public String humidity;
        public ArrayList<WeatherItem> weather;
        public String speed;
        public String deg;
        public String gust;
        public String clouds;
        public String pop;
        public String rain;
    }

    /*
    * {
    "city": {
        "id": 6958812,
        "name": "Qigang",
        "coord": {
            "lon": 113.8257,
            "lat": 22.6397
        },
        "country": "CN",
        "population": 0,
        "timezone": 28800
    },
    "cod": "200",
    "message": 5.086296,
    "cnt": 4,
    "list": [
        {
            "dt": 1622433600,
            "sunrise": 1622410799,
            "sunset": 1622459104,
            "temp": {
                "day": 31.3,
                "min": 28.19,
                "max": 32.18,
                "night": 28.53,
                "eve": 30.71,
                "morn": 28.19
            },
            "feels_like": {
                "day": 40.44,
                "night": 34.64,
                "eve": 39.92,
                "morn": 33.26
            },
            "pressure": 1003,
            "humidity": 76,
            "weather": [
                {
                    "id": 501,
                    "main": "Rain",
                    "description": "moderate rain",
                    "icon": "10d"
                }
            ],
            "speed": 8.58,
            "deg": 183,
            "gust": 11.17,
            "clouds": 68,
            "pop": 1,
            "rain": 30.82
        },
        {
            "dt": 1622520000,
            "sunrise": 1622497191,
            "sunset": 1622545530,
            "temp": {
                "day": 28.33,
                "min": 27,
                "max": 29.51,
                "night": 28.31,
                "eve": 29.06,
                "morn": 27.43
            },
            "feels_like": {
                "day": 33.12,
                "night": 33.6,
                "eve": 34.47,
                "morn": 31.46
            },
            "pressure": 1007,
            "humidity": 81,
            "weather": [
                {
                    "id": 501,
                    "main": "Rain",
                    "description": "moderate rain",
                    "icon": "10d"
                }
            ],
            "speed": 5.97,
            "deg": 155,
            "gust": 7.86,
            "clouds": 100,
            "pop": 1,
            "rain": 14.6
        },
        {
            "dt": 1622606400,
            "sunrise": 1622583584,
            "sunset": 1622631955,
            "temp": {
                "day": 28.71,
                "min": 27.78,
                "max": 29.49,
                "night": 28.49,
                "eve": 29.46,
                "morn": 27.88
            },
            "feels_like": {
                "day": 34.56,
                "night": 34.51,
                "eve": 35.81,
                "morn": 32.88
            },
            "pressure": 1008,
            "humidity": 83,
            "weather": [
                {
                    "id": 501,
                    "main": "Rain",
                    "description": "moderate rain",
                    "icon": "10d"
                }
            ],
            "speed": 5.44,
            "deg": 164,
            "gust": 7.68,
            "clouds": 100,
            "pop": 0.96,
            "rain": 7.31
        },
        {
            "dt": 1622692800,
            "sunrise": 1622669978,
            "sunset": 1622718380,
            "temp": {
                "day": 30.51,
                "min": 28.01,
                "max": 31.12,
                "night": 28.8,
                "eve": 30.41,
                "morn": 28.01
            },
            "feels_like": {
                "day": 36.59,
                "night": 34.82,
                "eve": 36.88,
                "morn": 33.26
            },
            "pressure": 1007,
            "humidity": 71,
            "weather": [
                {
                    "id": 500,
                    "main": "Rain",
                    "description": "light rain",
                    "icon": "10d"
                }
            ],
            "speed": 8,
            "deg": 174,
            "gust": 9.15,
            "clouds": 100,
            "pop": 0.35,
            "rain": 0.59
        }
    ]
}
    * */
}
