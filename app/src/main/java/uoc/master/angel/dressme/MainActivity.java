package uoc.master.angel.dressme;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;


import java.util.HashMap;
import java.util.Map;

import uoc.master.angel.dressme.db.DressMeSQLHelper;
import uoc.master.angel.dressme.fragment.ConjuntosListFragment;
import uoc.master.angel.dressme.fragment.PlanificacionFragment;
import uoc.master.angel.dressme.fragment.PrendasListFragment;
import uoc.master.angel.dressme.fragment.container.BaseContainerFragment;
import uoc.master.angel.dressme.fragment.container.ConjuntoContainerFragment;
import uoc.master.angel.dressme.fragment.container.ConjuntoSugeridoContainerFragment;
import uoc.master.angel.dressme.fragment.container.PlanificacionContainerFragment;
import uoc.master.angel.dressme.fragment.container.PrendaContainerFragment;
import uoc.master.angel.dressme.util.PermissionsUtil;

public class MainActivity extends AppCompatActivity {

    //Vista de pestañas
    private FragmentTabHost tabHost;
    //Permiso de acceso al almacenamiento externo
    private boolean externalStoragePermission = false;

//    //Constante para identificar el permiso de almacenamiento externo
//    private final int EXTERNAL_STORAGE_PERMISSION = 1;
//    //Constante para identificar el permiso de utilizar la camara
//    private final int CAMERA_PERMISSION = 2;

    private final int PERMISSION_ALL = 1;
    //Etiquetas identificadoras para cada pestaña
    private final String CONJUNTO_SUG_TAB = "conjuntoSugTab";
    private final String CONJUNTOS_TAB = "conjuntosTab";
    private final String PRENDAS_TAB = "prendasTab";
    private final String PLANIFICACION_TAB = "planificacionTab";

    //Hashmap para guardar las pestañas. y volver a ponerlas. Al guardarlas evitamos
    //tener que volver a crearlas
    private Map<String, TabHost.TabSpec> tabMap = new HashMap<>();

    private boolean showGenerador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Pedimos los permisos necesarios al usuario
        this.askForPermission();

//
//        //Inicializamos las pestañas
       this.initializeTabs();
//
//        //Inicializamos la base de datos
       this.setDatabase();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //Establecemos el valor para la opcion de mostrar el generador de conjuntos
        menu.findItem(R.id.sug_enable_menu).setChecked(showGenerador);
        //Ocultamos la opcion de navegacion al generador de conjuntos si esta desactivada
        menu.findItem(R.id.conjunto_sug_menu).setVisible(showGenerador);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.conjunto_sug_menu:
                tabHost.setCurrentTabByTag(CONJUNTO_SUG_TAB);
                return true;
            case R.id.prendas_menu:
                tabHost.setCurrentTabByTag(PRENDAS_TAB);
                return true;
            case R.id.conjuntos_menu:
                tabHost.setCurrentTabByTag(CONJUNTOS_TAB);
                return true;
            case R.id.planificacion_menu:
                tabHost.setCurrentTabByTag(PLANIFICACION_TAB);
                return true;
            //Caso de la activacion/desactivacion de la pestaña del generador de conjuntos
            case R.id.sug_enable_menu:
                //Cambiamos el valor del check
                item.setChecked(!item.isChecked());
                //Establecemos el valor del boolean
                showGenerador = item.isChecked();
                //Lo guardamos en las preferencias
                SharedPreferences sp = getSharedPreferences(getString(R.string.preferences_file), Activity.MODE_PRIVATE);
                sp.edit().putBoolean(getString(R.string.show_gen_pref), showGenerador).apply();
                //invalidamos el menu para que vuelva a crearse, sin la opcion del menu del generador
                invalidateOptionsMenu();
                //Cambiamos las pestañas
                toggleGeneradorTab();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Realiza las funciones de configuracion inicial de las pestañas
     */
    private void initializeTabs() {

        //Obtenemos el valor guardado de las preferencias
        SharedPreferences sp = getSharedPreferences(getString(R.string.preferences_file), Activity.MODE_PRIVATE);
        showGenerador = sp.getBoolean(getString(R.string.show_gen_pref), true);

        tabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        tabHost.setup(this,
                getSupportFragmentManager(), android.R.id.tabcontent);
        //Mostramos el generador de conjuntos solo si esta establecido en las preferencias
        if (showGenerador) {

            tabHost.addTab(tabHost.newTabSpec(CONJUNTO_SUG_TAB).setIndicator("", getResources().getDrawable(R.drawable.magic_m)),
                    ConjuntoSugeridoContainerFragment.class, null);
        }
        //Añadimos las pestañas al map.  Al agregar y quitar la vista del generador de conjuntos,
        //no queda mas remedio que quitar todas y volver a poner las que necesitemos
        //La del generador no la guardamos porque, en caso de no ponerla no queremos que consuma
        //recursos
//        TabHost.TabSpec tabSpec = tabHost.newTabSpec(PRENDAS_TAB).
//                setIndicator(getString(R.string.prendas_tab_label));
        TabHost.TabSpec tabSpec = tabHost.newTabSpec(PRENDAS_TAB).
                setIndicator("", getResources().getDrawable(R.drawable.tshirtonhang_m));
        tabMap.put(PRENDAS_TAB, tabSpec);
        tabHost.addTab(tabSpec, PrendaContainerFragment.class, null);

        tabSpec = tabHost.newTabSpec(CONJUNTOS_TAB).
                setIndicator("", getResources().getDrawable(R.drawable.outfit_m));
        tabMap.put(CONJUNTOS_TAB, tabSpec);
        tabHost.addTab(tabSpec, ConjuntoContainerFragment.class, null);

        tabSpec = tabHost.newTabSpec(PLANIFICACION_TAB).
                setIndicator("", getResources().getDrawable(R.drawable.calendar_m));
        tabMap.put(PLANIFICACION_TAB, tabSpec);
        tabHost.addTab(tabSpec, PlanificacionContainerFragment.class, null);



        resizeTabIcons();

    }

    private void resizeTabIcons(){
        float density = getResources().getDisplayMetrics().density;
        int padding = (int) (5f * density);
        int imageSize = (int) (36 * density);

        TabWidget tabs = tabHost.getTabWidget();
        for (int i = 0; i < tabs.getChildCount(); i++) {
            LinearLayout tab = (LinearLayout) tabs.getChildAt(i);

            AppCompatTextView tv = (AppCompatTextView) tab.getChildAt(1);
            AppCompatImageView iv = (AppCompatImageView) tab.getChildAt(0);

            android.widget.LinearLayout.LayoutParams lp = new android.widget.LinearLayout.LayoutParams(imageSize, imageSize);
            iv.setLayoutParams(lp);
            iv.setPadding(padding, padding, padding, padding);
        }
    }


    public TabHost.TabSpec setIndicator(TabHost.TabSpec spec, int resId) {
        View v = LayoutInflater.from(this).inflate(R.layout.tabhost_row, null);
        ImageView imgTab = (ImageView) v.findViewById(R.id.imgTab);
        imgTab.setImageDrawable(getResources().getDrawable(resId));
        return spec.setIndicator(v);
    }

    /**
     * Adapter para la vista de pestañas
     */
    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return new ConjuntosListFragment();
                case 1:
                    return new PrendasListFragment();
                default:
                    return new PlanificacionFragment();

            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Conjuntos";
                case 1:
                    return "Prendas";
                default:
                    return "Planificar";

            }
        }
    }


    /**
     * Metodo para encapsular la configuracion relativa a la inicializacion de la base de datos
     */
    private void setDatabase() {
        DressMeSQLHelper dmdbh = new DressMeSQLHelper(this, "DBDressMe", null, 1);
        SQLiteDatabase db = dmdbh.getWritableDatabase();


    }


    private void askForPermission() {
        //Si no tenemos permisos, los pedimos

        // The request code used in ActivityCompat.requestPermissions()
        // and returned in the Activity's onRequestPermissionsResult()

        String[] permisos = {Manifest.permission.INTERNET, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA};

        if (!PermissionsUtil.hasPermissions(this, permisos)) {
            ActivityCompat.requestPermissions(this, permisos, PERMISSION_ALL);
        }


//        if (ContextCompat.checkSelfPermission(this,
//                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//
//
//            ActivityCompat.requestPermissions(this,
//                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                            android.Manifest.permission.READ_EXTERNAL_STORAGE},EXTERNAL_STORAGE_PERMISSION);
//
//
//        }else{
//            //Si los tenemos, establecemos la variable a verdadero
//            externalStoragePermission = true;
//        }
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.CAMERA)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.CAMERA},CAMERA_PERMISSION);
//        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            //
            case PERMISSION_ALL: {
                // Comprobamos si se ha concedido el permiso o no. En este caso tienen que estar
                //todos concedidos
                boolean todosConcedidos = true;
                for(int i=0; i<grantResults.length && todosConcedidos; i++){
                    if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                        todosConcedidos = false;
                    }
                }
                if (grantResults.length > 0 && todosConcedidos) {
                    externalStoragePermission = true;
                    //Inicializamos las pestañas
                    this.toggleGeneradorTab();

                    //Inicializamos la base de datos
                    //this.setDatabase();
                } else {
                    //Si no tenemos permisos, avisamos y cerramos la aplicacion
                    externalStoragePermission = false;
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(getString(R.string.no_permission_critical_error))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //Cerramos la aplicacion
                                    finish();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }

        }

    }


    /**
     * Sobrescribimos el metodo onBackPressed para adaptar el uso de la tecla de retroceso
     * a la carga de fragmentos en cada pestaña
     */
    @Override
    public void onBackPressed() {
        boolean isPopFragment = false;
        String currentTabTag = tabHost.getCurrentTabTag();
        if (currentTabTag.equals(CONJUNTOS_TAB)) {
            isPopFragment = ((BaseContainerFragment) getSupportFragmentManager().findFragmentByTag(CONJUNTOS_TAB)).popFragment();
        } else if (currentTabTag.equals(PRENDAS_TAB)) {
            isPopFragment = ((BaseContainerFragment) getSupportFragmentManager().findFragmentByTag(PRENDAS_TAB)).popFragment();
        } else if (currentTabTag.equals(PLANIFICACION_TAB)) {
            isPopFragment = ((BaseContainerFragment) getSupportFragmentManager().findFragmentByTag(PLANIFICACION_TAB)).popFragment();
        }
        if (!isPopFragment) {
            finish();
        }
    }

    private void toggleGeneradorTab() {
        tabHost.clearAllTabs();
        if (showGenerador) {
            tabHost.addTab(tabHost.newTabSpec(CONJUNTO_SUG_TAB).setIndicator("", getResources().getDrawable(R.drawable.magic_m)),
                    ConjuntoSugeridoContainerFragment.class, null);
        }

        tabHost.addTab(tabMap.get(PRENDAS_TAB), PrendaContainerFragment.class, null);
        tabHost.addTab(tabMap.get(CONJUNTOS_TAB), ConjuntoContainerFragment.class, null);
        tabHost.addTab(tabMap.get(PLANIFICACION_TAB), PlanificacionContainerFragment.class, null);
        resizeTabIcons();
    }


}
