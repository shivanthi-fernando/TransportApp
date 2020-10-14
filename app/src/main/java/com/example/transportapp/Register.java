package com.example.transportapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Register extends AppCompatActivity {

    private Button create_acc_btn;
    private EditText InputName, InputNicNo , InputPassword;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        create_acc_btn = (Button) findViewById(R.id.register_btn);
        InputName = (EditText) findViewById(R.id.register_user_name_input);
        InputNicNo = (EditText) findViewById(R.id.register_nic_number_input);
        InputPassword = (EditText) findViewById(R.id.register_password_input);
        loadingBar = new ProgressDialog(this);

        create_acc_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });
    }

    private void createAccount()
    {
        String user_name = InputName.getText().toString();
        String nic_no = InputNicNo.getText().toString();
        String password = InputPassword.getText().toString();

        if (TextUtils.isEmpty(user_name))
        {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(nic_no))
        {
            Toast.makeText(this, "Please enter your NIC no", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please wait, while we are checking the credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidatePhoneNo(user_name, nic_no , password);
        }
    }

    private void ValidatePhoneNo(final String user_name, final String nic_no, final String password)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (!(snapshot.child("Users").child(nic_no).exists()))
                {
                    HashMap<String, Object> userDataMap = new HashMap<>();
                    userDataMap.put("nic", nic_no);
                    userDataMap.put("password",password);
                    userDataMap.put("name",user_name);

                    RootRef.child("Users").child(nic_no).updateChildren(userDataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(Register.this, "Congratulations your account has been created" , Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                        Intent intent = new Intent(Register.this, Login.class);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        loadingBar.dismiss();
                                        Toast.makeText(Register.this, "Network error, please try again" , Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else
                {
                    Toast.makeText(Register.this, "This" + nic_no + "already exist" , Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(Register.this, "Please try again using another phone number" , Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(Register.this, Home.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}