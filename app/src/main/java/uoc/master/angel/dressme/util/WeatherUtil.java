package uoc.master.angel.dressme.util;


import android.location.Location;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Arrays;

/**
 * Created by angel on 15/04/2017.
 */

public class WeatherUtil {

    //Tag con el nombre de la clase para los catch
    private final static String TAG = JSONObject.class.getSimpleName();

    //Lista ORDENADA de codigos de lluvia.
    private final static int[] rainCodes = {200,201,202,230,231,232,300,301,302,310,311,312,313,314,
    321,500,501,502,503,504,511,520,521,522,531,600,601,602,611,612,615,616,620,621,622,901,906};

    /**
     * Obtiene el tiempo para la localizacion que recibe como parametro
     * @param location La localizacion de la que deseamos saber el tiempo
     * @return objeto WeatherInfo con la informacion simplificada del tiempo. null en caso de error.
     */
    public static WeatherInfo getWeather(Location location){
        //URL del servicio de prediccion del tiempo
        //El parametro appid es necesario para identificar al usuario del servicio. Se obtiene
        //al darse de alta. Por ultimo, units=metric hace que las temperaturas se obtengan en grados
        //centigrados en lugar de kelvin, que es la opcion por defecto.
        String reqUrl = "http://api.openweathermap.org/data/2.5/weather?lat="+location.getLatitude()
                +"&lon="+location.getLongitude()+"&appid=64e41d291ae363c2154323d1253132da&units=metric";
        //Respuesta en forma de string
        String response = null;

        //JSON para almacenar la respuesta
        JSONObject jsonObject = null;
        try {
            //Obtenemos el objeto URL a partir del string
            URL url = new URL(reqUrl);
            //Abrimos la conexion, con el metodo get
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            //Leemos la respuesta y la pasamos al string
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        try {
            //Creamos el json a partir del string
            jsonObject = new JSONObject(response);
        }catch (JSONException e){
            Log.e(TAG, "JSONException: " + e.getMessage());
        }

        //Obtenemos el objeto WeatherInfo a partir del json
        WeatherInfo returnValue = parseWeatherInfo(jsonObject);
        //Comprobamos si el valor de la temperatura no es valido
        //En tal caso, devolvemos un null, supondra menos acoplamiento al tratarlo en clases
        //externas
        if(returnValue == null || returnValue.temp < -100){
            return null;
        }
        return returnValue;
    }

    /**
     * Utiliza un InputStream para obtener un String del contenido
     * @param is InputStream de la fuente
     * @return String con el contenido obtenido
     */
    private static String convertStreamToString(InputStream is) {
        //Creamos un BufferedReader a partir del InputStream
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        //Creamos un String builder
        StringBuilder sb = new StringBuilder();
        //Cada linea que leamos del BufferedReader
        String line;
        try {
            //Vamos leyendo ineas del BufferedReader las agregamos a la cadena
            //seguidas de un salto de linea
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //Pasamos el StringBuilder a string y lo devolvemos
        return sb.toString();
    }


    /**
     * Procesa el json recibido y devuelve la informacion relevante simplificada
     * @param jWeatherInfo El objeto json a procesar
     * @return WeatherInfo
     */
    private static WeatherInfo parseWeatherInfo(JSONObject jWeatherInfo){
        //Creamos el objeto a devolver
        WeatherInfo wi = new WeatherUtil().new WeatherInfo();

        try {
            //Para la informacion de lluvia vamos a utilizar el codigo que proporciona el servicio
            //El servicio puede devolver varios identificadores de condiciones atmosfericas
            //Recorremos el array y si alguno es identificativo de lluvia, establecemos la lluvia
            //a verdadero
            JSONArray weatherArray = jWeatherInfo.getJSONArray("weather");
            for(int i=0; i<weatherArray.length(); i++){
                int weatherCode = weatherArray.getJSONObject(i).getInt("id");
                //Con el numero de identificador, sabremos si hay lluvia o no
                //en la documentación de la API, se pueden consultar los valores de codigo
                //que implican lluvia: https://openweathermap.org/weather-conditions
                if(Arrays.binarySearch(rainCodes, weatherCode) >= 0){
                    //Si alguno de los codigos recibidos esta en la lista de los que corresponden
                    //a lluvia, establecemos la propiedad y terminamos el bucle
                    wi.lluvia = true;
                    break;
                }
            }
            //Ahora tomamos la temperatura, en la misma unidad en la que se haya obtenido
            wi.temp = Float.parseFloat(jWeatherInfo.getJSONObject("main").getString("temp"));

        }catch(JSONException e){
            Log.e(TAG, "JSONException: " + e.getMessage());
        }


        return wi;
    }

    /**
     * Clase interna con informacion resumida del tiempo
     */
    public class WeatherInfo{
        public WeatherInfo(){
            lluvia = false;
            //Establecemos la temperatura a un valor no valido para
            //despues poder saber si se el objeto se ha configurado correctamente
            temp = -101;
        }

        public boolean lluvia;
        public float temp;
    }

}
