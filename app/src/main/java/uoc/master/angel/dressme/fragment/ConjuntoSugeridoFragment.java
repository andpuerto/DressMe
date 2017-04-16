package uoc.master.angel.dressme.fragment;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import uoc.master.angel.dressme.R;
import uoc.master.angel.dressme.util.WeatherUtil;

/**
 * Created by angel on 29/03/2017.
 */

public class ConjuntoSugeridoFragment extends Fragment{


    //Localizacion mas reciente
    private Location mLastLocation;

    //Para la localizacion
    private LocationManager locationManager;

    //Dialogo a mostrar mientras se consulta el tiempo
    private ProgressDialog pDialog;

    //Informacion meteorologica
    private WeatherUtil.WeatherInfo wi = null;

    @Override

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //Creamos un LocationManager con un listener para obtener la localización actual
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                //Actualizamos la localizacion
                mLastLocation = location;
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        //Registramos las actualizaciones tanto a traves de la localizacion de la red, como
        //del GPS
        //Dado que no se necesita que la localizacion sea demasiado precisa, puede obtenerse solamente
        //de la red, con lo que podria quitarse la segunda linea
        //En las pruebas durante el desarrollo, en el emulador se puede simular el GPS pero no la
        //localizacion por red, de ahi que registremos en principio ambas.
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }catch(SecurityException e){
            Toast.makeText(getContext(), getString(R.string.error_location_permission),
                    Toast.LENGTH_SHORT).show();
        }

        new GetWeather().execute();

    }



    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,

                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.conjunto_sugerido, container, false);

    }




    private void mostrarConjuntoSugerido(){

    }


    /**
     * Tarea asincrona para obtener la prediccion meteorologica
     * Dado que requiere contectarse a un servicio a traves de Internet, debe ser asincrona
     */
    private class GetWeather extends AsyncTask<Void, Void, Void> {

        //Antes de la ejecucion, mostramos un dialogo indicando que la tarea esta en curso
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage(getString(R.string.weather_progress));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        /**
         * Tarea a ejecutar
         */
        @Override
            protected Void doInBackground(Void... arg0) {

            //En caso de que no se disponga de la ultima localizacion, intentamos obtenerla
            //Igual que en el listener, se hace desde los dos proveedores por comodidad durante
            //el desarrollo. Podria dejarse solamente la localizacion por red.
            if(mLastLocation == null) {
                try {
                    mLastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    mLastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }catch (SecurityException e){
                    Log.e("SecurityExeption", e.getMessage());
                }
            }
            if(mLastLocation != null) {
                //Si tenemos la localizacion, obtenemos la informacion meteorologica
                wi = WeatherUtil.getWeather(mLastLocation);
                //El resto lo haremos en onPostExecute
            }else{
                //error: no se ha podido obtener la localizacion
                Toast.makeText(getContext(), getString(R.string.error_location_connection),
                        Toast.LENGTH_SHORT).show();
            }

            return null;
        }


        /**
         * Tras la ejecucion de la tarea
         */
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //Quitamos el cuadro de dialogo del progreso
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }

            //Utilizamos la informacion obtenida
            if(wi != null){
                //Si la informacion meteorologica se obtiene correctamente, buscaremos
                //las prendas para el conjunto apropiadas y estableceremos los valores de la vista
                Log.i("doInbackground","Obtenido el json");
            } else {
                //error: no se ha podido consultar la informacion meteorologica
                Toast.makeText(getContext(), getString(R.string.error_weather),
                        Toast.LENGTH_SHORT).show();
            }


        }

    }
}



