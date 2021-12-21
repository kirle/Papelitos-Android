package com.example.proyectodm.iu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.proyectodm.R;

public class Instructions extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        ImageButton btn_back = (ImageButton) findViewById(R.id.btn_backtimer);
        ImageButton btn_ok = (ImageButton) findViewById(R.id.btnoktimer);

        // * BTN BACK
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound();
                v.startAnimation(AnimationUtils.loadAnimation(Instructions.this, R.anim.click_animation));
                Intent myIntent = new Intent(Instructions.this, RegistrarEquipos.class);
                Instructions.this.startActivity(myIntent);
            }
        });

        // * BTN GO
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound();
                v.startAnimation(AnimationUtils.loadAnimation(Instructions.this, R.anim.click_animation));
                Intent myIntent = new Intent(Instructions.this, RoundTimer.class);
                Instructions.this.startActivity(myIntent);
            }
        });

        TextView txt_infoscroll = (TextView) findViewById(R.id.txt_trytoscroll);

        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        txt_infoscroll.startAnimation(anim);

    }
    public void playSound(){
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.changesound);
        mediaPlayer.start();
    }


}