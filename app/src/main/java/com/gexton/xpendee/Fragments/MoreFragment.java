package com.gexton.xpendee.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gexton.xpendee.AddExpenseActivity;
import com.gexton.xpendee.AddIncomeActivity;
import com.gexton.xpendee.BuildConfig;
import com.gexton.xpendee.ManageCategories;
import com.gexton.xpendee.R;
import com.squareup.picasso.Picasso;

import static android.content.Context.MODE_PRIVATE;

public class MoreFragment extends Fragment {
    String MY_PREFS_NAME = "Xpendee";
    TextView tv_username, tv_email;
    CircleImageView profile_image;
    RelativeLayout manage_categories, manual_wallets;
    View view;
    RelativeLayout logout_layout, about_app_layout, share_app_layout, rate_app_layout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_more, container, false);

        tv_username = view.findViewById(R.id.tv_username);
        tv_email = view.findViewById(R.id.tv_email);
        profile_image = view.findViewById(R.id.profile_image);
        manage_categories = view.findViewById(R.id.manage_categories);
        manual_wallets = view.findViewById(R.id.manual_wallets);
        logout_layout = view.findViewById(R.id.logout_layout);
        about_app_layout = view.findViewById(R.id.about_app_layout);
        share_app_layout = view.findViewById(R.id.share_app_layout);
        rate_app_layout = view.findViewById(R.id.rate_app_layout);

        manage_categories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ManageCategories.class));
            }
        });

        manual_wallets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AddIncomeActivity.class));
            }
        });

        about_app_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        share_app_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareIntent();
            }
        });

        rate_app_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getContext().getPackageName())));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getContext().getPackageName())));
                }
            }
        });

        logout_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Logout", Toast.LENGTH_SHORT).show();
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

    private void shareIntent() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Xpendee");
            String shareMessage = "Let me recommend you this application\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "Please select"));
        } catch (Exception e) {
            e.toString();
        }
    }

}