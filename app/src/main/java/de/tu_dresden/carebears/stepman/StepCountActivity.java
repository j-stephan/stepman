package de.tu_dresden.carebears.stepman;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class StepCountActivity extends AppCompatActivity {

    private SensorManager mSensorManager;
    private Sensor stepCounter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_count);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        //if(!mSensorManager.registerListener(stepCounterListener,stepCounter,10)) {
            // warn
        //}
    }
}
