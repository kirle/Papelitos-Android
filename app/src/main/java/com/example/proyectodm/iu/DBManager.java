package com.example.proyectodm.iu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBManager extends SQLiteOpenHelper {
    public static final String db_name = "papelitos";
    public static final int db_version = 1;
    public static final String tabla_jugador = "jugador";
    public static final String tabla_equipo = "equipo";
    public static final String tabla_puntuacion = "puntuacion";

    /*Campos de tabla JUGADOR*/
    public static String JUGADOR_id = "_id";
    public static String JUGADOR_nombre = "nombre";
    public static String EQUIPO_id_fk = "id_equipo";

    /*Campos tabla EQUIPO, existe relacion 1 a n entre equipo y jugadores*/
    public static String EQUIPO_id = "_id";
    public static String EQUIPO_nombre = "equipo_nombre";

    /*Campos tabla PUNTUACION*/
    public static String EQUIPO_id_fk2 = "_id";
    public static String puntuacion = "punt";

    SQLiteDatabase db;

    public DBManager(Context context){
        super(context, db_name, null, db_version);
        //context.deleteDatabase("papelitos");

    }
    @Override
    public void onCreate(SQLiteDatabase db){
        Log.i("DBManager", "Creando BBDD "+db_name+" v"+db_version);

        //db.setForeignKeyConstraintsEnabled(true);

        /*Creacion tabla Jugador*/
        try{
            db.beginTransaction();
            db.execSQL("CREATE TABLE IF NOT EXISTS " + tabla_jugador + "( "
                    + JUGADOR_id + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + JUGADOR_nombre + " TEXT NOT NULL,"
                    + EQUIPO_id_fk + " INTEGER , FOREIGN KEY ("+EQUIPO_id_fk+") REFERENCES " + tabla_equipo + "("+EQUIPO_id+") ON DELETE CASCADE"
                    + ")"
            );
            db.setTransactionSuccessful();
        }catch (SQLException exec){
            Log.e("DBManager.onCreate", "Creando "+tabla_jugador+": "+exec.getMessage());
        }finally {
            db.endTransaction();
        }

        /*Creacion tabla Equipo*/
        try{
            db.beginTransaction();
            db.execSQL("CREATE TABLE IF NOT EXISTS " + tabla_equipo + "("
                    + EQUIPO_id + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                    + EQUIPO_nombre + " TEXT NOT NULL"
                    + ")"
            );
            db.setTransactionSuccessful();
        }catch (SQLException exec){
            Log.e("DBManager.onCreate", "Creando "+tabla_equipo+": "+exec.getMessage());
        }finally {
            db.endTransaction();
        }

        /*Creacion tabla Puntuacion*/
        try{
            db.beginTransaction();
            db.execSQL("CREATE TABLE IF NOT EXISTS " + tabla_puntuacion + "("
                    + puntuacion + " INTEGER NOT NULL DEFAULT 0,"
                    + EQUIPO_id_fk2 +" INTEGER NOT NULL, FOREIGN KEY ("+EQUIPO_id_fk2+") REFERENCES " + tabla_equipo + "("+EQUIPO_id+") ON DELETE CASCADE"
                    + ")"
            );
            db.setTransactionSuccessful();
        }catch (SQLException exec){
            Log.e("DBManager.onCreate", "Creando "+tabla_puntuacion+": "+exec.getMessage());
        }finally {
            db.endTransaction();
        }
    }

    public void onUpgrade(SQLiteDatabase db, int v1, int v2){
        Log.i("DBManager", "DB: "+db_name+":v "+v1+ " -> "+v2);
        try{
            db.beginTransaction();
            db.execSQL("DROP TABLE IF EXISTS " + tabla_jugador);
            db.setTransactionSuccessful();
        }catch(SQLException exec){
            Log.e("DBManager.onUpgrade", exec.getMessage());
        }finally {
            db.endTransaction();
        }
        this.onCreate(db);
    }

    //nuevo insertar Jugador
    public boolean insertarJugador(String nombre){ //registrar jugador

        boolean toret = false;
        Cursor cursor = null;
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(JUGADOR_nombre, nombre);
        values.put(EQUIPO_id_fk, "null");
        //values.put(JUGADOR_id, num);

        try{
            db.beginTransaction();
            cursor = db.query(tabla_jugador,
                    null,
                    JUGADOR_nombre + "=?",
                    new String[]{nombre},
                    null, null, null, null); //Se busca si existe ya ese jugador
            if(cursor.getCount() > 0){
                db.update(tabla_jugador, values, JUGADOR_nombre + "=?", new String[]{nombre}); //Si existe, se actualiza el registro en vez de insertar
            }
            else{
                db.insert(tabla_jugador, null, values); // Si no existe, se inserta simplemente
            }
            db.setTransactionSuccessful();
            toret = true;
        }catch(SQLException exc){

        }
        finally {
            if(cursor != null){
                cursor.close();
            }
            db.endTransaction();
        }
        return toret;
    }



    public boolean eliminarJugador(String id_jugador){
        boolean toret = false;
        db = this.getWritableDatabase();
        try{
            db.beginTransaction();
            db.delete(tabla_jugador, JUGADOR_id + "=?",new String[]{id_jugador});
            db.setTransactionSuccessful();
            toret = true;
        }catch(SQLException exc){
        }finally {
            db.endTransaction();
        }
        return toret;
    }

    public boolean modificarJugador(String id_jugador, String nuevo_nombre){
        boolean toret = false;
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(JUGADOR_nombre, nuevo_nombre);
        try{
            db.beginTransaction();
            db.update(tabla_jugador, values,JUGADOR_id+"=? ", new String[]{id_jugador});
            db.setTransactionSuccessful();
            toret = true;
        }catch(SQLException exc){
            Log.e("DBM.modificarJugador",exc.getMessage());
        }finally {
            db.endTransaction();
        }
        return toret;
    }




    public boolean insertarEquipo(String nombre_equipo){ /*Registrar equipo*/
        boolean toret = false;
        Cursor cursor = null;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values =  new ContentValues();

        values.put(EQUIPO_nombre, nombre_equipo);

        try{
            db.beginTransaction();
            cursor = db.query(tabla_equipo, null, EQUIPO_nombre + "=?", new String[]{nombre_equipo}, null, null, null, null);
            if(cursor.getCount() > 0){
                db.update(tabla_equipo, values, EQUIPO_nombre + "=?", new String[]{nombre_equipo});
            }
            else{
                db.insert(tabla_equipo, null, values);
            }
            db.setTransactionSuccessful();
            toret = true;
        }catch (SQLException exc){
        }
        finally {
            if(cursor != null){
                cursor.close();
            }
            db.endTransaction();
        }
        return toret;
    }

    public boolean eliminarEquipo(String id_equipo){
        boolean toret = false;
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            db.beginTransaction();
            db.delete(tabla_equipo, EQUIPO_id + "=?", new String[]{id_equipo});
            db.setTransactionSuccessful();
            toret = true;
        }catch(SQLException exc){
        }finally {
            db.endTransaction();
        }
        return toret;
    }


    public boolean asignarJugador_Equipo(String id_jugador, String id_equipo){/*jugador X -> Y equipo*/
        boolean toret = false;
        Cursor cursor = null;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(JUGADOR_id, id_jugador);
        values.put(EQUIPO_id_fk, id_equipo);

        try{
            db.beginTransaction();
            cursor = db.query(tabla_jugador, null, JUGADOR_id + "=? AND " + EQUIPO_id_fk
                    + "=?", new String[]{id_jugador, id_equipo}, null, null, null, null);
            if(cursor.getCount() > 0){
                db.update(tabla_jugador, values, JUGADOR_id + "=? AND " + EQUIPO_id_fk
                        + "=?", new String[]{id_jugador, id_equipo});
            }
            else{
                db.insert(tabla_jugador, null, values);
            }
            db.setTransactionSuccessful();
            toret = true;
        }catch(SQLException exc){
        }finally {
            if(cursor != null){
                cursor.close();
            }
            db.endTransaction();
        }
        return toret;
    }

    public boolean eliminarAsignacionJugador_Equipo(String id_jugador, String id_equipo){
        boolean toret = false;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            db.delete(tabla_jugador, JUGADOR_id + "=? AND "+EQUIPO_id_fk+" =?", new String[]{id_jugador, id_equipo});
            db.setTransactionSuccessful();
            toret = true;
        }catch(SQLException exc){
        }finally {
            db.endTransaction();
        }
        return toret;
    }

    public boolean modificarPuntuacion(String id_equipo, int puntos){/*Actualizar la puntuacion*/
        boolean toret = false;
        Cursor cursor = null;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(EQUIPO_id_fk2, id_equipo);
        values.put(puntuacion, puntos);

        try{
            db.beginTransaction();
            cursor = db.query(tabla_puntuacion, new String[]{puntuacion} , EQUIPO_id_fk2 + "=?",
                    new String[]{id_equipo}, null, null, null, null);
            int aux = cursor.getColumnIndex(puntuacion);
            int punt_Act = cursor.getInt(aux);
            int nueva_puntuacion = punt_Act + puntos;
            db.execSQL("UPDATE tabla_puntuacion " +
                            "SET " +puntuacion+ "=?"+
                            "WHERE "+EQUIPO_id_fk2+"=?",
                    new String[]{String.valueOf(nueva_puntuacion), id_equipo});
            db.setTransactionSuccessful();
            toret = true;
        }catch(SQLException exc){
        }finally {
            if(cursor != null){
                cursor.close();
            }
            db.endTransaction();
        }
        return toret;
    }
    public Cursor getJugadores()
    {
        return this.getReadableDatabase().query( tabla_jugador,
                new String[]{JUGADOR_id}, null, null, null, null, null );
    }

    //Get certain player from id
    public String getJugador(int id){
        String text = "";
        boolean toRet = false;
        Cursor c = null;
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(JUGADOR_id, id);

        try{
            db.beginTransaction();
            String query = "SELECT nombre FROM jugador WHERE _id==" + id+ ";";
            c = db.rawQuery(query,null);
            c.moveToFirst();
            text = c.getString(0);
        } catch (SQLException e){
            e.getMessage();
        }finally {
            if(c != null){
                c.close();
            }
            db.endTransaction();
        }
        return text;
    }

    /*¿Hace falta eliminarPuntuacion() si ya tengo ON DELETE CAS
    .



    CADE en la fk?*/

}