package uoc.master.angel.dressme.util;


import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;



/**
 * Created by angel on 30/04/2017.
 */


public class DateUtil {

    //Formato para las fechas
    private static DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    /**
     * Devuelve un Date a partir de una cadena con formato dd/mm/aaaa
     * @param dateString Cadena con formato dd/mm/aaaa
     * @return Date
     */
    public static Date stringToDate(String dateString){
        if(dateString == null){
            return null;
        }

        try {
            return format.parse(dateString);
        }catch(ParseException e){
            Log.e("DateUtil.stringToDate", e.getMessage());
            return null;
        }
    }


    /**
     * Devuelve un String con el formato dd/mm/aaaa a partir de un Date
     * @param date Date
     * @return Cadena con formato dd/mm/aaaa
     */
    public static String dateToString(Date date){
        if(date == null){
            return null;
        }
        return format.format(date);
    }

    /**
     * Devuelve una cadena para presentar al usuario a partir de un Date
     * @param date el objeto con la fecha
     * @return String con la fecha en formato presentable
     */
    public static String dateToPresentableString(Date date){
        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        return df.format(date);
    }
}
