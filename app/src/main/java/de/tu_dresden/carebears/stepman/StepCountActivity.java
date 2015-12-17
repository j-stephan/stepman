package de.tu_dresden.carebears.stepman;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Timer;
import java.util.TimerTask;

public class StepCountActivity extends AppCompatActivity {
    private Button resetButton;
    private ToggleButton onOffButton;
    private StepCounter counter;
    private DistanceManager distanceManager;
    private Boolean counterServiceRunning;
    private Boolean distanceServiceRunning;
    private Boolean servicesBound = false;
    private Timer updateTimer;
    private boolean initialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(initialized) {
            return;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_count);

        this.updateTimer = new Timer();
        this.updateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                update();
            }
        }, 0, 1000);

        initialized = true;

        this.resetButton = (Button) findViewById(R.id.buttonReset);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter.reset();
                distanceManager.reset();
            }
        });

        distanceServiceRunning = isMyServiceRunning(DistanceManager.class);
        counterServiceRunning = isMyServiceRunning(StepCounter.class);

        if((distanceServiceRunning & counterServiceRunning) & !servicesBound){
            Intent distanceIntent = new Intent(this, DistanceManager.class);
            bindService(distanceIntent, DistanceManagerCallback, Context.BIND_AUTO_CREATE);
            Intent stepIntent = new Intent(this, StepCounter.class);
            bindService(stepIntent, StepCounterCallback, Context.BIND_AUTO_CREATE);
        }

        this.onOffButton = (ToggleButton) findViewById(R.id.onOffButton);
        if(distanceServiceRunning && counterServiceRunning){
            onOffButton.setChecked(true);
        } else {
            onOffButton.setChecked(false);
        }
        onOffButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateServiceStatus();
            }
        });
    }

    private void updateServiceStatus() {
        if(!(distanceServiceRunning & counterServiceRunning)){
            if(!distanceServiceRunning) {
                Intent distanceIntent = new Intent(this, DistanceManager.class);
                startService(distanceIntent);
                bindService(distanceIntent, DistanceManagerCallback, Context.BIND_AUTO_CREATE);
                distanceServiceRunning = true;
            }
            if(!counterServiceRunning){
                Intent stepIntent = new Intent(this, StepCounter.class);
                startService(stepIntent);
                bindService(stepIntent, StepCounterCallback, Context.BIND_AUTO_CREATE);
                counterServiceRunning = true;
            }
            onOffButton.setChecked(true);
        } else {
            if(distanceServiceRunning){
                unbindService(DistanceManagerCallback);
                stopService(new Intent(this, DistanceManager.class));
                distanceServiceRunning = false;
            }
            if(counterServiceRunning){
                unbindService(StepCounterCallback);
                stopService(new Intent(this, StepCounter.class));
                counterServiceRunning = false;
            }
            onOffButton.setChecked(false);
        }
    }

    private void update() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!onOffButton.isChecked()) {
                    TextView text = (TextView) findViewById(R.id.text);
                    text.setText(getString(R.string.services_not_running));
                    return;
                }
                if (distanceManager != null && counter != null) {
                    TextView text = (TextView) findViewById(R.id.text);
                    text.setText(getString(R.string.distance) + ":\t" + distanceManager.getData() + " m\n" +
                            getString(R.string.steps) + ":\t" + (int) counter.getData() + "\n" +
                            getString(R.string.step_length) + ":\t" + distanceManager.getData() / counter.getData() + " m");
                }
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(isFinishing()) {
            unbindService(DistanceManagerCallback);
            unbindService(StepCounterCallback);
        }
    }

    protected void onPause(){
        super.onPause();
    }

    protected void onResume(){
        super.onResume();
    }

    @Override
    protected  void onSaveInstanceState(Bundle state)	{
        super.onSaveInstanceState(state);

    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);

    }

    private ServiceConnection DistanceManagerCallback = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DistanceManager.LocalBinder mService = (DistanceManager.LocalBinder) service;
            StepCountActivity.this.distanceManager =  mService.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private ServiceConnection StepCounterCallback = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            StepCounter.LocalBinder mService = (StepCounter.LocalBinder) service;
            StepCountActivity.this.counter = mService.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
