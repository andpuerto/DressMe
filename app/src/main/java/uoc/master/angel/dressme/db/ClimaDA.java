package uoc.master.angel.dressme.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import uoc.master.angel.dressme.modelo.Clima;


/**
 * Created by angel on 11/04/2017.
 */

public class ClimaDA {
    private  DressMeSQLHelper helper;

    /**
     * Constructor
     * @param context
     */
    public ClimaDA(Context context) {
        //Obtenemos el helper
        helper = new DressMeSQLHelper(context, DressMeSQLHelper.dbName, null, DressMeSQLHelper.dbCurrentVersion);
    }


    /**
     * Devueve una lista con todos los objetos Clima de la base de datos
     * @return
     */
    public List<Clima> getAllClima(){
        SQLiteDatabase db = helper.getReadableDatabase();
        //Consultaremos el id y el nombre
        String[] campos = new String[] {"id", "nombre", "min_temp", "max_temp", "lluvia"};
        Cursor c = db.query("clima", campos, null, null, null, null, "id");

        ArrayList<Clima> climas = new ArrayList<>();
        //Nos aseguramos de que existe al menos un registro
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                climas.add(new Clima(c.getInt(0),c.getString(1),c.getFloat(2),
                        c.getFloat(3),c.getInt(4)==1));
            } while(c.moveToNext());
        }
        c.close();
        db.close();
        return climas;
     }


    /**
     * Devueve un array de Strings con los nombres de los climas ordenados por id
     * @return
     */
    public String[] getAllClimaNombre(){
        SQLiteDatabase db = helper.getReadableDatabase();
        //Consultaremos el id y el nombre
        String[] campos = new String[] {"id", "nombre"};
        Cursor c = db.query("clima", campos, null, null, null, null, "id");

        String[] climas = new String[c.getCount()];
        //Nos aseguramos de que existe al menos un registro
        if (c.moveToFirst()) {
            int i=0;
            //Recorremos el cursor hasta que no haya más registros
            do {
                climas[i] = c.getString(1);
                i++;
            } while(c.moveToNext());
        }
        c.close();
        db.close();
        return climas;
    }


    public Clima getClima(int idClima){
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] campos = new String[] {"id", "nombre", "min_temp", "max_temp", "lluvia"};
        String where = "id = ?";
        String[] whereArgs = new String[] {
                Integer.toString(idClima)
        };
        Clima clima = null;
        Cursor c = db.query("clima", campos, where, whereArgs, null, null, "id");

        //Nos aseguramos de que existe al menos un registro
        if (c.moveToFirst()) {
            //Devolvemos el Clima con los valores recogidos
            clima = new Clima(c.getInt(0),c.getString(1),c.getFloat(2),c.getFloat(3),c.getInt(4)==1);
        }
        c.close();
        db.close();
        return clima;
    }

}
