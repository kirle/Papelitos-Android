package com.example.proyectodm.iu;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectodm.R;
import com.example.proyectodm.core.DBManager;

import java.util.ArrayList;

public class MostrarPuntuaciones extends AppCompatActivity {
    private DBManager gestorDB;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mostrar_puntuaciones);

        this.gestorDB = DBManager.getInstance(this.getApplicationContext());

        ImageButton btngo = (ImageButton) findViewById(R.id.btn_go);
        ImageButton btnback = (ImageButton) findViewById(R.id.btn_back);

        btngo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MostrarPuntuaciones.this, MainActivity.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                MostrarPuntuaciones.this.startActivity(myIntent);
            }
        });

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent2 = new Intent(MostrarPuntuaciones.this, RoundTimer.class);
                myIntent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                MostrarPuntuaciones.this.startActivity(myIntent2);
            }
        });

        //List Adapter
        String[] projections = {gestorDB.EQUIPO_id_fk3, gestorDB.puntuacion};
        Cursor c = gestorDB.getWritableDatabase().query(gestorDB.tabla_puntuacion, projections, null,
                null,null,null,null);
        ListView listView = (ListView) findViewById(R.id.pointsList);
        this.myAdapter = new MyAdapter(this,c);

        //Assign adapter to ListView

        listView.setAdapter(myAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void updatePoints(){
        this.myAdapter.changeCursor(gestorDB.getPuntuaciones());
    }

    class MyAdapter extends CursorAdapter{
        private LayoutInflater mLayoutInflater;

        ArrayList<String> data;
        Context context;

        public MyAdapter(Context context, Cursor c){
            super(context, c);
            this.context = context;
            this.data = new ArrayList<>();
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View v = mLayoutInflater.inflate(R.layout.points_row, parent, false);
            return v;        }

        @Override
        public void bindView(View v, Context context, Cursor cursor) {

            //Index of player
            String index = cursor.getString(0);

            System.out.println("TEXT ---> " + index);
            TextView txtView_puntuacion = (TextView) v.findViewById(R.id.txt_points);

            //Text from database for first view
            String puntuacion = gestorDB.getPuntuacion(Integer.valueOf(index));

            txtView_puntuacion.setText(puntuacion);

            TextView txtView_nombreEquipo = (TextView) v.findViewById(R.id.lbl_teamName);
            String nombre = gestorDB.getNombreEquipo(index);
            txtView_nombreEquipo.setText(nombre);
        }

    }
}


