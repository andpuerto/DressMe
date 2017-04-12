package uoc.master.angel.dressme.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import uoc.master.angel.dressme.modelo.ColorPrenda;

/**
 * Created by angel on 11/04/2017.
 */

public class ColorPrendaDA {
    private  DressMeSQLHelper helper;

    /**
     * Constructor
     * @param context
     */
    public ColorPrendaDA(Context context) {
        //Obtenemos el helper
        helper = new DressMeSQLHelper(context, DressMeSQLHelper.dbName, null, DressMeSQLHelper.dbCurrentVersion);
    }


    /**
     * Devueve una lista con todos los objetos ColorPrenda de la base de datos
     * @return
     */
    public List<ColorPrenda> getAllColorPrenda(){
        SQLiteDatabase db = helper.getReadableDatabase();
        //Consultaremos el id, el nombre y el valor RGB
        String[] campos = new String[] {"id", "nombre", "rgb"};
        Cursor c = db.query("color", campos, null, null, null, null, null);

        ArrayList<ColorPrenda> colores = new ArrayList<ColorPrenda>();
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
}
