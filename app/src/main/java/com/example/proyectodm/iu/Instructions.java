package com.example.proyectodm.iu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.proyectodm.R;

public class Instructions extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        ImageButton btn_back = (ImageButton) findViewById(R.id.btn_backtimer);
        ImageButton btn_ok = (ImageButton) findViewById(R.id.btnoktimer);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound();
                Intent myIntent = new Intent(Instructions.this, registrarEquipos.class);
                Instructions.this.startActivity(myIntent);
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound();
                Intent myIntent = new Intent(Instructions.this, RoundTimer.class);
                Instructions.this.startActivity(myIntent);
            }
        });

    }
    public void playSound(){
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.changesound);
        mediaPlayer.start();
    }


}