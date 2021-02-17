package com.gexton.xpendee;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

public class ViewImageActivity extends AppCompatActivity {
    ImageView expenseImage, imgBack;
    String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.black, this.getTheme()));
        }

        expenseImage = findViewById(R.id.expenseImage);
        imagePath = getIntent().getStringExtra("image_path");
        imgBack = findViewById(R.id.imgBack);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        try {
            if (TextUtils.isEmpty(imagePath)) {

            } else {
                File file = new File(imagePath);
                Picasso.get().load(file).into(expenseImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}