package com.example.loginapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText username,password,x,y;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username=(EditText)findViewById(R.id.username);
        password=(EditText)findViewById(R.id.password);
    }
    public void enter (View view)
    {
        String getusername=username.getText().toString();
        String getpassword=password.getText().toString();
        if( (getusername.equals("admin") ) && (getpassword.equals("1234"))  )
        {
                Intent i = new Intent(this, CameraActivity.class);
                startActivity(i);
        }
        else
            {
                Toast.makeText(this, "Invalid Credentials",Toast.LENGTH_LONG).show();
            }
    }
    public void regbtn (View view)
    {
        Intent i=new Intent(this,ResgisterActivity.class);
        startActivity(i);
    }

}