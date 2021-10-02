package com.navii.streamcamp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SelectCampsActivity extends AppCompatActivity {
    private ViewPager2 viewPager2;
    private List<GettingStarted> gettingStartedList;
    private  GettingStartedAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_camps);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ;
        SharedPreferences sharedPreferences = getSharedPreferences("STREAMCAMP", MODE_PRIVATE);
        SharedPreferences.Editor editor =  sharedPreferences.edit();
        editor.putString("LOGGED_IN", "1");

        editor.commit();
        View view = findViewById(R.id.btnDone);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activity2Intent = new Intent(getApplicationContext(), MainActivity.class);
                activity2Intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                finish();
                startActivity(activity2Intent);
            }
        });
        loadSlides();
    }
    private void loadSlides(){
        gettingStartedList = new ArrayList<>();

        viewPager2 = findViewById(R.id.viewPager2Slides);


        gettingStartedList.add(new GettingStarted("A"));
        gettingStartedList.add(new GettingStarted("B"));
        gettingStartedList.add(new GettingStarted("C"));


        adapter = new GettingStartedAdapter(gettingStartedList,viewPager2);
        viewPager2.setAdapter(adapter);
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
    }
}