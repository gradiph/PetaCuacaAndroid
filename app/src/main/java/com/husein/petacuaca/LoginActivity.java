package com.husein.petacuaca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void login(View view) {
        //Get input data
        EditText emailInput = findViewById(R.id.login_email_input);
        String email = emailInput.getText().toString();

        EditText passwordInput = findViewById(R.id.login_password_input);
        String password = passwordInput.getText().toString();

        //get url, client_id, and client_secret from strings.xml
        String url = getString(R.string.url) + "api/login";
        String client_id = getString(R.string.client_id);
        String client_secret = getString(R.string.client_secret);

        //Register
        Map<String, String> params = new HashMap<>();
        params.put("grant_type", "password");
        params.put("username", email);
        params.put("password", password);
        params.put("client_id", client_id);
        params.put("client_secret", client_secret);

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

                            //set shared preferences
                            SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                            SharedPreferences.Editor myEdit = sh.edit();

                            myEdit.putString("user_id", response.getJSONObject("data").getJSONObject("user").getString("id"));
                            myEdit.putString("name", response.getJSONObject("data").getJSONObject("user").getString("name"));
                            myEdit.putString("access_token", response.getJSONObject("data").getString("access_token"));

                            myEdit.apply();

                            //check whether the user is verified
                            if (response.getJSONObject("data").getJSONObject("user").getString("email_verified_at").equals("null"))
                            {
                                startActivity(new Intent(getApplicationContext(), EmailVerificationActivity.class));
                            }
                            else
                            {
                                startActivity(new Intent(getApplicationContext(), InputActivity.class));
                            }
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

    public void openResetPasswordActivity(View view) {
        startActivity(new Intent(this, ResetPasswordActivity.class));
    }
}
