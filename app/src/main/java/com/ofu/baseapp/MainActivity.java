package com.ofu.baseapp;

import android.os.Bundle;

import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.yokeyword.fragmentation.SupportActivity;


public class MainActivity extends SupportActivity {

    @BindView(R.id.tv)
    TextView tv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        Gson gson=new Gson();

        tv1.setOnClickListener(v -> Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show());

    }
}
