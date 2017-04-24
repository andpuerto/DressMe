package uoc.master.angel.dressme.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * Created by angel on 03/04/2017.
 */

public class ImageUtil {


    /**
     * Devuelve un array de bytes a partir de un File
     * @param file
     * @return
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
     * @param path
     * @return
     */
    public static byte[] toByteArray(String path){
        return ImageUtil.toByteArray(new File(path));
    }


    /**
     * Devuelve  un array de bytes a partir de un Bitmap
     * @param bmp
     * @return
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
     * @param bytearray
     * @return
     */
    public static Bitmap toBitmap(byte[] bytearray){
        if(bytearray == null){
            return null;
        }
        return BitmapFactory.decodeByteArray(bytearray, 0, bytearray.length);
    }

}
