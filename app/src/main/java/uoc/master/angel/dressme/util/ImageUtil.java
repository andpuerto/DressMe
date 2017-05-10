package uoc.master.angel.dressme.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by angel on 03/04/2017.
 */

public class ImageUtil {


    /**
     * Devuelve un array de bytes a partir de un File
     * @param file el archivo a transformar
     * @return byte[]
     */
    public static byte[] toByteArray(File file){
        //Obtenemos el tamaño del fichero
        int size = (int) file.length();
        //Creamos el array de ese tamaño
        byte[] bytes = new byte[size];
        try {
            //Vamos leyendo el contenido del fichero y pasandolo al array a traves de un InputStream
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            //Cerramos el flujo
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    return bytes;
    }


    /**
     * Devuelve un array de bytes a partir de un path
     * @param path el path del archivo
     * @return byte[]
     */
    public static byte[] toByteArray(String path){
        return ImageUtil.toByteArray(new File(path));
    }


    /**
     * Devuelve  un array de bytes a partir de un Bitmap
     * @param bmp el bitmap a transformar
     * @return byte[]
     */
    public static byte[] toByteArray(Bitmap bmp){
        if(bmp == null){
            return null;
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }



    /**
     * Devuelve un bitmap a partir de un array de bytes
     * @param bytearray el array de bytes a transformar
     * @return bitmap
     */
    public static Bitmap toBitmap(byte[] bytearray){
        if(bytearray == null){
            return null;
        }
        return BitmapFactory.decodeByteArray(bytearray, 0, bytearray.length);
    }

    /**
     * Obtiene un bitmap de la imagen cuya url se recibe (como string)
     * @param src La URL de la imagen
     * @return Bitmap con la imagen
     */
     static Bitmap urlToBitmap(String src){
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }


    /**
     * Obtiene un array de bytes a partir de un drawable
     * @param drawable El Drawable a convertir
     * @return byte[] con la imagen
     */
    public static byte[] drawableToByteArray(Drawable drawable){
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

}
