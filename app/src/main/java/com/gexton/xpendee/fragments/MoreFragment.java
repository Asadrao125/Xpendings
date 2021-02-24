package com.gexton.xpendee.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.gexton.xpendee.AboutAppActivity;
import com.gexton.xpendee.AddExpenseActivity;
import com.gexton.xpendee.AddIncomeActivity;
import com.gexton.xpendee.BuildConfig;
import com.gexton.xpendee.LoginActivity;
import com.gexton.xpendee.ManageCategories;
import com.gexton.xpendee.R;
import com.gexton.xpendee.SpendingsOverviewActivity;
import com.gexton.xpendee.model.WalletBean;
import com.gexton.xpendee.util.AdUtil;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class MoreFragment extends Fragment {
    View view;
    String json;
    AdView adView;
    CircleImageView profile_image;
    TextView tv_username, tv_email;
    SharedPreferences preferences;
    String MY_PREFS_NAME = "Xpendee";
    RelativeLayout manage_categories, manageIncExp, spendings_overview;
    RelativeLayout logout_layout, about_app_layout, share_app_layout, rate_app_layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_more, container, false);

        tv_username = view.findViewById(R.id.tv_username);
        tv_email = view.findViewById(R.id.tv_email);
        profile_image = view.findViewById(R.id.profile_image);
        manage_categories = view.findViewById(R.id.manage_categories);
        logout_layout = view.findViewById(R.id.logout_layout);
        about_app_layout = view.findViewById(R.id.about_app_layout);
        share_app_layout = view.findViewById(R.id.share_app_layout);
        rate_app_layout = view.findViewById(R.id.rate_app_layout);
        manageIncExp = view.findViewById(R.id.manageIncExp);
        spendings_overview = view.findViewById(R.id.spendings_overview);
        preferences = getContext().getSharedPreferences("Xpendee", 0);

        adView = view.findViewById(R.id.adView);
        AdUtil adUtil = new AdUtil(getActivity());
        adUtil.loadBannerAd(adView);

        checkWalletExistance();

        spendings_overview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), SpendingsOverviewActivity.class));
            }
        });

        manageIncExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AddExpenseActivity.class));
            }
        });

        manage_categories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ManageCategories.class));
            }
        });

        about_app_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AboutAppActivity.class));
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
                showDialog();
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
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Xpendings");
            String shareMessage = "Let me recommend you Xpendings application\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "Please select"));
        } catch (Exception e) {
            e.toString();
        }
    }

    public void showDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        preferences.edit().remove("email").apply();
                        preferences.edit().remove("name").apply();
                        preferences.edit().remove("image").apply();
                        startActivity(new Intent(getContext(), LoginActivity.class));
                        getActivity().finish();
                    }
                })

                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void checkWalletExistance() {
        SharedPreferences prefs1 = getContext().getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE);
        json = prefs1.getString("Wallet_Bean", "");
        Gson gson = new Gson();
        WalletBean walletBean = gson.fromJson(json, WalletBean.class);
        if (walletBean != null) {
            manageIncExp.setVisibility(View.VISIBLE);
        } else {
            manageIncExp.setVisibility(View.GONE);
        }
    }
}