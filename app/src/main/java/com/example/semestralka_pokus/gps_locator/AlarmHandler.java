package com.example.semestralka_pokus.gps_locator;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.semestralka_pokus.network.BTNetwork;

public class AlarmHandler {
    private Context context;

    public AlarmHandler(Context context) {
        this.context = context;
    }

    public void setAlarmManager() {
        Intent intent = new Intent(context, GPSLocatorService.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 2, intent, FLAG_IMMUTABLE);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am != null) {
            long triggerAfter = 24 * 60 * 60 * 1000;
            long triggerEvery = 24 * 60 * 60 * 1000;
            am.setRepeating(AlarmManager.RTC_WAKEUP, triggerAfter, triggerEvery, sender);
        }
    }

    public void cancelAlarmManager() {
        Intent intent = new Intent(context, BTNetwork.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 2, intent, FLAG_IMMUTABLE);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am != null) {
            am.cancel(sender);
        }
    }
}
