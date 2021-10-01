package com.navii.streamcamp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class LoginActivity extends AppCompatActivity {
    private static final String EMAIL = "email";
    private static String userID = "userID";
    Context context;
    CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_login);
//        FacebookSdk.sdkInitialize(getApplicationContext());
//        AppEventsLogger.activateApp(this);

        context = getApplicationContext();


        View view =  findViewById(R.id.continue_with_fb);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(
                         LoginActivity.this ,
                        Arrays.asList("user_photos", "email", "user_birthday", "public_profile")
                );
            }
        });

        View btnLoginPhoneEmail =  findViewById(R.id.flLoginPhoneEmail);
        btnLoginPhoneEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activity2Intent = new Intent(getApplicationContext(), SignUpActivity.class);

                startActivity(activity2Intent);
            }
        });


        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(
                callbackManager,
                new FacebookCallback < LoginResult > () {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // Handle success
                        userID = loginResult.getAccessToken().getUserId();
                        GraphRequest request = GraphRequest.newMeRequest(
                                AccessToken.getCurrentAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse response) {
                                        String last_name = null;
                                        String gender = null;
                                        String birthday = null;
                                        String image_url = null;
                                        String email = null;
                                        String first_name = null;
                                        String id = null;
                                        try {
                                            Log.d("LOG_TAG", "fb json object: " + object);
                                            Log.d("LOG_TAG", "fb graph response: " + response);

                                            id = object.getString("id");
                                            first_name = object.getString("first_name");
                                            last_name = object.getString("last_name");
                                            gender = object.getString("gender");
                                            birthday = object.getString("birthday");
                                            image_url = "http://graph.facebook.com/" + id + "/picture?type=large";

                                            if (object.has("email")) {
                                                email = object.getString("email");
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        SharedPreferences sharedPreferences = getSharedPreferences("STREAMCAMP", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("USERID", id);
                                        editor.putString("FIRST_NAME", first_name);
                                        editor.putString("LAST_NAME", last_name);
                                        editor.putString("GENDER", gender);
                                        editor.putString("BIRTHDAY", birthday);
                                        editor.putString("IMAGE_URL", image_url);
                                        editor.putString("EMAIL", email);

                                        editor.commit();
                                        saveAccount();
                                    }
                                });

                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,first_name,last_name,email,gender,birthday"); // id,first_name,last_name,email,gender,birthday,cover,picture.type(large)
                        request.setParameters(parameters);
                        request.executeAsync();




                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException exception) {
                    }
                }
        );
    }
    public void saveAccount(){


        OkHttpClient client = new OkHttpClient();
        String ROOT_URL = "http://34.70.130.106:8000/api/";

        SharedPreferences sharedPreferences = getSharedPreferences("STREAMCAMP", MODE_PRIVATE);
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        JSONObject postData = new JSONObject();
        try {
            postData.put("username", UUID.randomUUID().toString());
            postData.put("userId", userID);
            postData.put("profile_name", sharedPreferences.getString("FIRST_NAME","") +" "+ sharedPreferences.getString("LAST_NAME","") ) ;
            postData.put("title", "Camper");

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
                                JSONArray arrResponse = new JSONArray(myResponse.string());

//                                Jobject = new JSONObject(arrResponse.get(0).toString());
//                                Log.d("Jobject",Jobject.toString());
//                                JSONArray Jarray = Jobject.getJSONArray("employees");
                                Log.wtf("arrResponse.length()",arrResponse.toString());
                                if(arrResponse.length()>0){
                                    Log.wtf("arrResponse", String.valueOf(arrResponse.getJSONObject(0)));

                                }

                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }

                            Intent activity2Intent = new Intent(getApplicationContext(), MainActivity.class);
                            activity2Intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            finish();
                            context.startActivity(activity2Intent);

                        }
                    });
                }else{
                    Log.wtf("API ERROR","CREATE USER");
                    Log.wtf("API ERROR",response.body().string());

                }
                client.dispatcher().executorService().shutdown();

            }
        });
    }
    public  void backPress(View view){
        super.onBackPressed();
    }

    // this part was missing thanks to wesely
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}