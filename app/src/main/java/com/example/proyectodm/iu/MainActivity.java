package com.example.proyectodm.iu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.proyectodm.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout lytPlay = (LinearLayout) findViewById(R.id.lyt_play);
        lytPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        LinearLayout layout = (LinearLayout) findViewById(R.id.lyt_frame);
        layout.setBackgroundResource(R.drawable.paperfly_animation);

        AnimationDrawable frameAnimation = (AnimationDrawable) layout.getBackground();
        frameAnimation.start();

        //Music
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.music);
        mediaPlayer.start();

    }

    public void sendMessage(){
        Intent intent = new Intent(this, registrar_equipos.class);

        startActivity(intent);
    }
}