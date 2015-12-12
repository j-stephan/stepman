package de.tu_dresden.carebears.stepman;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by jan on 09.12.15.
 */
public class StepCounter implements SensorHandler{

    private static StepCounter instance;

    private SensorManager mSensorManager;
    private Sensor stepCounter;
    private Context context;
    private StepListener stepListener;

    private boolean initialized;
    private boolean firstSteps;
    private int steps;
    private int initialSteps;
    private String status;

    private StepCounter(Context ctx) {
        this.context = ctx;
        this.steps = 0;
        this.initialized = false;
        this.status = ctx.getString(R.string.sensor_not_initialized);
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

        this.firstSteps = true;

        if (stepCounter != null) {
            if(!mSensorManager.registerListener(stepListener,stepCounter,  SensorManager.SENSOR_DELAY_NORMAL)) {
                status = context.getString(R.string.init_step_sensor_fail);
                return false;
            }
            status = context.getString(R.string.sensor_initialized);
            return true;
        }

        status = context.getString(R.string.init_step_sensor_fail);
        return false;
    }

    public void close() {
        mSensorManager.unregisterListener(stepListener);
        steps = 0;
        initialized = false;
    }

    private void setSteps(int steps){
        this.steps = steps - this.initialSteps;
    }

    public float getData() {
        return steps;
    }

    public void reset() {
        initialSteps += steps;
        steps = 0;
    }

    @Override
    public String getStatusMessage() {
        return status;
    }

    private class StepListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            if(firstSteps){
                initialSteps = Math.round(event.values[0]);
                firstSteps = false;
            }
            instance.setSteps(Math.round(event.values[0]));
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }
}
