package com.example.meektestapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meektestapp.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setEvents();
    }

    private void setEvents()
    {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(getIntent().getExtras()!=null)
        {
            binding.txtusername.setVisibility(View.VISIBLE);
            binding.txtsignout.setVisibility(View.VISIBLE);
            if(currentUser!=null)
            {
                binding.txtusername.setText(currentUser.getDisplayName());

            }

            binding.txtsignout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAuth.signOut();
                    finish();
                    binding.txtusername.setText("");
                    binding.txtsignout.setVisibility(View.GONE);
                    finishAffinity();
                }
            });
        }
        else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i=new Intent(getApplicationContext(),Login.class);
                    startActivity(i);
                }
            }, 2500);

        }

    }
}