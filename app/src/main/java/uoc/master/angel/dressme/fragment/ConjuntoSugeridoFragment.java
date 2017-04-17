package uoc.master.angel.dressme.fragment;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
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


import java.util.ArrayList;
import java.util.List;

import uoc.master.angel.dressme.R;
import uoc.master.angel.dressme.db.ColorPrendaDA;
import uoc.master.angel.dressme.db.PrendaDA;
import uoc.master.angel.dressme.db.UsoDA;
import uoc.master.angel.dressme.modelo.ColorPrenda;
import uoc.master.angel.dressme.modelo.Prenda;
import uoc.master.angel.dressme.modelo.Uso;
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

    private List<Uso> usos = new ArrayList<>();

    @Override

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //Obtenemos la lista de usos
        usos = new UsoDA(getContext()).getAllUso();

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
        //Obtenemos las prendas del conjunto.
        //El algoritmo hace lo siguiente:
        //  - Para cada TipoParteConjunto, obtiene la lista de prendas que cumplen los requisitos
        //    de uso (seleccionado por el usuario) y clima (obtenido de la prediccion meteorologica)
        PrendaDA prendaDA = new PrendaDA(getContext());
        ColorPrendaDA colorPrendaDA = new ColorPrendaDA(getContext());

        //Antes de tratar las prendas, guardamos la lista de colores y sus combinaciones
        List<ColorPrenda> colores = colorPrendaDA.getAllColorPrenda();
        for(ColorPrenda color : colores){
            color.setColoresCombinados(colorPrendaDA.getColoresCombinados(color));
        }

        List<List<Prenda>> listasPrendas= new ArrayList<>();
        //Recorremos la lista de usos y para cada uno, hallamos la lista de posibles prendas
        //para ese uso y el clima actual
        for(Uso uso : usos){
            List<Prenda> prendas = prendaDA.getPrendas(uso, wi.temp, wi.lluvia);
            listasPrendas.add(prendas);
        }

        //  - Para el primer TipoParteConjunto toma una prenda de las obtenidas aleatoriamente
        //  - En el resto de TipoParteConjunto, selecciona prendas cuyo color combine con el de la
        //    primera prenda seleccionada
        //  - Si se encuentran prendas para todas las partes, el algoritmo termina
        //  - Si queda alguna parte sin prenda asignada, se guarda el conjunto, junto con el numero
        //    de prendas que se han conseguido asignar.
        //  - Se selecciona otra prenda de la parte superior, aleatoria entre las restantes y se
        //    repite el proceso.
        //  - Si se asignan todas las prendas, se termina. Si el numero de prendas asignadas es
        //    mayor que el del conjunto almacenado, se sustituye el conjunto almacenado por este
        //    y se continua.
        //  - Si no se consiguen asignar todas las predas menos una tomando elementos del primer
        //    TipoParteConjunto, empezamos a hacer lo mismo pero tomando como referencia el
        //    siguiente TipoParteConjunto. A partir de aquí es seguro que no vamos a poder asignar
        //    todas las partes puesto que no lo hemos conseguido con ninguna prenda del primer
        //    TipoParteConjunto, como mucho, conseguiriamos n-1.



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
                mostrarConjuntoSugerido();
            } else {
                //error: no se ha podido consultar la informacion meteorologica
                Toast.makeText(getContext(), getString(R.string.error_weather),
                        Toast.LENGTH_SHORT).show();
            }


        }

    }
}



