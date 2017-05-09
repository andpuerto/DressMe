package uoc.master.angel.dressme.db.helper;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteStatement;
import uoc.master.angel.dressme.util.ImageUtil;

/**
 * Created by angel on 01/04/2017.
 */

public class DressMeSQLHelper extends SQLiteOpenHelper {
    public static String dbName = "DBDressMe";
    public static int dbCurrentVersion = 1;

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

        //PARA PRUEBAS: INSERTA DATOS DE PRUEBA
        this.insertarDatosPrueba(db);
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
        //Lluvia deberia ser un boolean pero SQLite no los admite, asi que hay que utilizar un
        //integer con 0=false y 1=true. Un valor true (1) representara que el clima es de lluvia
        //Un valor false (0) no indica ausencia de lluvia, sino que en ese clima el parametro
        //lluvia es indiferente
        db.execSQL("CREATE TABLE clima(id INTEGER PRIMARY KEY, nombre TEXT, " +
                "min_temp REAL, max_temp REAL, lluvia INTEGER)");

        //Tabla uso
        //Identifica los diferentes usos sociales posibles para las prendas
        db.execSQL("CREATE TABLE uso(id INTEGER PRIMARY KEY, nombre TEXT)");

        //Tabla tipo_parte_conjunto
        db.execSQL("CREATE TABLE tipo_parte_conjunto (id INTEGER PRIMARY KEY, nombre TEXT)");

        //Tabla prenda
        db.execSQL("CREATE TABLE prenda (id INTEGER PRIMARY KEY, foto BLOB, marca TEXT, material" +
                " TEXT, color INTEGER REFERENCES color(id) ON DELETE RESTRICT ON UPDATE CASCADE, " +
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
     * @param db objeto SQLiteDatabase
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
        db.execSQL("INSERT INTO color VALUES(2,'beige','D0B99A')");
        db.execSQL("INSERT INTO color VALUES(3,'rojo','770000')");
        db.execSQL("INSERT INTO color VALUES(4,'verde','007700')");
        db.execSQL("INSERT INTO color VALUES(5,'azul','000077')");

        //Combinaciones de colores
        //Por comodidad a la hora de consultar, se introduciran los colores combinados tomando
        //como base el color de la izquierda, sin considerarse una relacion conmutativa
        //Es decir, si con el color 3 combina el 4, introduciremos (3,4), pero esto solo servirá
        //para las búsquedas de los colores que combinan con el 3, no para los que combinan con el 4
        //Si queremos esto último, tendremos que introducir también (4,3)
        //Tambien debera incluirse una combinacion consigo mismo, considerando que puede que un color
        //no convenga repetirlo en mas de una prenda del vestuario, por lo que debe estar combinado
        //explicitamente
        db.execSQL("INSERT INTO color_combina VALUES(0,0)");
        db.execSQL("INSERT INTO color_combina VALUES(0,1)");
        db.execSQL("INSERT INTO color_combina VALUES(0,2)");
        db.execSQL("INSERT INTO color_combina VALUES(0,3)");
        db.execSQL("INSERT INTO color_combina VALUES(0,4)");
        db.execSQL("INSERT INTO color_combina VALUES(0,5)");
        db.execSQL("INSERT INTO color_combina VALUES(1,0)");
        db.execSQL("INSERT INTO color_combina VALUES(1,1)");
        db.execSQL("INSERT INTO color_combina VALUES(1,2)");
        db.execSQL("INSERT INTO color_combina VALUES(1,3)");
        db.execSQL("INSERT INTO color_combina VALUES(1,4)");
        db.execSQL("INSERT INTO color_combina VALUES(1,5)");
        db.execSQL("INSERT INTO color_combina VALUES(2,0)");
        db.execSQL("INSERT INTO color_combina VALUES(2,1)");
        db.execSQL("INSERT INTO color_combina VALUES(2,2)");
        db.execSQL("INSERT INTO color_combina VALUES(3,0)");
        db.execSQL("INSERT INTO color_combina VALUES(3,1)");
        db.execSQL("INSERT INTO color_combina VALUES(3,3)");
        db.execSQL("INSERT INTO color_combina VALUES(3,4)");
        db.execSQL("INSERT INTO color_combina VALUES(4,0)");
        db.execSQL("INSERT INTO color_combina VALUES(4,1)");
        db.execSQL("INSERT INTO color_combina VALUES(4,3)");
        db.execSQL("INSERT INTO color_combina VALUES(4,4)");
        db.execSQL("INSERT INTO color_combina VALUES(4,5)");
        db.execSQL("INSERT INTO color_combina VALUES(5,0)");
        db.execSQL("INSERT INTO color_combina VALUES(5,1)");
        db.execSQL("INSERT INTO color_combina VALUES(5,4)");
        db.execSQL("INSERT INTO color_combina VALUES(5,5)");


        //Climas
        //Algunos climas pueden representar solo condiciones de lluvia sin importar la temperatura
        //Así, los valores de temperatura seran tan amplios como para abarcar todos los rangos
        //En otros casos se recoge temperatura, sin importar la lluvia. Aqui dejamos la lluvia a
        //falso. Como una prenda puede tener varios climas adecuados, si es valida para lluvia
        //sera adecuada tambien para los climas de lluvia, con lo que las consultas de prendas
        //para todos los climas adecuados solo nos de
        db.execSQL("INSERT INTO clima VALUES(0,'caluroso',22,100,0)");
        db.execSQL("INSERT INTO clima VALUES(1,'frío',-100,12.50,0)");
        db.execSQL("INSERT INTO clima VALUES(2,'templado',12.51,21.99,0)");
        db.execSQL("INSERT INTO clima VALUES(3,'lluvia',-100,100,1)");

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
        //db.execSQL("INSERT INTO tipo_parte_conjunto VALUES(4,'complemento')");
    }


    private void insertarDatosPrueba(SQLiteDatabase db){


        //Metemos unas prendas con una imagen de prueba asociada. Esto solo va a funcionar en nuestro emulador
        //puesto que hemos insertado previamente las imagenes
        String dir = "/sdcard/DCIM/ropa/";
        String[] paths = {dir+"Camiseta_peq.jpg", dir+"Jersey_peq.jpg", dir+"Pantalon_peq.jpg", dir+"Zapatos_peq.jpg"};

        String sql =   "INSERT INTO prenda(id,foto,marca,material,color,tipo_parte_conjunto) VALUES (?,?," +
                "?,?,?,?)";
        SQLiteStatement insertStmt = db.compileStatement(sql);


        //Primera prenda
        insertStmt.clearBindings();
        insertStmt.bindString(1, "0");
        insertStmt.bindBlob(2, ImageUtil.toByteArray(paths[0]));
        insertStmt.bindString(3, "Zara");
        insertStmt.bindString(4, "Algodón");
        insertStmt.bindString(5, "0");
        insertStmt.bindString(6, "0");
        insertStmt.executeInsert();

        //Segunda prenda
        insertStmt.clearBindings();
        insertStmt.bindString(1, "1");
        insertStmt.bindBlob(2, ImageUtil.toByteArray(paths[1]));
        insertStmt.bindString(3, "Zara");
        insertStmt.bindString(4, "Algodón");
        insertStmt.bindString(5, "1");
        insertStmt.bindString(6, "1");
        insertStmt.executeInsert();

        //Tercera prenda
        insertStmt.clearBindings();
        insertStmt.bindString(1, "2");
        insertStmt.bindBlob(2, ImageUtil.toByteArray(paths[2]));
        insertStmt.bindString(3, "Zara");
        insertStmt.bindString(4, "Algodón");
        insertStmt.bindString(5, "3");
        insertStmt.bindString(6, "2");
        insertStmt.executeInsert();

        //Cuarta prenda
        insertStmt.clearBindings();
        insertStmt.bindString(1, "3");
        insertStmt.bindBlob(2, ImageUtil.toByteArray(paths[3]));
        insertStmt.bindString(3, "Zara");
        insertStmt.bindString(4, "Algodón");
        insertStmt.bindString(5, "4");
        insertStmt.bindString(6, "3");
        insertStmt.executeInsert();

        //Quinta prenda
        insertStmt.clearBindings();
        insertStmt.bindString(1, "4");
        insertStmt.bindBlob(2, ImageUtil.toByteArray(paths[0]));
        insertStmt.bindString(3, "Zara");
        insertStmt.bindString(4, "Algodón");
        insertStmt.bindString(5, "1");
        insertStmt.bindString(6, "0");
        insertStmt.executeInsert();

        //Sexta prenda
        insertStmt.clearBindings();
        insertStmt.bindString(1, "5");
        insertStmt.bindBlob(2, ImageUtil.toByteArray(paths[1]));
        insertStmt.bindString(3, "Zara");
        insertStmt.bindString(4, "Algodón");
        insertStmt.bindString(5, "0");
        insertStmt.bindString(6, "1");
        insertStmt.executeInsert();

        //Septima prenda
        insertStmt.clearBindings();
        insertStmt.bindString(1, "6");
        insertStmt.bindBlob(2, ImageUtil.toByteArray(paths[0]));
        insertStmt.bindString(3, "Zara");
        insertStmt.bindString(4, "Algodón");
        insertStmt.bindString(5, "2");
        insertStmt.bindString(6, "0");
        insertStmt.executeInsert();

        //Octava prenda
        insertStmt.clearBindings();
        insertStmt.bindString(1, "7");
        insertStmt.bindBlob(2, ImageUtil.toByteArray(paths[3]));
        insertStmt.bindString(3, "Zara");
        insertStmt.bindString(4, "Algodón");
        insertStmt.bindString(5, "5");
        insertStmt.bindString(6, "3");
        insertStmt.executeInsert();

        //Novena prenda
        insertStmt.clearBindings();
        insertStmt.bindString(1, "8");
        insertStmt.bindBlob(2, ImageUtil.toByteArray(paths[1]));
        insertStmt.bindString(3, "Zara");
        insertStmt.bindString(4, "Algodón");
        insertStmt.bindString(5, "2");
        insertStmt.bindString(6, "1");
        insertStmt.executeInsert();

        //Decima prenda
        insertStmt.clearBindings();
        insertStmt.bindString(1, "9");
        insertStmt.bindBlob(2, ImageUtil.toByteArray(paths[2]));
        insertStmt.bindString(3, "Zara");
        insertStmt.bindString(4, "Algodón");
        insertStmt.bindString(5, "3");
        insertStmt.bindString(6, "2");
        insertStmt.executeInsert();

//        db.execSQL("INSERT INTO prenda(id,marca,material,color,tipo_parte_conjunto) VALUES (0,'Zara'," +
//                "'Algodón',1,0)");
//        db.execSQL("INSERT INTO prenda(id,marca,material,color,tipo_parte_conjunto) VALUES (1,'Zara'," +
//                "'Algodón',0,1)");
//        db.execSQL("INSERT INTO prenda(id,marca,material,color,tipo_parte_conjunto) VALUES (2,'Zara'," +
//                "'Algodón',2,2)");
//        db.execSQL("INSERT INTO prenda(id,marca,material,color,tipo_parte_conjunto) VALUES (3,'Zara'," +
//                "'Algodón',0,3)");
//        db.execSQL("INSERT INTO prenda(id,marca,material,color,tipo_parte_conjunto) VALUES (4,'Zara'," +
//                "'Algodón',0,3)");
//        db.execSQL("INSERT INTO prenda(id,marca,material,color,tipo_parte_conjunto) VALUES (5,'Zara'," +
//                "'Algodón',0,3)");
//        db.execSQL("INSERT INTO prenda(id,marca,material,color,tipo_parte_conjunto) VALUES (6,'Zara'," +
//                "'Algodón',0,3)");
//        db.execSQL("INSERT INTO prenda(id,marca,material,color,tipo_parte_conjunto) VALUES (7,'Zara'," +
//                "'Algodón',0,3)");
//        db.execSQL("INSERT INTO prenda(id,marca,material,color,tipo_parte_conjunto) VALUES (8,'Zara'," +
//                "'Algodón',0,3)");

        db.execSQL("INSERT INTO prenda_clima VALUES(0,1)");
        db.execSQL("INSERT INTO prenda_clima VALUES(0,2)");
        db.execSQL("INSERT INTO prenda_clima VALUES(0,3)");
        db.execSQL("INSERT INTO prenda_clima VALUES(1,0)");
        db.execSQL("INSERT INTO prenda_clima VALUES(1,2)");
        db.execSQL("INSERT INTO prenda_clima VALUES(1,3)");
        db.execSQL("INSERT INTO prenda_clima VALUES(2,2)");
        db.execSQL("INSERT INTO prenda_clima VALUES(2,3)");
        db.execSQL("INSERT INTO prenda_clima VALUES(3,2)");
        db.execSQL("INSERT INTO prenda_clima VALUES(3,3)");
        db.execSQL("INSERT INTO prenda_clima VALUES(4,2)");
        db.execSQL("INSERT INTO prenda_clima VALUES(4,3)");
        db.execSQL("INSERT INTO prenda_clima VALUES(5,1)");
        db.execSQL("INSERT INTO prenda_clima VALUES(5,2)");
        db.execSQL("INSERT INTO prenda_clima VALUES(5,3)");
        db.execSQL("INSERT INTO prenda_clima VALUES(6,0)");
        db.execSQL("INSERT INTO prenda_clima VALUES(6,2)");
        db.execSQL("INSERT INTO prenda_clima VALUES(7,1)");
        db.execSQL("INSERT INTO prenda_clima VALUES(7,2)");
        db.execSQL("INSERT INTO prenda_clima VALUES(8,1)");
        db.execSQL("INSERT INTO prenda_clima VALUES(9,0)");
        db.execSQL("INSERT INTO prenda_clima VALUES(9,2)");


        db.execSQL("INSERT INTO uso_prenda VALUES(0,0)");
        db.execSQL("INSERT INTO uso_prenda VALUES(0,1)");
        db.execSQL("INSERT INTO uso_prenda VALUES(1,0)");
        db.execSQL("INSERT INTO uso_prenda VALUES(1,3)");
        db.execSQL("INSERT INTO uso_prenda VALUES(2,0)");
        db.execSQL("INSERT INTO uso_prenda VALUES(2,3)");
        db.execSQL("INSERT INTO uso_prenda VALUES(3,0)");
        db.execSQL("INSERT INTO uso_prenda VALUES(3,4)");
        db.execSQL("INSERT INTO uso_prenda VALUES(4,0)");
        db.execSQL("INSERT INTO uso_prenda VALUES(5,0)");
        db.execSQL("INSERT INTO uso_prenda VALUES(6,3)");
        db.execSQL("INSERT INTO uso_prenda VALUES(7,0)");
        db.execSQL("INSERT INTO uso_prenda VALUES(7,4)");
        db.execSQL("INSERT INTO uso_prenda VALUES(8,0)");
        db.execSQL("INSERT INTO uso_prenda VALUES(8,3)");
        db.execSQL("INSERT INTO uso_prenda VALUES(9,0)");
        db.execSQL("INSERT INTO uso_prenda VALUES(9,1)");


        db.execSQL("INSERT INTO conjunto VALUES(0)");
        db.execSQL("INSERT INTO parte_conjunto VALUES(0,0,4,0)");
        db.execSQL("INSERT INTO parte_conjunto VALUES(1,1,8,0)");
        db.execSQL("INSERT INTO parte_conjunto VALUES(2,2,9,0)");
        db.execSQL("INSERT INTO parte_conjunto VALUES(3,3,7,0)");

        db.execSQL("INSERT INTO conjunto VALUES(1)");
        db.execSQL("INSERT INTO parte_conjunto VALUES(4,0,0,1)");
        db.execSQL("INSERT INTO parte_conjunto VALUES(5,1,1,1)");
        db.execSQL("INSERT INTO parte_conjunto VALUES(6,2,2,1)");
        db.execSQL("INSERT INTO parte_conjunto VALUES(7,3,3,1)");

        db.execSQL("INSERT INTO conjunto VALUES(2)");
        db.execSQL("INSERT INTO parte_conjunto VALUES(8,1,8,2)");
        db.execSQL("INSERT INTO parte_conjunto VALUES(9,2,2,2)");
        db.execSQL("INSERT INTO parte_conjunto VALUES(10,3,3,2)");

        db.execSQL("INSERT INTO conjunto VALUES(3)");
        db.execSQL("INSERT INTO parte_conjunto VALUES(11,0,0,3)");
        db.execSQL("INSERT INTO parte_conjunto VALUES(12,3,7,3)");

        db.execSQL("INSERT INTO conjunto VALUES(4)");
        db.execSQL("INSERT INTO parte_conjunto VALUES(13,1,8,4)");
        db.execSQL("INSERT INTO parte_conjunto VALUES(14,2,2,4)");
        db.execSQL("INSERT INTO parte_conjunto VALUES(15,3,3,4)");

        db.execSQL("INSERT INTO conjunto VALUES(5)");
        db.execSQL("INSERT INTO parte_conjunto VALUES(16,1,8,5)");
        db.execSQL("INSERT INTO parte_conjunto VALUES(17,2,2,5)");
        db.execSQL("INSERT INTO parte_conjunto VALUES(18,3,3,5)");

        db.execSQL("INSERT INTO conjunto VALUES(6)");
        db.execSQL("INSERT INTO parte_conjunto VALUES(19,1,8,6)");
        db.execSQL("INSERT INTO parte_conjunto VALUES(20,2,2,6)");
        db.execSQL("INSERT INTO parte_conjunto VALUES(21,3,3,6)");


    }
}
