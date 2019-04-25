package com.example.finalproject;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationBuilderWithBuilderAccessor;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

//Sourced from Android Developer's website "Create a Notification" document
//link: https://developer.android.com/training/notify-user/build-notification.html#java
//As well as Android Developer's video tutorial "Background work with JobScheduler"
//link: https://www.youtube.com/watch?v=XFN3MrnNhZA
public class MyJobService extends JobService {
    public MyJobService() {
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        GregorianCalendar current = new GregorianCalendar();
        current.add(Calendar.DAY_OF_MONTH, 14);
        String date = "";
        date += "" + current.get(Calendar.YEAR);
        date += "-" + (current.get(Calendar.MONTH) +1)%12;
        date += "-" + current.get(Calendar.DAY_OF_MONTH);
        String[] args ={date};
        Cursor c = MainActivity.database.rawQuery("SELECT P.description, P.exDate FROM Product P where P.exDate = ?", args);
        while(c.moveToNext()) {

            if(Build.VERSION.SDK_INT >= 26){
                NotificationChannel noteChannel = new NotificationChannel("COMM433", "Expiration Channel", NotificationManager.IMPORTANCE_DEFAULT);
                noteChannel.setDescription("Notifications for when a product expires");
                NotificationManager noteManager = getSystemService(NotificationManager.class);
                noteManager.createNotificationChannel(noteChannel);
            }
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "COMM433");
            builder.setSmallIcon(R.drawable.light);
            builder.setContentTitle("One of your products expires soon!");
            builder.setContentText(c.getString(0) +" expires on " + c.getString(1));
            builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

            Intent mainA = new Intent(this, MainActivity.class);
            mainA.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingMainA = PendingIntent.getActivity(this, 0, mainA, 0);
            builder.setContentIntent(pendingMainA);
            builder.setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(433, builder.build());
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
