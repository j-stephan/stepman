package de.tu_dresden.carebears.stepman;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class StepLengthActivity extends AppCompatActivity {

    private StepCounter stepCounter;
    private DistanceManager distanceManager;
    private Timer updateTimer;

    private boolean initialized = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(initialized)
            return;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_length);

        stepCounter = StepCounter.getInstance(this);
        if(!stepCounter.initialize())
            Toast.makeText(this, stepCounter.getStatusMessage(), Toast.LENGTH_LONG).show();

        distanceManager = DistanceManager.getInstance(this);
        if(!distanceManager.initialize())
            Toast.makeText(this, distanceManager.getStatusMessage(), Toast.LENGTH_LONG).show();

        updateTimer = new Timer();
        updateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                update();
            }
        }, 0, 1000);

        initialized = true;
    }

    private void update() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView lengthText = (TextView) findViewById(R.id.textStepLength);
                if(distanceManager.getTimeBetween() > 60000) // time in milliseconds
                    lengthText.setText("Could not measure distance accurately");
                else {
                    float length = distanceManager.getData() / stepCounter.getData();
                    if(Double.isNaN(length))
                        lengthText.setText("Invalid value");
                    else
                        lengthText.setText(length + " m");
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isFinishing()) {
            stepCounter.close();
            distanceManager.close();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        distanceManager.setDistance(outState.getFloat("Distance"));
        stepCounter.setInitialSteps(outState.getInt("InitialSteps"));
        initialized = outState.getBoolean("Initialized");
        stepCounter.setFirstSteps(outState.getBoolean("FirstSteps"));
    }
}
