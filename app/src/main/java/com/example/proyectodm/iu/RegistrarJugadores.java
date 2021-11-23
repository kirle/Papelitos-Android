package com.example.proyectodm.iu;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.proyectodm.R;

import java.util.ArrayList;
import java.util.Arrays;

public class RegistrarJugadores extends AppCompatActivity {
    private DBManager gestorDB;
    private  MyAdapter myAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registrar_jugadores);

        //Creating DB

        this.gestorDB = new DBManager( this.getApplicationContext());


        ListView listView = (ListView) findViewById(R.id.playerList);
        ImageButton btngoplayers = (ImageButton) findViewById(R.id.btn_goplayers);
        ImageButton btnbackplayers = (ImageButton) findViewById(R.id.btn_backplayers);
        ImageButton btnadd = (ImageButton) findViewById(R.id.btn_addPlayer);


        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAdd();
            }
        });

        btngoplayers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound();
                Intent myIntent = new Intent(RegistrarJugadores.this, registrar_equipos.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                RegistrarJugadores.this.startActivity(myIntent);
            }
        });

        btnbackplayers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound();
                Intent myIntent2 = new Intent(RegistrarJugadores.this, MainActivity.class);
                myIntent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                RegistrarJugadores.this.startActivity(myIntent2);
            }
        });

        //List Adapter

        myAdapter = new MyAdapter(this, new ArrayList<String>(Arrays.asList(DBManager.JUGADOR_nombre, DBManager.JUGADOR_id))
        );
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_row, R.id.list, data);
        // Assign adapter to ListView

        listView.setAdapter(myAdapter);


    }
    public void playSound(){
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.changesound);
        mediaPlayer.start();
    }
    private void onAdd(){
        final EditText editText = new EditText(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Introduce nombre jugador");
        builder.setMessage("Nombre jugador:");
        builder.setView(editText);
        builder.setPositiveButton("+", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String text = editText.getText().toString();
                myAdapter.add(text);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.create().show();

    }

    private void onRemove(int pos){
        myAdapter.remove(pos);
    }

    private void onModify(int pos){
        final EditText editText = new EditText(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nuevo nombre:");
        builder.setMessage("Nombre:");
        builder.setView(editText);
        builder.setPositiveButton("+", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String text = editText.getText().toString();
                myAdapter.modify(pos,text);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }




    class MyAdapter extends ArrayAdapter<String> {
        ArrayList<String>  data;
        Context context;
        public MyAdapter(Context context, ArrayList<String> data) {
            super(context, R.layout.player_row, data);
            this.data = data;
            this.context = context;
        }
        @Override
        public int getCount() {
            return data.size();
        }

        public void add(String text){
            this.data.add(text);
            RegistrarJugadores.this.gestorDB.insertarJugador(text);

            notifyDataSetChanged();

        }
        public void remove(int pos){
            this.data.remove(pos);
            notifyDataSetChanged();

        }
        public void modify(int pos, String text){
            this.data.set(pos, text);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int i = position;

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.player_row, parent, false);

            TextView textView_name = (TextView) v.findViewById(R.id.lbl_playerName);
            textView_name.setText(data.get(i));

            //On player textView click
            textView_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onModify(i);
                }
            });

            //On Player image icon (delete) click
            ImageView delete_image_view = (ImageView) v.findViewById(R.id.btn_delete_player);
            delete_image_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myAdapter.remove(i);
                }
            });


            return v;
        }


    }


}

