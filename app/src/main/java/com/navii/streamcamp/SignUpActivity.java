package com.navii.streamcamp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        View backButton = findViewById(R.id.ivBack);
        EditText editEmail = findViewById(R.id.editEmail);
        EditText editPassword = findViewById(R.id.editPassword);
        EditText editUsername = findViewById(R.id.editUserName);
        View btnSignUp = findViewById(R.id.ftSignup);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpActivity.super.onBackPressed();
            }
        });


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAccount(editUsername.getText().toString(),editEmail.getText().toString(),editPassword.getText().toString());
            }
        });
    }


    public void saveAccount(String userName,String email,String password){
        SharedPreferences sharedPreferences = getSharedPreferences("STREAMCAMP", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String userID = UUID.randomUUID().toString();
        editor.putString("USERID", userID);
        editor.putString("EMAIL", email);
        editor.putString("USERNAME", userName);


        editor.commit();
        OkHttpClient client = new OkHttpClient();
        String ROOT_URL = "http://34.70.130.106:8000/api/";

        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        JSONObject postData = new JSONObject();
        try {
            postData.put("username", userName);
            postData.put("userId", userID);
            postData.put("email",  email ) ;
            postData.put("title", "Camper");
            postData.put("profile_name", "Camper");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody =  RequestBody.create(JSON,postData.toString());
        Request request = new Request.Builder().url(ROOT_URL + "campfires/campers/").post(requestBody).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    ResponseBody myResponse = response.body();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Log.d("CAMPERS", String.valueOf(myResponse));
                            JSONObject Jobject = null;
                            try {
                                JSONObject arrResponse = new JSONObject(myResponse.string());

//                                Jobject = new JSONObject(arrResponse.get(0).toString());
//                                Log.d("Jobject",Jobject.toString());
//                                JSONArray Jarray = Jobject.getJSONArray("employees");
                                Log.wtf("arrResponse.length()",arrResponse.toString());
                                if(arrResponse.length()>0){
                                    Log.wtf("arrResponse", String.valueOf(arrResponse));

                                    SharedPreferences sharedPreferences = getSharedPreferences("STREAMCAMP", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();

                                    editor.putString("TITLE", String.valueOf(arrResponse.getString("title")));



                                    editor.commit();

                                }

                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }

                            Intent activity2Intent = new Intent(getApplicationContext(), MainActivity.class);
                            activity2Intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            finish();
                            startActivity(activity2Intent);

                        }
                    });
                }else{
                    Log.wtf("API ERROR","CREATE USER");
                    Log.wtf("API ERROR",response.body().string());

                }
            }
        });
    }
}