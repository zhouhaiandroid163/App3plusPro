package com.zjw.apps3pluspro.bleservice.scan;

import android.bluetooth.BluetoothDevice;

public interface MyBleScanCallback {
    /**
     * after start BLE Scan, the new device found will be callback by this function
     *
     * @param device
     * @param rssi
     * @param scanRecord
     */
    void onScanResult(BluetoothDevice device, int rssi, byte[] scanRecord);

    void onScanStop(MyBleScanState myBleScanState);

}
