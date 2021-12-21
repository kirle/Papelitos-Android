package com.example.proyectodm.iu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.proyectodm.R;

public class MainActivity extends AppCompatActivity {


    LinearLayout layout; // parent layout on activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setTheme(R.style.Theme_ProyectoDM);
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        LinearLayout lytPlay = (LinearLayout) findViewById(R.id.lyt_play);
        lytPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        LinearLayout lytOptions = (LinearLayout) findViewById(R.id.lyt_options);
        lytOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Options.class);
                MainActivity.this.startActivity(intent);
            }
        });

        LinearLayout lytHelp = (LinearLayout) findViewById(R.id.lyt_help);
        lytHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Help.class);
                MainActivity.this.startActivity(intent);
            }
        });

        layout = findViewById(R.id.lyt_frame);
        layout.setBackgroundResource(R.drawable.paperfly_animation);

        playAnimation();

    }

    @Override
    protected void onPause() {
        super.onPause();
        //mediaPlayer.stopMusic();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //mediaPlayer.playMusic();
    }

    public void playAnimation(){
        AnimationDrawable frameAnimation = (AnimationDrawable) this.layout.getBackground();
        frameAnimation.start();
    }



    public void sendMessage(){
        Intent intent = new Intent(MainActivity.this, RegistrarJugadores.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);

    }
}
