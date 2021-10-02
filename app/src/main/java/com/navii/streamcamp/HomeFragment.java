package com.navii.streamcamp;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ViewPager2 viewPager2;
    private List<Video> videoList;
    private  VideoAdapter adapter;
    private Handler videoHandler = new Handler();
    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);





        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



    } private Runnable videoRunnable =  new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
        }
    };

//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        videoHandler.removeCallbacks(videoRunnable);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        videoHandler.postDelayed(videoRunnable,10000);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        videoList = new ArrayList<>();
        viewPager2 = view.findViewById(R.id.viewPager2);
        loadCampFires();
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                videoHandler.removeCallbacks(videoRunnable);
                videoHandler.postDelayed(videoRunnable,10000);
            }
        });



       return  view;

    }
    public void loadCampFires(){
//        Request request = new Request.Builder()
//                .url(url)
//                .post(requestBody)
//                .build();

        MediaType   JSON = MediaType.parse("application/json;charset=utf-8");

//        JSONObject postData = new JSONObject();RequestBody requestBody =  RequestBody.create(JSON,postData.toString());
//        Request request = new Request.Builder().url(URL).post().build();
        OkHttpClient client = new OkHttpClient();
        String ROOT_URL = "http://34.70.130.106:8000/api/";

        Request request = new Request.Builder().url(ROOT_URL + "campfires/campfires/").build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    ResponseBody myResponse = response.body();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            videoList = new ArrayList<>();
                            Log.d("CAMPFIRES", String.valueOf(myResponse));
                            JSONObject Jobject = null;
                            try {
                                JSONArray arrResponse = new JSONArray(myResponse.string());
//                                Jobject = new JSONObject(arrResponse.get(0).toString());
                                Log.d("Jobject",myResponse.toString());


//                                JSONArray Jarray = Jobject.getJSONArray("employees");

                                for (int i = 0; i < arrResponse.length(); i++) {
                                    JSONObject object     = arrResponse.getJSONObject(i);
                                    JSONObject campMaster = object.getJSONObject("campMaster");
                                    Log.wtf("campMaster",campMaster.toString());

                                    videoList.add(new Video(object.getString("videoUrl"),
                                            object.getString("name"),object.getString("subtitle"),campMaster.getString("userId"),object.getString("id"),campMaster.getString("name"),campMaster.getString("title"),object.getBoolean("isPremium"),object.getString("imageFile")));

                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }

                            adapter = new VideoAdapter(videoList,viewPager2);
                            viewPager2.setAdapter(adapter);
                            viewPager2.setClipToPadding(false);
                            viewPager2.setClipChildren(false);
                            viewPager2.setOffscreenPageLimit(3);
                            viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

                        }
                    });
                }else{
                    videoList = new ArrayList<>();

                    //        videoList.add(new Video("android.resource://" + getPackageName() +"/" +R.raw.a,"New Title","Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum"));

//                    videoList.add(new Video("https://assets.mixkit.co/videos/preview/mixkit-behind-the-scenes-of-a-speaker-talking-on-camera-34486-large.mp4","New Title","Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum","1"));
//                    videoList.add(new Video("https://assets.mixkit.co/videos/preview/mixkit-tree-with-yellow-flowers-1173-large.mp4","New Title","Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum","1"));
//                    videoList.add(new Video("https://assets.mixkit.co/videos/preview/mixkit-father-and-his-little-daughter-eating-marshmallows-in-nature-39765-large.mp4","New Title","Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum","1"));
//                    videoList.add(new Video("https://assets.mixkit.co/videos/preview/mixkit-cheerful-man-moves-forward-dancing-in-the-middle-of-nature-32746-large.mp4","New Title","Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum","1"));
//                    videoList.add(new Video("https://assets.mixkit.co/videos/preview/mixkit-urban-trendy-mans-portrait-with-blue-background-1239-large.mp4","New Title","Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum","1"));
//                    videoList.add(new Video("https://assets.mixkit.co/videos/preview/mixkit-man-runs-past-ground-level-shot-32809-large.mp4","New Title","Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum","1"));
//                    videoList.add(new Video("https://assets.mixkit.co/videos/preview/mixkit-hands-holding-a-smart-watch-with-the-stopwatch-running-32808-large.mp4","New Title","Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum","1"));
                    adapter = new VideoAdapter(videoList,viewPager2);
                    viewPager2.setAdapter(adapter);
                    viewPager2.setClipToPadding(false);
                    viewPager2.setClipChildren(false);
                    viewPager2.setOffscreenPageLimit(3);
                    viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
                }
            }
        });
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


}