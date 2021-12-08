package com.example.proyectodm.iu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.proyectodm.R;

public class Options extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        ImageButton btn_back = (ImageButton) findViewById(R.id.btn_backpapelitos);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Options.this, MainActivity.class);
                Options.this.startActivity(intent);
            }
        });

        final boolean[] clicked = {false};
        ImageView imgClickable = (ImageView) findViewById(R.id.img_OpMusic_clickable);
        imgClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                if(clicked[0]){
                    clicked[0] = false;
                    imgClickable.setBackgroundResource(R.mipmap.clicable);



                }  else {
                    clicked[0] = true;
                    imgClickable.setBackgroundResource(R.mipmap.clickableclicked);


                }
            }
        });

    }
}