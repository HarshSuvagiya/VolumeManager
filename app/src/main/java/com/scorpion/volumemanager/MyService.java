package com.scorpion.volumemanager;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.multidex.BuildConfig;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

public class MyService extends Service {

    CountDownTimer countDownTimer;
    public ArrayList<VolumeModel> brightnessManagerList = new ArrayList<>();
    String key = "brightnessManagerList";
    SharedPreferences shref;
    SharedPreferences.Editor editor;
    Gson gson = new Gson();
    String response;
    float curBrightnessValue = 50;
    boolean flag = true;
    boolean flag2 = true;
    int defaultMode;
    int ring;
    int media;
    int alarm;
    int voiceCall;
    int notification;
    AudioManager audioManager;
    boolean tmp = true;

    @Override
    public void onCreate() {
        super.onCreate();
        onDestroy();
        setForeground();

        shref = getSharedPreferences("MyPref", Context.MODE_PRIVATE);

        countDownTimer = new CountDownTimer(10000, 2000) {
            @Override
            public void onTick(long millisUntilFinished) {
                ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfo = am.getRunningAppProcesses();
                audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
//                defaultMode = shref.getInt("defaultMode", 0);
                ring = shref.getInt("ringProgress", audioManager.getStreamMaxVolume(AudioManager.STREAM_RING));
                media = shref.getInt("mediaProgress", audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) - 1);
                alarm = shref.getInt("alarmProgress", audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM) - 1);
                voiceCall = shref.getInt("voiceProgress", audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL) - 1);
                notification = shref.getInt("notificationProgress", audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION));
                response = shref.getString(key, "");
                if (gson.fromJson(response, new TypeToken<List<VolumeModel>>() {
                }.getType()) != null)
                    brightnessManagerList = gson.fromJson(response, new TypeToken<List<VolumeModel>>() {
                    }.getType());

                Log.e("SIZE", String.valueOf(brightnessManagerList.size()));

                for (int j = 0; j < brightnessManagerList.size(); j++) {
                    Log.e("APP1", brightnessManagerList.get(j).getPkgName());
                    Log.e("APP2", getCurrentAppName());
                    if (brightnessManagerList.get(j).getPkgName().equals(getCurrentAppName())) {
                        flag = false;
                        flag2 = true;

                        if (tmp) {
                            if (brightnessManagerList.get(j).getMode() == 0)
                                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                            else if (brightnessManagerList.get(j).getMode() == 1)
                                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                            else if (brightnessManagerList.get(j).getMode() == 2)
                                audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);

                            Log.e("SIZE1", String.valueOf(brightnessManagerList.get(j).getRing()));
                            audioManager.setStreamVolume(AudioManager.STREAM_RING, brightnessManagerList.get(j).getRing(), 0);
                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, brightnessManagerList.get(j).getMedia(), 0);
                            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, brightnessManagerList.get(j).getAlarm(), 0);
                            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, brightnessManagerList.get(j).getVoiceCall(), 0);
                            audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, brightnessManagerList.get(j).getNotification(), 0);
                            tmp = false;
                        }




                        break;
//                        if (brightnessManagerList.get(j).isAutoRotation()) {
//                            setAutoOrientationEnabled(getApplicationContext(),true);
//                        } else {
//                            if(brightnessManagerList.get(j).getOrientationMode() == 1){
//                                Settings.System.putInt(getApplicationContext().getContentResolver(), "accelerometer_rotation", 0);
//                                Settings.System.putInt(MyService.this.getContentResolver(), "user_rotation", 0);
//                            }
//                            else{
//                                Settings.System.putInt(getApplicationContext().getContentResolver(), "accelerometer_rotation", 0);
//                                Settings.System.putInt(MyService.this.getContentResolver(), "user_rotation", 1);
//                            }
//
////                            Log.e("WAKELOCK1", String.valueOf(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)));
////                            Log.e("WAKELOCK2", String.valueOf(audioManager.getStreamVolume(AudioManager.STREAM_RING)));
////                            Log.e("WAKELOCK3", String.valueOf(audioManager.getStreamVolume(AudioManager.STREAM_ALARM)));
////                            Log.e("WAKELOCK4", String.valueOf(audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM)));
////                            Log.e("WAKELOCK5", String.valueOf(audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL)));
////                            Log.e("WAKELOCK6", String.valueOf(audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION)));
////                            Log.e("WAKELOCK", String.valueOf(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)));
////
////                            audioManager.setStreamVolume(AudioManager.STREAM_RING,15 , 0);
                    } else
                        flag = true;
                }
                if (flag) {
                    if (flag2) {
                        tmp = true;

                        if (defaultMode == 0) {
                            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                            Log.e("MODE", "NORMAL1");
                        } else if (defaultMode == 1)
                            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        else if (defaultMode == 2)
                            audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);

                        audioManager.setStreamVolume(AudioManager.STREAM_RING, ring, 0);
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, media, 0);
                        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, alarm, 0);
                        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, voiceCall, 0);
                        audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, notification, 0);
                        Log.e("MODE", "NORMAL2");
                        flag = false;
                        flag2 = false;
                    }
                }
            }

            @Override
            public void onFinish() {
                countDownTimer.start();
            }
        }.start();

    }

    public String getCurrentAppName() {

        long currentTimeMillis = System.currentTimeMillis();
        List<UsageStats> queryUsageStats = ((UsageStatsManager) getApplicationContext().getSystemService("usagestats")).queryUsageStats(0, currentTimeMillis - 3600000, currentTimeMillis);
        ActivityManager activityManager = (ActivityManager) getSystemService("activity");
        String str = activityManager.getRunningAppProcesses().get(0).processName;
        List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(5);

        if (Build.VERSION.SDK_INT > 20) {
            if (queryUsageStats != null) {
                TreeMap treeMap = new TreeMap();
                for (UsageStats next : queryUsageStats) {
                    treeMap.put(Long.valueOf(next.getLastTimeUsed()), next);
                }
                if (treeMap.isEmpty()) {
                    str = BuildConfig.FLAVOR;
                } else {
                    UsageStats usageStats = (UsageStats) treeMap.get(treeMap.lastKey());
                    if (usageStats.getTotalTimeInForeground() > 0) {
                        str = usageStats.getPackageName();
                    }
                }
            }
        } else if (runningTasks.size() > 0) {
            str = runningTasks.get(0).topActivity.getPackageName();
        }
        Log.e("PROCESSMANAGER123", str);
        return str;
    }

    private void setForeground() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager notificationManager = (NotificationManager) getSystemService("notification");
            NotificationChannel notificationChannel = new NotificationChannel("noni", "system", 4);
            notificationManager.createNotificationChannel(notificationChannel);
            startForeground(11, new Notification.Builder(this, notificationChannel.getId()).setContentTitle(getResources().getString(R.string.app_name)).setContentText("Long press to hide this notification").setSmallIcon(R.drawable.ic_notifications_black_24dp).build());
            return;
        }
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.setContentTitle(getResources().getString(R.string.app_name)).setSmallIcon(R.drawable.ic_notifications_black_24dp).setContentText("Long press to hide this notification");
        startForeground(11, builder.build());
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(NotificationCompat.CATEGORY_ALARM);
        long elapsedRealtime = SystemClock.elapsedRealtime() + 10000;
        Intent intent2 = new Intent(this, CallBroadCastReceiver.class);
        intent2.setAction("com.wond.call.coming");
        alarmManager.set(2, elapsedRealtime, PendingIntent.getBroadcast(this, 0, intent2, 0));
        return super.onStartCommand(intent, i, i2);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null)
            countDownTimer.cancel();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class LocalBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }
}
