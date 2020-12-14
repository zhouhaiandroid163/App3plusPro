package com.zjw.apps3pluspro.bleservice.protocol;

import android.bluetooth.BluetoothGattCharacteristic;

import com.zjw.apps3pluspro.bleservice.BleConstant;

public class BleBaseProtocol {

    byte[] mBtRecData = new byte[800];
    byte[] mNullBuffer = new byte[800];

    private int mRcvDataState = 0; // first frame
    private int received_content_length = 0;
    private int length_to_receive = 0;
    boolean isDataReady = false;


    public void clearData() {
        mBtRecData = new byte[800];
        mNullBuffer = new byte[800];
    }


    public byte[] readingBleData(BluetoothGattCharacteristic characteristic) {
        if (characteristic.getUuid().equals(BleConstant.UUID_BASE_READ) || characteristic.getUuid().equals(BleConstant.CHAR_BIG_UUID_03)) {
            byte[] btData = characteristic.getValue();
            if (btData.length > 0) {
                processRcvData(btData);
            }
            if (isDataReady) {
                //数据返回
                return mBtRecData;
            }
        }
        return null;
    }

    public void setRcvDataState(int mRcvDataState){
        this.mRcvDataState = mRcvDataState;
    }

    //========================系统接口===============================

    /**
     * 判断收到手环返回的数据
     *
     * @param data
     */
    private void processRcvData(byte[] data) {

        switch (mRcvDataState) {
            case 0:
                if (data[0] == (byte) (0xab)) {
                    received_content_length = 0;
                    System.arraycopy(mNullBuffer, 0, mBtRecData, received_content_length, 100);
                    System.arraycopy(data, 0, mBtRecData, received_content_length, data.length);
                    received_content_length = data.length;
//                    int new_lenght = data[2] << 8 | data[3];
                    int new_lenght = (data[2] & 0xff) << 8 | (data[3] & 0xff);
                    length_to_receive = new_lenght + 8;
                    length_to_receive -= data.length;
                    if (length_to_receive <= 0) {
                        mRcvDataState = 0;
                        received_content_length = 0;
                        isDataReady = true;
                    } else {
                        mRcvDataState = 1;
                        isDataReady = false;
                    }
                }
                break;
            case 1:
                System.arraycopy(data, 0, mBtRecData, received_content_length, data.length);
                received_content_length += data.length;
                length_to_receive -= data.length;
                if (length_to_receive <= 0) {
                    mRcvDataState = 0;
                    isDataReady = true;
                } else {
                    isDataReady = false;
                }
                break;
        }
    }
}
