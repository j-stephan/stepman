package de.tu_dresden.carebears.stepman;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class StepLengthActivity extends AppCompatActivity {

    private DistanceManager distanceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_length);

        distanceManager = DistanceManager.getInstance();
    }
}
