package com.navii.streamcamp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExploreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExploreFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private List<ExploreHelperClass> videoList;
    private List<ExploreItemHelperClass> videoItemList;
    private  ExploreAdapter adapter;
    private  ExploreItemAdapter itemAdapter;
    private Handler videoHandler = new Handler();
    public ExploreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExploreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExploreFragment newInstance(String param1, String param2) {
        ExploreFragment fragment = new ExploreFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);



        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



    }


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
    RecyclerView exploreRecycler;
    RecyclerView exploreItemRecycler;
    RecyclerView exploreItemRecycler2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_explore, container, false);
        videoList = new ArrayList<>();
        videoItemList = new ArrayList<>();

        exploreRecycler = view.findViewById(R.id.exploreRecycler);
        exploreRecycler.setHasFixedSize(true);
        exploreRecycler.setLayoutManager( new LinearLayoutManager( getActivity(),LinearLayoutManager.HORIZONTAL,false));


//        videoList.add(new Video("android.resource://" + getPackageName() +"/" +R.raw.a,"New Title","Sample contents for  all."));
//        videoList.add(new Video("android.resource://" + getPackageName() +"/" +R.raw.b,"New Title","Sample contents for  all."));
//        videoList.add(new Video("android.resource://" + getPackageName() +"/" +R.raw.c,"New Title","Sample contents for  all."));
        videoList.add(new ExploreHelperClass("https://assets.mixkit.co/videos/preview/mixkit-behind-the-scenes-of-a-speaker-talking-on-camera-34486-large.mp4","Film Editing Basics","Adobe Premiere Pro Beginner to Advance"));
        videoList.add(new ExploreHelperClass("https://assets.mixkit.co/videos/preview/mixkit-tree-with-yellow-flowers-1173-large.mp4","Nature Photography","Oh I love Nature"));
        videoList.add(new ExploreHelperClass("https://assets.mixkit.co/videos/preview/mixkit-father-and-his-little-daughter-eating-marshmallows-in-nature-39765-large.mp4","Social Media Marketing","Lets do it social."));
        videoList.add(new ExploreHelperClass("https://assets.mixkit.co/videos/preview/mixkit-cheerful-man-moves-forward-dancing-in-the-middle-of-nature-32746-large.mp4","Home Workout Routines","Sample contents for  all."));
        videoList.add(new ExploreHelperClass("https://assets.mixkit.co/videos/preview/mixkit-urban-trendy-mans-portrait-with-blue-background-1239-large.mp4","New Title","Sample contents for  all."));
        videoList.add(new ExploreHelperClass("https://assets.mixkit.co/videos/preview/mixkit-man-runs-past-ground-level-shot-32809-large.mp4","New Title","Sample contents for  all."));
        videoList.add(new ExploreHelperClass("https://assets.mixkit.co/videos/preview/mixkit-hands-holding-a-smart-watch-with-the-stopwatch-running-32808-large.mp4","New Title","Sample contents for  all."));

        adapter = new ExploreAdapter(videoList);
        exploreRecycler.setAdapter(adapter);


        exploreItemRecycler = view.findViewById(R.id.exploreRecyclerBoot);
        exploreItemRecycler.setHasFixedSize(true);
        exploreItemRecycler.setLayoutManager( new LinearLayoutManager( getActivity(),LinearLayoutManager.HORIZONTAL,false));


//        videoList.add(new Video("android.resource://" + getPackageName() +"/" +R.raw.a,"New Title","Sample contents for  all."));
//        videoList.add(new Video("android.resource://" + getPackageName() +"/" +R.raw.b,"New Title","Sample contents for  all."));
//        videoList.add(new Video("android.resource://" + getPackageName() +"/" +R.raw.c,"New Title","Sample contents for  all."));
        videoItemList.add(new ExploreItemHelperClass("https://assets.mixkit.co/videos/preview/mixkit-behind-the-scenes-of-a-speaker-talking-on-camera-34486-large.mp4","New Title","Sample contents for  all."));
        videoItemList.add(new ExploreItemHelperClass("https://assets.mixkit.co/videos/preview/mixkit-tree-with-yellow-flowers-1173-large.mp4","New Title","Sample contents for  all."));
        videoItemList.add(new ExploreItemHelperClass("https://assets.mixkit.co/videos/preview/mixkit-father-and-his-little-daughter-eating-marshmallows-in-nature-39765-large.mp4","New Title","Sample contents for  all."));
        videoItemList.add(new ExploreItemHelperClass("https://assets.mixkit.co/videos/preview/mixkit-cheerful-man-moves-forward-dancing-in-the-middle-of-nature-32746-large.mp4","New Title","Sample contents for  all."));
        videoItemList.add(new ExploreItemHelperClass("https://assets.mixkit.co/videos/preview/mixkit-urban-trendy-mans-portrait-with-blue-background-1239-large.mp4","New Title","Sample contents for  all."));
        videoItemList.add(new ExploreItemHelperClass("https://assets.mixkit.co/videos/preview/mixkit-man-runs-past-ground-level-shot-32809-large.mp4","New Title","Sample contents for  all."));
        videoItemList.add(new ExploreItemHelperClass("https://assets.mixkit.co/videos/preview/mixkit-hands-holding-a-smart-watch-with-the-stopwatch-running-32808-large.mp4","New Title","Sample contents for  all."));


        itemAdapter = new ExploreItemAdapter(videoItemList);
        exploreItemRecycler.setAdapter(itemAdapter);


        exploreItemRecycler2 = view.findViewById(R.id.exploreRecyclerBootB);
        exploreItemRecycler2.setHasFixedSize(true);
        exploreItemRecycler2.setLayoutManager( new LinearLayoutManager( getActivity(),LinearLayoutManager.HORIZONTAL,false));
        exploreItemRecycler2.setAdapter(itemAdapter);

        return  view;

    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


}