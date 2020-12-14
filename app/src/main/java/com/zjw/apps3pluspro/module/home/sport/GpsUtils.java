package com.zjw.apps3pluspro.module.home.sport;

/**
 * GPS 工具类
 * 进一组经纬度数据，出一组经纬度数据
 * 每次开始调用先调用一下初始化方法init()
 */
public class GpsUtils {

    //数据长度
    final static int GPS_DATALENGTH = 10;

    static double[] longiData = new double[GPS_DATALENGTH];//经度
    static double[] latiData = new double[GPS_DATALENGTH];//经度
    static int StartNo = 0;//下标

    //经纬度输出结果
    static double longitude_out;
    static double latitude_out;

    public static double getLongitude_out() {
        return longitude_out;
    }

    public static void setLongitude_out(double longitude_out) {
        GpsUtils.longitude_out = longitude_out;
    }

    public static double getLatitude_out() {
        return latitude_out;
    }

    public static void setLatitude_out(double latitude_out) {
        GpsUtils.latitude_out = latitude_out;
    }

    /**
     * 初始化
     */
    public static void init() {
        StartNo = 0;
        longiData = new double[GPS_DATALENGTH];//经度
        latiData = new double[GPS_DATALENGTH];//纬度
    }


    /**
     * GPS处理
     *
     * @param longitude 经度
     * @param latitude  纬度
     */
    public static void gps_Processing(double longitude, double latitude) {

        StartNo++;

        for (int i = 0; i < GPS_DATALENGTH - 1; i++) {
            longiData[i] = longiData[i + 1];
            latiData[i] = latiData[i + 1];
        }
        longiData[GPS_DATALENGTH - 1] = longitude;
        latiData[GPS_DATALENGTH - 1] = latitude;

        if (StartNo < GPS_DATALENGTH) {
            setLongitude_out(longitude);
            setLatitude_out(latitude);
        } else {
            setLongitude_out(gpsFilter(longiData, GPS_DATALENGTH, 0, 0.0005, 10));
            setLatitude_out(gpsFilter(latiData, GPS_DATALENGTH, 0, 0.0005, 10));
        }
        if (StartNo > GPS_DATALENGTH - 1) {
            StartNo = GPS_DATALENGTH - 1;
        }

    }


    /**
     * @param Data              数据
     * @param DataLength        数据长度
     * @param ProcessNiose_Q
     * @param MeasureNoise_R
     * @param InitialPrediction
     * @return
     */
    static double gpsFilter(double[] Data, int DataLength, double ProcessNiose_Q, double MeasureNoise_R, double InitialPrediction) {
        int i;
        double R = MeasureNoise_R;
        double Q = ProcessNiose_Q;
        double x_last = Data[0];
        double x_mid = x_last;
        double x_now = 0;
        double p_last = InitialPrediction;
        double p_mid = 0;
        double p_now = 0;
        double kg = 0;

        for (i = 0; i < DataLength; i++) {
            x_mid = x_last;
            p_mid = p_last + Q;
            kg = p_mid / (p_mid + R);
            x_now = x_mid + kg * (Data[i] - x_mid);
            p_now = (1 - kg) * p_mid;

            p_last = p_now;
            x_last = x_now;
        }
        return x_now;
    }


}
