package com.example.proyectodm.iu;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.LinearLayout;

import com.example.proyectodm.R;

public class MainActivity extends AppCompatActivity {

    LinearLayout layout; // parent layout on activity
    MediaPlayer musicPlayer;
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

        layout = findViewById(R.id.lyt_frame);
        layout.setBackgroundResource(R.drawable.paperfly_animation);

        playAnimation();
        playMusic();

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopMusic();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        playMusic();
    }

    public void playAnimation(){
        AnimationDrawable frameAnimation = (AnimationDrawable) this.layout.getBackground();
        frameAnimation.start();
    }
    public void playMusic(){
        musicPlayer = MediaPlayer.create(this, R.raw.music);
        musicPlayer.start();
    }
    public void stopMusic(){
        musicPlayer.pause();
    }
    public void playSound(){
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.changesound);
        mediaPlayer.start();
    }

    public void sendMessage(){
        Intent intent = new Intent(this, registrar_equipos.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        playSound();
        startActivity(intent);
        musicPlayer.stop();

    }
}
