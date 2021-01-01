package com.gexton.xpendee.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gexton.xpendee.ManageCategories;
import com.gexton.xpendee.R;
import com.squareup.picasso.Picasso;

import static android.content.Context.MODE_PRIVATE;

public class MoreFragment extends Fragment {
    String MY_PREFS_NAME = "Xpendee";
    TextView tv_username, tv_email;
    CircleImageView profile_image;
    View view;
    RelativeLayout manage_categories;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_more, container, false);

        tv_username = view.findViewById(R.id.tv_username);
        tv_email = view.findViewById(R.id.tv_email);
        profile_image = view.findViewById(R.id.profile_image);
        manage_categories = view.findViewById(R.id.manage_categories);

        manage_categories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ManageCategories.class));
            }
        });

        SharedPreferences prefs = getContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String name = prefs.getString("name", "No name defined");
        String image = prefs.getString("image", "No name defined");
        String email = prefs.getString("email", "No name defined");

        tv_username.setText(name);
        tv_email.setText(email);
        Picasso.get().load(image).into(profile_image);

        return view;
    }
}