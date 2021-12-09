package com.example.proyectodm.iu;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.proyectodm.R;
import com.example.proyectodm.core.DBManager;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public class RoundTimer extends AppCompatActivity {

    public static final long START_TIME_IN_MILLIS = 30000;

    private TextView txt_timer;
    private Button mButtonStartPause;
    private Button mButtonReset;

    private CountDownTimer timer;

    private Boolean mTimerRunning = false;
    private Boolean palabraAcertada;

    private LinearLayout layout_current_word;
    private TextView txt_current_word;


    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private ArrayList<String> ids_equipos;
    private DBManager gestorDB;
    private  String leftTime;
    private boolean empezar;
    private int numEquipos;
    private int turno;
    private TextView lbl_puntuacion;
    ArrayList<String> array_nombres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_round_timer);

        numEquipos = 0;
        turno = 0;

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

        array_nombres = gestorDB.getNombreEquipos();
        numEquipos = array_nombres.size();

        pulsaParaEmpezar();

        // ** Listeners
        empezar = false;

        TextView txt_nombreEquipo = (TextView) findViewById(R.id.txt_equipoActual);
        txt_nombreEquipo.setText(array_nombres.get(turno));

        lbl_puntuacion = (TextView) findViewById(R.id.lbl_puntuacion);
        lbl_puntuacion.setText("0");


        // ** BOTON EMPEZAR
        layout_current_word = (LinearLayout) findViewById(R.id.lyt_words_container);
        layout_current_word.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {

            }
        });

        // * BUTTON MENU
        ImageButton btn_back = (ImageButton) findViewById(R.id.btn_menu);
        this.registerForContextMenu(btn_back);



        // * BUTTON PALABRA ACERTADA
        ImageButton btn_ok = (ImageButton) findViewById(R.id.btn_go);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                if(Integer.valueOf(txt_timer.getText().toString()) > 0){
                    actualizarPalabra();
                    gestorDB.modificarPuntuacion(ids_equipos.get(turno), 1);
                    lbl_puntuacion.setText(gestorDB.getPuntuacion(Integer.valueOf(ids_equipos.get(turno))));

                } else {
                    Toast.makeText(RoundTimer.this, "SE ACABÓ EL TIEMPO", Toast.LENGTH_SHORT).show();

                }

            }
        });

        // ** BTN NEXT EQUIPO
        Button btn_nextEquipo = (Button) findViewById(R.id.btn_nextEquipo);
        btn_nextEquipo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                checkTimer();
            }
        });


        //updateCountDownText();




    }

    // ** Menu contextual
    public void onCreateContextMenu(ContextMenu contxt, View v, ContextMenu.ContextMenuInfo cmi){
        if(v.getId() == R.id.btn_menu){
            this.getMenuInflater().inflate(R.menu.menucontextual, contxt);
            contxt.setHeaderTitle("MENU");
        }
    }
    @Override
    public boolean onContextItemSelected(MenuItem menuItem)
    {
        boolean toret = false;
        switch( menuItem.getItemId() ) {
            case R.id.context_volverMenu:
                Intent myIntent2 = new Intent(RoundTimer.this, Instructions.class);
                ActivityOptions.makeSceneTransitionAnimation(RoundTimer.this).toBundle();
                RoundTimer.this.startActivity(myIntent2);
                toret = true;
                break;
            case R.id.context_resetTimer:
                restartTimer();
                toret = true;
                break;
        }
        return toret;
    }


    public void pulsaParaEmpezar(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("PULSA PARA EMPEZAR");
        builder.setCancelable(false);
        builder.setPositiveButton("¡EMPEZAR!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick (DialogInterface dialog, int which) {
                empezar = true;

                startTimer();
                updateCountDownText();
                actualizarPalabra();

            }
        });
        builder.create().show();
    }

    public void checkTimer(){
        TextView txt_nombreEquipo = (TextView) findViewById(R.id.txt_equipoActual);
        txt_nombreEquipo.setText(array_nombres.get(turno));

        TextView txt_timer = (TextView) findViewById(R.id.txt_timer);
        leftTime = txt_timer.getText().toString();

        if (Integer.valueOf(leftTime) == 0){
            if(turno == numEquipos - 1){
                Toast.makeText(RoundTimer.this, "FIN PARTIDA", Toast.LENGTH_SHORT);
                sendMessage();
            } else {
                turno += 1;
                txt_nombreEquipo.setText(array_nombres.get(turno));
                lbl_puntuacion.setText("0");
                actualizarPalabra();
                resetTimer();
                pulsaParaEmpezar();
            }
        }

    }

    public void sendMessage(){
        Intent intent = new Intent(RoundTimer.this, MostrarPuntuaciones.class);
        startActivity(intent);
    }

    public void actualizarPalabra(){
        ArrayList<String> ids_papelitos = new ArrayList<>();

        Cursor cursor_papelitos = this.gestorDB.getPapelitosDisponibles();

        cursor_papelitos.moveToFirst();
        while(!cursor_papelitos.isAfterLast()) {
            ids_papelitos.add(cursor_papelitos.getString(0)); //add the item
            cursor_papelitos.moveToNext();

        }
        int random = ThreadLocalRandom.current().nextInt(0, ids_papelitos.size());
        String current_id = ids_papelitos.get(random);

        String papelito_text = this.gestorDB.getPapelito(Integer.valueOf(current_id));
        txt_current_word = (TextView) findViewById(R.id.current_word);
        txt_current_word.setText(papelito_text);


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
                //mButtonStartPause.setText("Start");
                //mButtonStartPause.setVisibility(View.INVISIBLE);
                //mButtonReset.setVisibility(View.VISIBLE);
            }
        }.start();

        mTimerRunning = true;
        //mButtonStartPause.setText("pause");
        //mButtonReset.setVisibility(View.INVISIBLE);
    }

    private void pauseTimer(){
        timer.cancel();
        mTimerRunning = false;
        //mButtonStartPause.setText("Start");
        //mButtonReset.setVisibility(View.VISIBLE);
    }

    private void resetTimer(){
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();
        //mButtonReset.setVisibility(View.INVISIBLE);
    }

    private void restartTimer(){
        if(Integer.valueOf(txt_timer.getText().toString()) > 0){
            Toast.makeText(this, "Debes esperar a que acabe el conteo actual", Toast.LENGTH_SHORT).show();
        } else{
            mTimeLeftInMillis = START_TIME_IN_MILLIS;
            updateCountDownText();
            startTimer();
        }
        
    }

    private int calculatePoints(){
        String aux = txt_timer.getText().toString().substring(2);
        int actualSeconds = Integer.parseInt(aux);
        int originalSeconds = (int) START_TIME_IN_MILLIS/1000;
        int points = originalSeconds - actualSeconds;
        return points;
    }

    private void updateCountDownText(){

        int seconds = (int) mTimeLeftInMillis / 1000 % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(),"%02d",  seconds);

        txt_timer.setText(timeLeftFormatted);
    }
}