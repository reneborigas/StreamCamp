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
import java.util.TreeMap;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    List<Video> videoList;
    ViewPager2 viewPager2;
    public VideoAdapter(List<Video >videoList,ViewPager2 viewPager2){
        this.videoList = videoList;
        this.viewPager2 = viewPager2;
    }


    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.each_video,parent,false);
      return  new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        holder.setVideoData(videoList.get(position));
        if (position == videoList.size() -2){
            viewPager2.post(runnable);
        }
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public class VideoViewHolder extends  RecyclerView.ViewHolder{
        VideoView videoView;
        ImageView ivBanner;
        TextView title,desc,campMaster,campMasterTitle;
        ProgressBar progressBar;
        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);

            videoView = itemView.findViewById(R.id.videoView);
            ivBanner = itemView.findViewById(R.id.ivBanner);
            title = itemView.findViewById(R.id.txtCampFire);

            desc = itemView.findViewById(R.id.txtCampFireDetails);
            campMaster = itemView.findViewById(R.id.txtCampMasterDetailName);
            campMasterTitle = itemView.findViewById(R.id.txtCampMasterDesc);
            progressBar = itemView.findViewById(R.id.videoProgressBar);


        }

        public void setVideoData(Video video){
            title.setText(video.getTitle());
            desc.setText(video.getDesc());
            ivBanner.setVisibility(View.GONE);
            videoView.setVisibility(View.GONE);

            if (video.getVideoUrl()!=""){
                videoView.setVisibility(View.VISIBLE);
                videoView.setVideoPath(video.getVideoUrl());
            }else{
                ivBanner.setVisibility(View.VISIBLE);
                Picasso.with(itemView.getContext()).load(video.getImageURl()).into(ivBanner);
                progressBar.setVisibility(View.GONE);
            }

            campMaster.setText(video.getCampMaster());
            campMasterTitle.setText(video.getCampMasterTitle());
            videoView.setTag(video.getCampMasterID());
            SharedPreferences sharedPreferences =  itemView.getContext().getSharedPreferences("STREAMCAMP",  itemView.getContext().MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("CURRENT_CAMPFIRE", video.getCampFireID());
            editor.putString("CURRENT_CAMPFIRE_CAMPMASTER", video.getCampMasterID());
            editor.putBoolean("IS_PREMIUM", video.isPremium());

            editor.commit();
            Log.wtf("savedUser",video.getCampMasterID());
            videoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent comminityStandard = new Intent(itemView.getContext(), CommunityStandardActivity.class);
                    Intent goLive = new Intent(itemView.getContext(), GoLiveActivity.class);
                    String value = view.getTag().toString();
                    SharedPreferences sharedPreferences =  itemView.getContext().getSharedPreferences("STREAMCAMP",  itemView.getContext().MODE_PRIVATE);
                    String savedUser= sharedPreferences.getString("USERID","");
                    Log.wtf("userId",savedUser);
                    Log.wtf("savedUser",value);
                    Log.wtf(value,savedUser);


                    if ((savedUser.equals(value))) {
                        goLive.putExtra("isCampMaster",true);
                        Log.wtf("master","true");
                        itemView.getContext().startActivity(goLive);
                    }else{
                        Log.wtf("PREMIUM", String.valueOf(sharedPreferences.getBoolean("IS_PREMIUM", false)));
                        if(sharedPreferences.getBoolean("IS_PREMIUM", false)){
                            Intent goPremium = new Intent(itemView.getContext(), PremiumActivity.class);
                            goPremium.putExtra("isCampMaster",false);
                            Log.wtf("master","true");
                            itemView.getContext().startActivity(goPremium);
                            return;
                        }

                        goLive.putExtra("isCampMaster",true);
                        Log.wtf("master","true");
                        itemView.getContext().startActivity(comminityStandard);
//                        intentToLive.putExtra("isCampMaster",false);
//                        Log.wtf("master","false");
//                        itemView.getContext().startActivity(intentToLive);
//                        Intent intent = new Intent( );
//                        intent.putExtra(Constants.KEY_CLIENT_ROLE, io.agora.rtc.Constants.CLIENT_ROLE_AUDIENCE);
//                        intent.setClass(  itemView.getContext(), CampFireActivity.class);
//                        itemView.getContext().startActivity(intent);

//                        intent.putExtra(Constants.KEY_CLIENT_ROLE, io.agora.rtc.Constants.CLIENT_ROLE_AUDIENCE);
//                        intent.setClass(itemView.getContext(), CampFireActivity.class);
//                        config().setChannelName("StreamCamp");
//                        itemView.getContext().startActivity(intent);
                    }



                }
            });
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
            videoList.addAll(videoList);
            notifyDataSetChanged();
        }
    };
}
