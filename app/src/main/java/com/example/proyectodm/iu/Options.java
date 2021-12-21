package com.example.proyectodm.iu;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectodm.R;

public class Options extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        
        Button btn_restablecer = (Button) findViewById(R.id.btn_restablecer);
        btn_restablecer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                RoundTimer.START_TIME_IN_MILLIS = 30000;
                Toast.makeText(Options.this, "Opciones restablecidas", Toast.LENGTH_SHORT).show();
            }
        });
        
        ImageButton btn_back = (ImageButton) findViewById(R.id.btn_backpapelitos);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Options.this, MainActivity.class);
                Options.this.startActivity(intent);
            }
        });

        final boolean[] clicked = {false};
        Button editButton = (Button) findViewById(R.id.btn_editarTiempoTimer);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Options.this);
                builder.setMessage("Duración cronometro en segundos:");
                EditText edtxt = new EditText(Options.this);
                builder.setView(edtxt);
                builder.setCancelable(false);
                builder.setNegativeButton("Cancelar", null);
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick (DialogInterface dialog, int which) {
                        String edtxtValue = edtxt.getText().toString();
                        if(edtxtValue.matches("\\d+(?:\\.\\d+)?")){
                            long longValue = Long.parseLong(edtxtValue);
                            longValue = longValue * 1000;
                            RoundTimer.START_TIME_IN_MILLIS = longValue;
                            Toast.makeText(Options.this, "Duración cronometro modificada", Toast.LENGTH_SHORT).show();
                        } else{
                            Toast.makeText(Options.this,"Duración no valida", Toast.LENGTH_SHORT).show();
                        }




                    }
                });
                builder.create().show();
            }
        });

    }
}