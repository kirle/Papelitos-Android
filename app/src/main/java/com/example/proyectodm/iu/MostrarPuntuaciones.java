package com.example.proyectodm.iu;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectodm.R;
import com.example.proyectodm.core.DBManager;

import org.w3c.dom.Text;

import java.util.ArrayList;

/*
 *** Clase MostrarPuntuaciones ***
 * Muestra las puntuaciones de cada equipo obtenidas de la BD ordenadas por cantidad
 */
public class MostrarPuntuaciones extends AppCompatActivity {
    private DBManager gestorDB;
    private MyAdapter myAdapter;
    public int pos;
    private TextView lbl_posicion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_puntuaciones);

        pos = 0;

        this.gestorDB = DBManager.getInstance(this.getApplicationContext());

        Button btn_mainmenu = (Button) findViewById(R.id.btn_backtomenu);
        btn_mainmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Intent intent = new Intent(MostrarPuntuaciones.this, MainActivity.class);
                startActivity(intent);
            }
        });


        //List Adapter


        String[] projections = {gestorDB.EQUIPO_id_fk2, gestorDB.puntuacion};

        Cursor c = gestorDB.getWritableDatabase().query(gestorDB.tabla_puntuacion, projections, null,
                null,null,null,gestorDB.puntuacion + " DESC");
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
        Context context;

        public MyAdapter(Context context, Cursor c){
            super(context, c);
            this.context = context;
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View v = mLayoutInflater.inflate(R.layout.points_row, parent, false);
            return v;        }

        @Override
        public void bindView(View v, Context context, Cursor cursor) {
            pos +=1;
            //Index of player
            String index = cursor.getString(0);

            System.out.println("TEXT ---> " + index);
            TextView txtView_puntuacion = (TextView) v.findViewById(R.id.txt_points);

            //Text from database for first view
            String puntuacion = gestorDB.getPuntuacion(Integer.valueOf(index));

            txtView_puntuacion.setText(puntuacion);

            String postxt = String.valueOf(pos);
            lbl_posicion = (TextView) v.findViewById(R.id.lbl_posicion);
            lbl_posicion.setText(postxt);

            TextView txtView_nombreEquipo = (TextView) v.findViewById(R.id.lbl_teamName);
            String nombre = gestorDB.getNombreEquipo(index);
            txtView_nombreEquipo.setText(nombre);
        }

    }
}


