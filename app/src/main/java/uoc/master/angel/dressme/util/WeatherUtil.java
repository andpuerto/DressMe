package uoc.master.angel.dressme.util;


import android.location.Location;
import android.util.Log;

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

/**
 * Created by angel on 15/04/2017.
 */

public class WeatherUtil {

    /**
     * Obtiene el tiempo para la localizacion que recibe como parametro
     * @param location La localizacion de la que deseamos saber el tiempo
     */
    public static JSONObject getWeather(Location location){
        //URL del servicio de prediccion del tiempo
        //El parametro appid es necesario para identificar al usuario del servicio. Se obtiene
        //al darse de alta en el
        String reqUrl = "http://api.openweathermap.org/data/2.5/weather?lat="+location.getLatitude()
                +"&lon="+location.getLongitude()+"&appid=64e41d291ae363c2154323d1253132da";
        String response = null;
        String TAG = JSONObject.class.getSimpleName();
        JSONObject jsonObject = null;
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            //Leemos la respuesta
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
            jsonObject = new JSONObject(response);
        }catch (JSONException e){
            Log.e(TAG, "JSONException: " + e.getMessage());
        }

        return jsonObject;
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
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
        return sb.toString();
    }

}
