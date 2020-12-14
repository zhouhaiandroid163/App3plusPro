package com.zjw.apps3pluspro.bleservice.scan;

public enum MyBleScanState {

    SCAN_SUCCESS(0, "SCAN_SUCCESS"),
    SCAN_TIMEOUT(-2, "SCAN_SUCCESS_TIME_OUT"),

    SCAN_FAILED_ALREADY_STARTED(1, "SCAN_FAILED_ALREADY_STARTED"),
    BLUETOOTH_OFF(-1, "BLUETOOTH_OFF"),
    SCAN_FAILED_APPLICATION_REGISTRATION_FAILED(2, "SCAN_FAILED_APPLICATION_REGISTRATION_FAILED"),
    SCAN_FAILED_INTERNAL_ERROR(3, "SCAN_FAILED_INTERNAL_ERROR"),
    SCAN_FAILED_FEATURE_UNSUPPORTED(4, "SCAN_FAILED_FEATURE_UNSUPPORTED"),
    SCAN_FAILED_NO_PERMISSION(5, "SCAN_FAILED_NO_PERMISSION"),
    SCAN_FAILED_OUT_OF_HARDWARE_RESOURCES(6, "SCAN_FAILED_OUT_OF_HARDWARE_RESOURCES"),

    SCAN_STOP_BY_NEW_SCAN_TASK(7, "SCAN_STOP_BY_NEW_SCAN_TASK"),
    SCAN_STOP_BY_NEW_BLE_CONNECT(8, "SCAN_STOP_BY_NEW_BLE_CONNECT"),
    SCAN_STOP_BY_BLE_DISCONNECT(9, "SCAN_STOP_BY_BLE_DISCONNECT"),
    SCAN_STOP_NORMAL(10, "SCAN_STOP_NORMAL");

    MyBleScanState(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private int code;
    private String message;

    public int getCode() {
        return code;
    }


    public String getMessage() {
        return message;
    }

    public static boolean isErrorState(MyBleScanState state) {
        if (state == BLUETOOTH_OFF
                || state == SCAN_FAILED_APPLICATION_REGISTRATION_FAILED
                || state == SCAN_FAILED_INTERNAL_ERROR
                || state == SCAN_FAILED_FEATURE_UNSUPPORTED
                || state == SCAN_FAILED_NO_PERMISSION
                || state == SCAN_FAILED_OUT_OF_HARDWARE_RESOURCES) {
            return true;
        }
        return false;
    }

    public static MyBleScanState newInstance(int code) {
        switch (code) {
            case -2:
                return MyBleScanState.SCAN_TIMEOUT;
            case -1:
                return MyBleScanState.BLUETOOTH_OFF;

            case 1:
                return MyBleScanState.SCAN_FAILED_ALREADY_STARTED;
            case 2:
                return MyBleScanState.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED;
            case 3:
                return MyBleScanState.SCAN_FAILED_INTERNAL_ERROR;
            case 4:
                return MyBleScanState.SCAN_FAILED_FEATURE_UNSUPPORTED;
            case 5:
                return MyBleScanState.SCAN_FAILED_OUT_OF_HARDWARE_RESOURCES;
            case 6:
                return MyBleScanState.SCAN_STOP_BY_NEW_SCAN_TASK;
            case 7:
                return MyBleScanState.SCAN_STOP_BY_NEW_BLE_CONNECT;
            case 8:
                return MyBleScanState.SCAN_STOP_BY_BLE_DISCONNECT;
            case 9:
                return MyBleScanState.SCAN_STOP_NORMAL;
            default:
                return MyBleScanState.SCAN_SUCCESS;
        }
    }
}
