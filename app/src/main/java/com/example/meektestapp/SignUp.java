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

import com.example.meektestapp.databinding.SignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUp extends AppCompatActivity {

    private SignUpBinding binding;
    private FirebaseAuth mAuth;
    private String TAG=SignUp.class.getName();
    private Context ctx=this;
    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = SignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        allocateMemory();
        setEvents();

    }

    private void setEvents() {

        binding.btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(SignUp.this);
                pDialog.setMessage("loading..");

                String email=binding.edtemail.getText().toString();
                String password=binding.edtpassword.getText().toString();
                if(CommonMethods .isValidEmail(email) && CommonMethods.isValidString(email) && CommonMethods.isValidString(password)) {
                    createUserInFirebase(email, password);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Please Enter Valid Details!!",
                            Toast.LENGTH_SHORT).show();

                }
            }
        });

        binding.txtsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(ctx,Login.class);
                startActivity(i);

            }
        });

    }

    private void createUserInFirebase(String email, String password)
    {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getApplicationContext(), "Signup Successfully",
                                    Toast.LENGTH_SHORT).show();
                            pDialog.cancel();
                            Intent loginintent=new Intent(ctx,Login.class);
                            startActivity(loginintent);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void allocateMemory() {
        mAuth = FirebaseAuth.getInstance();


    }



}