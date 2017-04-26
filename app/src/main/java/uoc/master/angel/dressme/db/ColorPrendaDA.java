package uoc.master.angel.dressme.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uoc.master.angel.dressme.modelo.ColorPrenda;

/**
 * Created by angel on 11/04/2017.
 */

public class ColorPrendaDA {
    private  DressMeSQLHelper helper;

    /**
     * Constructor
     * @param context Context
     */
    public ColorPrendaDA(Context context) {
        //Obtenemos el helper
        helper = new DressMeSQLHelper(context, DressMeSQLHelper.dbName, null, DressMeSQLHelper.dbCurrentVersion);
    }


    /**
     * Devueve una lista con todos los objetos ColorPrenda de la base de datos
     * @return List con todos los ColorPrenda
     */
    public List<ColorPrenda> getAllColorPrenda(){
        SQLiteDatabase db = helper.getReadableDatabase();
        //Consultaremos el id, el nombre y el valor RGB
        String[] campos = new String[] {"id", "nombre", "rgb"};
        Cursor c = db.query("color", campos, null, null, null, null, null);

        ArrayList<ColorPrenda> colores = new ArrayList<>();
        //Nos aseguramos de que existe al menos un registro
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                colores.add(new ColorPrenda(c.getInt(0),c.getString(1), c.getString(2), null));
            } while(c.moveToNext());
        }
        c.close();
        db.close();
        return colores;
     }


    /**
     * Devuelve un sparse array de los colores que combinan con el color que recibe como parametro
     * @param color ColorPrenda del que queremos obtener los colores combinados
     * @return SparseArray con los ColorPrenda que combinan, usando el id como clave
     */
    public HashMap<Integer,ColorPrenda> getColoresCombinados(ColorPrenda color){
        HashMap<Integer,ColorPrenda> coloresCombinados = new HashMap<>();
        //Abrimos la base de datos
        SQLiteDatabase db = helper.getReadableDatabase();
        //Obtenemos el id del color
        String[] parameters = {Integer.toString(color.getId())};
        Cursor c = db.rawQuery("SELECT c.id, c.nombre, c.rgb FROM color c INNER JOIN color_combina co " +
                "ON co.color2 = c.id WHERE co.color1 = ?", parameters);
        if (c.moveToFirst()) {
            //Agregamos todos los colores obtenidos al sparse array
            do{
                coloresCombinados.put(c.getInt(0), new ColorPrenda(c.getInt(0),
                        c.getString(1), c.getString(2), null));
            }while (c.moveToNext());
        }
        c.close();
        db.close();
        return coloresCombinados;
    }

}
