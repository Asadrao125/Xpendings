package com.gexton.xpendee;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    ImageView iv_google, iv_fb, iv_apple;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN_FACEBOOK = 64206;
    private static final int RC_SIGN_IN_GOOGLE = 9001;
    CallbackManager callbackManager;
    String MY_PREFS_NAME = "Xpendee";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create();

        checkingIfUserSignedIn();

        iv_google = findViewById(R.id.iv_google);
        iv_fb = findViewById(R.id.iv_fb);
        iv_apple = findViewById(R.id.iv_apple);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        AppEventsLogger.activateApp(getApplication());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.white, this.getTheme()));
        }

        iv_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginWithGoogle();
            }
        });

        iv_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fbLogin2();
            }
        });

        iv_apple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void checkingIfUserSignedIn() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String name = prefs.getString("name", "No name defined");

        if (name.equals("") || name.equals("No name defined")) {
            //Toast.makeText(this, "Please Sign In", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "checkingIfUserSignedIn: Please Sign In");
        } else {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }
    }

    public void loginWithGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        try {
            Log.d("", "handleSignInResult:" + result.isSuccess());
            System.out.println("--sign in result " + result.getSignInAccount().getEmail() + " ; " + result.getSignInAccount().getId()
                    + " account: " + result.getSignInAccount().toString());
            if (result.isSuccess()) {
                // Signed in successfully, show authenticated UI.
                GoogleSignInAccount acct = result.getSignInAccount();
                System.out.println("--sign in information " + acct.getEmail());

                String id = acct.getId();
                String email = acct.getEmail();
                String imageUrl = "https://onlinecare.com/";
                if (acct.getPhotoUrl() != null) {
                    imageUrl = acct.getPhotoUrl().toString();
                }
                String fullName = acct.getDisplayName();
                //Toast.makeText(this, "" + fullName, Toast.LENGTH_SHORT).show();

                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString("name", fullName);
                editor.putString("image", imageUrl);
                editor.putString("email", email);
                editor.apply();

                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();

                mGoogleApiClient.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        System.out.println("-- Request code: " + requestCode + " result code: " + resultCode + " intentData: " + data);

        if (requestCode == RC_SIGN_IN_GOOGLE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else if (requestCode == RC_SIGN_IN_FACEBOOK) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void fbLogin2() {
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("Success", "Login");
                        System.out.println("-- Login Resul: " + loginResult.toString());

                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        Log.v("LoginActivity", response.toString());
                                        System.out.println("-- json " + object.toString() + "\n-- graph " + response.toString());
                                        // Application code
                                        try {
                                            String email = object.getString("email");
                                            String fullName = object.getString("name"); //
                                            String id = object.getString("id");
                                            String imageUrl = "";
                                            if (object.has("picture")) {
                                                imageUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");
                                                System.out.println("image url: " + imageUrl);
                                            }
                                            //Toast.makeText(LoginActivity.this, "" + fullName, Toast.LENGTH_SHORT).show();

                                            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                            editor.putString("name", fullName);
                                            editor.putString("image", imageUrl);
                                            editor.putString("email", email);
                                            editor.apply();

                                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                            startActivity(intent);
                                            finish();

                                            LoginManager loginManager = LoginManager.getInstance();
                                            loginManager.logOut();

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender,picture.type(large)");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(getApplicationContext(), "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
    }
}