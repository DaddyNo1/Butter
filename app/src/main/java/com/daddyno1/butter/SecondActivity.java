package com.daddyno1.butter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.daddyno1.butter_annotation.BindString;
import com.daddyno1.butter_annotation.BindView;
import com.daddyno1.butterapi.Butter;

public class SecondActivity extends AppCompatActivity {

    @BindView(R2.id.tx)
    TextView tx;

    @BindView(R2.id.tx2)
    TextView tx2;

    @BindString(R2.string.hello)
    String tips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Butter.bind(this);

        tx.setText(tips);
        tx2.setText(tips);
    }
}
