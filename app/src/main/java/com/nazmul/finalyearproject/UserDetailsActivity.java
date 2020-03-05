package com.nazmul.finalyearproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class UserDetailsActivity extends AppCompatActivity {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    private Button btn_emergency,btn_hospital,btn_school,btn_restaurant,btn_ambulance,btn_home;
    TextView output;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);


        btn_emergency = findViewById(R.id.policeBT);
        btn_hospital = findViewById(R.id.hospitalBT);
        btn_school = findViewById(R.id.schoolBT);
        btn_restaurant = findViewById(R.id.restaurantBT);
        btn_home = findViewById(R.id.houseBT);
        output = findViewById(R.id.txt_output);

        btn_emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phoneNumber ="999";
                Uri uri = Uri.parse("tel:"+phoneNumber);
                Intent intent = new Intent(Intent.ACTION_CALL,uri);
                startActivity(intent);

            }
        });

        btn_hospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UserDetailsActivity.this,"Hospital",Toast.LENGTH_LONG).show();

            }
        });

        btn_restaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri gmmIntentUri = Uri.parse("geo:0,0?q=restaurants");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });





        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onShake(int count) {
                /*
                 * The following method, "handleShakeEvent(count):" is a stub //
                 * method you would use to setup whatever you want done once the
                 * device has been shook.
                 */
                handleShakeEvent(count);



            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void handleShakeEvent(int count) {

        initiat();

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initiat(){

        String phoneNumber ="01834462681";
        Uri uri = Uri.parse("tel:"+phoneNumber);
        Intent intent = new Intent(Intent.ACTION_CALL,uri);
        if (intent.resolveActivity(getPackageManager())!= null){
            if (checkPhonePermission()){
                startActivity(intent);
            }
        }
        else {
            Toast.makeText(this,"No compound found",Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean checkPhonePermission(){
        String [] permission ={Manifest.permission.CALL_PHONE};

        if (checkSelfPermission(Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(permission,999);
            return false;
        }
        return true;

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 999 && grantResults [0] ==PackageManager.PERMISSION_GRANTED ){
            initiat();

        }
    }

    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

    public void btnSpeech(View view) {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Hi Speak Something");

        try {
            startActivityForResult(intent,1);
        }
        catch (ActivityNotFoundException e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 1:
                if (resultCode==RESULT_OK && null!=data){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    output.setText(result.get(0));

                    boolean ans = result.contains("help");
                    if (ans){
                        initiat();
                    }
                    else {
                        Toast.makeText(this, "Not find hello", Toast.LENGTH_SHORT).show();
                    }

                }

                break;
        }
    }
}
