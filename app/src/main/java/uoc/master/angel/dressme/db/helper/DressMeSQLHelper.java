package uoc.master.angel.dressme.db.helper;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteStatement;

import uoc.master.angel.dressme.R;
import uoc.master.angel.dressme.util.ImageUtil;

/**
 * Created by angel on 01/04/2017.
 */

public class DressMeSQLHelper extends SQLiteOpenHelper {
    public static String dbName = "DBDressMe";
    public static int dbCurrentVersion = 1;
    private Context context;

    public DressMeSQLHelper(Context contexto, String nombre,
                                CursorFactory factory, int version) {
        super(contexto, nombre, factory, version);
        this.context = contexto;
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
        db.execSQL("INSERT INTO color VALUES(0,'"+ context.getString(R.string.negro) +"','000000')");
        db.execSQL("INSERT INTO color VALUES(1,'"+ context.getString(R.string.gris) +"','999999')");
        db.execSQL("INSERT INTO color VALUES(2,'"+ context.getString(R.string.blanco) +"','ffffff')");
        db.execSQL("INSERT INTO color VALUES(3,'"+ context.getString(R.string.marron) +"','8B4513')");
        db.execSQL("INSERT INTO color VALUES(4,'"+ context.getString(R.string.beige) +"','D0B99A')");
        db.execSQL("INSERT INTO color VALUES(5,'"+ context.getString(R.string.rojo) +"','770000')");
        db.execSQL("INSERT INTO color VALUES(6,'"+ context.getString(R.string.verde) +"','007700')");
        db.execSQL("INSERT INTO color VALUES(7,'"+ context.getString(R.string.azul) +"','000077')");
        db.execSQL("INSERT INTO color VALUES(8,'"+ context.getString(R.string.rosa) +"','ff4081')");

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
        db.execSQL("INSERT INTO color_combina VALUES(0,6)");
        db.execSQL("INSERT INTO color_combina VALUES(0,7)");
        db.execSQL("INSERT INTO color_combina VALUES(0,8)");

        db.execSQL("INSERT INTO color_combina VALUES(1,0)");
        db.execSQL("INSERT INTO color_combina VALUES(1,1)");
        db.execSQL("INSERT INTO color_combina VALUES(1,2)");
        db.execSQL("INSERT INTO color_combina VALUES(1,3)");
        db.execSQL("INSERT INTO color_combina VALUES(1,4)");
        db.execSQL("INSERT INTO color_combina VALUES(1,5)");
        db.execSQL("INSERT INTO color_combina VALUES(1,6)");
        db.execSQL("INSERT INTO color_combina VALUES(1,7)");
        db.execSQL("INSERT INTO color_combina VALUES(1,8)");

        db.execSQL("INSERT INTO color_combina VALUES(2,0)");
        db.execSQL("INSERT INTO color_combina VALUES(2,1)");
        db.execSQL("INSERT INTO color_combina VALUES(2,2)");
        db.execSQL("INSERT INTO color_combina VALUES(2,3)");
        db.execSQL("INSERT INTO color_combina VALUES(2,4)");
        db.execSQL("INSERT INTO color_combina VALUES(2,6)");
        db.execSQL("INSERT INTO color_combina VALUES(2,7)");

        db.execSQL("INSERT INTO color_combina VALUES(3,0)");
        db.execSQL("INSERT INTO color_combina VALUES(3,1)");
        db.execSQL("INSERT INTO color_combina VALUES(3,2)");
        db.execSQL("INSERT INTO color_combina VALUES(3,3)");
        db.execSQL("INSERT INTO color_combina VALUES(3,4)");
        db.execSQL("INSERT INTO color_combina VALUES(3,5)");
        db.execSQL("INSERT INTO color_combina VALUES(3,6)");
        db.execSQL("INSERT INTO color_combina VALUES(3,7)");
        db.execSQL("INSERT INTO color_combina VALUES(3,8)");

        db.execSQL("INSERT INTO color_combina VALUES(4,0)");
        db.execSQL("INSERT INTO color_combina VALUES(4,1)");
        db.execSQL("INSERT INTO color_combina VALUES(4,2)");
        db.execSQL("INSERT INTO color_combina VALUES(4,3)");
        db.execSQL("INSERT INTO color_combina VALUES(4,4)");

        db.execSQL("INSERT INTO color_combina VALUES(5,0)");
        db.execSQL("INSERT INTO color_combina VALUES(5,1)");
        db.execSQL("INSERT INTO color_combina VALUES(5,3)");
        db.execSQL("INSERT INTO color_combina VALUES(5,5)");
        db.execSQL("INSERT INTO color_combina VALUES(5,6)");

        db.execSQL("INSERT INTO color_combina VALUES(6,0)");
        db.execSQL("INSERT INTO color_combina VALUES(6,1)");
        db.execSQL("INSERT INTO color_combina VALUES(6,2)");
        db.execSQL("INSERT INTO color_combina VALUES(6,3)");
        db.execSQL("INSERT INTO color_combina VALUES(6,5)");
        db.execSQL("INSERT INTO color_combina VALUES(6,6)");
        db.execSQL("INSERT INTO color_combina VALUES(6,7)");

        db.execSQL("INSERT INTO color_combina VALUES(7,0)");
        db.execSQL("INSERT INTO color_combina VALUES(7,1)");
        db.execSQL("INSERT INTO color_combina VALUES(7,2)");
        db.execSQL("INSERT INTO color_combina VALUES(7,3)");
        db.execSQL("INSERT INTO color_combina VALUES(7,6)");
        db.execSQL("INSERT INTO color_combina VALUES(7,7)");

        db.execSQL("INSERT INTO color_combina VALUES(8,0)");
        db.execSQL("INSERT INTO color_combina VALUES(8,1)");
        db.execSQL("INSERT INTO color_combina VALUES(8,8)");


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
//        insertStmt.bindBlob(2, ImageUtil.toByteArray(paths[0]));
        insertStmt.bindBlob(2, ImageUtil.drawableToByteArray(context.getResources().getDrawable(R.drawable.superior_lig1)));
        insertStmt.bindString(3, "Zara");
        insertStmt.bindString(4, "Algodón");
        insertStmt.bindString(5, "2");
        insertStmt.bindString(6, "0");
        insertStmt.executeInsert();

        //Segunda prenda
        insertStmt.clearBindings();
        insertStmt.bindString(1, "1");
        insertStmt.bindBlob(2, ImageUtil.drawableToByteArray(context.getResources().getDrawable(R.drawable.superior_lig2)));
        insertStmt.bindString(3, "Zara");
        insertStmt.bindString(4, "Algodón");
        insertStmt.bindString(5, "4");
        insertStmt.bindString(6, "0");
        insertStmt.executeInsert();

        //Tercera prenda
        insertStmt.clearBindings();
        insertStmt.bindString(1, "2");
        insertStmt.bindBlob(2, ImageUtil.drawableToByteArray(context.getResources().getDrawable(R.drawable.superior_lig3)));
        insertStmt.bindString(3, "Zara");
        insertStmt.bindString(4, "Algodón");
        insertStmt.bindString(5, "2");
        insertStmt.bindString(6, "0");
        insertStmt.executeInsert();

        //Cuarta prenda
        insertStmt.clearBindings();
        insertStmt.bindString(1, "3");
        insertStmt.bindBlob(2, ImageUtil.drawableToByteArray(context.getResources().getDrawable(R.drawable.superior_lig4)));
        insertStmt.bindString(3, "Zara");
        insertStmt.bindString(4, "Algodón");
        insertStmt.bindString(5, "7");
        insertStmt.bindString(6, "0");
        insertStmt.executeInsert();

        //Quinta prenda
        insertStmt.clearBindings();
        insertStmt.bindString(1, "4");
        insertStmt.bindBlob(2, ImageUtil.drawableToByteArray(context.getResources().getDrawable(R.drawable.superior_lig5)));
        insertStmt.bindString(3, "Zara");
        insertStmt.bindString(4, "Algodón");
        insertStmt.bindString(5, "1");
        insertStmt.bindString(6, "0");
        insertStmt.executeInsert();

        //Sexta prenda
        insertStmt.clearBindings();
        insertStmt.bindString(1, "5");
        insertStmt.bindBlob(2, ImageUtil.drawableToByteArray(context.getResources().getDrawable(R.drawable.superior_lig6)));
        insertStmt.bindString(3, "Zara");
        insertStmt.bindString(4, "Algodón");
        insertStmt.bindString(5, "8");
        insertStmt.bindString(6, "0");
        insertStmt.executeInsert();

        //Septima prenda
        insertStmt.clearBindings();
        insertStmt.bindString(1, "6");
        insertStmt.bindBlob(2, ImageUtil.drawableToByteArray(context.getResources().getDrawable(R.drawable.superior_abr1)));
        insertStmt.bindString(3, "Zara");
        insertStmt.bindString(4, "Algodón");
        insertStmt.bindString(5, "2");
        insertStmt.bindString(6, "1");
        insertStmt.executeInsert();

        //Octava prenda
        insertStmt.clearBindings();
        insertStmt.bindString(1, "7");
        insertStmt.bindBlob(2, ImageUtil.drawableToByteArray(context.getResources().getDrawable(R.drawable.superior_abr2)));
        insertStmt.bindString(3, "Zara");
        insertStmt.bindString(4, "Algodón");
        insertStmt.bindString(5, "4");
        insertStmt.bindString(6, "1");
        insertStmt.executeInsert();

        //Novena prenda
        insertStmt.clearBindings();
        insertStmt.bindString(1, "8");
        insertStmt.bindBlob(2, ImageUtil.drawableToByteArray(context.getResources().getDrawable(R.drawable.superior_abr3)));
        insertStmt.bindString(3, "Zara");
        insertStmt.bindString(4, "Algodón");
        insertStmt.bindString(5, "1");
        insertStmt.bindString(6, "1");
        insertStmt.executeInsert();

        //Decima prenda
        insertStmt.clearBindings();
        insertStmt.bindString(1, "9");
        insertStmt.bindBlob(2, ImageUtil.drawableToByteArray(context.getResources().getDrawable(R.drawable.superior_abr4)));
        insertStmt.bindString(3, "Zara");
        insertStmt.bindString(4, "Algodón");
        insertStmt.bindString(5, "1");
        insertStmt.bindString(6, "1");
        insertStmt.executeInsert();


        insertStmt.clearBindings();
        insertStmt.bindString(1, "10");
        insertStmt.bindBlob(2, ImageUtil.drawableToByteArray(context.getResources().getDrawable(R.drawable.superior_abr5)));
        insertStmt.bindString(3, "Zara");
        insertStmt.bindString(4, "Algodón");
        insertStmt.bindString(5, "0");
        insertStmt.bindString(6, "1");
        insertStmt.executeInsert();


        insertStmt.clearBindings();
        insertStmt.bindString(1, "11");
        insertStmt.bindBlob(2, ImageUtil.drawableToByteArray(context.getResources().getDrawable(R.drawable.superior_abr6)));
        insertStmt.bindString(3, "Zara");
        insertStmt.bindString(4, "Algodón");
        insertStmt.bindString(5, "1");
        insertStmt.bindString(6, "1");
        insertStmt.executeInsert();


        insertStmt.clearBindings();
        insertStmt.bindString(1, "12");
        insertStmt.bindBlob(2, ImageUtil.drawableToByteArray(context.getResources().getDrawable(R.drawable.inferior1)));
        insertStmt.bindString(3, "Zara");
        insertStmt.bindString(4, "Algodón");
        insertStmt.bindString(5, "3");
        insertStmt.bindString(6, "2");
        insertStmt.executeInsert();


        insertStmt.clearBindings();
        insertStmt.bindString(1, "13");
        insertStmt.bindBlob(2, ImageUtil.drawableToByteArray(context.getResources().getDrawable(R.drawable.inferior2)));
        insertStmt.bindString(3, "Zara");
        insertStmt.bindString(4, "Algodón");
        insertStmt.bindString(5, "2");
        insertStmt.bindString(6, "2");
        insertStmt.executeInsert();


        insertStmt.clearBindings();
        insertStmt.bindString(1, "14");
        insertStmt.bindBlob(2, ImageUtil.drawableToByteArray(context.getResources().getDrawable(R.drawable.inferior3)));
        insertStmt.bindString(3, "Zara");
        insertStmt.bindString(4, "Algodón");
        insertStmt.bindString(5, "7");
        insertStmt.bindString(6, "2");
        insertStmt.executeInsert();


        insertStmt.clearBindings();
        insertStmt.bindString(1, "15");
        insertStmt.bindBlob(2, ImageUtil.drawableToByteArray(context.getResources().getDrawable(R.drawable.inferior4)));
        insertStmt.bindString(3, "Zara");
        insertStmt.bindString(4, "Algodón");
        insertStmt.bindString(5, "0");
        insertStmt.bindString(6, "2");
        insertStmt.executeInsert();


        insertStmt.clearBindings();
        insertStmt.bindString(1, "16");
        insertStmt.bindBlob(2, ImageUtil.drawableToByteArray(context.getResources().getDrawable(R.drawable.inferior5)));
        insertStmt.bindString(3, "Zara");
        insertStmt.bindString(4, "Algodón");
        insertStmt.bindString(5, "4");
        insertStmt.bindString(6, "2");
        insertStmt.executeInsert();


        insertStmt.clearBindings();
        insertStmt.bindString(1, "17");
        insertStmt.bindBlob(2, ImageUtil.drawableToByteArray(context.getResources().getDrawable(R.drawable.inferior6)));
        insertStmt.bindString(3, "Zara");
        insertStmt.bindString(4, "Algodón");
        insertStmt.bindString(5, "1");
        insertStmt.bindString(6, "2");
        insertStmt.executeInsert();


        insertStmt.clearBindings();
        insertStmt.bindString(1, "18");
        insertStmt.bindBlob(2, ImageUtil.drawableToByteArray(context.getResources().getDrawable(R.drawable.calzado1)));
        insertStmt.bindString(3, "Zara");
        insertStmt.bindString(4, "Algodón");
        insertStmt.bindString(5, "0");
        insertStmt.bindString(6, "3");
        insertStmt.executeInsert();


        insertStmt.clearBindings();
        insertStmt.bindString(1, "19");
        insertStmt.bindBlob(2, ImageUtil.drawableToByteArray(context.getResources().getDrawable(R.drawable.calzado2)));
        insertStmt.bindString(3, "Zara");
        insertStmt.bindString(4, "Algodón");
        insertStmt.bindString(5, "3");
        insertStmt.bindString(6, "3");
        insertStmt.executeInsert();


        insertStmt.clearBindings();
        insertStmt.bindString(1, "20");
        insertStmt.bindBlob(2, ImageUtil.drawableToByteArray(context.getResources().getDrawable(R.drawable.calzado3)));
        insertStmt.bindString(3, "Zara");
        insertStmt.bindString(4, "Algodón");
        insertStmt.bindString(5, "0");
        insertStmt.bindString(6, "3");
        insertStmt.executeInsert();


        insertStmt.clearBindings();
        insertStmt.bindString(1, "21");
        insertStmt.bindBlob(2, ImageUtil.drawableToByteArray(context.getResources().getDrawable(R.drawable.calzado4)));
        insertStmt.bindString(3, "Zara");
        insertStmt.bindString(4, "Algodón");
        insertStmt.bindString(5, "1");
        insertStmt.bindString(6, "3");
        insertStmt.executeInsert();


        insertStmt.clearBindings();
        insertStmt.bindString(1, "22");
        insertStmt.bindBlob(2, ImageUtil.drawableToByteArray(context.getResources().getDrawable(R.drawable.calzado5)));
        insertStmt.bindString(3, "Zara");
        insertStmt.bindString(4, "Algodón");
        insertStmt.bindString(5, "3");
        insertStmt.bindString(6, "3");
        insertStmt.executeInsert();

        db.execSQL("INSERT INTO prenda_clima VALUES(0,1)");
        db.execSQL("INSERT INTO prenda_clima VALUES(0,2)");
        db.execSQL("INSERT INTO prenda_clima VALUES(1,1)");
        db.execSQL("INSERT INTO prenda_clima VALUES(1,2)");
        db.execSQL("INSERT INTO prenda_clima VALUES(1,3)");
        db.execSQL("INSERT INTO prenda_clima VALUES(2,0)");
        db.execSQL("INSERT INTO prenda_clima VALUES(2,1)");
        db.execSQL("INSERT INTO prenda_clima VALUES(2,2)");
        db.execSQL("INSERT INTO prenda_clima VALUES(2,3)");
        db.execSQL("INSERT INTO prenda_clima VALUES(3,1)");
        db.execSQL("INSERT INTO prenda_clima VALUES(3,2)");
        db.execSQL("INSERT INTO prenda_clima VALUES(3,3)");
        db.execSQL("INSERT INTO prenda_clima VALUES(4,0)");
        db.execSQL("INSERT INTO prenda_clima VALUES(4,1)");
        db.execSQL("INSERT INTO prenda_clima VALUES(4,2)");
        db.execSQL("INSERT INTO prenda_clima VALUES(4,3)");
        db.execSQL("INSERT INTO prenda_clima VALUES(5,0)");
        db.execSQL("INSERT INTO prenda_clima VALUES(5,1)");
        db.execSQL("INSERT INTO prenda_clima VALUES(5,2)");
        db.execSQL("INSERT INTO prenda_clima VALUES(5,3)");

        db.execSQL("INSERT INTO prenda_clima VALUES(6,2)");
        db.execSQL("INSERT INTO prenda_clima VALUES(6,3)");
        db.execSQL("INSERT INTO prenda_clima VALUES(7,1)");
        db.execSQL("INSERT INTO prenda_clima VALUES(7,2)");
        db.execSQL("INSERT INTO prenda_clima VALUES(7,3)");
        db.execSQL("INSERT INTO prenda_clima VALUES(8,1)");
        db.execSQL("INSERT INTO prenda_clima VALUES(8,2)");
        db.execSQL("INSERT INTO prenda_clima VALUES(9,1)");
        db.execSQL("INSERT INTO prenda_clima VALUES(9,3)");
        db.execSQL("INSERT INTO prenda_clima VALUES(10,1)");
        db.execSQL("INSERT INTO prenda_clima VALUES(10,3)");
        db.execSQL("INSERT INTO prenda_clima VALUES(11,1)");
        db.execSQL("INSERT INTO prenda_clima VALUES(11,3)");

        db.execSQL("INSERT INTO prenda_clima VALUES(12,1)");
        db.execSQL("INSERT INTO prenda_clima VALUES(12,2)");
        db.execSQL("INSERT INTO prenda_clima VALUES(12,3)");
        db.execSQL("INSERT INTO prenda_clima VALUES(13,0)");
        db.execSQL("INSERT INTO prenda_clima VALUES(13,1)");
        db.execSQL("INSERT INTO prenda_clima VALUES(13,2)");
        db.execSQL("INSERT INTO prenda_clima VALUES(14,1)");
        db.execSQL("INSERT INTO prenda_clima VALUES(14,2)");
        db.execSQL("INSERT INTO prenda_clima VALUES(14,3)");
        db.execSQL("INSERT INTO prenda_clima VALUES(15,1)");
        db.execSQL("INSERT INTO prenda_clima VALUES(15,2)");
        db.execSQL("INSERT INTO prenda_clima VALUES(15,3)");
        db.execSQL("INSERT INTO prenda_clima VALUES(16,0)");
        db.execSQL("INSERT INTO prenda_clima VALUES(16,1)");
        db.execSQL("INSERT INTO prenda_clima VALUES(17,0)");
        db.execSQL("INSERT INTO prenda_clima VALUES(17,1)");

        db.execSQL("INSERT INTO prenda_clima VALUES(18,0)");
        db.execSQL("INSERT INTO prenda_clima VALUES(18,1)");
        db.execSQL("INSERT INTO prenda_clima VALUES(18,2)");
        db.execSQL("INSERT INTO prenda_clima VALUES(18,3)");
        db.execSQL("INSERT INTO prenda_clima VALUES(19,0)");
        db.execSQL("INSERT INTO prenda_clima VALUES(19,1)");
        db.execSQL("INSERT INTO prenda_clima VALUES(19,2)");
        db.execSQL("INSERT INTO prenda_clima VALUES(20,1)");
        db.execSQL("INSERT INTO prenda_clima VALUES(20,2)");
        db.execSQL("INSERT INTO prenda_clima VALUES(20,3)");
        db.execSQL("INSERT INTO prenda_clima VALUES(21,0)");
        db.execSQL("INSERT INTO prenda_clima VALUES(21,1)");
        db.execSQL("INSERT INTO prenda_clima VALUES(22,1)");
        db.execSQL("INSERT INTO prenda_clima VALUES(22,2)");
        db.execSQL("INSERT INTO prenda_clima VALUES(22,3)");


        db.execSQL("INSERT INTO uso_prenda VALUES(0,3)");
        db.execSQL("INSERT INTO uso_prenda VALUES(0,4)");
        db.execSQL("INSERT INTO uso_prenda VALUES(1,0)");
        db.execSQL("INSERT INTO uso_prenda VALUES(1,1)");
        db.execSQL("INSERT INTO uso_prenda VALUES(1,3)");
        db.execSQL("INSERT INTO uso_prenda VALUES(1,4)");
        db.execSQL("INSERT INTO uso_prenda VALUES(2,0)");
        db.execSQL("INSERT INTO uso_prenda VALUES(2,1)");
        db.execSQL("INSERT INTO uso_prenda VALUES(2,3)");
        db.execSQL("INSERT INTO uso_prenda VALUES(3,0)");
        db.execSQL("INSERT INTO uso_prenda VALUES(3,4)");
        db.execSQL("INSERT INTO uso_prenda VALUES(4,2)");
        db.execSQL("INSERT INTO uso_prenda VALUES(4,4)");
        db.execSQL("INSERT INTO uso_prenda VALUES(5,0)");
        db.execSQL("INSERT INTO uso_prenda VALUES(5,3)");
        db.execSQL("INSERT INTO uso_prenda VALUES(5,4)");

        db.execSQL("INSERT INTO uso_prenda VALUES(6,0)");
        db.execSQL("INSERT INTO uso_prenda VALUES(6,4)");
        db.execSQL("INSERT INTO uso_prenda VALUES(7,0)");
        db.execSQL("INSERT INTO uso_prenda VALUES(7,4)");
        db.execSQL("INSERT INTO uso_prenda VALUES(8,1)");
        db.execSQL("INSERT INTO uso_prenda VALUES(8,3)");
        db.execSQL("INSERT INTO uso_prenda VALUES(9,0)");
        db.execSQL("INSERT INTO uso_prenda VALUES(9,1)");
        db.execSQL("INSERT INTO uso_prenda VALUES(9,3)");
        db.execSQL("INSERT INTO uso_prenda VALUES(10,0)");
        db.execSQL("INSERT INTO uso_prenda VALUES(10,1)");
        db.execSQL("INSERT INTO uso_prenda VALUES(10,3)");
        db.execSQL("INSERT INTO uso_prenda VALUES(11,2)");
        db.execSQL("INSERT INTO uso_prenda VALUES(11,4)");

        db.execSQL("INSERT INTO uso_prenda VALUES(12,0)");
        db.execSQL("INSERT INTO uso_prenda VALUES(12,1)");
        db.execSQL("INSERT INTO uso_prenda VALUES(12,3)");
        db.execSQL("INSERT INTO uso_prenda VALUES(13,0)");
        db.execSQL("INSERT INTO uso_prenda VALUES(13,3)");
        db.execSQL("INSERT INTO uso_prenda VALUES(13,4)");
        db.execSQL("INSERT INTO uso_prenda VALUES(14,4)");
        db.execSQL("INSERT INTO uso_prenda VALUES(15,0)");
        db.execSQL("INSERT INTO uso_prenda VALUES(15,1)");
        db.execSQL("INSERT INTO uso_prenda VALUES(15,3)");
        db.execSQL("INSERT INTO uso_prenda VALUES(16,3)");
        db.execSQL("INSERT INTO uso_prenda VALUES(17,0)");
        db.execSQL("INSERT INTO uso_prenda VALUES(17,1)");
        db.execSQL("INSERT INTO uso_prenda VALUES(17,4)");

        db.execSQL("INSERT INTO uso_prenda VALUES(18,2)");
        db.execSQL("INSERT INTO uso_prenda VALUES(18,4)");
        db.execSQL("INSERT INTO uso_prenda VALUES(19,3)");
        db.execSQL("INSERT INTO uso_prenda VALUES(19,4)");
        db.execSQL("INSERT INTO uso_prenda VALUES(20,0)");
        db.execSQL("INSERT INTO uso_prenda VALUES(20,1)");
        db.execSQL("INSERT INTO uso_prenda VALUES(20,3)");
        db.execSQL("INSERT INTO uso_prenda VALUES(21,0)");
        db.execSQL("INSERT INTO uso_prenda VALUES(21,4)");
        db.execSQL("INSERT INTO uso_prenda VALUES(22,0)");
        db.execSQL("INSERT INTO uso_prenda VALUES(22,1)");
        db.execSQL("INSERT INTO uso_prenda VALUES(22,3)");


//        db.execSQL("INSERT INTO conjunto VALUES(0)");
//        db.execSQL("INSERT INTO parte_conjunto VALUES(0,0,4,0)");
//        db.execSQL("INSERT INTO parte_conjunto VALUES(1,1,8,0)");
//        db.execSQL("INSERT INTO parte_conjunto VALUES(2,2,9,0)");
//        db.execSQL("INSERT INTO parte_conjunto VALUES(3,3,7,0)");
//
//        db.execSQL("INSERT INTO conjunto VALUES(1)");
//        db.execSQL("INSERT INTO parte_conjunto VALUES(4,0,0,1)");
//        db.execSQL("INSERT INTO parte_conjunto VALUES(5,1,1,1)");
//        db.execSQL("INSERT INTO parte_conjunto VALUES(6,2,2,1)");
//        db.execSQL("INSERT INTO parte_conjunto VALUES(7,3,3,1)");
//
//        db.execSQL("INSERT INTO conjunto VALUES(2)");
//        db.execSQL("INSERT INTO parte_conjunto VALUES(8,1,8,2)");
//        db.execSQL("INSERT INTO parte_conjunto VALUES(9,2,2,2)");
//        db.execSQL("INSERT INTO parte_conjunto VALUES(10,3,3,2)");
//
//        db.execSQL("INSERT INTO conjunto VALUES(3)");
//        db.execSQL("INSERT INTO parte_conjunto VALUES(11,0,0,3)");
//        db.execSQL("INSERT INTO parte_conjunto VALUES(12,3,7,3)");
//
//        db.execSQL("INSERT INTO conjunto VALUES(4)");
//        db.execSQL("INSERT INTO parte_conjunto VALUES(13,1,8,4)");
//        db.execSQL("INSERT INTO parte_conjunto VALUES(14,2,2,4)");
//        db.execSQL("INSERT INTO parte_conjunto VALUES(15,3,3,4)");
//
//        db.execSQL("INSERT INTO conjunto VALUES(5)");
//        db.execSQL("INSERT INTO parte_conjunto VALUES(16,1,8,5)");
//        db.execSQL("INSERT INTO parte_conjunto VALUES(17,2,2,5)");
//        db.execSQL("INSERT INTO parte_conjunto VALUES(18,3,3,5)");
//
//        db.execSQL("INSERT INTO conjunto VALUES(6)");
//        db.execSQL("INSERT INTO parte_conjunto VALUES(19,1,8,6)");
//        db.execSQL("INSERT INTO parte_conjunto VALUES(20,2,2,6)");
//        db.execSQL("INSERT INTO parte_conjunto VALUES(21,3,3,6)");


    }
}
