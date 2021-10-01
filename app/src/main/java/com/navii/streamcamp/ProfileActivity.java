package com.navii.streamcamp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SharedPreferences sharedPreferences = getSharedPreferences("STREAMCAMP", MODE_PRIVATE);


        TextView txtCamper = findViewById(R.id.txtCamper);
        TextView txtTitle = findViewById(R.id.txtTitle);
        txtCamper.setText(  sharedPreferences.getString("USERNAME",""));
        txtTitle.setText(  sharedPreferences.getString("TITLE",""));
    }
}