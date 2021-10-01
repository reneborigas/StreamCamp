package com.navii.streamcamp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExploreItemAdapter extends RecyclerView.Adapter<ExploreItemAdapter.ExploreViewHolder>{


    List<ExploreItemHelperClass> exploreCamps;

    CardView cardView;
    public ExploreItemAdapter(List<ExploreItemHelperClass> exploreCamps) {
        this.exploreCamps = exploreCamps;
//        this.cardView = cardView;
    }

    @NonNull
    @Override
    public ExploreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.explore_item_card_design,parent,false);
        return  new ExploreItemAdapter.ExploreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExploreViewHolder holder, int position) {
        holder.setVideoData(exploreCamps.get(position));
//        if (position == videoList.size() -2){
//            viewPager2.post(runnable);
//        }
    }

    @Override
    public int getItemCount() {
        return exploreCamps.size();
    }

    public  static  class  ExploreViewHolder extends  RecyclerView.ViewHolder{

        VideoView videoView;
        TextView title,desc;
        ProgressBar progressBar;
        public ExploreViewHolder(@NonNull View itemView) {
            super(itemView);

//            videoView = itemView.findViewById(R.id.videoView);
            title = itemView.findViewById(R.id.video_title);
            desc = itemView.findViewById(R.id.video_description);
            progressBar = itemView.findViewById(R.id.videoProgressBar);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent activity2Intent = new Intent(itemView.getContext(), LiveActivity.class);
                    itemView.getContext().startActivity(activity2Intent);

                }
            });
        }
        public void setVideoData(ExploreItemHelperClass exploreItemHelperClass){
            title.setText(exploreItemHelperClass.getTitle());
            desc.setText(exploreItemHelperClass.getDesc());
//            videoView.setVideoPath(exploreItemHelperClass.getVideoUrl());
//
//            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mp) {
//                    progressBar.setVisibility(View.GONE);
//                    mp.start();
//                    float videoRatio = mp.getVideoWidth() / (float) mp.getVideoHeight();
//                    float screenRatio =videoView.getWidth()/ (float) videoView.getHeight();
//
//                    float scale = videoRatio/screenRatio;
//
//                    if(scale >=1f){
//                        videoView.setScaleX(scale);
//                    }else{
//                        videoView.setScaleY(1f/scale);
//                    }
//                }
//            });
//
//            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mediaPlayer) {
//                    mediaPlayer.start();
//                }
//            });
        }
    }

}
