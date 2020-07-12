package com.daddyno1.butter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.daddyno1.butter_annotation.BindString;
import com.daddyno1.butter_annotation.BindView;
import com.daddyno1.butter_annotation.OnClick;
import com.daddyno1.butterapi.Butter;
import com.daddyno1.test.LoginActivity;

public class MainActivity extends AppCompatActivity {

    @BindString(R2.string.app_name) String appName;
    @BindView(R2.id.txt) TextView tvTxt;
    @BindView(R2.id.txt2) TextView tvTxt2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Butter.bind(this);

        tvTxt.setText(appName);
        tvTxt2.setText(appName);

    }

    @OnClick(R2.id.btn)
    void click(View v){
        Toast.makeText(this, "MainActivity - click", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
    }
}
