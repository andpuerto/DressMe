package uoc.master.angel.dressme.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import uoc.master.angel.dressme.modelo.Uso;

/**
 * Created by angel on 11/04/2017.
 */

public class UsoDA {
    private DressMeSQLHelper helper;

    /**
     * Constructor
     *
     * @param context contexto
     */
    public UsoDA(Context context) {
        //Obtenemos el helper
        helper = new DressMeSQLHelper(context, DressMeSQLHelper.dbName, null, DressMeSQLHelper.dbCurrentVersion);
    }


    /**
     * Devueve una lista con todos los objetos Uso de la base de datos
     *
     * @return lista con todos los objetos Uso de la base de datos
     */
    public List<Uso> getAllUso() {
        SQLiteDatabase db = helper.getReadableDatabase();
        //Consultaremos el id y el nombre
        String[] campos = new String[]{"id", "nombre"};
        Cursor c = db.query("uso", campos, null, null, null, null, "id");

        ArrayList<Uso> usos = new ArrayList<>();
        //Nos aseguramos de que existe al menos un registro
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                usos.add(new Uso(c.getInt(0), c.getString(1)));
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return usos;
    }


    /**
     * Devueve un array de Strings con los nombres de los usos ordenados por id
     *
     * @return array de Strings con los nombres de los usos ordenados por id
     */
    public String[] getAllUsoNombre() {
        SQLiteDatabase db = helper.getReadableDatabase();
        //Consultaremos el id y el nombre
        String[] campos = new String[]{"id", "nombre"};
        Cursor c = db.query("uso", campos, null, null, null, null, "id");

        String[] usos = new String[c.getCount()];
        //Nos aseguramos de que existe al menos un registro
        if (c.moveToFirst()) {
            int i = 0;
            //Recorremos el cursor hasta que no haya más registros
            do {
                usos[i] = c.getString(1);
                i++;
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return usos;
    }


    /**
     * Obtiene un uso a partir de su id
     * @param idUso id del uso
     * @return uso
     */
    public Uso getUso(int idUso) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] campos = new String[]{"id", "nombre"};
        String where = "id = ?";
        String[] whereArgs = new String[]{
                Integer.toString(idUso)
        };
        Uso uso = null;
        Cursor c = db.query("uso", campos, where, whereArgs, null, null, "id");

        //Nos aseguramos de que existe al menos un registro
        if (c.moveToFirst()) {
            //Devolvemos el Uso con los valores recogidos
            uso = new Uso(c.getInt(0), c.getString(1));
        }
        c.close();
        db.close();
        return uso;
    }

}
