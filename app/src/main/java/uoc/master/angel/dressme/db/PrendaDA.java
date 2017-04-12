package uoc.master.angel.dressme.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import uoc.master.angel.dressme.modelo.Clima;
import uoc.master.angel.dressme.modelo.ColorPrenda;
import uoc.master.angel.dressme.modelo.Prenda;
import uoc.master.angel.dressme.modelo.TipoParteConjunto;
import uoc.master.angel.dressme.modelo.Uso;

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
                prendas.add(new Prenda(c.getInt(0),c.getBlob(1), null, null, null, null, null, null));
            } while(c.moveToNext());
        }
        c.close();
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
                        null, null, null, parteConjunto));
            } while(c.moveToNext());
        }
        c.close();
        db.close();
        return prendas;
    }


    /**
     * Llena todos los campos asociados a la prenda que recibe como parametro
     * @param prenda
     */
    public void fillPrendaDetails(Prenda prenda){
        //Abrimos la base de datos
        SQLiteDatabase db = helper.getReadableDatabase();
        //Obtenemos el color de la prenda
        String[] parameters = {Integer.toString(prenda.getId())};
        Cursor c = db.rawQuery("SELECT c.id, c.nombre, c.rgb FROM color c INNER JOIN prenda p " +
                "ON p.color = c.id WHERE p.id = ?", parameters);
        if (c.moveToFirst()) {
                prenda.setColor(new ColorPrenda(c.getInt(0), c.getString(1), c.getString(2), null));
        }
        c.close();
        //Obtenemos los climas adecuados de la prenda
        c = db.rawQuery("SELECT c.id, c.nombre FROM prenda p INNER JOIN prenda_clima pc " +
                "ON p.id = pc.prenda INNER JOIN clima c ON c.id = pc.clima " +
                "WHERE p.id = ? ORDER BY c.id", parameters);
        if (c.moveToFirst()) {
            List<Clima> climas = new ArrayList<>();
            //Recorremos el cursor hasta que no haya más registros
            do {
                climas.add(new Clima(c.getInt(0), c.getString(1)));
            } while(c.moveToNext());
            prenda.setClimasAdecuados(climas);
        }
        c.close();
        //Obtenemos los usos adecuados de la prenda
        c = db.rawQuery("SELECT u.id, u.nombre FROM prenda p INNER JOIN uso_prenda pu " +
                "ON p.id = pu.prenda INNER JOIN uso u ON u.id = pu.uso " +
                "WHERE p.id = ? ORDER BY u.id", parameters);
        if (c.moveToFirst()) {
            List<Uso> usos = new ArrayList<>();
            //Recorremos el cursor hasta que no haya más registros
            do {
                usos.add(new Uso(c.getInt(0), c.getString(1)));
            } while(c.moveToNext());
            prenda.setUsosAdecuados(usos);
        }
        c.close();
        //Cerramos la base de datos
        db.close();

    }


}
