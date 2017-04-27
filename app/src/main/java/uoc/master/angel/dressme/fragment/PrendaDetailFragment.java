package uoc.master.angel.dressme.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import uoc.master.angel.dressme.R;
import uoc.master.angel.dressme.db.ClimaDA;
import uoc.master.angel.dressme.db.ColorPrendaDA;
import uoc.master.angel.dressme.db.PrendaDA;
import uoc.master.angel.dressme.db.UsoDA;
import uoc.master.angel.dressme.fragment.container.BaseContainerFragment;
import uoc.master.angel.dressme.modelo.Clima;
import uoc.master.angel.dressme.modelo.ColorPrenda;
import uoc.master.angel.dressme.modelo.Prenda;
import uoc.master.angel.dressme.modelo.Uso;
import uoc.master.angel.dressme.util.ImageUtil;

import static android.app.Activity.RESULT_OK;

/**
 * Created by angel on 05/04/2017.
 */

public class PrendaDetailFragment extends Fragment {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    //Prenda que se muestra en el panel
    private Prenda prenda;
    //Imagen con la foto
    private ImageView fotoView;
    //Spinner de seleccion de color
    private Spinner colorSpinner;
    //Textview del material
    private TextView materialText;
    //Textview de la marca
    private TextView marcaText;
    //Boton de seleccion de uso
    private Button usoButton;
    //Boton de seleccion de clima
    private Button climaButton;
    //Boton de la camara de fotos
    private ImageButton camaraButton;
    //boton de guardar
    private FloatingActionButton guardarButton;
    //boton de eliminar
    private ImageButton eliminarButton;

    //Lista con los colores de prendas posibles
    private List<ColorPrenda> coloresPrenda;
    //Lista con los usos de prenda posibles
    private List<Uso> usos;
    //Lista con los climas posibles
    private List<Clima> climas;

    //Adaptador para el spinner
    private ColorPrendaSpinnerAdapter adapter;

    //Bitmap temporal para la foto que se haya hecho
    private Bitmap fotoTemp;

    @Override

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }


    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Realizamos las inicializaciones en onCreateView en lugar de onCreate porque
        //si no, no se puede acceder a los elementos de la vista.
        View rootView = inflater.inflate(R.layout.prenda_detail, container, false);
        //Obtenemos los elementos de la vista que vamos a usar
        fotoView = (ImageView) rootView.findViewById(R.id.prenda_detail_foto);
        colorSpinner = (Spinner) rootView.findViewById(R.id.color_spinner);
        materialText = (TextView) rootView.findViewById(R.id.prenda_material_edit);
        marcaText = (TextView) rootView.findViewById(R.id.prenda_marca_edit);
        usoButton = (Button) rootView.findViewById(R.id.prenda_uso_button);
        climaButton = (Button) rootView.findViewById(R.id.prenda_clima_button);
        camaraButton = (ImageButton) rootView.findViewById(R.id.camera_buttion);
        guardarButton = (FloatingActionButton) rootView.findViewById(R.id.save_prenda_button);
        eliminarButton = (ImageButton) rootView.findViewById(R.id.delete_prenda_button);

        //Inicializamos el spinner con los colores
        this.initializeColores();


        //Obtenemos los datos de la prenda, en caso de tenerla
        //Si prenda es null es porque se esta creando una nueva prenda
        if (getArguments() != null) {
            Bundle bundle = this.getArguments();
            prenda = (Prenda) bundle.getSerializable(getString(R.string.prenda_bundle_key));
            showPredaData();


        } else {
            //Aqui llegariamos si se esta creando una nueva prenda
            prenda = new Prenda();
        }

        //Inicializamos los botones
        initializeButtons();

        //Inicializamos el boton de la camara
        initializeCameraButton();


        return rootView;

    }

    /**
     * Muestra la informacion basica de la prenda
     */
    private void showPredaData() {
        if (prenda != null) {
            new PrendaDA(this.getContext()).fillPrendaDetails(prenda);
            //Realizaremos las inicializaciones para mostrar todos los datos de la prenda
            Log.i("PrendaDetail", "Identificador de la prenda: " + prenda.getId());
            //Establecemos la foto
            if (prenda.getFoto() != null && fotoView != null) {
                fotoView.setImageBitmap(ImageUtil.toBitmap(prenda.getFoto()));
            }
            //Seleccionamos el color de la prenda
            if (prenda.getColor() != null) {
                colorSpinner.setSelection(prenda.getColor().getId());
            }
            //Establecemos los valores de los textView
            materialText.setText(prenda.getMaterial());
            marcaText.setText(prenda.getMarca());
        }
    }


    /**
     * Inicializa el spinner de los colores
     */
    private void initializeColores() {
        //Obtenemos la lista de todos los colores
        if (this.coloresPrenda == null) {
            this.coloresPrenda = new ColorPrendaDA(getContext()).getAllColorPrenda();
        }
        //Creamos y establecemos el adaptador
        adapter = new ColorPrendaSpinnerAdapter(getContext());
        this.colorSpinner.setAdapter(adapter);
    }


    /**
     * Inicializa los botones
     */
    private void initializeButtons() {

        //Listener para el boton de usos
        usoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUsosSelectionDialog();
            }
        });

        //Listener para el boton de climas
        climaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createClimasSelectionDialog();
            }
        });

        //Listener para el boton de guardar
        guardarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Antes de nada, comprobamos que los datos obligatorios esten rellenos
                if (prenda.getFoto() == null && fotoTemp == null) {
                    Toast.makeText(getContext(), getString(R.string.no_foto_error), Toast.LENGTH_LONG).show();
                    return;
                }
                //Primero, almacenamos todos los datos modificados en la prenda
                prenda.setMarca(marcaText.getText().toString());
                prenda.setMaterial(materialText.getText().toString());
                prenda.setColor((ColorPrenda) colorSpinner.getSelectedItem());
                if (fotoTemp != null) {
                    prenda.setFoto(ImageUtil.toByteArray(fotoTemp));
                }
                //Los usos y los climas se cambian al cerrar el cuadro de dialogo
                //Una vez actualizados los datos, se menten en la BD
                new PrendaDA(getContext()).savePrenda(prenda);
                //Volvemos a la vista de la lista
                returnToPrendasList();

            }
        });

        if (prenda.getId() < 0) {
            eliminarButton.setVisibility(View.INVISIBLE);
        } else {
            //Listener para el boton de eliminar
            eliminarButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createDeleteConfirmationDialog();
                }
            });
        }
    }


    /**
     * Crea y muestra el dialogo de seleccion de usos
     */
    private void createUsosSelectionDialog() {
        //Obtenemos los nombres de los usos
        String nombresUso[] = new UsoDA(getContext()).getAllUsoNombre();
        //Creamos e inicializamos el array indicando si cada elemento debe estar seleccionado o no
        final boolean usosSelected[] = new boolean[nombresUso.length];
        for (int i = 0; i < nombresUso.length; i++) {
            usosSelected[i] = prenda != null && prenda.hasUso(i);
        }
        //Creamos e inicializamos el panel de seleccion de usos
        AlertDialog.Builder usosBuilder = new AlertDialog.Builder(getActivity());
        usosBuilder.setMultiChoiceItems(nombresUso, usosSelected, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                //Al hacer click en un campo, cambiamos el valor del array de boolean
                usosSelected[which] = isChecked;
            }
        });

        //Al presionar el boton de OK
        usosBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Revisamos la lista de elementos seleccionados. Si esta seleccionado
                //añadimos a los usos de la prenda, si no, lo eliminamos.
                //Delegamos en el objeto prenda la responsabilidad de comprobar si ya
                //existe o no el elemento
                for (int i = 0; i < usosSelected.length; i++) {
                    if (usosSelected[i]) {
                        prenda.addUso(new UsoDA(getContext()).getUso(i));
                    } else {
                        prenda.removeUso(i);
                    }
                }
            }
        });
        //Creamos y mostramos el dialogo
        AlertDialog dialog = usosBuilder.create();
        dialog.show();
    }


    /**
     * Crea y muestra el dialogo de seleccion de climas
     */
    private void createClimasSelectionDialog() {
        //Obtenemos los nombres de los climas
        String nombresClima[] = new ClimaDA(getContext()).getAllClimaNombre();
        //Creamos e inicializamos el array indicando si cada elemento debe estar seleccionado o no
        final boolean climasSelected[] = new boolean[nombresClima.length];
        for (int i = 0; i < nombresClima.length; i++) {
            climasSelected[i] = prenda != null && prenda.hasClima(i);
        }
        //Creamos e inicializamos el panel de seleccion de clima
        AlertDialog.Builder climasBuilder = new AlertDialog.Builder(getActivity());
        climasBuilder.setMultiChoiceItems(nombresClima, climasSelected, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                //Al hacer click en un campo, cambiamos el valor del array de boolean
                climasSelected[which] = isChecked;
            }
        });

        //Al presionar el boton de OK
        climasBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Revisamos la lista de elementos seleccionados. Si esta seleccionado
                //añadimos a los climas de la prenda, si no, lo eliminamos.
                //Delegamos en el objeto prenda la responsabilidad de comprobar si ya
                //existe o no el elemento
                for (int i = 0; i < climasSelected.length; i++) {
                    if (climasSelected[i]) {
                        prenda.addClima(new ClimaDA(getContext()).getClima(i));
                    } else {
                        prenda.removeClima(i);
                    }
                }
            }
        });
        //Creamos y mostramos el dialogo
        AlertDialog dialog = climasBuilder.create();
        dialog.show();
    }


    /**
     * Crea el panel de confirmacion de borrado de prenda
     */
    private void createDeleteConfirmationDialog() {
        AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(getActivity());
        deleteBuilder.setTitle(getString(R.string.delete_prenda_confirm_title));
        deleteBuilder.setMessage(getString(R.string.delete_prenda_confirm));
        deleteBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        deleteBuilder.setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Eliminamos la prenda
                new PrendaDA(getContext()).deletePrenda(prenda);
                returnToPrendasList();
            }
        })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Al pulsar cancel no hacemos nada
                    }
                }).show();

    }

    /**
     * Vuelve a la pantalla del listado de prendas
     */
    private void returnToPrendasList() {
        //PrendasListFragment pl = new PrendasListFragment();
        ((BaseContainerFragment) getParentFragment()).popFragment();
    }

    /**
     * Inicializa el boton de la camara
     */
    private void initializeCameraButton() {
        camaraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });


    }


    /**
     * Metodo invocado cuando se vuelva de una actividad, principalemnte la de la camara de fotos
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //Vamos a recoger la imagen del bundle que obtenemos del Intent
            //Se trata de una imagen a baja resolucion. En principio para nuestro proposito
            //nos vale, pero se puede intentar hacer a mas calidad
            //En este caso, lo suyo sera luego bajarla un poco para evitar consumir mucha memoria
            //e introducir demasiados datos en la BD
            Bundle extras = data.getExtras();
            fotoTemp = (Bitmap) extras.get("data");
            //Establecemos la imagen en el imageview
            fotoView.setImageBitmap(fotoTemp);
        }
    }


    /**
     * Clase personalizada para el adaptador del spinner de seleccion de color
     * Lo hacemos personalizado para poder mostrar tanto la etiqueta con el nombre
     * del color como un cuadrito donde se vea el color
     */
    public class ColorPrendaSpinnerAdapter extends ArrayAdapter<ColorPrenda> {

        //Valor de cada color de prenda
        ColorPrenda tempValues = null;
        LayoutInflater inflater;

        /**
         * Constructor
         *
         * @param context
         */
        public ColorPrendaSpinnerAdapter(Context context) {
            //Llamamos al constructor de la clase base
            super(context, R.layout.color_spiner, coloresPrenda);
            //Obtenemos el inflater para poder crear las vistas
            inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        /**
         * Se le llama para la vista de los elementos desplegados
         */
        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            //Queremos que los elementos desplegados tengan mas altura para que sea mas facil
            //seleccionarlos con el dedo sin tocar un elemento adyacente por error
            return getCustomView(position, convertView, parent, R.layout.color_spiner_item);
        }

        /**
         * Se le llama para la vista del spinner cerrado
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //Para el spinner cerrado queremos que tenga una altura menor, por eso usaremos
            //un layout ligeramente diferente
            return getCustomView(position, convertView, parent, R.layout.color_spiner);
        }

        /**
         * Se le llama para cada fila
         */
        public View getCustomView(int position, View convertView, ViewGroup parent, int layoutId) {

            //Para cada elemento se mostrara la vista indicada como parametro
            View row = inflater.inflate(layoutId, parent, false);

            //Obtenemos el elemento de la lista correspondiente a esa posicion
            tempValues = coloresPrenda.get(position);

            //Obtenemos los elementos de la interfaz para esa fila
            TextView label = (TextView) row.findViewById(R.id.item_color_label);
            ImageView colorImage = (ImageView) row.findViewById(R.id.item_color_square);

            //Establecemos los valores para los elementos de la interfaz
            label.setText(tempValues.getNombre());
            colorImage.setBackgroundColor(Color.parseColor("#" + tempValues.getRgb()));

            //Devolvemos la vista de la fila
            return row;
        }


    }


}
