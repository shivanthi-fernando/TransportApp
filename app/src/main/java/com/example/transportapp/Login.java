package com.example.transportapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.transportapp.Model.Users;
import com.example.transportapp.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class Login extends AppCompatActivity {

    private EditText InputNicNo, InputPassword;
    private Button LoginBtn;
    private ProgressDialog loadingBar;
    private TextView DriverLink, NotDriverLink;

    private String parentDbName = "Users";
    private CheckBox checkBoxRememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginBtn = (Button) findViewById(R.id.login_btn);
        InputNicNo = (EditText) findViewById(R.id.login_nic_number_input);
        InputPassword = (EditText) findViewById(R.id.login_password_input);
        DriverLink = (TextView) findViewById(R.id.driver_option);
        NotDriverLink = (TextView) findViewById(R.id.not_driver_option);

        loadingBar = new ProgressDialog(this);

        checkBoxRememberMe = (CheckBox) findViewById(R.id.remember_me_chk);
        Paper.init(this);

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                LoginUser();
            }
        });

        DriverLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                LoginBtn.setText("Driver Login");
                DriverLink.setVisibility(View.INVISIBLE);
                NotDriverLink.setVisibility(View.VISIBLE);

                parentDbName = "Drivers";
            }
        });

        NotDriverLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                LoginBtn.setText("Login");
                DriverLink.setVisibility(View.VISIBLE);
                NotDriverLink.setVisibility(View.INVISIBLE);

                parentDbName = "Users";
            }
        });
    }

    private void LoginUser()
    {
        String nic_no = InputNicNo.getText().toString();
        String password = InputPassword.getText().toString();

        if (TextUtils.isEmpty(nic_no))
        {
            Toast.makeText(this, "Please enter your NIC no", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait, while we are checking the credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            AllowAccessAccount(nic_no, password);
        }
    }

    private void AllowAccessAccount(final String nic_no, final String password)
    {
        if (checkBoxRememberMe.isChecked())
        {
            Paper.book().write(Prevalent.UserNicKey, nic_no);
            Paper.book().write(Prevalent.UserPasswordKey,password);
        }

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.child(parentDbName).child(nic_no).exists())
                {
                    Users usersData = snapshot.child(parentDbName).child(nic_no).getValue(Users.class);

                    if (usersData.getNic().equals(nic_no))
                    {
                        if (usersData.getPassword().equals(password))
                        {
                            Toast.makeText(Login.this, "Logged in successfully", Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();

                            Intent intent = new Intent(Login.this, UserDashboard.class);
                            startActivity(intent);
                        }
                    }
                }
                else
                {
                    Toast.makeText(Login.this, "Account with this NIC do not exists", Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();
                    Toast.makeText(Login.this, "You need to create a new account", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}