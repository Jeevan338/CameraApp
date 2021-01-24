package com.example.loginapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ResgisterActivity extends AppCompatActivity {
    EditText name,username,password,confirmpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resgister);
        name=(EditText)findViewById(R.id.name);
        username=(EditText)findViewById(R.id.username);
        password=(EditText)findViewById(R.id.password);
        confirmpassword=(EditText)findViewById(R.id.confirmpassword);
    }

    public void enter (View view)
    {
        String getname=name.getText().toString();
        String getusername=username.getText().toString();
        String getpassword=password.getText().toString();
        String getconfirmpassword=confirmpassword.getText().toString();
        Toast.makeText(this,getname+getusername+getpassword+getconfirmpassword,Toast.LENGTH_LONG).show();
    }
    public void logbtn (View view)
    {
        Intent i= new Intent(this,MainActivity.class);
        startActivity(i);
    }
}