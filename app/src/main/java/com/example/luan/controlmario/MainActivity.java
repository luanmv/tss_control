package com.example.luan.controlmario;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    private Button Start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Start = (Button) findViewById(R.id.StartButton);
    }

    public void onClick(View V) {
        Intent controles = new Intent(getApplicationContext(), Controles.class);
        startActivity(controles);
    }
}
