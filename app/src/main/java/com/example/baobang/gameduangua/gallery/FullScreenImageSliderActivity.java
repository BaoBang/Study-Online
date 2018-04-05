package com.example.baobang.gameduangua.gallery;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.baobang.gameduangua.R;
import com.example.baobang.gameduangua.adapter.FullScreenAdapter;
import com.example.baobang.gameduangua.model.Gallery;

import java.util.ArrayList;

public class FullScreenImageSliderActivity extends AppCompatActivity {

    private ArrayList<Gallery> imgURL;
    private FullScreenAdapter fullScreenAdapter;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image_slider);
        imgURL = new ArrayList<>();
        Bundle bundle = getIntent().getExtras();
        int pos = bundle.getInt("POS");
        imgURL = (ArrayList<Gallery>) bundle.getSerializable("LIST");
        fullScreenAdapter = new FullScreenAdapter(this, imgURL);
        viewPager = findViewById(R.id.slider);
        viewPager.setAdapter(fullScreenAdapter);
        viewPager.setCurrentItem(pos);

    }
}
