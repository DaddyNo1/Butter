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

    @BindString(R.string.app_name) String appName;
    @BindView(R.id.txt) TextView tvTxt;
    @BindView(R.id.txt2) TextView tvTxt2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Butter.bind(this);

        Button btn = findViewById(R.id.btn);
        btn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ListActivity.class));
        });
    }

    @OnClick(R.id.btn)
    void click(View v){
        Toast.makeText(this, "MainActivity - click", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
    }
}
