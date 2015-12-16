package de.tu_dresden.carebears.stepman;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class StepCountActivity extends AppCompatActivity {
    private Button resetButton;
    private Button lengthButton;
    private StepCounter counter;
    private DistanceManager distanceManager;
    private Timer updateTimer;
    private boolean initialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(initialized) {
            return;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_count);

        this.counter = StepCounter.getInstance(this);
        if(!counter.initialize()) {
            Toast.makeText(this, counter.getStatusMessage(), Toast.LENGTH_LONG).show();
        }

        this.distanceManager = DistanceManager.getInstance(this);
        if(!distanceManager.initialize()){
            Toast.makeText(this, distanceManager.getStatusMessage(), Toast.LENGTH_LONG).show();
        }

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

        lengthButton = (Button) findViewById(R.id.buttonSwitchToLength);
        lengthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), StepLengthActivity.class);
                startActivity(i);
            }
        });

    }

    private void update() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView stepText = (TextView) findViewById(R.id.textStepCount);
                stepText.setText((int) counter.getData() + " " + getString(R.string.steps));
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(isFinishing()) {
            counter.close();
            distanceManager.close();
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

        state.putFloat("Distance", distanceManager.getData());
        state.putInt("InitialSteps", counter.getInitialSteps());
        state.putBoolean("FirstSteps", counter.getFirstSteps());
        state.putBoolean("Initialized", this.initialized);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);

        distanceManager.setDistance(state.getFloat("Distance"));
        counter.setInitialSteps(state.getInt("InitialSteps"));
        this.initialized = state.getBoolean("Initialized");
        counter.setFirstSteps(state.getBoolean("FirstSteps"));
    }
}
