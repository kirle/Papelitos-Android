package com.example.proyectodm.iu;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectodm.R;
import com.example.proyectodm.core.DBManager;

import java.util.ArrayList;


public class RegistrarEquipos extends AppCompatActivity {

    private DBManager gestorDB;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_equipos);

        this.gestorDB = DBManager.getInstance(this.getApplicationContext());


        ListView listView = (ListView) findViewById(R.id.list);

        String[] projections = {gestorDB.JUGADOR_id, gestorDB.JUGADOR_nombre};
        Cursor c = gestorDB.getWritableDatabase().query( gestorDB.tabla_jugador,
                projections, null, null, null, null, null );

        this.myAdapter = new MyAdapter(this,c);
        listView.setAdapter(myAdapter);

        ImageButton btn_back = (ImageButton) findViewById(R.id.imageButtonLeft);
        ImageButton btn_go = (ImageButton) findViewById(R.id.imageButtonRight);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound();

                Intent myIntent = new Intent(RegistrarEquipos.this, RegistrarJugadores.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                RegistrarEquipos.this.startActivity(myIntent);

            }
        });

        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound();
                Intent myIntent2 = new Intent(RegistrarEquipos.this, Instructions.class);
                myIntent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                RegistrarEquipos.this.startActivity(myIntent2);
            }
        });

    }

    public void playSound(){
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.changesound);
        mediaPlayer.start();
    }

    public void updateTeams(){
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

        }//hola


        public void add(String text){
            if(RegistrarEquipos.this.gestorDB.insertarJugador(text)){
                System.out.println("Jugador añadido");
                updateTeams();
                notifyDataSetChanged();
            } else{
                System.out.println("ERROR al añadir jugador");
            }


        }
        public void remove(String text){
            if(RegistrarEquipos.this.gestorDB.eliminarJugador(text)){
                updateTeams();
                notifyDataSetChanged();
                Log.e("Error" , "player deleted");

            } else{
                Log.e("Error" , "error deleting player ");
            }



        }
        public void modify(String pos, String text){
            RegistrarEquipos.this.gestorDB.modificarJugador(pos, text);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View v = mLayoutInflater.inflate(R.layout.list_row, parent, false);
            return v;        }

        @Override
        public void bindView(View v, Context context, Cursor cursor) {

            //Index of player
            String index = cursor.getString(0);



            //On Player image icon (delete) click
            ImageView delete_image_view = (ImageView) v.findViewById(R.id.btn_delete_player);
            delete_image_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    builder.setTitle("Eliminar equipo:");
                    builder.setMessage("¿Estas seguro?:");
                    builder.setPositiveButton("CONFIRMAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            remove(index);
                        }
                    });
                    builder.setNegativeButton("CANCELAR", null);
                    builder.create();

                    LayoutInflater inflater = getLayoutInflater();
                    View dialogLayout = inflater.inflate(R.layout.dialog_layout_delete_player, null);
                    builder.setView(dialogLayout);
                    builder.show();

                }
            });

        }


    }




}



