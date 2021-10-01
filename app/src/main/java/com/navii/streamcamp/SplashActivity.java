package com.navii.streamcamp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;

public class SplashActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        callbackManager = CallbackManager.Factory.create();
//
//
//        LoginManager.getInstance().registerCallback(callbackManager,
//                new FacebookCallback<LoginResult>() {
//                    @Override
//                    public void onSuccess(LoginResult loginResult) {
//                        // App code
//                    }
//
//                    @Override
//                    public void onCancel() {
//                        // App code
//                    }
//
//                    @Override
//                    public void onError(FacebookException exception) {
//                        // App code
//                    }
//                });


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent nextIntent;
                SharedPreferences sharedPreferences = getSharedPreferences("STREAMCAMP", MODE_PRIVATE);
                String email = sharedPreferences.getString("EMAIL","");
                String username = sharedPreferences.getString("USERNAME","");

                Log.wtf("email",email);
                Log.wtf("email","email");
                if (email !=""){
                    nextIntent = new Intent(getApplicationContext(), MainActivity.class);
                    nextIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    finish();
                    startActivity(nextIntent);
                    return;
                }
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

                if (isLoggedIn){


                    nextIntent = new Intent(getApplicationContext(), MainActivity.class);
                }else{
                    nextIntent = new Intent(getApplicationContext(), LoginActivity.class);

                }

                nextIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                finish();
                startActivity(nextIntent);


            }
        } ,3000);
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        callbackManager.onActivityResult(requestCode, resultCode, data);
//        super.onActivityResult(requestCode, resultCode, data);
//    }
}