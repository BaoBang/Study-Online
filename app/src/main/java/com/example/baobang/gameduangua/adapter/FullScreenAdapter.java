package com.example.baobang.gameduangua.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.example.baobang.gameduangua.R;
import com.example.baobang.gameduangua.model.Gallery;
import com.example.baobang.gameduangua.utils.TouchImageView;

import java.util.ArrayList;

/*
 * Created by baobang on 4/5/18.
 */
public class FullScreenAdapter extends PagerAdapter {

    private Activity activity;
    private ArrayList<Gallery> imgURL;

    // constructor
    public FullScreenAdapter(Activity activity,
                                  ArrayList<Gallery> imagePaths) {
        this.activity = activity;
        this.imgURL = imagePaths;
    }

    @Override
    public int getCount() {
        return this.imgURL.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        TouchImageView imgDisplay;
        Button btnClose;

        LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image_item, container,
                false);

        imgDisplay = viewLayout.findViewById(R.id.imgDisplay);
        btnClose =  viewLayout.findViewById(R.id.btnClose);

        Glide.with(activity).load(imgURL.get(position).getPhotUrl()).into(imgDisplay);

        // close button click event
        btnClose.setOnClickListener(v -> activity.finish());

        container.addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
         container.removeView((RelativeLayout) object);

    }
}