package uoc.master.angel.dressme;

import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;



import java.util.ArrayList;

import uoc.master.angel.dressme.db.DressMeSQLHelper;

public class MainActivity extends AppCompatActivity {

    //Vista de pestañas
    private FragmentTabHost tabHost;
    //Permiso de acceso al almacenamiento externo
    private boolean externalStoragePermission = false;
    //Constante para identificar el permiso de almacenamiento externo
    private final int EXTERNAL_STORAGE_PERMISSION = 1;

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
        tabHost.addTab(tabHost.newTabSpec("conjuntosTab").setIndicator("Conjuntos"),
                ConjuntosList.class, null);
        tabHost.addTab(tabHost.newTabSpec("prendasTab").setIndicator("Prendas"),
                PrendasList.class, null);
        tabHost.addTab(tabHost.newTabSpec("planificacionTab").setIndicator("Planificar"),
                Planificacion.class, null);

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
                    return new ConjuntosList();
                case 1:
                    return new PrendasList();
                default:
                    return new Planificacion();

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
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE},EXTERNAL_STORAGE_PERMISSION);

        }else{
            //Si los tenemos, establecemos la variable a verdadero
            externalStoragePermission = true;
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            //
            case EXTERNAL_STORAGE_PERMISSION: {
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
}
