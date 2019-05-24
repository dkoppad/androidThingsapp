package edu.pdx.rsurya07.thingsintro;

import android.os.Bundle;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;


import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Timestamp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String RED_INDEX = "red";
    private static final String BLUE_INDEX = "blue";
    private static final String GREEN_INDEX = "green";
    private static final String MOTOR_SPEED = "green";

    private SeekBar mRedControl;
    private SeekBar mBlueControl;
    private SeekBar mGreenControl;
    private ProgressBar mMotorSpeed;
    private TextView mMSpeed;
    private TextView madc1;
    private TextView madc2;
    private TextView madc3;

    private TextView mpwm3;
    private TextView mTemp;
    private EditText mdac;
    private TextView mTimestamp;

    private Handler handler = new Handler();

    private int pwm0 = 0;
    private int pwm1 = 0;
    private int pwm2 = 0;
    private int dac;

    private String pwm3_iot = String.valueOf(0);
    private String adc1_iot = String.valueOf(0);
    private String adc2_iot = String.valueOf(0);
    private String adc3_iot = String.valueOf(0);
    private String Temp_iot = String.valueOf(0);
    private int status = 0;
    private String mtemp =String.valueOf(0);
    Timestamp ts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "OnCreate called");
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            pwm0 = savedInstanceState.getInt(RED_INDEX, 0);
            pwm1 = savedInstanceState.getInt(GREEN_INDEX, 0);
            pwm2 = savedInstanceState.getInt(BLUE_INDEX, 0);
            pwm3_iot = savedInstanceState.getString(MOTOR_SPEED, String.valueOf(0));
        }
        /*
        Initial Values

        Set values initially to zero when the app starts up for the first time
        so that the LEDs are not lit.
         */
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference().child("pwm0").setValue(pwm0);
        database.getReference().child("pwm1").setValue(pwm1);
        database.getReference().child("pwm2").setValue(pwm2);
        database.getReference().child("dac1").setValue(dac);


        getDataInit();

        madc1 = (TextView) findViewById(R.id.adc1);
        madc1.setText(adc1_iot);

        madc2 = (TextView) findViewById(R.id.adc2);
        madc2.setText(adc2_iot);

        madc3 = (TextView) findViewById(R.id.adc3);
        madc3.setText(adc3_iot);

        mTemp = (TextView) findViewById(R.id.temp);
        mTemp.setText(Temp_iot);

        mdac = (EditText) findViewById(R.id.dac);
        mdac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dac = Integer.parseInt(String.valueOf(mdac));
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                if((dac > 0) && (dac < 31)) {
                    database.getReference().child("dac1").setValue(dac);

                }
                else {
                    Toast.makeText(MainActivity.this, "Out of range input!" , Toast.LENGTH_SHORT).show();
                }
            }
        });

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
                database.getReference().child("pwm0").setValue(pwm0);
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
                database.getReference().child("pwm1").setValue(pwm1);
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
                database.getReference().child("pwm2").setValue(pwm2);
            }
        });


        /*
        Motor Speed Progress Bar
         */
        mMotorSpeed = (ProgressBar) findViewById(R.id.progressBar);
        mMSpeed = (TextView) findViewById(R.id.motorSpeed);
        status = Integer.parseInt(pwm3_iot);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (status < 100) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mMotorSpeed.setProgress(status);
                            mMSpeed.setText(mMSpeed + "/ 100");

                        }
                    });
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();


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

    private void getDataInit() {
        ValueEventListener dataListner = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Get data from database
                pwm3_iot = dataSnapshot.child("pwm3").getValue().toString();
                adc1_iot = dataSnapshot.child("adc3").getValue().toString();
                adc2_iot = dataSnapshot.child("adc4").getValue().toString();
                adc3_iot = dataSnapshot.child("adc5").getValue().toString();
                Temp_iot = dataSnapshot.child("Temp").getValue().toString();
                //ts = dataSnapshot.child("Timestamp").getValue().toString();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }


}
