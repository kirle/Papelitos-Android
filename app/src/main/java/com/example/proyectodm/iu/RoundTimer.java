package com.example.proyectodm.iu;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.proyectodm.R;

public class RoundTimer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_round_timer);
        ImageButton btn_back = (ImageButton) findViewById(R.id.btn_menu);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound();
                Intent myIntent2 = new Intent(RoundTimer.this, Instructions.class);
                ActivityOptions.makeSceneTransitionAnimation(RoundTimer.this).toBundle();

                RoundTimer.this.startActivity(myIntent2);

            }
        });

    }
    public void playSound(){
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.changesound);
        mediaPlayer.start();
    }
}