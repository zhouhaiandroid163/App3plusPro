package com.zjw.apps3pluspro.kml;

/**
 * Created by android
 * on 2020/5/8.
 */
public class TrackPoint {

    private double longitude;
    private double latitude;
    private double altitude;
    private long time;

    public TrackPoint(double longitude, double latitude, double altitude, long time) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
        this.time = time;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
