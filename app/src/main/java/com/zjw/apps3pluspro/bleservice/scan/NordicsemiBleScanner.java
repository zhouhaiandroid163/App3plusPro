package com.zjw.apps3pluspro.bleservice.scan;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;

import com.zjw.apps3pluspro.utils.log.MyLog;

import java.util.ArrayList;
import java.util.List;

import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;


@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class NordicsemiBleScanner extends BaseBleScanner {
    private final static String TAG = NordicsemiBleScanner.class.getName();


    private BluetoothAdapter mBluetoothAdapter = null;
    private SimpleScanCallback mScanCallback = null;

    Context mContext;

    public NordicsemiBleScanner(Context context, SimpleScanCallback callback) {
        this.mContext = context;
        mScanCallback = callback;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @SuppressWarnings(value = {"deprecation"})
    @Override
    public void onStartBleScan() {
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            try {
                final BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
                final no.nordicsemi.android.support.v18.scanner.ScanSettings settings = new no.nordicsemi.android.support.v18.scanner.ScanSettings.Builder()
                        .setLegacy(false)
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).setReportDelay(1000).setUseHardwareBatchingIfSupported(false).build();
                final List<no.nordicsemi.android.support.v18.scanner.ScanFilter> filters = new ArrayList<>();

                filters.add(new ScanFilter.Builder().setServiceUuid(null).build());

                scanner.startScan(filters, settings, scanCallback);

                isScanning = true;
            } catch (Exception e) {
                isScanning = false;
                mScanCallback.onBleScanStop(MyBleScanState.BLUETOOTH_OFF);
                MyLog.e(TAG, e.toString());
            }
        } else {
            mScanCallback.onBleScanStop(MyBleScanState.BLUETOOTH_OFF);
        }
        MyLog.e(TAG, "mBluetoothScanner.startScan()");
    }

    @SuppressWarnings(value = {"deprecation"})
    @Override
    public void onStopBleScan() {
        isScanning = false;
        final BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
        scanner.stopScan(scanCallback);
    }


    @Override
    public void onBleScanFailed(MyBleScanState scanState) {
        mScanCallback.onBleScanStop(scanState);
    }

    private no.nordicsemi.android.support.v18.scanner.ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(final int callbackType, @NonNull final no.nordicsemi.android.support.v18.scanner.ScanResult result) {
            // do nothing
        }

        @Override
        public void onBatchScanResults(@NonNull final List<ScanResult> results) {
            mScanCallback.onBleScan(results);
        }
        @Override
        public void onScanFailed(final int errorCode) {
            // should never be called
        }
    };
}
