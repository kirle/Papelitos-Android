package com.example.proyectodm.iu;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.example.proyectodm.R;
import com.example.proyectodm.core.DBManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public class RoundTimer extends AppCompatActivity {

    private static final long START_TIME_IN_MILLIS = 60000;

    private TextView txt_timer;
    private Button mButtonStartPause;
    private Button mButtonReset;

    private CountDownTimer timer;

    private Boolean mTimerRunning = false;

    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private ArrayList<String> ids_equipos;
    private DBManager gestorDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_round_timer);

        this.gestorDB = DBManager.getInstance(this.getApplicationContext());
        txt_timer = (TextView) findViewById(R.id.txt_timer);

        //Array list with teams from db cursor
        Cursor cursorEquipos = this.gestorDB.getEquipos();
        ids_equipos = new ArrayList<>();

        cursorEquipos.moveToFirst();
        while(!cursorEquipos.isAfterLast()) {
            ids_equipos.add(cursorEquipos.getString(0)); //add the item
            cursorEquipos.moveToNext();
        }
        Collections.sort(ids_equipos); // random order for teams

        // ** Listeners

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

        updateCountDownText();

        //Bucle de juego


        boolean seguir = true;
        TextView txt_nombreEquipo = (TextView) findViewById(R.id.txt_equipoActual);


        Cursor cursor = this.gestorDB.getPapelitos();
        ArrayList<String> ids_papelitos = new ArrayList<String>();

        System.out.println("CURSOR:" + cursor.getCount());
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            ids_papelitos.add(cursor.getString(0)); //add the item
            cursor.moveToNext();
        }


        while(ids_papelitos.size() > 0){

            for (String equipo_id : ids_equipos){
                // break if palabras==null
                txt_nombreEquipo.setText(this.gestorDB.getNombreEquipo(equipo_id));


                Cursor cursor_papelitos = this.gestorDB.getPapelitosDisponibles();
                cursor_papelitos.moveToFirst();
                while(!cursor_papelitos.isAfterLast()) {
                    ids_papelitos.add(cursor_papelitos.getString(0)); //add the item
                    cursor_papelitos.moveToNext();
                }

                TextView txt_timer = (TextView) findViewById(R.id.txt_timer);
                String timer = txt_timer.getText().toString();
                while (Integer.valueOf(timer) > 0){



                    int random = ThreadLocalRandom.current().nextInt(0, ids_papelitos.size() + 1);
                    String current_id = ids_papelitos.get(random);

                    String papelito_text = this.gestorDB.getPapelito(Integer.valueOf(current_id));
                    TextView current_word = (TextView) findViewById(R.id.current_word);
                    current_word.setText(papelito_text);

                    final boolean[] palabraAcertada = {false};
                    ImageButton btn_ok = (ImageButton) findViewById(R.id.btn_go);

                    while(!palabraAcertada[0]){
                        btn_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick (View v) {
                                gestorDB.asignarPapelito_Equipo(current_id, equipo_id);
                                ids_papelitos.remove(current_id);
                                palabraAcertada[0] =true;
                                Log.i("ASIGNACION_PAPELITO", "Asignación correcta"); //* LogCat.

                            }
                        });
                    }


                }

            }

        }


    }
    public void playSound(){
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.changesound);
        mediaPlayer.start();
    }

    private void startTimer(){
        timer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                mButtonStartPause.setText("Start");
                mButtonStartPause.setVisibility(View.INVISIBLE);
                mButtonReset.setVisibility(View.VISIBLE);
            }
        }.start();

        mTimerRunning = true;
        mButtonStartPause.setText("pause");
        mButtonReset.setVisibility(View.INVISIBLE);
    }

    private void pauseTimer(){
        timer.cancel();
        mTimerRunning = false;
        mButtonStartPause.setText("Start");
        mButtonReset.setVisibility(View.VISIBLE);
    }

    private void resetTimer(){
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();
        mButtonReset.setVisibility(View.INVISIBLE);
    }

    private int calculatePoints(){
        String aux = txt_timer.getText().toString().substring(2);
        int actualSeconds = Integer.parseInt(aux);
        int originalSeconds = (int) START_TIME_IN_MILLIS/1000;
        int points = originalSeconds - actualSeconds;
        return points;
    }

    private void updateCountDownText(){
        int minutes = (int) mTimeLeftInMillis / 1000 / 60;
        int seconds = (int) mTimeLeftInMillis / 1000 % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds);

        txt_timer.setText(timeLeftFormatted);
    }
}