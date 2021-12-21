package com.example.proyectodm.core;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class DBManager extends SQLiteOpenHelper {
    public static final String db_name = "papelitos";
    public static final int db_version = 1;
    public static final String tabla_jugador = "jugador";
    public static final String tabla_equipo = "equipo";
    public static final String tabla_puntuacion = "puntuacion";
    public static final String tabla_papelito = "papelito";


    private static DBManager instance;

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

    /*Campos tabla PAPELITO*/
    public static String PAPELITO_id="_id";
    public static String PAPELITO_texto="texto";
    public static String EQUIPO_id_fk3="id_equipo_2";

    SQLiteDatabase db;

    private DBManager(Context context){
        super(context, db_name, null, db_version);
        context.deleteDatabase("papelitos");

    }

    public static synchronized DBManager getInstance(Context context){ // Singleton pattern
        if(instance == null){
            instance = new DBManager(context.getApplicationContext());
        }
        return instance;
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
                    + EQUIPO_id_fk + " INTEGER , FOREIGN KEY ("+EQUIPO_id_fk+") REFERENCES " + tabla_equipo + "("+EQUIPO_id+") "
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
                    + EQUIPO_id + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
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

        /*Creacion tabla Papelito*/
        try{
            db.beginTransaction();
            db.execSQL("CREATE TABLE IF NOT EXISTS "+tabla_papelito + " ("
            + PAPELITO_id + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
            + PAPELITO_texto + " TEXT NOT NULL,"
            + EQUIPO_id_fk3 +" INTEGER, FOREIGN KEY ("+EQUIPO_id_fk3+") REFERENCES " + tabla_equipo + "("+EQUIPO_id+") ON DELETE CASCADE"
            +")"
            );
            db.setTransactionSuccessful();
        }catch (SQLException exc){
            Log.e("DBManager.onCreate", "Creando "+tabla_papelito+": "+exc.getMessage());
        }
        finally {
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

    // ****
    // -- OPERACIONES JUGADORES
    // ****

    public Cursor getJugadores()
    {
        return this.getReadableDatabase().query( tabla_jugador,
                new String[]{JUGADOR_id}, null, null, null, null, null );
    }

    public Cursor getJugadoresFromEquipo(String id_equipo)
    {
        return this.getReadableDatabase().query( tabla_jugador,
                new String[]{JUGADOR_id}, EQUIPO_id_fk + "=?", new String[]{id_equipo}, null, null, null );
    }


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

    public Cursor getJugadoresDisponibles(){
        Cursor c = null;
        Cursor aux = null;
        db = this.getWritableDatabase();
        try{
            db.beginTransaction();
            aux = db.query(tabla_jugador,null,null,null,null,null,null);
            if(aux.getCount() > 0) {
                c = db.query(tabla_jugador, null, EQUIPO_id_fk + " = 'null'", null, null, null, null);
            }
        } catch (SQLException e){
            System.err.println(e.getMessage());
        }
        finally {
            db.endTransaction();
        }
        return c;
    }
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


    // ****
    // -- OPERACIONES EQUIPOS
    // ****

    public Cursor getEquipos()
    {
        return this.getReadableDatabase().query( tabla_equipo,
                new String[]{EQUIPO_id}, null, null, null, null, null );
    }
    public String getIdFromTeamName(String team_name){
        String id = "";
        Cursor c = null;
        db = this.getWritableDatabase();
        try{
            db.beginTransaction();
            String query = "SELECT _id FROM equipo WHERE equipo_nombre = '"+team_name+"'";;
            c = db.rawQuery(query,null);
            c.moveToFirst();
            id = c.getString(0);
        } catch (SQLException e){
            e.getMessage();
        }finally {
            if(c != null){
                c.close();
            }
            db.endTransaction();
        }
        return id;
    }

    public String getNombreEquipo(String id){
        String text = "";
        boolean toRet = false;
        Cursor c = null;
        Cursor aux = null;
        db = this.getWritableDatabase();
        try{
            db.beginTransaction();
            aux = db.query(tabla_equipo,null,null,null,null,null,null);
            if(aux.getCount() > 0){
                String query = "SELECT "+EQUIPO_nombre+" FROM "+ tabla_equipo +" WHERE _id==" + id+ ";";
                c = db.rawQuery(query,null);
                c.moveToFirst();
                text = c.getString(0);
            }

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

    public ArrayList<String> getNombreEquipos(){
        Cursor c = null;
        ArrayList<String> array_nombres = new ArrayList<String>();

        db = this.getWritableDatabase();
        try{
            db.beginTransaction();
            c = db.query(tabla_equipo, new String[]{EQUIPO_nombre},
                    null,null,null,null,null);

            c.moveToFirst();
            while(!c.isAfterLast()) {
                array_nombres.add(c.getString(0)); //add the item
                c.moveToNext();
            }
        } catch (SQLException e){
            e.getMessage();
        }finally {

            if(c != null){
                c.close();
            }

            db.endTransaction();
        }
        return array_nombres;
    }

    public String getEquipo(int id){
        String text = "";
        boolean toRet = false;
        Cursor c = null;
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EQUIPO_id, id);

        try{
            db.beginTransaction();
            String query = "SELECT equipo_nombre FROM equipo WHERE _id==" + id+ ";";
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

    // ****
    // -- OPERACIONES PAPELITOS
    // ****

    public boolean insertarPapelito(String texto){
        boolean toret=false;
        ContentValues values = new ContentValues();
        SQLiteDatabase db = this.getWritableDatabase();

        values.put(PAPELITO_texto, texto);
        values.put(EQUIPO_id_fk3, "null");
        try{
            db.beginTransaction();
            db.insert(tabla_papelito,null, values); //Puede haber repetidos
            db.setTransactionSuccessful();
            toret = true;
        }catch (SQLException exc){
            Log.e("DBM.insertarPapelito", exc.getMessage());
        }
        finally {
            db.endTransaction();
        }
        return toret;
    }

    public boolean eliminarPapelito(String id_papelito){
        boolean toret = false;
        Cursor cursor = null;
        ContentValues values = new ContentValues();
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            db.beginTransaction();
            db.delete(tabla_papelito, PAPELITO_id + "=?", new String[]{id_papelito});
            db.setTransactionSuccessful();
            toret = true;
        }catch (SQLException exc){
            Log.e("DBM.eliminarPepelito", exc.getMessage());
        }
        finally {
            db.endTransaction();
        }
        return toret;
    }

    public boolean modificarPapelito(String id_papelito, String new_Text){
        boolean toret = false;
        ContentValues values = new ContentValues();
        SQLiteDatabase db = this.getWritableDatabase();

        values.put(PAPELITO_texto, new_Text);

        try{
            db.beginTransaction();
            db.update(tabla_papelito, values, PAPELITO_id+"=?", new String[]{id_papelito});
            db.setTransactionSuccessful();
            toret = true;
        }catch (SQLException exc){
            Log.e("DBM.modificarPapelito", exc.getMessage());
        }
        finally {
            db.endTransaction();
        }
        return toret;
    }

    public String getPapelito(int id){
        String text = "";
        boolean toRet = false;
        Cursor c = null;
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PAPELITO_id, id);

        try{
            db.beginTransaction();
            String query = "SELECT "+PAPELITO_texto+" FROM "+ tabla_papelito +" WHERE _id==" + id+ ";";
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

    public Cursor getPapelitos(){
        return this.getWritableDatabase().query(tabla_papelito, new String[]{PAPELITO_id},
                null,null,null, null,null);
    }

    public Cursor getPapelitosDisponibles(){
        Cursor c = null;
        db = this.getReadableDatabase();
        try{
            db.beginTransaction();
            c = db.query(tabla_papelito, null, EQUIPO_id_fk3 + " = 'null'" , null, null, null,null);
        } catch (SQLException e){
            System.err.println(e.getMessage());
        }
        finally {
            db.endTransaction();
        }
        return c;
    }

    public Cursor getPapelitoByName(CharSequence filtro){
        String id = "";
        Cursor c = null;
        String query = "";
        db = this.getWritableDatabase();
        try{
            db.beginTransaction();
            if(filtro.length() == 0){
                query = "SELECT _id FROM papelito WHERE texto IS NOT NULL";
            } else {
                query = "SELECT _id FROM papelito WHERE texto = '" + filtro + "'";
                ;
            }
            c = db.rawQuery(query,null);
        } catch (SQLException e){
            e.getMessage();
        }finally {
            if(c != null){
                //c.close();
            }
            db.endTransaction();
        }
        return c;
    }


    // ****
    // -- ASIGNACIONES
    // ****

    public boolean asignarJugador_Equipo(String id_jugador, String id_equipo){/*jugador X -> Y equipo*/
        boolean toret = false;
        Cursor cursor = null;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //values.put(JUGADOR_id, id_jugador);
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
                db.update(tabla_jugador, values, JUGADOR_id + "=?", new String[]{id_jugador});

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

    public boolean asignarPapelito_Equipo(String id_papelito, String id_equipo){
        boolean toret = false;
        Cursor cursor = null;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(PAPELITO_id, id_papelito);
        values.put(EQUIPO_id_fk3, id_equipo);

        try{
            db.beginTransaction();
            cursor = db.query(tabla_papelito, null, PAPELITO_id + "=? AND " + EQUIPO_id_fk3
                    + "=?", new String[]{id_papelito, id_equipo}, null, null, null, null);
            if(cursor.getCount() > 0){
                db.update(tabla_papelito, values, PAPELITO_id + "=? AND " + EQUIPO_id_fk3
                        + "=?", new String[]{id_papelito, id_equipo});
            }
            else{
                db.insert(tabla_papelito, null, values);
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

    public boolean eliminarAsignacionJugador_Equipo(String id_jugador){
        boolean toret = false;
        ContentValues values = new ContentValues();

        values.put(EQUIPO_id_fk, "null");

        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            db.update(tabla_jugador, values, JUGADOR_id + "=?", new String[]{id_jugador});
            db.setTransactionSuccessful();
            toret = true;
        }catch(SQLException exc){
        }finally {
            db.endTransaction();
        }
        return toret;
    }

    public boolean eliminarAsignacionPapelito_Equipo(String id_papelito, String id_equipo){
        boolean toret = false;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            db.delete(tabla_papelito, PAPELITO_id+ "=? AND "+EQUIPO_id_fk3+" =?", new String[]{id_papelito, id_equipo});
            db.setTransactionSuccessful();
            toret = true;
        }catch(SQLException exc){
        }finally {
            db.endTransaction();
        }
        return toret;
    }

    // ****
    // -- OPERACIONES PUNTUACIONES
    // ****

    public Cursor getPuntuaciones(){
        return this.getWritableDatabase().query(tabla_puntuacion, new String[]{EQUIPO_id_fk3},
                null,null,null, null,null);
    }

    public String getPuntuacion(int id){
        String text = "";
        boolean toRet = false;
        Cursor c = null;
        db = this.getWritableDatabase();
        try{
            db.beginTransaction();
            String query = "SELECT "+puntuacion+" FROM "+ tabla_puntuacion +" WHERE _id==" + id+ ";";
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

    public boolean insertarPuntuacion(String id){
        boolean toret=false;
        ContentValues values = new ContentValues();
        SQLiteDatabase db = this.getWritableDatabase();

        values.put(EQUIPO_id_fk2, id);

        try{
            db.beginTransaction();
            db.insert(tabla_puntuacion,null, values); //Puede haber repetidos
            db.setTransactionSuccessful();
            toret = true;
        }catch (SQLException exc){
            Log.e("DBM.insertarPapelito", exc.getMessage());
        }
        finally {
            db.endTransaction();
        }
        return toret;
    }


    public boolean modificarPuntuacion(String id_equipo, int puntos){/*Actualizar la puntuacion*/
        boolean toret = false;
        Cursor cursor = null;
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(puntuacion, puntos);
        values.put(EQUIPO_id_fk2, id_equipo);

        try{
            db.beginTransaction();
            cursor = db.query(tabla_puntuacion, new String[]{puntuacion} , EQUIPO_id_fk2 + "=?",
                    new String[]{id_equipo}, null, null, null, null);

            cursor.moveToFirst();
            int aux = cursor.getColumnIndex("punt");
            int punt_Act = cursor.getInt(aux);
            int nueva_puntuacion = punt_Act + puntos;
            System.out.println("!!!NUEVA PUNTUACION: "  + nueva_puntuacion);
            db.execSQL("UPDATE puntuacion " +
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



}