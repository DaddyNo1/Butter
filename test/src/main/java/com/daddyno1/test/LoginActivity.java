package com.daddyno1.test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.daddyno1.butter_annotation.BindView;
import com.daddyno1.butterapi.Butter;

public class LoginActivity extends AppCompatActivity {

    @BindView(R2.id.test_tx)
    TextView testTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Butter.bind(this);

        testTv.setText(":test - LoginActivity");
    }
}
