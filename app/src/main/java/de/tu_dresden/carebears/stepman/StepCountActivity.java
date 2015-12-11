package de.tu_dresden.carebears.stepman;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class StepCountActivity extends AppCompatActivity {
    private StepCounter counter;
    private Timer updateTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_count);

        this.counter = StepCounter.getInstance(this);
        if(!counter.initialize()) {
            Toast.makeText(this, getString(R.string.init_step_sensor_fail), Toast.LENGTH_LONG).show();
        }

        this.updateTimer = new Timer();
        this.updateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                update();
            }
        }, 0, 1000);

    }

    private void update() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView stepText = (TextView) findViewById(R.id.textStepCount);
                stepText.setText(counter.getSteps() + " " + getString(R.string.steps));
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        counter.close();
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

        //state.putFloat("Distance", distanceManager.getDistance());
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);

        //distanceManager.setDistance(state.getFloat("Distance"));
    }
}
