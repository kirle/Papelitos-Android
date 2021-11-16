package com.example.proyectodm.iu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.proyectodm.R;

public class RegistrarJugadores extends AppCompatActivity {
    String[] data = new String[]{   "Equipo1", "Equipo2", "Equipo3", "Equipo4", "Equipo5", "Equipo6", "Caca"  };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_jugadores);
        ListView listView = (ListView) findViewById(R.id.list);

        RegistrarJugadores.CustomAdapter customAdapter = new RegistrarJugadores.CustomAdapter();
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_row, R.id.list, data);
        // Assign adapter to ListView

        listView.setAdapter(customAdapter);
    }

    class CustomAdapter extends BaseAdapter {

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

