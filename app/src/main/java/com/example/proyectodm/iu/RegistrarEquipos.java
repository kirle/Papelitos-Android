package com.example.proyectodm.iu;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectodm.R;
import com.example.proyectodm.core.DBManager;

import java.util.ArrayList;


public class RegistrarEquipos extends AppCompatActivity {

    private CustomListAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_equipos);

        ListView listView = (ListView) findViewById(R.id.list);

        DBManager gestorDB= DBManager.getInstance(this);
        String[] projections = {gestorDB.EQUIPO_id, gestorDB.EQUIPO_nombre};
        Cursor c = gestorDB.getWritableDatabase().query( gestorDB.tabla_equipo,
                projections, null, null, null, null, null );

        this.myAdapter = new CustomListAdapter(this,c);
        listView.setAdapter(myAdapter);

        // ** LISTENERS **

        // * Previous view listener
        ImageButton btn_back = (ImageButton) findViewById(R.id.imageButtonLeft);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound();

                Intent myIntent = new Intent(RegistrarEquipos.this, GestionPapelitos.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                RegistrarEquipos.this.startActivity(myIntent);

            }
        });

        // * Next view listener
        ImageButton btn_go = (ImageButton) findViewById(R.id.imageButtonRight);
        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound();
                Intent myIntent2 = new Intent(RegistrarEquipos.this, Instructions.class);
                myIntent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                RegistrarEquipos.this.startActivity(myIntent2);
            }
        });

        // * Add listener
        ImageButton btn_add = (ImageButton) findViewById(R.id.btn_addTeam);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gestorDB.getJugadoresDisponibles().getCount() == 0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegistrarEquipos.this);
                    builder.setMessage("¡Ya no quedan jugadores sin asignar a un equipo!");
                    builder.create().show();
                } else{
                    onAdd();
                }
            }
        });

    }

    // ** LISTENERS FUNCTIONS **

    public void onAdd(){
        Intent intent = new Intent(RegistrarEquipos.this, VistaEquipo.class);
        startActivity(intent);

    }


    public void playSound(){
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.changesound);
        mediaPlayer.start();
    }


    // ****
    // CUSTOM ADAPTER FOR LISTVIEW
    // ****

    public class CustomListAdapter extends CursorAdapter {
        private DBManager gestorDB;
        private LayoutInflater mLayoutInflater;
        Context context;
        ArrayList<String> ids_equipos = new ArrayList<>();

        public CustomListAdapter (Context context, Cursor cursor) {
            super(context, cursor);
            this.context = context;
            mLayoutInflater = LayoutInflater.from(context);
            this.gestorDB = DBManager.getInstance(context);
        }

        //  ** CRUD TEAMS **


        public void updateTeams(){
            this.changeCursor(gestorDB.getEquipos());
            notifyDataSetChanged();
        }

        public void removeTeam(String teamId) {
            Cursor c = gestorDB.getJugadoresFromEquipo(teamId);
            //Cursor to array list
            ArrayList<String> ids_jugadores = new ArrayList<String>();
            c.moveToFirst();
            while(!c.isAfterLast()) {
                ids_jugadores.add(c.getString(0)); //add the item
                c.moveToNext();
            }

            for (String jugador:ids_jugadores) {
                this.gestorDB.eliminarAsignacionJugador_Equipo(jugador);
            }
            //System.out.println("JUGADORES: " + ids_jugadores.toString());


            if (this.gestorDB.eliminarEquipo(teamId)) {
                updateTeams();
                notifyDataSetChanged();
            } else {
                System.err.println("ERROR ELIMINANDO EQUIPO");
            }
        }

        @Override
        public View newView (Context context, Cursor cursor, ViewGroup parent) {
            View v = mLayoutInflater.inflate(R.layout.team_row, parent, false);
            String index = cursor.getString(0);
            ids_equipos.add(index);

            return v;
        }

        @Override
        public void bindView (View view, Context context, Cursor cursor) {
            String index = cursor.getString(0);

            TextView lbl_player = (TextView) view.findViewById(R.id.lbl_team_name);
            lbl_player.setText(gestorDB.getEquipo(Integer.valueOf(index))); // setting row text

            ImageView delete_image_view = (ImageView) view.findViewById(R.id.btn_delete_team);


            delete_image_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    builder.setTitle("Eliminar equipo:");
                    builder.setMessage("¿Estas seguro?:");
                    builder.setPositiveButton("CONFIRMAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            removeTeam(index);
                        }
                    });
                    builder.setNegativeButton("CANCELAR", null);
                    builder.create();

                    LayoutInflater inflater = LayoutInflater.from(context);
                    View dialogLayout = inflater.inflate(R.layout.dialog_layout_delete_player, null);
                    builder.setView(dialogLayout);
                    builder.show();

                }
            });

        }

    }




}



