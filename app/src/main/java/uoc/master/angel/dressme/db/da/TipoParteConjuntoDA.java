package uoc.master.angel.dressme.db.da;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import uoc.master.angel.dressme.db.helper.DressMeSQLHelper;
import uoc.master.angel.dressme.modelo.TipoParteConjunto;

/**
 * Created by angel on 05/04/2017.
 */

public class TipoParteConjuntoDA {

    private DressMeSQLHelper helper;

    /**
     * Constructor
     *
     * @param context contexto
     */
    public TipoParteConjuntoDA(Context context) {
        //Obtenemos el helper
        helper = new DressMeSQLHelper(context, DressMeSQLHelper.dbName, null, DressMeSQLHelper.dbCurrentVersion);
    }

    /**
     * Devuelve una lista con todos los TipoParteConjunto de la base de datos
     *
     * @return lista con todos los TipoParteConjunto de la base de datos
     */
    public List<TipoParteConjunto> getAllTipoParteConjunto() {
        SQLiteDatabase db = helper.getReadableDatabase();
        //Metemos solamente el identificador y la foto
        String[] campos = new String[]{"id", "nombre"};
        Cursor c = db.query("tipo_parte_conjunto", campos, null, null, null, null, null);

        ArrayList<TipoParteConjunto> tiposParteConjunto = new ArrayList<>();
        //Nos aseguramos de que existe al menos un registro
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                tiposParteConjunto.add(new TipoParteConjunto(c.getInt(0), c.getString(1)));
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return tiposParteConjunto;
    }
}
