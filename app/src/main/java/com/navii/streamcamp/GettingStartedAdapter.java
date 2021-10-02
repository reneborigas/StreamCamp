package com.navii.streamcamp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.squareup.picasso.Picasso;

import java.util.List;

public class GettingStartedAdapter extends RecyclerView.Adapter<GettingStartedAdapter.VideoViewHolder> {

    List<GettingStarted> gettingStartedList;
    ViewPager2 viewPager2;
    public GettingStartedAdapter(List<GettingStarted >gettingStartedList, ViewPager2 viewPager2){
        this.gettingStartedList = gettingStartedList;
        this.viewPager2 = viewPager2;
    }


    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.each_steps,parent,false);
      return  new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        holder.setVideoData(gettingStartedList.get(position));

    }

    @Override
    public int getItemCount() {
        return gettingStartedList.size();
    }

    public class VideoViewHolder extends  RecyclerView.ViewHolder{
         ImageView ivSlide;

        ProgressBar progressBar;
        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);

            ivSlide = itemView.findViewById(R.id.ivSlide);

//            progressBar = itemView.findViewById(R.id.videoProgressBar);


        }

        public void setVideoData(GettingStarted gettingStarted ){
            if (gettingStarted.getpage() == "1"){
                ivSlide.setImageResource(R.drawable.step_1);
            }
            if (gettingStarted.getpage() == "2"){
                ivSlide.setImageResource(R.drawable.step_2);
            }

            if (gettingStarted.getpage() == "3"){
                ivSlide.setImageResource(R.drawable.step_3);
            }
            if (gettingStarted.getpage() == "A"){
                ivSlide.setImageResource(R.drawable.slide_a);
            }
            if (gettingStarted.getpage() == "B"){
                ivSlide.setImageResource(R.drawable.slide_b);
            }

            if (gettingStarted.getpage() == "C"){
                ivSlide.setImageResource(R.drawable.slide_c);
            }
        }
    }

}
