package com.example.proyectodm.iu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.proyectodm.R;

public class RegistrarJugadores extends AppCompatActivity {
    String[] data = new String[]{  "Prueba1" , "Prueba2", "Prueba3", "Prueba4", "Prueba5", "Prueba6", "Prueba7"  };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registrar_jugadores);

        ListView listView = (ListView) findViewById(R.id.playerList);
        ImageButton btngoplayers = (ImageButton) findViewById(R.id.btn_goplayers);
        ImageButton btnbackplayers = (ImageButton) findViewById(R.id.btn_backplayers);

        btngoplayers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(RegistrarJugadores.this, registrar_equipos.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                RegistrarJugadores.this.startActivity(myIntent);
            }
        });

        btnbackplayers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent2 = new Intent(RegistrarJugadores.this, MainActivity.class);
                myIntent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                RegistrarJugadores.this.startActivity(myIntent2);
            }
        });

        MyAdapter myAdapter = new MyAdapter();
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_row, R.id.list, data);
        // Assign adapter to ListView

        listView.setAdapter(myAdapter);
    }
    public void playSound(){
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.changesound);
        mediaPlayer.start();
    }


    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return data.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            view = getLayoutInflater().inflate(R.layout.player_row, null);
            TextView textView_name = (TextView) view.findViewById(R.id.lbl_playerName);
            textView_name.setText(data[position]);

            return view;
        }
    }


}

