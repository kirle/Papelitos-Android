package com.example.proyectodm.iu;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.proyectodm.R;
import com.example.proyectodm.core.DBManager;

import java.util.ArrayList;

public class RegistrarJugadores extends AppCompatActivity {
    private DBManager gestorDB;
    private MyAdapter myAdapter;
    //private SimpleCursorAdapter adaptadorDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registrar_jugadores);

        // Creating DB
        this.gestorDB = DBManager.getInstance(this.getApplicationContext()); //Singleton

        // Views for layout
        ImageButton btngoplayers = (ImageButton) findViewById(R.id.btn_goplayers);
        ImageButton btnbackplayers = (ImageButton) findViewById(R.id.btn_backplayers);
        ImageButton btnadd = (ImageButton) findViewById(R.id.btn_addPlayer);

        // Btn Listener añadir
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
                Intent myIntent = new Intent(RegistrarJugadores.this, registrarEquipos.class);
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

        String[] projections = {gestorDB.JUGADOR_id, gestorDB.JUGADOR_nombre};

        Cursor c = gestorDB.getWritableDatabase().query( gestorDB.tabla_jugador,
                projections, null, null, null, null, null );
        ListView listView = (ListView) findViewById(R.id.playerList);

        this.myAdapter = new MyAdapter(this, c);

        // Assign adapter to ListView

        listView.setAdapter(myAdapter);


    }

    @Override
    protected void onStart() {
        super.onStart();
        updatePlayers();

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
                String text = editText.getText().toString();
                myAdapter.add(text);
                /*
                TextView textView_name = (TextView) findViewById(R.id.lbl_playerName);
                textView_name.setText(text); */
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.create().show();

    }



    private void onRemove(String text){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Eliminar jugador:");
        builder.setMessage("¿Estas seguro?:");
        builder.setPositiveButton("CONFIRMAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myAdapter.remove(text);
            }
        });
        builder.setNegativeButton("CANCELAR", null);
        builder.create();

        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_layout_delete_player, null);
        builder.setView(dialogLayout);
        builder.show();



    }

    private void onModify(String pos){
        final EditText editText = new EditText(this);

        //Builds dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nuevo nombre:");
        builder.setMessage("Nombre:");
        builder.setView(editText);
        builder.setPositiveButton("+", new DialogInterface.OnClickListener() {
            @Override
            //When confirm
            public void onClick(DialogInterface dialog, int which) {
                final String text = editText.getText().toString();
                myAdapter.modify(pos,text);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    public void updatePlayers(){
        this.myAdapter.changeCursor(gestorDB.getJugadores());

    }




    class MyAdapter extends CursorAdapter {
        private LayoutInflater mLayoutInflater;

        ArrayList<String> data;
        Context context;

        public MyAdapter(Context context, Cursor c) {
            super(context, c );
            this.context = context;

            this.data = new ArrayList<>();

            mLayoutInflater = LayoutInflater.from(context);

        }


        public void add(String text){
            if(RegistrarJugadores.this.gestorDB.insertarJugador(text)){
                System.out.println("Jugador añadido");
                updatePlayers();
                notifyDataSetChanged();
            } else{
                System.out.println("ERROR al añadir jugador");
            }


        }
        public void remove(String text){
            if(RegistrarJugadores.this.gestorDB.eliminarJugador(text)){
                updatePlayers();
                notifyDataSetChanged();
                Log.e("Error" , "player deleted");

            } else{
                Log.e("Error" , "error deleting player ");
            }



        }
        public void modify(String pos, String text){
            RegistrarJugadores.this.gestorDB.modificarJugador(pos, text);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View v = mLayoutInflater.inflate(R.layout.player_row, parent, false);
            return v;        }

        @Override
        public void bindView(View v, Context context, Cursor cursor) {

            //Index of player
            String index = cursor.getString(0);

            System.out.println("TEXT ---> " + index);
            TextView txtView = (TextView) v.findViewById(R.id.lbl_playerName);

            //Text from database for first view
            String text = gestorDB.getJugador(Integer.valueOf(index));


            txtView.setText(text);


            //On Player image icon (delete) click
            ImageView delete_image_view = (ImageView) v.findViewById(R.id.btn_delete_player);
            delete_image_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRemove(index);
                }
            });


            //On player textView click

            txtView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int i = Integer.valueOf(index);
                    onModify(index);


                }
            });





        }


    }


}
