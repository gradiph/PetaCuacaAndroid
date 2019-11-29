package com.husein.petacuaca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void register(View view) {
        //Get input data
        EditText nameInput = findViewById(R.id.register_name_input);
        String name = nameInput.getText().toString().trim();

        EditText emailInput = findViewById(R.id.register_email_input);
        String email = emailInput.getText().toString().trim();

        EditText passwordInput = findViewById(R.id.register_password_input);
        String password = passwordInput.getText().toString().trim();

        EditText passwordConfirmationInput = findViewById(R.id.register_password_confirmation_input);
        String password_confirmation = passwordConfirmationInput.getText().toString().trim();

        //get url from strings.xml
        String url = getString(R.string.url) + "api/register";

        //Register
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("email", email);
        params.put("password", password);

        params.put("password_confirmation", password_confirmation);

        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    public void openLoginActivity(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }
}
