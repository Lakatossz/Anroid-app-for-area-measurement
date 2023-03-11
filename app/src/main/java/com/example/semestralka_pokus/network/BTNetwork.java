package com.example.semestralka_pokus.network;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.semestralka_pokus.gps_locator.LocatorActivity;

import java.util.List;
import java.util.UUID;

public class BTNetwork extends Activity {

    /* Konstanty pro ziskani povoleni. */
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final int PERMISSION_REQUEST_FINE_LOCATION = 2;
    private static final int PERMISSION_REQUEST_BLUETOOTH = 3;

    private static final String LOG_HEAD = "BLE";

    /* Konstanta doby scanu serveru. */
    private static final long SCAN_PERIOD = 10000;

    /* UUID service pro rozpoznani serveru. */
    public static final UUID SERVICE_ID = UUID.fromString("63b3d92a-85fc-11ed-a1eb-0242ac120002");

    public static final UUID CHARACTERISTIC_UUID = UUID.fromString("651abf45-9387-4e45-a75a-bd493c610fcb");

    public static final UUID DESCRIPTOR_UUID = UUID.fromString("fcc8b7c9-1360-47a6-8ea4-5bc808de1825"/*00002902-0000-1000-8000-00805f9b34fb*/);

    /* */
    private BluetoothGatt bluetoothGatt;

    /* */
    private BluetoothLeScanner bluetoothLeScanner;

    /* Nalezene zarizeni. */
    private BluetoothDevice discoveredDevice;

    /* Aktivita, ve ktere se zobrazuji inforace. */
    private final LocatorActivity activity;

    public BTNetwork(LocatorActivity newActivity) {
        activity = newActivity;
    }

    public boolean intialize() {
        discoveredDevice = null;
        BluetoothManager bluetoothManager =
                (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null) {
            Log.d(LOG_HEAD, "Nepodarilo se ziskat BluetoothManager.");
            return false;
        }

        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            Log.d(LOG_HEAD, "Nepodarilo se ziskat BluetoothAdapter.");
            return false;
        }

        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        if (bluetoothLeScanner == null) {
            Log.d(LOG_HEAD, "Nepodarilo se ziskat BluetoothLeScanner.");
            return false;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(),
                    Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                Log.d(LOG_HEAD, "Overuji moznosti pouziti bluetooth.");
                activity.startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            }
        }

        if (ContextCompat.checkSelfPermission(activity.getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            Log.d(LOG_HEAD, "Overuji moznosti urceni priblizne polohy.");
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_REQUEST_COARSE_LOCATION);
        }

        if (ContextCompat.checkSelfPermission(activity.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            Log.d(LOG_HEAD, "Overuji moznosti urceni presne lokace.");
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_FINE_LOCATION);
        }

        return true;
    }

    /* Skenovani zarizeni */
    private final ScanCallback leScanCallBack = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (discoveredDevice == null) {
                super.onScanResult(callbackType, result);

                Log.d(LOG_HEAD, "Nalezene zarizeni: " + result.getScanRecord());

                discoveredDevice = result.getDevice();

                if (ContextCompat.checkSelfPermission(activity
                        .getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT)
                        != PackageManager.PERMISSION_GRANTED) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        ActivityCompat.requestPermissions(activity,
                                new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                                123);

                        bluetoothGatt = discoveredDevice.connectGatt(activity
                                .getApplicationContext(), false, bluetoothGattCallback);

                        Log.d(LOG_HEAD, "Pripojil jsem zarizeni: " + bluetoothGatt.getServices());

                        activity.setClickabiltyDisconnectButton(true);
                        activity.setClickabiltySearchButton(false);
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        ActivityCompat.requestPermissions(activity,
                                new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                                123);

                        bluetoothGatt = discoveredDevice.connectGatt(activity.getApplicationContext()
                                .getApplicationContext(), false, bluetoothGattCallback);

                        stopScanning(); // Prestanu skenovat, abych usetril energii.

                        activity.setClickabiltyDisconnectButton(true);
                        activity.setClickabiltySearchButton(false);
                    }
                }
            }
        }
    };

    /* GattCallback pr zpracovani zmeny ble stavu. */
    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {

        private void setNotifySensor(BluetoothGatt gatt) {
            Log.d(LOG_HEAD, "Zapinam oznameni ze serveru.");
            if (ContextCompat.checkSelfPermission(activity
                    .getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT)
                    == PackageManager.PERMISSION_GRANTED) {
                gatt.discoverServices();
                Log.d(LOG_HEAD, "Nasel jsem: " + gatt.getService(SERVICE_ID));
                BluetoothGattCharacteristic characteristic = gatt.getService(SERVICE_ID)
                        .getCharacteristic(CHARACTERISTIC_UUID);
                gatt.setCharacteristicNotification(characteristic, true);
            }
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.d(LOG_HEAD, "Novy stav: " + newState);
            switch (newState) {
                case BluetoothProfile.STATE_DISCONNECTED:
                    // Server se odpojil.
                    Log.d(LOG_HEAD, "Server se odpojil tady.");

                    activity.runOnUiThread(() -> {
                        activity.setClickabiltySearchButton(false);
                        activity.setClickabiltyDisconnectButton(true);
                    });

                    break;
                case BluetoothProfile.STATE_CONNECTED:
                    activity.runOnUiThread(() -> {
                        Log.d(LOG_HEAD, "Server se pripojil.");
                        activity.setClickabiltySearchButton(true);
                        activity.setClickabiltyDisconnectButton(false);

                        if (ContextCompat.checkSelfPermission(activity
                                .getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT)
                                == PackageManager.PERMISSION_GRANTED) {

                            gatt.discoverServices();
                        }
                    });
                    break;

                default:
                    Log.d(LOG_HEAD, "Stalo se neco jineho.");
                    break;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Log.d(LOG_HEAD, "Nasel jsem novy service: " + gatt.getService(SERVICE_ID).getUuid());

            if (ContextCompat.checkSelfPermission(activity
                    .getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT)
                    == PackageManager.PERMISSION_GRANTED) {

                BluetoothGattCharacteristic characteristic = gatt.getService(SERVICE_ID)
                        .getCharacteristic(CHARACTERISTIC_UUID);
                gatt.setCharacteristicNotification(characteristic, true);
                gatt.writeDescriptor(characteristic.getDescriptor(DESCRIPTOR_UUID));
                Log.d(LOG_HEAD, "Jsem tady.");
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.d(LOG_HEAD, "Vyzadal jsem si data.");
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            Log.d(LOG_HEAD, "Prisly nova data.");
            activity.runOnUiThread(() -> {
                String read_value = characteristic.getStringValue(0);
                String[] values = read_value.split("\\|");
                Log.d(LOG_HEAD, read_value);
                activity.setLocation(Float.parseFloat(values[0]), Float.parseFloat(values[1]));
            });
        }
    };

    /* Zapocne skenovani. */
    public void startScanning() {

        Log.d(LOG_HEAD, "Zahajeni skenovani.");

        discoveredDevice = null;

        Handler handler = new Handler();

        AsyncTask.execute(() -> {
            if (ActivityCompat.checkSelfPermission(activity.getApplicationContext()
                    , Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_DENIED) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.BLUETOOTH_SCAN},
                            PERMISSION_REQUEST_BLUETOOTH);
                }
            } else {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    bluetoothLeScanner.startScan(List.of(new ScanFilter.Builder()
                                    .setServiceUuid(new ParcelUuid(SERVICE_ID))
                                    .build()),
                            new ScanSettings.Builder()
                                    .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                                    .build(),
                            leScanCallBack);
                }
            }
        });

        handler.postDelayed(this::stopScanning, SCAN_PERIOD);
    }

    /* Ukonceni skenovani. */
    public void stopScanning() {
        AsyncTask.execute(() -> {

            if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(),
                    Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
                bluetoothLeScanner.stopScan(leScanCallBack);
                activity.setClickabiltySearchButton(true);
                Log.d(LOG_HEAD, "Prestavam skenovat.");
            }
        });
    }

    /* Odpojeni se. */
    public void disconnectDevice() {
        if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(),
                Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            /* TODO muze byt null. */
            Log.d(LOG_HEAD, "Odpojuji zarizeni.");
            bluetoothGatt.disconnect();
        }
    }
}
