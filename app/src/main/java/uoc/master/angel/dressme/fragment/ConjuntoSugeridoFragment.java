package uoc.master.angel.dressme.fragment;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import uoc.master.angel.dressme.R;
import uoc.master.angel.dressme.db.ColorPrendaDA;
import uoc.master.angel.dressme.db.ConjuntoDA;
import uoc.master.angel.dressme.db.PrendaDA;
import uoc.master.angel.dressme.db.TipoParteConjuntoDA;
import uoc.master.angel.dressme.db.UsoDA;
import uoc.master.angel.dressme.modelo.ColorPrenda;
import uoc.master.angel.dressme.modelo.Conjunto;
import uoc.master.angel.dressme.modelo.ParteConjunto;
import uoc.master.angel.dressme.modelo.Prenda;
import uoc.master.angel.dressme.modelo.TipoParteConjunto;
import uoc.master.angel.dressme.modelo.Uso;
import uoc.master.angel.dressme.util.ImageUtil;
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

    //Array con los usos y array con los nombres de los usos para el Spinner
    private List<Uso> usos = new ArrayList<>();
    private String[] usosStrings;

    //Uso seleccionado por el usuario
    private Uso usoSeleccionado=null;

    //Spinner para seleccionar los usos adecuados para el conjunto sugerido
    private Spinner usoSpinner;

    //Boton para volver a generar el conjunto
    private Button regenerarButton;

    //El conjunto sugerido
    Conjunto conjuntoSugerido = null;


    //Lista con las vistas de imagenes
    List<ImageView> imageViews = new ArrayList<>();

    @Override

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //Obtenemos la lista de usos
        usos = new UsoDA(getContext()).getAllUso();
        //Añadimos un uso con un valor null para hacer referencia a todos los usos
        usos.add(0, null);
        //Creamos la lista de las strings de los usos
        usosStrings = new String[usos.size()];
        //Insertamos un primer elemento en el array con la opcion por defectop
        usosStrings[0] = getString(R.string.usos_spinner_default);
        //Insertamos el resto de elementos en el array
        for(int i=1; i<usos.size(); i++){
            usosStrings[i] = usos.get(i).getNombre();
        }


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



    }



    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,

                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.conjunto_sugerido, container, false);

    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        //Obtenemos las referencias a las ImageView
        //Aunque usamos una lista de ImageView para que sea mas generico, en esta parte estamos
        //cogiendolos estaticamente. Seria lo que habria que cambiar para hacerlo totalmente
        //generico
        imageViews.clear();
        imageViews.add((ImageView) getView().findViewById(R.id.prenda_sug1));
        imageViews.add((ImageView) getView().findViewById(R.id.prenda_sug2));
        imageViews.add((ImageView) getView().findViewById(R.id.prenda_sug3));
        imageViews.add((ImageView) getView().findViewById(R.id.prenda_sug4));

        //Establecemos las etiquetas para los nombres de partes de conjuntos
        List<TipoParteConjunto> tpcs= new TipoParteConjuntoDA(getContext()).getAllTipoParteConjunto();
        if(tpcs.size() >= 4){
            ((TextView)getView().findViewById(R.id.parte_sug_text1)).setText(tpcs.get(0).getNombre());
            ((TextView)getView().findViewById(R.id.parte_sug_text2)).setText(tpcs.get(1).getNombre());
            ((TextView)getView().findViewById(R.id.parte_sug_text3)).setText(tpcs.get(2).getNombre());
            ((TextView)getView().findViewById(R.id.parte_sug_text4)).setText(tpcs.get(3).getNombre());
        }

        //Establecemos el spinner y el boton
        usoSpinner = (Spinner)getView().findViewById(R.id.uso_sug_spinner);
        regenerarButton = (Button)getView().findViewById(R.id.regenerar_sug_button);

        //Adapter y listener para el spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, usosStrings);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        usoSpinner.setAdapter(spinnerAdapter);
        usoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                usoSeleccionado = usos.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Listener para el boton de regenerar
        regenerarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetWeather().execute();
            }
        });

        //Listener para el boton de guardar el conjunto sugerido
        FloatingActionButton botonGuardar = (FloatingActionButton)getView().
                findViewById(R.id.save_conjunto_sug_button);
        botonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Antes de nada, comprobamos que haya al menos una prenda asignada para poder guardar
                if (conjuntoSugerido==null || conjuntoSugerido.getPartesConjunto().isEmpty()) {
                    Toast.makeText(getContext(), getString(R.string.no_prenda_error), Toast.LENGTH_LONG).show();
                    return;
                }
                //Guardamos el conjunto. Al guardar un conjunto sugerido, siempre queremos que se
                //inserte. Para ello, ponemos su identificador a -1
                conjuntoSugerido.setId(-1);
                new ConjuntoDA(getContext()).saveConjunto(conjuntoSugerido);
                //Notificamos que se ha guardado correctamente
                Toast.makeText(getContext(), getString(R.string.conjunto_guardado),
                        Toast.LENGTH_SHORT).show();
            }
        });

        //Solo obtenemos el conjunto obtenemos un nuevo conjunto al crear la vista si no hay ninguno
        //Esto evita que al cambiar de pestaña y volver se cree otro conjunto
        if(conjuntoSugerido == null) {
            new GetWeather().execute();
        }else{
            //Si el conjunto existe, establecemos las imagenes directamente
            setImages();
        }
    }

    /**
     * Obtiene un conjunto sugerido considerando los parametros climaticos, el uso sugerido por
     * el usuario y las combinaciones esteticamente adecuadas de colores de prendas
     * @return El conjunto sugerido con id=-1 por no estar registrado en la BD.
     */
    private Conjunto getConjuntoSugerido(){

        //El algoritmo hace lo siguiente:
        //  - Para el primer TipoParteConjunto toma una prenda de las obtenidas aleatoriamente
        //  - Para cada TipoParteConjunto, obtiene la lista de prendas que cumplen los requisitos
        //    de uso (seleccionado por el usuario) y clima (obtenido de la prediccion meteorologica)
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

        //Objetos de acceso a datos que se van a necesitar
        PrendaDA prendaDA = new PrendaDA(getContext());
        ColorPrendaDA colorPrendaDA = new ColorPrendaDA(getContext());
        TipoParteConjuntoDA tpcDA = new TipoParteConjuntoDA(getContext());

        //Antes de tratar las prendas, obtenemos la lista de colores y sus combinaciones
        //Para poder utilizarla despues comodamente, aprovechamos el bucle en el que
        //obtenemos los colores combinados para meterlos en un sparse array
        List<ColorPrenda> colores = colorPrendaDA.getAllColorPrenda();
        HashMap<Integer,ColorPrenda> coloresHash = new HashMap<>();
        for(ColorPrenda color : colores){
            color.setColoresCombinados(colorPrendaDA.getColoresCombinados(color));
            coloresHash.put(color.getId(), color);
        }


        //Obtenemos la lista de TipoParteConjunto
        List<TipoParteConjunto> tpcsInicial = tpcDA.getAllTipoParteConjunto();

        //Para generar numeros aleatorios
        Random random = new Random();

        //Para ser equitativo, se va a partir de un tpc aleatorio y en cada iteracion se cogera
        //otro aleatorio. Esto es especialmente interesante en los casos en los que no se puede
        //encontrar prendas adecuadas para todos los TPC. Si no lo hacemos aleatorio, los conjuntos
        //con el mismo numero de prendas siempre darian preferencia a los primeros TPC de la lista
        //Introducimos los TPCS en una nueva lista en orden aleatorio
        List<TipoParteConjunto> tpcs = new ArrayList<>();
        while(!tpcsInicial.isEmpty()){
            tpcs.add(tpcsInicial.remove(random.nextInt(tpcsInicial.size())));
        }

        List<List<Prenda>> listasPrendas= new ArrayList<>();
        //Recorremos la lista de TipoParteConjunto y para cada uno, hallamos la lista de posibles prendas
        //para ese tipo, el uso seleccionado (si hay alguno) y el clima actual
        //La lista tpc y la lista prendas deben tener los elementos relacionados en la misma posicion
        for(TipoParteConjunto tpc : tpcs){
            //Dependiendo de si la informacion del tiempo es nula o no, se consulta usandola o no.
            //En caso de no haber informacion del tiempo, por tanto, se usaran todas las prendas
            //independientemente de su validez climatologica
            List<Prenda> prendas = wi!=null ?
                    prendaDA.getPrendas(usoSeleccionado, tpc, wi.temp, wi.lluvia) :
                    prendaDA.getPrendas(usoSeleccionado,tpc);
            listasPrendas.add(prendas);
        }
        //Conjunto que vamos a generar
        Conjunto conjuntoGenerado = new Conjunto(-1, null);
        //Referencia al TipoParteConjunto que se trata en cada momento
        int tpcActual=0;
        //Vamos recorriendo todos los TipoParteConjunto en busca de una prenda de inicio
        //a partir de la que intentaremos empezar a conjuntar. Pararemos cuando lleguemos al
        //final de la lista de prendas para cada TipoParteConjunto o cuando hayamos encontrado
        //un con conjunto con todas las prendas posibles
        while(tpcActual < listasPrendas.size() &&
                conjuntoGenerado.getNumeroPartesConjunto() < tpcs.size()){
            //Cogemos la lista correspondiente al uso
            //La copiamos para poder manipularla sin tocar la original
            List<Prenda> prendasRef = new ArrayList<>(listasPrendas.get(tpcActual));
            //Trataremos esta lista mientras queden elementos (los iremos sacando si los
            //descartamos)
            while(!prendasRef.isEmpty() &&
                    conjuntoGenerado.getNumeroPartesConjunto() < tpcs.size()){
                //Cogemos una prenda de las restantes aleatoriamente
                Prenda prendaBase = prendasRef.get(random.nextInt(prendasRef.size()));
                //Creamos un conjunto temporal y le asignamos una parteconjunto del tipo actual
                //y con la prenda actual asignada
                Conjunto conjuntoTemp = new Conjunto();
                //Añadimos a la lista de partes conjunto, usando el id del tipo como clave
                conjuntoTemp.getPartesConjunto().put(tpcs.get(tpcActual).getId(),
                        new ParteConjunto(-1,tpcs.get(tpcActual), prendaBase));
                //Buscamos prendas para otras partes del conjunto
                for(int i=0; i<listasPrendas.size(); i++){
                    //Buscaremos solo para los TipoParteConjunto que no sean el que estamos tratando
                    if(i != tpcActual){
                        //Para cada elemento de la lista de este TipoParteConjunto, comprobaremos
                        //si su color combina con el de la prenda base. Si es asi, los añadimos
                        //al conjunto.
                        //Hacemos una copia de la lista actual para ir cogiedo aleatoriamente y
                        //eliminarlos de la lista
                        List<Prenda> prendasTemp = new ArrayList<>(listasPrendas.get(i));
                        while(!prendasTemp.isEmpty()){
                            //Tomamos la prenda aleatoriamente de la lista actual
                            Prenda prendaTemp = prendasTemp.get(random.nextInt(prendasTemp.size()));
                            //Insertamos la prenda si combina con las ya asignadas
                            if(conjuntoTemp.insertPrendaSiCombina(prendaTemp, tpcs.get(i),coloresHash)){
                                //Si se ha insertado, terminamos este bucle para evitar
                                //iteraciones innecesarias
                                break;
                            }
                            //Eliminamos la prenda de la lista temporal para que no se tenga en
                            //cuenta en la proxima iteracion
                            prendasTemp.remove(prendaTemp);
                        }
                    }
                }

                //Si el conjunto temporal tiene mas prendas asignadas que el conjunto definitivo,
                //el temporal pasa a ser el definitivo
                if(conjuntoTemp.getNumeroPartesConjunto() >
                        conjuntoGenerado.getNumeroPartesConjunto()){
                    conjuntoGenerado = conjuntoTemp;
                }

                //Eliminamos la prenda de la lista de prendas de referencia para que no se
                //tenga en cuenta en la proxima iteracion
                prendasRef.remove(prendaBase);
            }
            tpcActual++;
        }

        //Devolvemos el conjunto generado. Si no se hubiera conseguido ni una prenda,
        //la lista de partesConjuto estara vacia
        return conjuntoGenerado;

    }

    private void setImages(){
        if(conjuntoSugerido == null){
            return;
        }
        PrendaDA prendaDA = new PrendaDA(getContext());
        for(int i=0; i<imageViews.size(); i++){
            ImageView currentImageView = imageViews.get(i);
            ParteConjunto pc = conjuntoSugerido.getPartesConjunto().get(i);
            if(currentImageView!=null){
                //Obtenemos los datos de la prenda, especialmente la imagen
                //Se obtienen aqui y no antes para que solo se recupere la imagen de las prendas
                //que se van a visualizar realmente
                Bitmap bitmap = pc==null ? null : ImageUtil.toBitmap(prendaDA.getPrenda(
                        pc.getPrendaAsignada().getId()).getFoto());
                //Si obtenemos el bitmap, establecemos la imagen con el.
                //Si la imagen esta vacia establecemos una imagen de un rectangulo transparente
                //Es necesario para que el layout de las imagenes quede bien
                if(bitmap!=null) {
                    currentImageView.setImageBitmap(bitmap);
                }else{
                    currentImageView.setImageResource(R.drawable.smalltransrect);
                }

            }
        }

    }

    /**
     * Tarea asincrona para obtener la prediccion meteorologica
     * Dado que requiere contectarse a un servicio a traves de Internet, debe ser asincrona
     */
    private class GetWeather extends AsyncTask<Void, Void, Integer> {

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
         * @return 0, si se ejecuta correctamente. El id del mensaje a mostrar si hay error
         */
        @Override
            protected Integer doInBackground(Void... arg0) {
            Integer returnValue = 0;
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


                //****** DEPURACION: DESCOMENTAR LO QUE ESTA COMENTADO Y VICEVERSA *************
                //wi = WeatherUtil.getWeather(mLastLocation);
                wi = new WeatherUtil().new WeatherInfo();
                wi.temp=15.35f;
                wi.lluvia=false;
                //*******************************************************************************

                //Establecemos el valor de retorno segun si se ha podido obtener el tiempo  o no
                if(wi == null){
                    returnValue = R.string.error_weather;
                }

                //Una vez obtenida la informacion meteorologica, obtenemos el conjunto sugerido
                //Dado que el algoritmo puede tardar, aprovechamos que se esta mostrando el
                //dialogo de progreso para hacerlo aqui
                //Utilizamos la informacion obtenida
                conjuntoSugerido = getConjuntoSugerido();
                //El resto lo haremos en onPostExecute

            }else{
                //error: no se ha podido obtener la localizacion
                 returnValue = R.string.error_location_connection;
            }

            return returnValue;
        }


        /**
         * Tras la ejecucion de la tarea
         */
        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            //Quitamos el cuadro de dialogo del progreso
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if(result != 0){
                Toast.makeText(getContext(), getString(result),
                        Toast.LENGTH_SHORT).show();
            }else{
                //Si se ha obtenido el conjunto sugerido, establecemos los valores en la vista
                setImages();
            }


        }

    }
}



