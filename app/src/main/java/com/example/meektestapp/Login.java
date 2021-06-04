package com.example.meektestapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.meektestapp.databinding.LoginBinding;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;


public class Login extends AppCompatActivity implements View.OnClickListener {

    private LoginBinding binding;
    private FirebaseAuth mAuth;
    private String TAG = Login.class.getName();
    private Context ctx = this;
    private String email = "", password = "";
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 1002;
    private static final int FB_SIGN_IN = 1001;
    private CallbackManager mCallbackManager;
    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = LoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        allocateMemory();
    }


    private void allocateMemory() {
        mAuth = FirebaseAuth.getInstance();
        binding.btnlogin.setOnClickListener(this);
        binding.imgfb.setOnClickListener(this);
        binding.imggoogle.setOnClickListener(this);
        binding.imglinkedin.setOnClickListener(this);
        binding.txtjoin.setOnClickListener(this);

        configureGoogleClient();

        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("Success", "Login");
                        Toast.makeText(Login.this, "Login Successfully", Toast.LENGTH_LONG).show();

                        handleFacebookAccessToken(loginResult.getAccessToken());

                    }

                    @Override
                    public void onCancel() {
                        pDialog.cancel();

                        Toast.makeText(Login.this, "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        pDialog.cancel();

                        Toast.makeText(Login.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

    }

    private void configureGoogleClient() {

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, UI will update with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            pDialog.cancel();

                            Toast.makeText(Login.this, "Authentication Succeeded.", Toast.LENGTH_SHORT).show();

                            Intent i = new Intent(ctx, MainActivity.class);
                            i.putExtra("screen", "login");
                            startActivity(i);

                        } else {
                            // If sign-in fails, a message will display to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnlogin:
                if (validate()) {
                    pDialog = new ProgressDialog(Login.this);
                    pDialog.setMessage("loading..");

                    emailLoginUsingFireBase();

                } else {
                    Toast.makeText(getApplicationContext(), "Please Enter Valid Details!!",
                            Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.imgfb:
                pDialog = new ProgressDialog(Login.this);
                pDialog.setMessage("loading..");

                fbLoginUsingFireBase();
                break;

            case R.id.imggoogle:
                pDialog = new ProgressDialog(Login.this);
                pDialog.setMessage("loading..");

                googleLoginUsingFireBase();
                break;

            case R.id.imglinkedin:
                linkdinLoginUsingFireBase();
                break;
            case R.id.txtjoin:
                Intent i = new Intent(ctx, SignUp.class);
                startActivity(i);
                break;


        }

    }


    private void linkdinLoginUsingFireBase() {

    }

    private boolean validate() {
        email = binding.edtemail.getText().toString();
        password = binding.edtpassword.getText().toString();
        if (CommonMethods.isValidEmail(email) && CommonMethods.isValidString(email) && CommonMethods.isValidString(password)) {
            return true;
        } else {
            return false;
        }
    }

    private void googleLoginUsingFireBase() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());


            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                pDialog.cancel();
                Toast.makeText(getApplicationContext(), "Something Went wrong",
                        Toast.LENGTH_SHORT).show();
            }
        }
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            pDialog.cancel();

                            Intent i = new Intent(ctx, MainActivity.class);
                            i.putExtra("screen", "login");
                            startActivity(i);


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            pDialog.cancel();
                            Toast.makeText(getApplicationContext(), "Signin Fail",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void fbLoginUsingFireBase() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));

    }

    private void emailLoginUsingFireBase() {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            pDialog.cancel();
                            Intent usernameIntent = new Intent(ctx, MainActivity.class);
                            usernameIntent.putExtra("screen", "login");
                            startActivity(usernameIntent);


                        } else {
                            // If sign in fails, display a message to the user.
                            pDialog.cancel();
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(ctx, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}