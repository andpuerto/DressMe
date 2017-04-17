package uoc.master.angel.dressme.db;

import android.content.ContentValues;
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
     * @param context El contexto
     */
    public PrendaDA(Context context) {
        //Obtenemos el helper
        helper = new DressMeSQLHelper(context, DressMeSQLHelper.dbName, null, DressMeSQLHelper.dbCurrentVersion);
    }

    /**
     * Obtiene una lista con todas las prendas
     * No carga todos los datos de todas las prendas para ahorrar memoria
     * Solo los que se necesitaran para el listado
     * @return Lista de prendas
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
     * @return Lista de prendas
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
     * @param prenda La prenda a rellenar
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
        c = db.rawQuery("SELECT c.id, c.nombre, c.min_temp, c.max_temp, c.lluvia FROM prenda p INNER JOIN prenda_clima pc " +
                "ON p.id = pc.prenda INNER JOIN clima c ON c.id = pc.clima " +
                "WHERE p.id = ? ORDER BY c.id", parameters);
        if (c.moveToFirst()) {
            List<Clima> climas = new ArrayList<>();
            //Recorremos el cursor hasta que no haya más registros
            do {
                climas.add(new Clima(c.getInt(0), c.getString(1), c.getFloat(3), c.getFloat(4),
                        c.getInt(5)==1));
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


    /**
     * Almacena la prenda en la base de datos
     * @param prenda La prenda a almacenar
     */
    public void savePrenda(Prenda prenda){
        //Si la prenda a insertar que recibimos es null, salimos
        if(prenda == null){
            return;
        }
        //Abrimos la base de datos
        SQLiteDatabase db = helper.getWritableDatabase();
        //Comenzamos una transaccion. Vamos a hacer varias operaciones, mejor en una transaccion.
        db.beginTransaction();
        //Rellenamos los valores para la base de datos a partir de los de la prenda
        ContentValues values = new ContentValues();
        if(prenda.getFoto() != null) {
            values.put("foto", prenda.getFoto());}
        if(prenda.getMarca() != null) {
            values.put("marca", prenda.getMarca());}
        if(prenda.getMaterial() != null){
            values.put("material", prenda.getMaterial());}
        if(prenda.getColor() != null) {
            values.put("color", prenda.getColor().getId());}
        if(prenda.getTipoParteConjunto() != null) {
            values.put("tipo_parte_conjunto", prenda.getTipoParteConjunto().getId());}

        //Si el id es negativo, es una nueva prenda, haremos un insert
        if(prenda.getId() <0) {
            int id = (int)db.insert("prenda", null, values);
            //Insert devuelve el id autoincremental del elemento insertado. Lo establecemos.
            prenda.setId(id);

        } else {
            //Si la prenda ya estaba en la base de datos, hay que actualizarla
            String[] whereArgs = {Integer.toString(prenda.getId())};
            db.update("prenda", values, "id=?", whereArgs);
            //Para las tablas relacionadas, vamos a borrar todos los valores e insertar solo los que
            //tenga la prenda
            db.delete("prenda_clima","prenda=?", whereArgs);
            db.delete("uso_prenda","prenda=?", whereArgs);
        }
        //Ahora debemos insertar los valores para las tablas de relaciones.
        //Esto es igual en los dos casos porque si la prenda existe, hemos borrado todos los elementos
        if(prenda.getClimasAdecuados() != null){
            for(Clima clima : prenda.getClimasAdecuados()){
                values.clear();
                values.put("prenda", prenda.getId());
                values.put("clima", clima.getId());
                db.insert("prenda_clima", null, values);
            }
        }
        if(prenda.getUsosAdecuados() != null){
            for(Uso uso : prenda.getUsosAdecuados()){
                values.clear();
                values.put("prenda", prenda.getId());
                values.put("uso", uso.getId());
                db.insert("uso_prenda", null, values);
            }
        }
        //Hacemos el commit de la transaccion
        db.setTransactionSuccessful();
        //Cerramos la transaccion y la base de datos
        db.endTransaction();
        db.close();
    }

    /**
     * Elimina la prenda y todos los elementos seleccionados
     * @param prenda La prenda a eliminar
     */
    public void deletePrenda(Prenda prenda){
        //Comprobamos que la prenda que recibamos sea valida
        if(prenda == null || prenda.getId() < 0){
            return;
        }
        SQLiteDatabase db = helper.getWritableDatabase();
        //Desafortundadamente, aunque en la creacion de las tablas se haya marcado,
        //sqlite no realiza automaticamente el borrado en cascada. Tendremos que hacer
        //manualmente el borrado de los elementos relacionados. Por eso lo hacemos en una
        //transaccion.
        db.beginTransaction();
        String[] whereArgs = {Integer.toString(prenda.getId())};
        //Borramos la prenda
        db.delete("prenda","id=?", whereArgs);
        //Borramos las relaciones con los climas
        db.delete("prenda_clima","prenda=?",whereArgs);
        //Borramos las relaciones con los usos
        db.delete("uso_prenda","prenda=?",whereArgs);
        //Hacemos el commit y cerramos la trasaccion y la base de datos
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }


    /**
     * Obtiene la lista de prendas acorde a los parametros recibidos
     * @param uso Un uso para el que deben ser adecuadas las prendas
     * @param temperatura Temperatura para la que deben ser adecuadas las prendas
     * @param lluvia Si las prendas deben ser adecuadas para la lluvia (true) o es indiferente (false)
     * @return List de prendas que cumplen los requisitos solamente con el id y el color rellenos
     */
    public List<Prenda> getPrendas(Uso uso, float temperatura, boolean lluvia){
        ArrayList<Prenda> prendas = new ArrayList<>();

        //El tratamiento de los climas en cuanto a la lluvia en conjuncion con las temperaturas
        //es un tanto particular, en cuanto que, tal y como se han establecido, hay climas que
        //indican temperaturas, sin importar las precipitaciones, y otros que indican lluvias sin
        //importar la temperatura.
        //Podrían haberse hecho de forma que se combinaran ambas cosas, como un clima "frío y lluvioso",
        //pero, de cara al usuario sería mas incomodo porque tendria que seleccionar de una lista
        //mas grande de climas, a pesar de que ganaramos genericidad.

        //La siguiente consulta seleciona los climas que cumplan con la temperatura que se pasa
        //como parametro y que, en el caso de que haya lluvia, tambien tengan ese elemento marcado
        //en otro de sus climas. Si no llueve, no se considerara este parametro, considerando
        //que las prendas utiles en tiempo lluvioso tambien pueden usarse si no lo es.
        //Se pierde algo de genericidad, pero sigue siendo bastante neutro al no hacer referencia
        //a climas concretos

        //Seleccionamos solo los datos que se van a necesitar
        String query = "select distinct p.id, co.id, co.nombre, co.rgb " +
                "from prenda p inner join prenda_clima pc1 on pc1.prenda=p.id " +
                "inner join uso_prenda up on up.prenda=p.id " +
                "inner join prenda_clima pc2 on pc2.prenda=p.id " +
                "inner join clima cli1 on cli1.id=pc1.clima";
        //Si llueve, agregamos el segundo clima al join
        if(lluvia){
            query += " inner join clima cli2 on cli2.id=pc2.clima";
        }
        //Agregamos el join con el color
        query += " inner join color co on p.color=co.id";
        //Agregamos la clausula where general para la temperatura y el uso
        query += " where cli1.min_temp<=" + temperatura + " and cli1.max_temp>=" + temperatura +
                " and up.uso=" + uso.getId();
        if(lluvia){
            //Si llueve, agregamos la condicion al where
            query += " and cli2.lluvia=1";
        }


        //Abrimos la base de datos
        SQLiteDatabase db = helper.getReadableDatabase();
        //Ejecutamos la consulta
        Cursor c = db.rawQuery(query, null);
        //Recorremos el cursor
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                //Creamos una nueva prenda con solo el id y el id del color
                Prenda prenda = new Prenda(c.getInt(0), null, null, null,
                        new ColorPrenda(c.getInt(1),c.getString(2),c.getString(3),null),
                        null, null, null);
                //Añadimos la prenda a la lista
                prendas.add(prenda);
            } while(c.moveToNext());

        }

        return prendas;
    }


}
