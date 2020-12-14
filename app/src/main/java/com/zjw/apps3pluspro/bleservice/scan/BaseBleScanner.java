package com.zjw.apps3pluspro.bleservice.scan;


public abstract class BaseBleScanner {
    public final static long defaultTimeout = 10 *1000;
    protected boolean isScanning;

    public abstract void onStartBleScan();
//    public abstract void onStartBleScan(long timeoutMillis);

    public abstract void onStopBleScan();

    public abstract void onBleScanFailed(MyBleScanState scanState);

//    protected Handler timeoutHandler  = new Handler();
//    protected Runnable timeoutRunnable = new Runnable() {
//        @Override
//        public void run() {
//            onStopBleScan();
//            onBleScanFailed(MyBleScanState.SCAN_TIMEOUT);
//        }
//    };
}
