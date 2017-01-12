package com.example.marto.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private Button mStep1Btn, mStep2Btn, mStep3Btn;
    private int mProgressStatus = 0;
    private Handler mHandler = new Handler();
    private static String[] mLogCat = {"logcat","lolol:V","*:S"};
    private String mStep1BtnStr = "", mStep2BtnStr = "";
    private static String mDataDir = "/data/data/com.example.marto.myapplication/";
    private static String mSystemDir = "/system/bin/";
    private ProgressBar mProgress;
    private static String mS1 = ".  ";
    private static String mS2 = ".. ";
    private static String mS3 = "...";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ProgressBar p = (ProgressBar)findViewById(R.id.progbar);
        p.setIndeterminate(false);
        p.setProgressDrawable(getApplicationContext().getDrawable(R.drawable.lolol));
        p.setProgress(0);
        mStep1Btn = (Button) findViewById(R.id.step1_btn);
        mStep2Btn = (Button) findViewById(R.id.step2_btn);
        mStep3Btn = (Button) findViewById(R.id.step3_btn);
        mStep1Btn.setEnabled(false);
        mStep2Btn.setEnabled(false);
        mStep3Btn.setEnabled(false);

        boolean doneStep1 = existsAsset("dirtycow") && existsAsset("appfix") &&
                existsAsset("tcfix") && existsSystemAsset("app_process32");
        if (!doneStep1) {
            mStep1Btn.setEnabled(true);
            return;
        }

        setButtonString("Done Step 1");
        p.setProgress(100);
        ApplicationInfo ai =
                null;
        try {
            ai = getApplicationContext().getPackageManager().getApplicationInfo("com.amazon.phoenix",0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            setButtonString("Failed to find Package Manager");
            return;
        }

        boolean appStatus = ai.enabled;
        if (appStatus) {
            mStep2Btn.setEnabled(true);
            return;
        }
        File directory = new File("/data/data/com.android.systemui/files");
        File[] contents = directory.listFiles();
        // the directory file is not really a directory..
        if (contents == null) {
            mStep2Btn.setEnabled(true);
            return;
        }
        setButtonString2("Done Step 2");
        // Folder is empty
        if (contents.length == 0) {
            mStep3Btn.setText("Done Step 3");
            return;
        }

        mStep3Btn.setEnabled(true);
        return;
    }

    private void setButtonString(String s) {
        mStep1BtnStr = s;
        mHandler.post(new Runnable() {
            public void run() {
                mStep1Btn.setText(mStep1BtnStr);
            }
        });
    }

    private void setButtonString2(String s) {
        mStep2BtnStr = s;
        mHandler.post(new Runnable() {
            public void run() {
                mStep2Btn.setText(mStep2BtnStr);
            }
        });
    }

    private boolean existsAsset(String assetname) {
        File dest = new File(mDataDir+assetname);
        return dest.exists();
    }

    private boolean copyAsset(String assetname) {
        try {
            File dest = new File(mDataDir+assetname);

            InputStream orig = getAssets().open(assetname);
            FileUtils.copyInputStreamToFile(orig,dest);
            if (dest.exists()) {
                setButtonString("File Exists!");
            } else {
                return false;
            }

            dest.setReadable(true,false);
            dest.setExecutable(true,false);

        } catch (IOException ex) {
            setButtonString(assetname+"::"+ex.getMessage());
            return false;
        }
        return true;
    }

    private boolean existsSystemAsset(String assetname) {
        File dest = new File(mDataDir+assetname+"old");
        return dest.exists();
    }

    private boolean copySystemAsset(String assetname) {
        try {
            File dest = new File(mDataDir+assetname+"old");//TODO

            File orig = new File(mSystemDir+assetname);
            FileUtils.copyFile(orig, dest);
            if (dest.exists()) {
                setButtonString("File Exists!");
            } else {
                return false;
            }

            dest.setReadable(true,false);
            dest.setExecutable(true,false);

        } catch (IOException ex) {
            setButtonString(assetname+"::"+ex.getMessage());// "Failed register asset "+assetname);
            return false;
        }
        return true;
    }
    private boolean runProcess(ProcessBuilder pb) {
        return runProcess(pb,false);
    }

    public static boolean isAlive( Process p ) {
        try
        {
            p.exitValue();
            return false;
        } catch (IllegalThreadStateException e) {
            return true;
        }
    }

    private boolean runProcess(ProcessBuilder pb, boolean showStatus) {
        try {
            mHandler.post(new Runnable() {
                public void run() {
                    mProgress.setIndeterminate(true);
                }
            });
            Process p = pb.start();
            p.waitFor();
            mHandler.post(new Runnable() {
                public void run() {
                    mProgress.setIndeterminate(false);
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
            setButtonString("Failed running process step 1");
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            setButtonString("Failed in step 1");
            return false;
        }

        setButtonString("Done Step 1");
        mHandler.post(new Runnable() {
            public void run() {
                mProgress.setProgress(100);
                mStep2Btn.setEnabled(true);
            }
        });

        return true;
    }

    public void step1Func(View view) {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Step 1");
        alertDialog.setMessage("This will take ~4 minutes, then run step 2.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
        /*try {
            alertDialog.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        mProgress = (ProgressBar) findViewById(R.id.progbar);


        mStep1Btn.setEnabled(false);
        new Thread(new Runnable() {
            public void run() {
                setButtonString("Running Step 1");

                if (!copyAsset("dirtycow")) {
                    return;
                }
                if (!copyAsset("appfix")) {
                    return;
                }
                if (!copyAsset("tcfix")) {
                    return;
                }
                if (!copySystemAsset("app_process32")) {
                    return;
                }
                if (!copyAsset("hello")) {
                    return;
                }

                setButtonString("Running Step 1");
                ProcessBuilder pbTcPatch = new ProcessBuilder(
                        mDataDir + "dirtycow", mSystemDir + "tc", mDataDir + "tcfix");
                if (!runProcess(pbTcPatch, true)) {
                    return;
                }
            }
        }).start();

        /*OutputStream out = new FileOutputStream("/data/data/"+"com.example.marto.myapplication"+"/"+"testexe");
        int read;
        byte[] buffer = new byte[4096];
        while ((read = in.read(buffer)) > 0) {
            out.write(buffer, 0, read);
        }
        out.close();
        in.close();*/

/*
        new Thread(new Runnable() {
            public void run() {
                try{
                    File oldappprocess = new File(getFilesDir().getAbsolutePath()+"/app_process32old2");
                    oldappprocess.setReadable(true,false);
                    oldappprocess.setWritable(true,false);
                    oldappprocess.setExecutable(true,false);
                    setButtonString("Trying to copy LogCat");
                    FileUtils.copyFile(new File("/system/bin/app_process32"),oldappprocess);
                    if (oldappprocess.exists()) {
                        Log.v("pizza3",oldappprocess.getAbsolutePath());
                    }

                } catch (IOException ex) {
                    Log.e("pizza2", "logcat failed", ex);
                    setButtonString("LogCat Failed");
                }
                setButtonString(getFilesDir().getAbsolutePath());

            }
        }).start();
*/
    }

    public void step2Func(View view) {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Step 2");
        alertDialog.setMessage("Phone will restart, return to the App to run Step 3");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
        /*try {
            alertDialog.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        mStep2Btn.setEnabled(false);

        //ok so resources should be here now.
        //and tc has been patched
        //just need to dirtycow the whole shebang
        //execute dirtycow

        // use this to start and trigger a service
        Intent i= new Intent(this.getApplicationContext(), MyService.class);
        this.getApplicationContext().startService(i);

        setButtonString2("Running Step 2");

    }

    public void step3Func(View view) {

        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Step 3");
        alertDialog.setMessage("Phone will restart, shortly after");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
        /*try {
            alertDialog.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        mStep3Btn.setEnabled(false);

        //ok so resources should be here now.
        //and tc has been patched
        //just need to dirtycow the whole shebang
        //execute dirtycow

        // use this to start and trigger a service
        Intent i= new Intent(this.getApplicationContext(), MyService.class);
        this.getApplicationContext().startService(i);

        mStep3Btn.setText("Running Step 3");
    }
}
