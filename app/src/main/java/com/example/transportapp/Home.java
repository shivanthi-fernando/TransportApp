package com.example.transportapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.transportapp.Model.Users;
import com.example.transportapp.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class Home extends AppCompatActivity {

    private Button joinNowButton , loginButton;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        joinNowButton = (Button) findViewById(R.id.main_join_now);
        loginButton = (Button) findViewById(R.id.main_login_btn);
        loadingBar = new ProgressDialog(this);

        Paper.init(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this, Login.class);
                startActivity(intent);
            }
        });

        joinNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this, Register.class);
                startActivity(intent);
            }
        });

        String UserNicKey = Paper.book().read(Prevalent.UserNicKey);
        String UserPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);

        if (UserNicKey != "" && UserPasswordKey != "")
        {
            if(!TextUtils.isEmpty(UserNicKey) && !TextUtils.isEmpty(UserPasswordKey))
            {
                AllowAccess(UserNicKey, UserPasswordKey);

                loadingBar.setTitle("Already loged in");
                loadingBar.setMessage("Please wait...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
            }
        }
    }

    private void AllowAccess(final String nic_no, final String password)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.child("Users").child(nic_no).exists())
                {
                    Users usersData = snapshot.child("Users").child(nic_no).getValue(Users.class);

                    if (usersData.getNic().equals(nic_no))
                    {
                        if (usersData.getPassword().equals(password))
                        {
                            Toast.makeText(Home.this, "Logged in successfully", Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();

                            Intent intent = new Intent(Home.this, UserDashboard.class);
                            startActivity(intent);
                        }
                    }
                }
                else
                {
                    Toast.makeText(Home.this, "Account with this NIC do not exists", Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();
                    Toast.makeText(Home.this, "You need to create a new account", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}