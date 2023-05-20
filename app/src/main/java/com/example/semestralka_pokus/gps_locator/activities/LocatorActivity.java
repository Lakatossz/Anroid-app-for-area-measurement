package com.example.semestralka_pokus.gps_locator.activities;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.BackoffPolicy;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.semestralka_pokus.R;
import com.example.semestralka_pokus.WelcomeActivity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class LocatorActivity extends AppCompatActivity {

    private TextView longValue;
    private TextView latValue;
    private TextView timeValue;

    private Button searchButton;
    private Button disconnectButton;

    String TAG_LOCATOR = "TAG";

    LocatorActivity activity = this;

    Handler handler;

    WorkManager workManager;

    Float lon, lat;

    @SuppressLint("StaticFieldLeak")
    public static BTNetwork network;

    public class BTNetwork {
        /* Konstanty pro ziskani povoleni. */
        private static final int REQUEST_ENABLE_BT = 1;
        private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
        private static final int PERMISSION_REQUEST_FINE_LOCATION = 2;
        private static final int PERMISSION_REQUEST_BLUETOOTH = 3;

        private static final String LOG_HEAD = "BLE";

        /* Konstanta doby scanu serveru. */
        private static final long SCAN_PERIOD = 10000;

        /* UUID service pro rozpoznani serveru. */
        public final UUID SERVICE_ID = UUID.fromString("63b3d92a-85fc-11ed-a1eb-0242ac120002");

        public final UUID CHARACTERISTIC_UUID = UUID.fromString("651abf45-9387-4e45-a75a-bd493c610fcb");

        public final UUID DESCRIPTOR_UUID = UUID.fromString("fcc8b7c9-1360-47a6-8ea4-5bc808de1825"/*00002902-0000-1000-8000-00805f9b34fb*/);

        /* */
        public BluetoothGatt bluetoothGatt;

        /* */
        public BluetoothLeScanner bluetoothLeScanner;

        /* Nalezena zarizeni. */
        public BluetoothDevice discoveredDevice;

        public BTNetwork() {

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
                Log.d("ALARM", characteristic.getStringValue(0));
                activity.runOnUiThread(() -> {
                    String read_value = characteristic.getStringValue(0);
                    if (read_value.startsWith("T")) {
                        Log.d(LOG_HEAD, read_value);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            activity.setTime(LocalDateTime.parse(read_value.substring(1),
                                    DateTimeFormatter.ofPattern("yyyy-MM-dd|HH:mm:ss")));
                        }
                    } else if (read_value.startsWith("L")) {
                        String[] values = read_value.substring(1).split("\\|");
                        Log.d(LOG_HEAD, read_value);
                        activity.setLocation(Float.parseFloat(values[1]), Float.parseFloat(values[0]));
                    }
                });
            }
        };

        /* Zapocne skenovani. */
        public void startScanning() {

            Log.d(LOG_HEAD, "Zahajeni skenovani.");

            discoveredDevice = null;

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

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopScanning();
                }
            }, SCAN_PERIOD);
//            handler = new Handler();
//            handler.postDelayed(this::stopScanning, SCAN_PERIOD);
        }

        /* Ukonceni skenovani. */
        public void stopScanning() {
            AsyncTask.execute(() -> {

                if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(),
                        Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
                    if (bluetoothLeScanner != null) {
                        bluetoothLeScanner.stopScan(leScanCallBack);
                    }
                    activity.setClickabiltySearchButton(true);
                    Log.d(LOG_HEAD, "Prestavam skenovat.");
                }
            });
        }

        /* Odpojeni se. */
        public void disconnectDevice() {
            if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(),
                    Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                if (bluetoothGatt != null) {
                    Log.d(LOG_HEAD, "Odpojuji zarizeni.");
                    bluetoothGatt.disconnect();
                }
            }
        }
    }

    public static class LocatorWorker extends Worker {

        public LocatorWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
            super(appContext, workerParams);
        }

        @NonNull
        @Override
        public Result doWork() {
            Log.d("TAG", "Poustim to znovu.");
            if (network != null && network.intialize()) {
                network.startScanning();
                return Result.success();
            }
            return Result.retry();
        }
    }

    /**
     * Vytvori a zobrazi aktivitu.
     */
    @SuppressLint({"SetTextI18n", "UnspecifiedImmutableFlag", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locator);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            lat = Float.parseFloat(b.getString("lat"));
            lon = Float.parseFloat(b.getString("lon"));
        }

        TextView label = findViewById(R.id.label);
        TextView longLabel = findViewById(R.id.tv_long_label);
        longValue = findViewById(R.id.tv_long);
        TextView latLabel = findViewById(R.id.tv_lat_label);
        latValue = findViewById(R.id.tv_lat);
        TextView timeLabel = findViewById(R.id.tv_timeLabel);
        timeValue = findViewById(R.id.tv_time);
        searchButton = findViewById(R.id.button_search);
        searchButton.setBackgroundColor(Color.argb(255, 118, 118, 255));
        disconnectButton = findViewById(R.id.button_disconnect);
        setClickabiltyDisconnectButton(false);
        Button homeButton = findViewById(R.id.button_home);
        homeButton.setBackgroundColor(Color.argb(255, 118, 118, 255));
        Button mapButton = findViewById(R.id.button_map);
        mapButton.setBackgroundColor(Color.argb(255, 118, 118, 255));
        Button stopButton = findViewById(R.id.button_stop);
        stopButton.setBackgroundColor(Color.argb(255, 118, 118, 255));

        label.setText("Posledni poloha");
        longLabel.setText("Zemepisna delka");
        latLabel.setText("Zemepisna sirka");
        timeLabel.setText("Datum a cas");
        longValue.setText(String.valueOf(lon));
        latValue.setText(String.valueOf(lat));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            timeValue.setText(String.valueOf(LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))));
        }

        network = new BTNetwork();

        searchButton.setOnClickListener(view -> {
            workManager = WorkManager.getInstance(this);
            PeriodicWorkRequest periodicWorkRequest =
                    new PeriodicWorkRequest
                            .Builder(LocatorWorker.class, 15, TimeUnit.MINUTES)
                            .addTag(TAG_LOCATOR)
                            .setBackoffCriteria(BackoffPolicy.LINEAR,
                                    PeriodicWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                            .build();
            workManager.cancelUniqueWork("LOCATOR_WORK");
            workManager.enqueueUniquePeriodicWork(
                    "LOCATOR_WORK",
                    ExistingPeriodicWorkPolicy.KEEP,
                    periodicWorkRequest
            );
            //Util.scheduleJob(activity.getApplicationContext());
            setClickabiltyDisconnectButton(false);
            setClickabiltySearchButton(false);
        });

        disconnectButton.setOnClickListener(view -> {
            Log.d("SCAN", "Koncim.");
            if (network != null) {
                network.disconnectDevice();
            }

            if (workManager != null) {
                Log.d("SCAN", "Koncim worker.");
                workManager.cancelUniqueWork("LOCATOR_WORK");
            }

            setClickabiltyDisconnectButton(false);
            setClickabiltySearchButton(true);
        });

        homeButton.setOnClickListener(view -> {
            openHomeActivity();
        });

        stopButton.setOnClickListener(view -> {
            if (network != null) {
                network.stopScanning();
            }

            if (workManager != null) {
                Log.d("SCAN", "Koncim worker.");
                workManager.cancelUniqueWork("LOCATOR_WORK");
            }

            setClickabiltySearchButton(true);
            setClickabiltyDisconnectButton(false);
        });

        mapButton.setOnClickListener(view -> {
            openMapActivity();
            Log.d("SETTINGS", "Jdu do nastaveni.");
        });
    }

    public void openHomeActivity() {
        Intent intent = new Intent(LocatorActivity.this, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("lat", Double.valueOf(lat).toString());
        intent.putExtra("lon", Double.valueOf(lon).toString());
        startActivity(intent);
    }

    public void openMapActivity() {
        Intent intent = new Intent(LocatorActivity.this, MapLocationActivity.class);
        intent.putExtra("lat", Double.valueOf(lat).toString());
        intent.putExtra("lon", Double.valueOf(lon).toString());
        startActivity(intent);
        finish();
    }

    public void setClickabiltySearchButton(boolean clickabilty) {
        if (!clickabilty)
            searchButton.setBackgroundColor(Color.argb(255, 118, 118, 188));
        else
            searchButton.setBackgroundColor(Color.argb(255, 118, 118, 255));
        searchButton.setClickable(clickabilty);
    }

    public void setClickabiltyDisconnectButton(boolean clickabilty) {
        if (!clickabilty)
            disconnectButton.setBackgroundColor(Color.argb(255, 118, 118, 188));
        else
            disconnectButton.setBackgroundColor(Color.argb(255, 118, 118, 255));
        disconnectButton.setClickable(clickabilty);
    }

    @SuppressLint("DefaultLocale")
    public void setLocation(float longitude, float latitude) {
        lon = longitude;
        lat = latitude;
        longValue.setText(String.format("%.5f", longitude));
        latValue.setText(String.format("%.5f", latitude));
    }

    public void setTime(LocalDateTime dateTime) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            timeValue.setText(String.valueOf(dateTime));
        }
    }
}
