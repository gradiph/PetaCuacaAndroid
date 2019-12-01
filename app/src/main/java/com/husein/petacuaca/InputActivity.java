package com.husein.petacuaca;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        List<String> depart = Arrays.asList(depart_input.getText().toString().split("\\s*,\\s*"));
        String depart_lat = depart.get(0);
        String depart_lng = depart.get(1);

        EditText destination_input = findViewById(R.id.input_destination_input);
        List<String> destination = Arrays.asList(destination_input.getText().toString().split("\\s*,\\s*"));
        String destination_lat = destination.get(0);
        String destination_lng = destination.get(1);

        EditText name_input = findViewById(R.id.input_name_input);
        String name = name_input.getText().toString();

        Switch type_input = findViewById(R.id.input_type_input);
        String type = type_input.isChecked() ? "1" : "0";

        EditText time_input = findViewById(R.id.input_time_input);
        String time = time_input.getText().toString();

        //get shared preferences
        final SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);

        //get url from strings.xml
        String url = getString(R.string.url) + "api/route/recommendation";

        //search route
        Map<String, String> params = new HashMap<>();
        params.put("route_history_id", "8");
        params.put("user_id", sh.getString("user_id", null));
        params.put("name", name);
        params.put("start_lat", depart_lat);
        params.put("start_lng", depart_lng);
        params.put("end_lat", destination_lat);
        params.put("end_lng", destination_lng);
        params.put("arrival_time", type);
        params.put("time", "2019-12-02 14:00:00");

        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Set Intent
                            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                            intent.putExtra("data", response.getJSONObject("data").toString());
                            startActivity(intent);
                            Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NetworkError) {
                        } else if (error instanceof ServerError) {
                            Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                        } else if (error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                        } else if (error instanceof TimeoutError) {
                            Toast.makeText(getApplicationContext(),"Waktu habis! Silakan coba lagi.",Toast.LENGTH_LONG).show();
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put("Authorization", "Bearer " + sh.getString ("access_token", ""));

                return params;
            }
        };

        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
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
