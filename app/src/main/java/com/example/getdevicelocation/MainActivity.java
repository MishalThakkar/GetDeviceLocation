package com.example.getdevicelocation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient client;
    private static final int SEND_SMS_PERMISSION_CODE = 111;
    Button button;
    TextView textView;
    String message,latitude,longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermission();
        textView = findViewById(R.id.location);
        client = LocationServices.getFusedLocationProviderClient(this);

        button = findViewById(R.id.getLocation);
        if (checkPermission(android.Manifest.permission.SEND_SMS))
        {
            button.setEnabled(true);
        }else
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},SEND_SMS_PERMISSION_CODE);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {

                    return;
                }

                client.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        if(location!= null){

                            String retrievelocation = location.toString();
                            latitude = retrievelocation.substring(15,24);
                            longitude = retrievelocation.substring(25,34);
                            textView.setText("Latitude = " + latitude + "  " + "  Lonigude = "+longitude);
                            //textView.setText(location.toString());
                            message = "http://maps.google.com?q="+latitude+","+longitude;
                        }
                        if(checkPermission(Manifest.permission.SEND_SMS))
                        {

                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage("9909677576",null,message,null,null);
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Permission Denied",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
    }
    private boolean checkPermission(String permission)
    {
        int checkPermission = ContextCompat.checkSelfPermission(this,permission);
        return checkPermission == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode){
            case SEND_SMS_PERMISSION_CODE :
                if(grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED))
                    button.setEnabled(true);
        }
    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
    }
}
