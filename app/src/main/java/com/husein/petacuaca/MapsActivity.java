package com.husein.petacuaca;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        JSONObject route = null;
        Double depart_lat = null;
        Double depart_lng = null;
        String depart_name = null;
        Double destination_lat = null;
        Double destination_lng = null;
        String destination_name = null;

        mMap = googleMap;

        //set route from intent
        try {
            route = new JSONObject(getIntent().getStringExtra("data"));
            depart_lat = Double.parseDouble(route.getJSONArray("legs").getJSONObject(0).getJSONObject("start_location").getString("lat"));
            depart_lng = Double.parseDouble(route.getJSONArray("legs").getJSONObject(0).getJSONObject("start_location").getString("lng"));
            depart_name = route.getJSONArray("legs").getJSONObject(0).getString("start_address");
            destination_lat = Double.parseDouble(route.getJSONArray("legs").getJSONObject(0).getJSONObject("end_location").getString("lat"));
            destination_lng = Double.parseDouble(route.getJSONArray("legs").getJSONObject(0).getJSONObject("end_location").getString("lng"));
            destination_name = route.getJSONArray("legs").getJSONObject(0).getString("end_address");

            //set up map
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.setMinZoomPreference(12);

            // Add a marker
            LatLng depart_loc = new LatLng(depart_lat, depart_lng);
            LatLng destination_loc = new LatLng(destination_lat, destination_lng);
            mMap.addMarker(new MarkerOptions().position(depart_loc).title(depart_name));
            mMap.addMarker(new MarkerOptions().position(destination_loc).title(destination_name));
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(depart_loc, 17);
            mMap.animateCamera(cameraUpdate);

            //add polyline
            List<LatLng> list = null;
            JSONArray overview_polyline = route.getJSONArray("overview_polyline");
            for (int i = 0; i < overview_polyline.length() ; i++) {
                Double lat = Double.parseDouble(overview_polyline.getJSONObject(i).getString("lat"));
                Double lng = Double.parseDouble(overview_polyline.getJSONObject(i).getString("lng"));
                LatLng point = new LatLng(lat, lng);
                list.add(point);
            }

            PolylineOptions options = new PolylineOptions()
                    .width(2)
                    .color(Color.BLUE)
                    .addAll(list);
            mMap.clear();
            mMap.addPolyline(options);

            /**
            //add LatLngBounds
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
//            builder.include(depart_loc);
//            builder.include(destination_loc);
            for (LatLng latLng : list) {
                builder.include(latLng);
            }
            LatLngBounds bounds = builder.build();
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (height * 0.20);
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
            mMap.animateCamera(cu);
             */
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
