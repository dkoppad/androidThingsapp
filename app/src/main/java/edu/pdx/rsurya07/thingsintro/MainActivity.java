package edu.pdx.rsurya07.thingsintro;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;


import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String RED_INDEX = "red";
    private static final String BLUE_INDEX = "blue";
    private static final String GREEN_INDEX = "green";

    private SeekBar mRedControl;
    private SeekBar mBlueControl;
    private SeekBar mGreenControl;

    private int pwm0 = 0;
    private int pwm1 = 0;
    private int pwm2 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "OnCreate called");
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            pwm0 = savedInstanceState.getInt(RED_INDEX, 0);
            pwm1 = savedInstanceState.getInt(GREEN_INDEX, 0);
            pwm2 = savedInstanceState.getInt(BLUE_INDEX, 0);
        }
        /*
        Initial Values

        Set values initially to zero when the app starts up for the first time
        so that the LEDs are not lit.
         */
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference().child("RBrightness").setValue(pwm0);
        database.getReference().child("GBrightness").setValue(pwm1);
        database.getReference().child("BBrightness").setValue(pwm2);
    /*
        Red Control Seek Bar
         */
        mRedControl = (SeekBar) findViewById(R.id.redbar);
        mRedControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                pwm0 = progressValue;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(MainActivity.this, "Intensity:" + pwm0, Toast.LENGTH_SHORT).show();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                database.getReference().child("RBrightness").setValue(pwm0);
            }
        });

        /*
        Red Control Seek Bar
         */
        mGreenControl = (SeekBar) findViewById(R.id.greenbar);
        mGreenControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                pwm1 = progressValue;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(MainActivity.this, "Intensity:" + pwm1, Toast.LENGTH_SHORT).show();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                database.getReference().child("GBrightness").setValue(pwm1);
            }
        });

        /*
        Blue Control Seek Bar
         */
        mBlueControl = (SeekBar) findViewById(R.id.bluebar);
        mBlueControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                pwm2 = progressValue;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(MainActivity.this, "Intensity:" + pwm2, Toast.LENGTH_SHORT).show();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                database.getReference().child("BBrightness").setValue(pwm2);
            }
        });

    }


    /*
   Save the current values of the LEDs so when the screen rotates,
   the information is saved.
    */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "OnSaveInstanceState");
        savedInstanceState.putInt(RED_INDEX, pwm0);
        savedInstanceState.putInt(GREEN_INDEX, pwm1);
        savedInstanceState.putInt(BLUE_INDEX, pwm2);
    }
}
