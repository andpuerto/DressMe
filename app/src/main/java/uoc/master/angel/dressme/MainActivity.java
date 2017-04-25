package uoc.master.angel.dressme;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


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

    //Adaptador para la lista de libros
 //   private BookListAdapter adapter = new BookListAdapter(new ArrayList<BookContent.BookItem>());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Pedimos los permisos necesarios al usuario
        this.askForPermission();

        //Inicializamos las pestañas
        this.initializeTabs();

        //Inicializamos la base de datos
        this.setDatabase();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Realiza las funciones de configuracion inicial de las pestañas
     */
    private void initializeTabs(){

        tabHost= (FragmentTabHost) findViewById(android.R.id.tabhost);

        tabHost.setup(this,
                getSupportFragmentManager(),android.R.id.tabcontent);
        tabHost.addTab(tabHost.newTabSpec(CONJUNTO_SUG_TAB).setIndicator(getString(R.string.conjunto_sugerido_tab_label)),
                ConjuntoSugeridoContainerFragment.class, null);
        tabHost.addTab(tabHost.newTabSpec(CONJUNTOS_TAB).setIndicator(getString(R.string.conjuntos_tab_label)),
                ConjuntoContainerFragment.class, null);
        tabHost.addTab(tabHost.newTabSpec(PRENDAS_TAB).setIndicator(getString(R.string.prendas_tab_label)),
                PrendaContainerFragment.class, null);
        tabHost.addTab(tabHost.newTabSpec(PLANIFICACION_TAB).setIndicator(getString(R.string.planifiacion_tab_label)),
                PlanificacionContainerFragment.class, null);

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
    private void setDatabase(){
        DressMeSQLHelper dmdbh = new DressMeSQLHelper(this, "DBDressMe", null, 1);
        SQLiteDatabase db = dmdbh.getWritableDatabase();


    }


    private void askForPermission(){
        //Si no tenemos permisos, los pedimos

        // The request code used in ActivityCompat.requestPermissions()
        // and returned in the Activity's onRequestPermissionsResult()

        String[] permisos = {Manifest.permission.INTERNET, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA};

        if(!PermissionsUtil.hasPermissions(this, permisos)){
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
                // Comprobamos si se ha concedido el permiso o no. Dejamos el bool con el valor
                //adecuado en cada caso
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    externalStoragePermission = true;
                } else {
                    externalStoragePermission = false;
                }
                return;
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
            isPopFragment = ((BaseContainerFragment)getSupportFragmentManager().findFragmentByTag(CONJUNTOS_TAB)).popFragment();
        } else if (currentTabTag.equals(PRENDAS_TAB)) {
            isPopFragment = ((BaseContainerFragment)getSupportFragmentManager().findFragmentByTag(PRENDAS_TAB)).popFragment();
        } else if (currentTabTag.equals(PLANIFICACION_TAB)) {
            isPopFragment = ((BaseContainerFragment)getSupportFragmentManager().findFragmentByTag(PLANIFICACION_TAB)).popFragment();
        }
        if (!isPopFragment) {
            finish();
        }
    }
}
