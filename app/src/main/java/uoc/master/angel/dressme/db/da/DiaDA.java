package uoc.master.angel.dressme.db.da;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import java.util.Date;
import java.util.HashMap;

import uoc.master.angel.dressme.db.helper.DressMeSQLHelper;
import uoc.master.angel.dressme.modelo.Conjunto;
import uoc.master.angel.dressme.modelo.Dia;
import uoc.master.angel.dressme.util.DateUtil;

/**
 * Created by angel on 30/04/2017.
 */

public class DiaDA {

    private DressMeSQLHelper helper;

    /**
     * Constructor
     *
     * @param context Contexto
     */
    public DiaDA(Context context) {
        //Obtenemos el helper
        helper = new DressMeSQLHelper(context, DressMeSQLHelper.dbName, null, DressMeSQLHelper.dbCurrentVersion);
    }


    /**
     * Devuelve un hasmap con los dias
     *
     * @return Hashmap con la fecha como clave y el objeto dia como valor
     */
    public HashMap<Date, Dia> getAllDia() {
        SQLiteDatabase db = helper.getReadableDatabase();

        //Creamos un builder para la consulta
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        //Asignamos las tablas al builder
        qb.setTables("dia d inner join conjunto c on d.conjunto_asignado=c.id");

        //Campos que vamos a consultar
        String[] campos = new String[]{"d.id", "d.fecha", "c.id"};

        //Ejecutamos la consulta
        Cursor c = qb.query(db, campos, null, null, null, null, null);


        HashMap<Date, Dia> dias = new HashMap<>();
        //Nos aseguramos de que existe al menos un registro
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                //Introducimos en el dia en el hashmap poniendo la fecha como clave
                Date fecha = DateUtil.stringToDate(c.getString(1));
                if (fecha != null) {
                    dias.put(fecha, new Dia(c.getInt(0), fecha, new Conjunto(c.getInt(2), null)));
                }
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return dias;
    }


    /**
     * Guarda los cambios en el dia que recibe como parametro o lo inserta si no existe
     *
     * @param dia el dia a modificar
     */
    public void saveDia(Dia dia) {
        //Si el dia o alguno de sus parametros es null, no hacemos nada
        if (dia == null || dia.getFecha() == null || dia.getConjuntoAsignado() == null) {
            return;
        }
        //Abrimos la base de datos
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("conjunto_asignado", dia.getConjuntoAsignado().getId());
        //Si el id es negativo, es un nuevo dia, haremos un insert
        if (dia.getId() < 0) {
            values.put("fecha", DateUtil.dateToString(dia.getFecha()));
            int id = (int) db.insert("dia", null, values);
            dia.setId(id);
        } else {
            String where = "id=?";
            String[] whereArgs = {Integer.toString(dia.getId())};
            db.update("dia", values, where, whereArgs);
        }

        db.close();
    }


    /**
     * Elimina de la base de datos el dia que recibe como parametro
     *
     * @param dia El dia a eliminar
     */
    public void deleteDia(Dia dia) {
        //Abrimos la base de datos
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] whereArgs = {Integer.toString(dia.getId())};
        //Borramos el dia
        db.delete("dia", "id=?", whereArgs);
        db.close();
    }
}
