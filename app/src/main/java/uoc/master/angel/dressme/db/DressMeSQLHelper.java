package uoc.master.angel.dressme.db;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

/**
 * Created by angel on 01/04/2017.
 */

public class DressMeSQLHelper extends SQLiteOpenHelper {

    public DressMeSQLHelper(Context contexto, String nombre,
                                CursorFactory factory, int version) {
        super(contexto, nombre, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //SOLO EN PRUEBAS
        this.eliminarTablas(db);

        //Se crean las tablas de la base de datos
        this.crearTablas(db);
        //Se insertan los datos base que debe contener la base de datos
        this.insertarDatosBase(db);

        //PARA PRUEBAS: INSERTA DATOS DE PRUEBA
        this.insertarDatosPrueba(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {
        //NOTA: Por simplicidad del ejemplo aquí utilizamos directamente la opción de
        //      eliminar la tabla anterior y crearla de nuevo vacía con el nuevo formato.
        //      Sin embargo lo normal será que haya que migrar datos de la tabla antigua
        //      a la nueva, por lo que este método debería ser más elaborado.

        //Se eliminan las tablas de la base de datos
        this.eliminarTablas(db);

        //Se crea la nueva versión de las tablas
        this.crearTablas(db);
        this.insertarDatosBase(db);
    }


    /**
     * Crea las tablas de la base de datos
     */
    private void crearTablas(SQLiteDatabase db){

        //Tabla color
        db.execSQL("CREATE TABLE color (id INTEGER PRIMARY KEY, nombre TEXT, rgb TEXT)");

        //Tabla color_combina
        //Representa los que colores combinan con otros
        db.execSQL("CREATE TABLE color_combina (color1 INTEGER REFERENCES color(id) " +
                "ON DELETE CASCADE ON UPDATE CASCADE, color2 INTEGER REFERENCES color(id) " +
                "ON DELETE CASCADE ON UPDATE CASCADE, PRIMARY KEY(color1, color2))");

        //Tabla clima
        db.execSQL("CREATE TABLE clima(id INTEGER PRIMARY KEY, nombre TEXT)");

        //Tabla uso
        //Identifica los diferentes usos sociales posibles para las prendas
        db.execSQL("CREATE TABLE uso(id INTEGER PRIMARY KEY, nombre TEXT)");

        //Tabla tipo_parte_conjunto
        db.execSQL("CREATE TABLE tipo_parte_conjunto (id INTEGER PRIMARY KEY, nombre TEXT)");

        //Tabla prenda
        db.execSQL("CREATE TABLE prenda (id INTEGER PRIMARY KEY, foto BLOB, marca TEXT, material" +
                " TEXT, color INTEGER REFERENCES color_prenda(id) ON DELETE RESTRICT ON UPDATE CASCADE, " +
                "tipo_parte_conjunto INTEGER REFERENCES tipo_parte_conjunto (id) " +
                "ON DELETE RESTRICT ON UPDATE CASCADE)");


        //Tabla conjunto
        db.execSQL("CREATE TABLE conjunto (id INTEGER PRIMARY KEY)");

        //Tabla parte_conjunto
        db.execSQL("CREATE TABLE parte_conjunto (id INTEGER PRIMARY KEY, " +
                "tipo_parte_conjunto INTEGER REFERENCES tipo_parte_conjunto(id) " +
                "ON DELETE RESTRICT ON UPDATE CASCADE, " +
                "prenda_asignada INTEGER REFERENCES prenda(id) " +
                "ON DELETE SET NULL ON UPDATE CASCADE, " +
                "conjunto INTEGER REFERENCES conjunto(id) ON DELETE CASCADE ON UPDATE CASCADE)");

        //Tabla dia
        //SQLite no proporciona un tipo de datos especifico para las fechas, en su lugar se almacena
        //como una cadena de caracteres
        //Si un dia  no tiene asignado un conjunto, no tiene sentido almacenarlo
        db.execSQL("CREATE TABLE dia (id INTEGER PRIMARY KEY, fecha TEXT NOT NULL, " +
                "conjunto_asignado INTEGER REFERENCES conjunto(id) ON DELETE CASCADE ON UPDATE CASCADE)");

        //Tabla prenda_clima
        //Identifica los climas para los que es adecuada una prenda, representando la relación N:N
        //entre ambas tablas
        db.execSQL("CREATE TABLE prenda_clima (prenda INTEGER REFERENCES prenda(id) " +
                "ON DELETE CASCADE ON UPDATE CASCADE, clima INTEGER REFERENCES clima(id) " +
                "ON DELETE CASCADE ON UPDATE CASCADE, PRIMARY KEY(prenda, clima))");


        //Tabla uso_prenda
        //Identifica los usos sociales para los que es adecuada una prenda, representando la relación N:N
        //entre ambas tablas
        db.execSQL("CREATE TABLE uso_prenda (prenda INTEGER REFERENCES prenda(id) " +
                "ON DELETE CASCADE ON UPDATE CASCADE, uso INTEGER REFERENCES uso(id) " +
                "ON DELETE CASCADE ON UPDATE CASCADE, PRIMARY KEY(prenda, uso))");


    }

    /**
     * Elimina todas las tablas de la base de datos
     * @param db
     */
    private void eliminarTablas(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS uso_prenda");
        db.execSQL("DROP TABLE IF EXISTS prenda_clima");
        db.execSQL("DROP TABLE IF EXISTS dia");
        db.execSQL("DROP TABLE IF EXISTS parte_conjunto");
        db.execSQL("DROP TABLE IF EXISTS conjunto");
        db.execSQL("DROP TABLE IF EXISTS prenda");
        db.execSQL("DROP TABLE IF EXISTS tipo_parte_conjunto");
        db.execSQL("DROP TABLE IF EXISTS uso");
        db.execSQL("DROP TABLE IF EXISTS clima");
        db.execSQL("DROP TABLE IF EXISTS color_combina");
        db.execSQL("DROP TABLE IF EXISTS color");
    }


    /**
     * Inserta los datos iniciales que debe tener la base de datos
     */
    private void insertarDatosBase(SQLiteDatabase db){
        //Colores
        db.execSQL("INSERT INTO color VALUES(0,'negro','000000')");
        db.execSQL("INSERT INTO color VALUES(1,'gris','999999')");
        db.execSQL("INSERT INTO color VALUES(2,'beige','323453')");
        db.execSQL("INSERT INTO color VALUES(3,'rojo','770000')");
        db.execSQL("INSERT INTO color VALUES(4,'verde','007700')");
        db.execSQL("INSERT INTO color VALUES(5,'azul','000077')");

        //Combinaciones de colores
        db.execSQL("INSERT INTO color_combina VALUES(0,1)");
        db.execSQL("INSERT INTO color_combina VALUES(0,2)");
        db.execSQL("INSERT INTO color_combina VALUES(0,3)");
        db.execSQL("INSERT INTO color_combina VALUES(1,0)");
        db.execSQL("INSERT INTO color_combina VALUES(1,2)");
        db.execSQL("INSERT INTO color_combina VALUES(2,0)");
        db.execSQL("INSERT INTO color_combina VALUES(2,1)");

        //Climas
        db.execSQL("INSERT INTO clima VALUES(0,'caluroso')");
        db.execSQL("INSERT INTO clima VALUES(1,'fío')");
        db.execSQL("INSERT INTO clima VALUES(2,'templado')");
        db.execSQL("INSERT INTO clima VALUES(3,'lluvia')");

        //usos
        db.execSQL("INSERT INTO uso VALUES(0,'trabajo')");
        db.execSQL("INSERT INTO uso VALUES(1,'reunión')");
        db.execSQL("INSERT INTO uso VALUES(2,'deporte')");
        db.execSQL("INSERT INTO uso VALUES(3,'fiesta')");
        db.execSQL("INSERT INTO uso VALUES(4,'informal')");

        //tipos de partes de conjunto
        db.execSQL("INSERT INTO tipo_parte_conjunto VALUES(0,'superior ligera')");
        db.execSQL("INSERT INTO tipo_parte_conjunto VALUES(1,'superior abrigo')");
        db.execSQL("INSERT INTO tipo_parte_conjunto VALUES(2,'inferior')");
        db.execSQL("INSERT INTO tipo_parte_conjunto VALUES(3,'calzado')");
        db.execSQL("INSERT INTO tipo_parte_conjunto VALUES(4,'complemento')");
    }


    private void insertarDatosPrueba(SQLiteDatabase db){

    }
}
