package de.tu_dresden.carebears.stepman;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

/**
 * Created by jan on 09.12.15.
 */
public class StepCounter {

    private static StepCounter instance;

    private SensorManager mSensorManager;
    private Sensor stepCounter;
    private Context ctx;

    private StepCounter(Context ctx) {
        mSensorManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
        stepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    }

    public static StepCounter getInstance(Context ctx) {
        if(instance == null)
            instance = new StepCounter(ctx);

        return instance;
    }
}
