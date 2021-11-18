package com.example.proyectodm.iu;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectodm.R;



public class registrar_equipos extends AppCompatActivity {

    String[] data = new String[]{"Equipo1", "Equipo2", "Equipo3", "Equipo4", "Equipo5", "Equipo6", "Caca"  };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_equipos);

        ListView listView = (ListView) findViewById(R.id.list);

        CustomAdapter customAdapter = new CustomAdapter();
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_row, R.id.list, data);
        // Assign adapter to ListView

        listView.setAdapter(customAdapter);

        ImageButton btn_back = (ImageButton) findViewById(R.id.imageButtonLeft);
        ImageButton btn_go = (ImageButton) findViewById(R.id.imageButtonRight);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound();

                Intent myIntent = new Intent(registrar_equipos.this, RegistrarJugadores.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                registrar_equipos.this.startActivity(myIntent);

            }
        });

        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound();
                Intent myIntent2 = new Intent(registrar_equipos.this, Instructions.class);
                myIntent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                registrar_equipos.this.startActivity(myIntent2);
            }
        });

    }

    public void playSound(){
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.changesound);
        mediaPlayer.start();
    }



    class CustomAdapter extends BaseAdapter{

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
            view = getLayoutInflater().inflate(R.layout.list_row, null);
            TextView textView_name = (TextView) view.findViewById(R.id.list_item_name);
            textView_name.setText(data[position]);

            return view;
        }
    }

}




