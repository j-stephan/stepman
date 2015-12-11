package de.tu_dresden.carebears.stepman;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class StepCountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_count);

        StepCounter counter = StepCounter.getInstance(this);
        if(!counter.initialize()) {
            //warn
        }




    }
}
