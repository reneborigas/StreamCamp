package com.navii.streamcamp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.squareup.picasso.Picasso;

import java.util.List;

public class SlideAdapter extends RecyclerView.Adapter<SlideAdapter.SlideViewHolder> {

    List<Slide> slideList;
    ViewPager2 viewPager2;
    public SlideAdapter(List<Slide >slideList, ViewPager2 viewPager2){
        this.slideList = slideList;
        this.viewPager2 = viewPager2;
    }


    @NonNull
    @Override
    public SlideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.each_slide,parent,false);
      return  new SlideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlideViewHolder holder, int position) {
        holder.setSlideData(slideList.get(position));
        if (position == slideList.size() -2){
            viewPager2.post(runnable);
        }
    }

    @Override
    public int getItemCount() {
        return slideList.size();
    }
    private Context context;
    public class SlideViewHolder extends  RecyclerView.ViewHolder{
        VideoView videoView;
        ImageView ivSlide;
        TextView campFireTitle,campFireDesc,campMasterName,campMasterTitle,txtCampMasterDetailName,txtCampMasterDesc,txtCampFireDetails;
        ProgressBar progressBar;
        LinearLayout llCampFireInfo;
        public SlideViewHolder(@NonNull View itemView) {
            super(itemView);

            videoView = itemView.findViewById(R.id.videoView);
            ivSlide = itemView.findViewById(R.id.ivSlideImage);

            campFireTitle = itemView.findViewById(R.id.txtCampFire);
            campFireDesc = itemView.findViewById(R.id.txtCampFireDetails);

            txtCampMasterDetailName = itemView.findViewById(R.id.txtCampMasterDetailName);
            txtCampMasterDesc = itemView.findViewById(R.id.txtCampMasterDesc);

            progressBar = itemView.findViewById(R.id.videoProgressBar);

            llCampFireInfo = itemView.findViewById(R.id.llCampFireInfo);
            context = itemView.getContext();

        }

        public void setSlideData(Slide slide){

//            ivSlide.setVideoPath(slide.getVideoUrl());
            ivSlide.setVisibility(View.GONE);
            llCampFireInfo.setVisibility(View.GONE);

            if(slide.getSlide()){
                ivSlide.setVisibility(View.VISIBLE);

                Picasso.with(context).load(slide.getImageUrl()).into(ivSlide);
            }

            if(slide.getEmpty()){
                ivSlide.setVisibility(View.GONE);
                llCampFireInfo.setVisibility(View.GONE);
            }

            if(slide.getInfo()){

                llCampFireInfo.setVisibility(View.VISIBLE);
                campFireTitle.setText(slide.getCampFire());
                campFireDesc.setText(slide.getCampFireDetails());
                txtCampMasterDetailName.setText(slide.getCampMasterName());
                txtCampMasterDesc.setText(slide.getCampMasterTitle());

            }
            progressBar.setVisibility(View.GONE);


            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    progressBar.setVisibility(View.GONE);
                    mp.start();
                    float videoRatio = mp.getVideoWidth() / (float) mp.getVideoHeight();
                    float screenRatio =videoView.getWidth()/ (float) videoView.getHeight();

                    float scale = videoRatio/screenRatio;

                    if(scale >=1f){
                        videoView.setScaleX(scale);
                    }else{
                        videoView.setScaleY(1f/scale);
                    }
                }
            });

            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });
        }
    }
    private  Runnable runnable = new Runnable() {
        @Override
        public void run() {
            slideList.addAll(slideList);
            notifyDataSetChanged();
        }
    };
}
