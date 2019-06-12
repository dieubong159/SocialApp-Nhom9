package com.nhom9.socialapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class FullScreenImageActivity extends AppCompatActivity {

    ImageView fullscreen_img;
    String img_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // and this
                finish();
            }
        });

        fullscreen_img = findViewById(R.id.fullscreen_img);

        Intent intent = getIntent();
        img_url = intent.getStringExtra("imageurl");
        Glide.with(getApplicationContext()).load(img_url)
                .into(fullscreen_img);
    }
}
