package com.example.proyectodm.iu;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectodm.R;
import com.example.proyectodm.core.DBManager;

import java.util.ArrayList;

public class GestionPapelitos extends AppCompatActivity {
    private DBManager gestorDB;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gestion_papelitos);

        this.gestorDB = DBManager.getInstance(this.getApplicationContext());

        ImageButton btngopapelitos = (ImageButton) findViewById(R.id.btn_gopapelitos);
        ImageButton btnbackpapelitos = (ImageButton) findViewById(R.id.btn_backpapelitos);
        ImageButton btnadd = (ImageButton) findViewById(R.id.btn_addPapelito);

        //listener add
        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAdd();
            }
        });

        btngopapelitos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound();
                Intent myIntent = new Intent(GestionPapelitos.this, RegistrarEquipos.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                GestionPapelitos.this.startActivity(myIntent);
            }
        });

        btnbackpapelitos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound();
                Intent myIntent2 = new Intent(GestionPapelitos.this, RegistrarJugadores.class);
                myIntent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                GestionPapelitos.this.startActivity(myIntent2);
            }
        });

        //Inicializacion del Adapter
        String[] projections = {gestorDB.PAPELITO_id, gestorDB.PAPELITO_texto};
        Cursor c = gestorDB.getWritableDatabase().query( gestorDB.tabla_papelito,
                projections, null, null, null, null, null );
        ListView listView = (ListView) findViewById(R.id.papersList);

        this.myAdapter = new MyAdapter(this, c);


        listView.setAdapter(myAdapter);

    }

    public void playSound(){
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.changesound);
        mediaPlayer.start();
    }

    private void onAdd(){
        final EditText editText = new EditText(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Introduce texto a representar");
        builder.setMessage("Papelito:");
        builder.setView(editText);
        builder.setPositiveButton("+", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = editText.getText().toString();
                if(text.length() == 0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(GestionPapelitos.this);
                    builder.setMessage("¡Papelito no puede estar vacio!");
                    builder.create().show();
                } else{
                    myAdapter.add(text);
                }

            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.create().show();

    }

    private void onRemove(String text){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Eliminar papelito:");
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
        View dialogLayout = inflater.inflate(R.layout.dialog_layout_delete_paper, null);
        builder.setView(dialogLayout);
        builder.show();

    }

    private void onModify(String pos){
        final EditText editText = new EditText(this);

        //Builds dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nueva palabra a representar:");
        builder.setMessage("Papelito:");
        builder.setView(editText);
        builder.setPositiveButton("+", new DialogInterface.OnClickListener() {
            @Override
            //When confirm
            public void onClick(DialogInterface dialog, int which) {
                final String text = editText.getText().toString();
                if(text.length() == 0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(GestionPapelitos.this);
                    builder.setMessage("¡Nombre papelito no puede ser vacio!");
                    builder.create().show();
                } else{
                    myAdapter.modify(pos,text);
                }
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    private void updatePapelitos(){
        this.myAdapter.changeCursor(gestorDB.getPapelitos());
    }

    class MyAdapter extends CursorAdapter{
        private LayoutInflater mLayoutInflater;

        ArrayList<String> data;
        Context context;

        public MyAdapter(Context context, Cursor c){
            super(context,c);
            this.context = context;
            this.data = new ArrayList<>();
            mLayoutInflater = LayoutInflater.from(context);
        }

        public void add(String text){
            if(GestionPapelitos.this.gestorDB.insertarPapelito(text)){
                System.out.println("Papelito añadido");
                updatePapelitos();
                notifyDataSetChanged();
            }
            else{
                System.out.println("Error al añadir papelito");
            }
        }

        public void remove(String text){
            if(GestionPapelitos.this.gestorDB.eliminarPapelito(text)){
                updatePapelitos();
                notifyDataSetChanged();
                Log.e("Error","Error al eliminar papelito");
            }
            else{
                Log.e("Error","Error al eliminar papelito");
            }
        }

        public void modify(String pos, String text){
            GestionPapelitos.this.gestorDB.modificarPapelito(pos, text);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View v = mLayoutInflater.inflate(R.layout.paper_row, parent, false);
            return v;        }

        @Override
        public void bindView(View v, Context context, Cursor cursor) {
            //Index of paper

            String index = cursor.getString(0);
            System.out.println("index = "+ index);

            System.out.println("TEXT ---> " + index);
            TextView txtView = (TextView) v.findViewById(R.id.lbl_paperName);

            //Text from database for first view
            String text = gestorDB.getPapelito(Integer.valueOf(index));
            if(text!=null) {
                System.out.print(text);
            }else{System.out.print("text es null");}

            txtView.setText(text);

            //On Player image icon (delete) click
            ImageView delete_image_view = (ImageView) v.findViewById(R.id.btn_delete_paper);
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
