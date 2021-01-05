package com.zjw.apps3pluspro.module.device.entity;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.zjw.apps3pluspro.bleservice.BleConstant;

import no.nordicsemi.android.support.v18.scanner.ScanResult;

public class DeviceModel implements Parcelable {

    public BluetoothDevice device;
    public int rssi;
    public int mumber;
    public String name;
    public String address;

    public String serviceDataString;

    public DeviceModel(@NonNull final ScanResult scanResult) {
        this.device = scanResult.getDevice();
        this.name = device.getName();
        this.address = device.getAddress();
//        this.name = scanResult.getScanRecord() != null ? scanResult.getScanRecord().getDeviceName() : null;
        this.rssi = scanResult.getRssi();

        try {
            if (scanResult.getScanRecord() != null && scanResult.getScanRecord().getServiceData() != null) {
                ParcelUuid parcelUuid = new ParcelUuid(BleConstant.SCAN_RECORD);
                byte[] serviceData = scanResult.getScanRecord().getServiceData().get(parcelUuid);
                if (serviceData == null) {
                    return;
                }
                final StringBuilder stringBuilder = new StringBuilder(serviceData.length);
                for (byte byteChar : serviceData)
                    stringBuilder.append(String.format("%02X ", byteChar));
                this.serviceDataString = stringBuilder.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected DeviceModel(Parcel in) {
        device = in.readParcelable(BluetoothDevice.class.getClassLoader());
        rssi = in.readInt();
        mumber = in.readInt();
        name = in.readString();
        address = in.readString();
        serviceDataString = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(device, flags);
        dest.writeInt(rssi);
        dest.writeInt(mumber);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(serviceDataString);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DeviceModel> CREATOR = new Creator<DeviceModel>() {
        @Override
        public DeviceModel createFromParcel(Parcel in) {
            return new DeviceModel(in);
        }

        @Override
        public DeviceModel[] newArray(int size) {
            return new DeviceModel[size];
        }
    };

    public int getMumber() {
        return mumber;
    }

    public void setMumber(int mumber) {
        this.mumber = mumber;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public String getServiceDataString() {
        return serviceDataString;
    }

    public void setServiceDataString(String serviceDataString) {
        this.serviceDataString = serviceDataString;
    }

    @Override
    public String toString() {
        return "DeviceModel{" +
                "device=" + device +
                ", rssi=" + rssi +
                ", mumber=" + mumber +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", serviceDataString='" + serviceDataString + '\'' +
                '}';
    }
}
