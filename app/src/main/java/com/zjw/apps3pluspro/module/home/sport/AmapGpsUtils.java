package com.zjw.apps3pluspro.module.home.sport;

import com.amap.api.maps.model.LatLng;

/**
 * GPS 工具类
 * 进一组经纬度数据，出一组经纬度数据
 * 每次开始调用先调用一下初始化方法init()
 */
public class AmapGpsUtils {

    //数据长度
    final static int GPS_DATALENGTH = 10;

    static double[] longiData = new double[GPS_DATALENGTH];//经度
    static double[] latiData = new double[GPS_DATALENGTH];//经度
    static int StartNo = 0;//下标

    public static double ProcessNiose_Q_T = 0.001;
    public static double MeasureNoise_R_T = 0.005;


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
     */
    public static LatLng gps_Processing(LatLng latlng) {

        double longitude_input = latlng.longitude;
        double latitude_input = latlng.latitude;
        //经纬度输出结果
        double longitude_out;
        double latitude_out;

        StartNo++;

        for (int i = 0; i < GPS_DATALENGTH - 1; i++) {
            longiData[i] = longiData[i + 1];
            latiData[i] = latiData[i + 1];
        }


        longiData[GPS_DATALENGTH - 1] = longitude_input;
        latiData[GPS_DATALENGTH - 1] = latitude_input;

        if (StartNo < GPS_DATALENGTH) {
            longitude_out = longitude_input;
            latitude_out = latitude_input;
        } else {
            longitude_out = gpsFilter(longiData, GPS_DATALENGTH, ProcessNiose_Q_T, MeasureNoise_R_T, 10);
            latitude_out = gpsFilter(latiData, GPS_DATALENGTH, ProcessNiose_Q_T, MeasureNoise_R_T, 10);
        }
        if (StartNo > GPS_DATALENGTH - 1) {
            StartNo = GPS_DATALENGTH - 1;
        }

        return new LatLng(latitude_out, longitude_out);
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
