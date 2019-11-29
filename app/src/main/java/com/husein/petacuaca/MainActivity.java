package com.husein.petacuaca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //check user_id from shared preferences
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        if (sh.getString("user_id", "").equals("")) {
            startActivity (new Intent(this, InputActivity.class));
        }
    }

    public void openLoginActivity(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void openRegisterActivity(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }
}
