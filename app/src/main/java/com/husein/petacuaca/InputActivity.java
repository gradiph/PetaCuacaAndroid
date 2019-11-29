package com.husein.petacuaca;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

public class InputActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        /**
         * change the time_input hint
         */
        Switch type_input = findViewById(R.id.input_type_input);
        final EditText time_input = findViewById(R.id.input_time_input);

        type_input.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { if (isChecked) time_input.setHint(R.string.input_arrive_time_input);
            else time_input.setHint(R.string.input_depart_time_input);
            }
        });
    }

    public void searchRoute(View view) {
        //Get input data
        EditText depart_input = findViewById(R.id.input_depart_input);
        EditText destination_input = findViewById(R.id.input_destination_input);
        String depart = depart_input.getText().toString();

        String destination = destination_input.getText().toString();

        //search route


        //Set Intent
        Intent intent = new Intent(this, ResultActivity.class);
//        intent.putExtra("", getText());
        startActivity(intent);
    }

    public void setGPSDepart(View view) {
        EditText depart_input = findViewById(R.id.input_depart_input);
        depart_input.setText(getGPS());
    }

    public void switchDepartDestination(View view) {
        EditText depart_input = findViewById(R.id.input_depart_input);
        EditText destination_input = findViewById(R.id.input_destination_input);
        Editable temp = depart_input.getText();

        depart_input.setText(destination_input.getText());
        destination_input.setText(temp);
    }

    public void setGPSDestination(View view) {
        EditText destination_input = findViewById(R.id.input_destination_input);
        destination_input.setText(getGPS());
    }

    public String getGPS () {
        Location gps_loc;
        Location network_loc;
        Location final_loc;
        double longitude;
        double latitude;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        gps_loc = null;
        network_loc = null;
        try {
            gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            network_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (gps_loc != null) {
            final_loc = gps_loc;
            latitude = final_loc.getLatitude();
            longitude = final_loc.getLongitude();
        }
        else if (network_loc != null) {
            final_loc = network_loc;
            latitude = final_loc.getLatitude();
            longitude = final_loc.getLongitude();
        }
        else {
            latitude = 0.0;
            longitude = 0.0;
        }
        return latitude + ", " + longitude;
    }
}
