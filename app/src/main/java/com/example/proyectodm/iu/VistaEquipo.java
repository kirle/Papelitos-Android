package com.example.proyectodm.iu;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectodm.R;
import com.example.proyectodm.core.DBManager;

import java.util.ArrayList;

public class VistaEquipo extends AppCompatActivity {
    private DBManager gestorDB;
    private CustomListAdapter myAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_equipo);

        this.gestorDB = DBManager.getInstance(this.getApplicationContext());
        ListView listView = (ListView) findViewById(R.id.list_players);

        //  db query for list
        String[] projections = {gestorDB.JUGADOR_id, gestorDB.JUGADOR_nombre};
        Cursor c = this.gestorDB.getJugadoresDisponibles();
        this.myAdapter = new CustomListAdapter(this, c);
        listView.setAdapter(myAdapter);

        // ** LISTENERS

        //Listener btn ok
        Button btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                //Add selected to the team and register team
                EditText txt_nombreEquipo = (EditText) findViewById(R.id.txt_nombreEquipo);
                AlertDialog.Builder builder = new AlertDialog.Builder(VistaEquipo.this);

                if(myAdapter.ids_jugadores.size() <= 0){
                    builder.setMessage("Tienes que seleccionar al menos un jugador");
                    builder.create().show();
                } else if(txt_nombreEquipo.length() <= 0){
                    builder.setMessage("Nombre del equipo no es válido");
                    builder.create().show();
                }else {
                    myAdapter.addTeam(txt_nombreEquipo.getText().toString());
                    Intent intent = new Intent(VistaEquipo.this, RegistrarEquipos.class);
                    startActivity(intent);
                }
            }
        });

        //Listener btn cancel
        Button btn_cancelar = (Button) findViewById(R.id.btn_cancelar);
        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                // Go back to the team activity without do nothing
                Intent intent = new Intent(VistaEquipo.this, RegistrarEquipos.class);
                startActivity(intent);
            }
        });

    }

    public class CustomListAdapter extends CursorAdapter {
        private DBManager gestorDB;
        private LayoutInflater mLayoutInflater;
        private Context context;
        private ArrayList<String> ids_jugadores;

        public CustomListAdapter (Context context, Cursor cursor) {
            super(context, cursor);
            this.context = context;
            mLayoutInflater = LayoutInflater.from(context);
            this.gestorDB = DBManager.getInstance(context);
            ids_jugadores = new ArrayList<>();
        }

        public void addTeam(String teamName){
            if(gestorDB.insertarEquipo(teamName) && gestorDB.insertarPuntuacion(gestorDB.getIdFromTeamName(teamName))){
                String equipo_id = gestorDB.getIdFromTeamName(teamName);

                for (int i=0; i<ids_jugadores.size(); i++){
                    System.out.println("id jugador: " + ids_jugadores.get(i));
                    System.out.println("EQUIPO ID:" + equipo_id);
                    gestorDB.asignarJugador_Equipo(ids_jugadores.get(i), equipo_id);
                }

                System.out.println("Equipo añadido");
                updateTeams();
                notifyDataSetChanged();
            } else{
                System.out.println("ERROR al añadir equipo");
            }
        }



        public void updateTeams(){
            this.changeCursor(gestorDB.getEquipos());
            notifyDataSetChanged();
        }

        @Override
        public View newView (Context context, Cursor cursor, ViewGroup parent) {
            View v = mLayoutInflater.inflate(R.layout.select_player_row, parent, false);
            return v;
        }

        @Override
        public void bindView (View view, Context context, Cursor cursor) {
            String index = cursor.getString(0);
            final boolean[] clicked = {false};
            TextView txtView = (TextView) view.findViewById(R.id.lbl_playerName);
            txtView.setText(gestorDB.getJugador(Integer.valueOf(index)));

            ImageView imgClickable = (ImageView) view.findViewById(R.id.img_clickable);
                imgClickable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v) {
                    if(clicked[0]){
                        clicked[0] = false;
                        imgClickable.setBackgroundResource(R.mipmap.clicable);
                        if( !(ids_jugadores.contains(index)) ) ids_jugadores.remove(index);


                    }  else {
                        clicked[0] = true;
                        imgClickable.setBackgroundResource(R.mipmap.clickableclicked);
                        ids_jugadores.add(index);

                    }
                }
            });
        }


    }

}