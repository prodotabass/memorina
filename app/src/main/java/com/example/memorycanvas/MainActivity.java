package com.example.memorycanvas;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    TilesView view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view = findViewById(R.id.view);

    }
    public void onNewGameClick(View v) {
        view.onClick(); // запустить игру заново

    }
}

