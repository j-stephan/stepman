package de.tu_dresden.carebears.stepman;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by jan on 09.12.15.
 */
public class StepCounter {

    private static StepCounter instance;

    private SensorManager mSensorManager;
    private Sensor stepCounter;
    private Context context;
    private StepListener stepListener;

    private boolean initialized;
    private int steps;

    private StepCounter(Context ctx) {
        this.context = ctx;
        this.steps = 0;
        this.initialized = false;
    }

    public static StepCounter getInstance(Context ctx) {
        if(instance == null) {
            instance = new StepCounter(ctx);
        }

        return instance;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public boolean initialize() {
        mSensorManager = (SensorManager) this.context.getSystemService(Context.SENSOR_SERVICE);
        stepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        this.stepListener = new StepListener();

        if (stepCounter != null) {
            if(!mSensorManager.registerListener(stepListener,stepCounter,  SensorManager.SENSOR_DELAY_NORMAL)) {
                return false;
            }
            return true;
        }

        return false;
    }

    public void close() {
        mSensorManager.unregisterListener(stepListener);
        steps = 0;
        initialized = false;
    }

    private void addSteps(int steps){
        this.steps += steps;
    }

    public int getSteps() {
        return steps;
    }

    public void reset() {
        steps = 0;
    }

    private class StepListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            instance.addSteps(Math.round(event.values[0]));
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }
}
