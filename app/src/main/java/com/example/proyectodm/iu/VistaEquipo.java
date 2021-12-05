package com.example.proyectodm.iu;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.proyectodm.R;
import com.example.proyectodm.core.DBManager;

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
        Cursor c = gestorDB.getJugadoresDisponibles();

        listView.setAdapter(myAdapter);
    }

    public class CustomListAdapter extends CursorAdapter {
        private DBManager gestorDB;
        private LayoutInflater mLayoutInflater;
        Context context;
        int rowLayoutId;

        public CustomListAdapter (Context context, Cursor cursor) {
            super(context, cursor);
            this.context = context;
            mLayoutInflater = LayoutInflater.from(context);
            this.gestorDB = DBManager.getInstance(context);
        }

        //  ** CRUD TEAMS **

        public void addTeam(String teamName){
            if(gestorDB.insertarEquipo(teamName)){
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

        public void removeTeam(String teamName) {
            if (this.gestorDB.eliminarEquipo(teamName)) {
                updateTeams();
                notifyDataSetChanged();
            } else {
                System.err.println("ERROR ELIMINANDO EQUIPO");
            }
        }

        @Override
        public View newView (Context context, Cursor cursor, ViewGroup parent) {
            View v = mLayoutInflater.inflate(rowLayoutId, parent, false);
            return v;
        }

        @Override
        public void bindView (View view, Context context, Cursor cursor) {
            String index = cursor.getString(0);

            TextView txtView = (TextView) view.findViewById(R.id.lbl_playerName);
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