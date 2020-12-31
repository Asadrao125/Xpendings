package com.gexton.xpendee;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.io.InputStream;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    ImageView iv_google, iv_fb, iv_apple;

    // Profile pic image size in pixels
    private static final int PROFILE_PIC_SIZE = 400;

    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;

    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;
    private boolean mShouldResolve;
    private ConnectionResult connectionResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        iv_google = findViewById(R.id.iv_google);
        iv_fb = findViewById(R.id.iv_fb);
        iv_apple = findViewById(R.id.iv_apple);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.white, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }

        iv_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if (!mGoogleApiClient.isConnecting()) {
                    mShouldResolve = true;
                    resolveSignInError();
                }*/
                startActivity(new Intent(getApplicationContext(), DashbordActivity.class));
            }
        });

        iv_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), DashbordActivity.class));
            }
        });

        iv_apple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), DashbordActivity.class));
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    0).show();
            return;
        }

        if (!mIntentInProgress) {

            connectionResult = result;

            if (mShouldResolve) {

                resolveSignInError();
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mShouldResolve = false;
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person person = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String personName = person.getDisplayName();
                String personPhotoUrl = person.getImage().getUrl();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

                Toast.makeText(this, "" + personName + "\n" + email + "\n" + personPhotoUrl, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), DashbordActivity.class);
                intent.putExtra("name", personName);
                intent.putExtra("name", email);
                intent.putExtra("name", personPhotoUrl);
                startActivity(intent);
                finish();

                /*tvName.setText(personName);
                tvMail.setText(email);*/

                personPhotoUrl = personPhotoUrl.substring(0,
                        personPhotoUrl.length() - 2)
                        + PROFILE_PIC_SIZE;

                //new LoadProfileImage(imgProfilePic).execute(personPhotoUrl);

                Toast.makeText(getApplicationContext(), "You are Logged In " + personName, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Couldnt Get the Person Info", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        signOutUI();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
        signInUI();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void resolveSignInError() {
        if (connectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                connectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mShouldResolve = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    private void signOutUI() {
        /*signInButton.setVisibility(View.GONE);
        tvNotSignedIn.setVisibility(View.GONE);
        signOutButton.setVisibility(View.VISIBLE);
        viewContainer.setVisibility(View.VISIBLE);*/
    }

    private void signInUI() {
        /*signInButton.setVisibility(View.VISIBLE);
        tvNotSignedIn.setVisibility(View.VISIBLE);
        signOutButton.setVisibility(View.GONE);
        viewContainer.setVisibility(View.GONE);*/
    }

    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public LoadProfileImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}