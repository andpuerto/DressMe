package uoc.master.angel.dressme.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import uoc.master.angel.dressme.modelo.Prenda;
import uoc.master.angel.dressme.modelo.TipoParteConjunto;

/**
 * Created by angel on 02/04/2017.
 */

public class PrendaDA {
    private  DressMeSQLHelper helper;

    /**
     * Constructor
     * @param context
     */
    public PrendaDA(Context context) {
        //Obtenemos el helper
        helper = new DressMeSQLHelper(context, DressMeSQLHelper.dbName, null, DressMeSQLHelper.dbCurrentVersion);
    }

    /**
     * Obtiene una lista con todas las prendas
     * No carga todos los datos de todas las prendas para ahorrar memoria
     * Solo los que se necesitaran para el listado
     * @return
     */
    public List<Prenda> getAllPrendas(){
        SQLiteDatabase db = helper.getReadableDatabase();
        //Metemos solamente el identificador y la foto
        String[] campos = new String[] {"id", "foto"};
        Cursor c = db.query("Prenda", campos, null, null, null, null, null);

        ArrayList<Prenda> prendas = new ArrayList<Prenda>();
        //Nos aseguramos de que existe al menos un registro
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                prendas.add(new Prenda(c.getInt(0),c.getBlob(1), null, null, null, null, null));
            } while(c.moveToNext());
        }
        db.close();
        return prendas;
    }


    /**
     * Obtiene una lista con las prendas para un TipoParteConjunto
     * No carga todos los datos de todas las prendas para ahorrar memoria
     * Solo los que se necesitaran para el listado
     * @return
     */
    public List<Prenda> getAllPrendas(TipoParteConjunto parteConjunto){
        SQLiteDatabase db = helper.getReadableDatabase();
        //Metemos solamente el identificador y la foto
        String[] campos = new String[] {"id", "foto", "marca", "material"};
        String where = "tipo_parte_conjunto = ?";
        String[] whereArgs = new String[] {
                Integer.toString(parteConjunto.getId())
        };
        Cursor c = db.query("Prenda", campos, where, whereArgs, null, null, null);

        ArrayList<Prenda> prendas = new ArrayList<Prenda>();
        //Nos aseguramos de que existe al menos un registro
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                prendas.add(new Prenda(c.getInt(0),c.getBlob(1), c.getString(2), c.getString(3),
                        null, null, parteConjunto));
            } while(c.moveToNext());
        }
        db.close();
        return prendas;
    }
}
