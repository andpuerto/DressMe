package uoc.master.angel.dressme.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import java.util.ArrayList;
import java.util.List;


import uoc.master.angel.dressme.modelo.Conjunto;
import uoc.master.angel.dressme.modelo.ParteConjunto;
import uoc.master.angel.dressme.modelo.Prenda;
import uoc.master.angel.dressme.modelo.TipoParteConjunto;


/**
 * Created by angel on 02/04/2017.
 */

public class ConjuntoDA {
    private  DressMeSQLHelper helper;

    /**
     * Constructor
     * @param context El contexto
     */
    public ConjuntoDA(Context context) {
        //Obtenemos el helper
        helper = new DressMeSQLHelper(context, DressMeSQLHelper.dbName, null, DressMeSQLHelper.dbCurrentVersion);
    }

    /**
     * Obtiene una lista con todos los conjuntos
     * No carga todos los datos de todas las prendas asociadas para ahorrar memoria
     * Solo los que se necesitaran para el listado
     * @return Lista de prendas
     */
    public List<Conjunto> getAllConjuntos(){
        SQLiteDatabase db = helper.getReadableDatabase();

        //Creamos un builder para la consulta
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        //Asignamos las tablas al builder
        qb.setTables("conjunto c inner join parte_conjunto pc on pc.conjunto=c.id " +
                "inner join prenda p on pc.prenda_asignada=p.id inner join tipo_parte_conjunto " +
                "tpc1 on pc.tipo_parte_conjunto=tpc1.id inner join tipo_parte_conjunto tpc2 " +
                "on p.tipo_parte_conjunto=tpc2.id");

        //Campos que vamos a consultar
        String[] campos = new String[] {"c.id", "pc.id", "tpc1.id", "tpc1.nombre", "p.id",
                "p.foto", "tpc2.id", "tpc2.nombre"};

        //Ejecutamos la consulta
        Cursor c = qb.query(db, campos, null, null, null, null, "c.id");


        ArrayList<Conjunto> conjuntos = new ArrayList<Conjunto>();
        //Nos aseguramos de que existe al menos un registro
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                Conjunto currentConjunto;
                //Obtenemos el id del conjunto de la iteracion actual
                int idConjunto = c.getInt(0);
                //Obtenemos el ultimo conjunto introducido, en caso de que lo haya
                Conjunto lastConjunto = conjuntos.isEmpty() ? null :
                        conjuntos.get(conjuntos.size()-1);
                //Si la lista de conjuntos esta vacia o si estamos tratando un conjunto diferente
                //a la ultima fila, creamos un conjunto nuevo con el id obtenido y lo insertamos
                if(lastConjunto==null || lastConjunto.getId()!=idConjunto){
                    currentConjunto = new Conjunto(idConjunto, null);
                    conjuntos.add(currentConjunto);

                }else{
                    //Si estamos tratando el mismo conjunto que en la iteracion anterior (porque
                    // son datos de otra parte del mismo conjunto), seguimos trabajando sobre el.
                    currentConjunto = lastConjunto;
                }
                //Agregamos al conjunto los datos de las partes de conjunto y prenda de la iteracion
                //actual
                TipoParteConjunto tpcPrendaTemp = new TipoParteConjunto(c.getInt(6), c.getString(7));
                Prenda prendaTemp = new Prenda(c.getInt(4), c.getBlob(5), null, null,
                        null, null, null, tpcPrendaTemp);
                TipoParteConjunto tpcPcTemp = new TipoParteConjunto(c.getInt(2), c.getString(3));
                ParteConjunto pcTemp = new ParteConjunto(c.getInt(1), tpcPcTemp, prendaTemp);
                //Agregamos la parteConjunto, poniendo como clave el id de su parte
                currentConjunto.getPartesConjunto().append(c.getInt(2), pcTemp);

            } while(c.moveToNext());
        }
        c.close();
        db.close();
        return conjuntos;
    }


}
