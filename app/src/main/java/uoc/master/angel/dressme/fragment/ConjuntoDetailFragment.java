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

import java.util.ArrayList;
import java.util.List;

import uoc.master.angel.dressme.R;
import uoc.master.angel.dressme.db.ClimaDA;
import uoc.master.angel.dressme.db.ColorPrendaDA;
import uoc.master.angel.dressme.db.ConjuntoDA;
import uoc.master.angel.dressme.db.PrendaDA;
import uoc.master.angel.dressme.db.TipoParteConjuntoDA;
import uoc.master.angel.dressme.db.UsoDA;
import uoc.master.angel.dressme.fragment.container.BaseContainerFragment;
import uoc.master.angel.dressme.modelo.Clima;
import uoc.master.angel.dressme.modelo.ColorPrenda;
import uoc.master.angel.dressme.modelo.Conjunto;
import uoc.master.angel.dressme.modelo.ParteConjunto;
import uoc.master.angel.dressme.modelo.Prenda;
import uoc.master.angel.dressme.modelo.TipoParteConjunto;
import uoc.master.angel.dressme.modelo.Uso;
import uoc.master.angel.dressme.util.ImageUtil;

import static android.app.Activity.RESULT_OK;

/**
 * Created by angel on 26/04/2017.
 */

public class ConjuntoDetailFragment extends Fragment {

    //Lista con las vistas de imagenes
    private List<ImageView> imageViews = new ArrayList<>();

    //Conjunto que estamos editando
    private Conjunto conjunto;

    //Boton de eliminar la prenda
    private ImageButton eliminarButton;

    @Override

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }



    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Realizamos las inicializaciones en onCreateView en lugar de onCreate porque
        //si no, no se puede acceder a los elementos de la vista.
        View rootView =  inflater.inflate(R.layout.conjunto_detail, container, false);

        //Obtenemos las referencias a las ImageView
        //Aunque usamos una lista de ImageView para que sea mas generico, en esta parte estamos
        //cogiendolos estaticamente. Seria lo que habria que cambiar para hacerlo totalmente
        //generico
        imageViews.clear();
        imageViews.add((ImageView) rootView.findViewById(R.id.prenda_det_img1));
        imageViews.add((ImageView) rootView.findViewById(R.id.prenda_det_img2));
        imageViews.add((ImageView) rootView.findViewById(R.id.prenda_det_img3));
        imageViews.add((ImageView) rootView.findViewById(R.id.prenda_det_img4));

        //Establecemos las etiquetas para los nombres de partes de conjuntos
        List<TipoParteConjunto> tpcs= new TipoParteConjuntoDA(getContext()).getAllTipoParteConjunto();
        if(tpcs.size() >= 4){
            ((TextView)rootView.findViewById(R.id.parte_det_text1)).setText(tpcs.get(0).getNombre());
            ((TextView)rootView.findViewById(R.id.parte_det_text2)).setText(tpcs.get(1).getNombre());
            ((TextView)rootView.findViewById(R.id.parte_det_text3)).setText(tpcs.get(2).getNombre());
            ((TextView)rootView.findViewById(R.id.parte_det_text4)).setText(tpcs.get(3).getNombre());
        }

        //Tomamos las referencias a otros elementos de la vista
        eliminarButton = (ImageButton) rootView.findViewById(R.id.delete_conjunto_button);

        //Obtenemos los datos del conjunto, en caso de tenerlo
        //Si conjunto es null es porque se esta creando un nuevo conjunto
        if(getArguments() != null){
            Bundle bundle = this.getArguments();
            conjunto = (Conjunto)bundle.getSerializable(getString(R.string.conjunto_bundle_key));
            showConjuntoData();


        }else{
            //Aqui llegariamos si se esta creando una nueva prenda
            conjunto = new Conjunto();
        }

        //Inicializamos los botones
        initializeButtons();


        return rootView;

    }



    /**
     * Muestra la informacion basica del conjunto
     */
    private void showConjuntoData (){
        if(conjunto != null) {
            for(int i=0; i<imageViews.size(); i++){
                ImageView currentImageView = imageViews.get(i);
                ParteConjunto pc = conjunto.getPartesConjunto().get(i);
                if(currentImageView!=null){
                    //Establecemos la imagen de la prenda asignada, comprobando que sea correcto
                    //A esta pantalla, suponemos que el bundle ya llega con las imagenes asociadas
                    Bitmap bitmap = pc==null || pc.getPrendaAsignada()==null ||
                            pc.getPrendaAsignada().getFoto()==null ? null
                            : ImageUtil.toBitmap(pc.getPrendaAsignada().getFoto());
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
    }



    /**
     * Inicializa los botones
     */
    private void initializeButtons(){

//
//        //Listener para el boton de guardar
//        guardarButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //Antes de nada, comprobamos que los datos obligatorios esten rellenos
//                if(prenda.getFoto() == null && fotoTemp == null){
//                    Toast.makeText(getContext(),getString(R.string.no_foto_error),Toast.LENGTH_LONG).show();
//                    return;
//                }
//                //Primero, almacenamos todos los datos modificados en la prenda
//                prenda.setMarca(marcaText.getText().toString());
//                prenda.setMaterial(materialText.getText().toString());
//                prenda.setColor((ColorPrenda)colorSpinner.getSelectedItem());
//                if(fotoTemp != null) {
//                    prenda.setFoto(ImageUtil.toByteArray(fotoTemp));
//                }
//                //Los usos y los climas se cambian al cerrar el cuadro de dialogo
//                //Una vez actualizados los datos, se menten en la BD
//                new PrendaDA(getContext()).savePrenda(prenda);
//                //Volvemos a la vista de la lista
//                returnToPrendasList();
//
//            }
//        });
//
        //Listener para el boton de eliminar
        eliminarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDeleteConfirmationDialog();
            }
        });
    }




    /**
     * Crea el panel de confirmacion de borrado de conjunto
     */
    private void createDeleteConfirmationDialog(){
        AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(getActivity());
        deleteBuilder.setTitle(getString(R.string.delete_conjunto_confirm_title));
        deleteBuilder.setMessage(getString(R.string.delete_conjunto_confirm));
        deleteBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        deleteBuilder.setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Eliminamos el conjunto
                new ConjuntoDA(getContext()).deleteConjunto(conjunto);
                returnToConjuntosList();
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
     * Vuelve a la pantalla del listado de conjuntos
     */
    private void returnToConjuntosList(){
        ConjuntosListFragment cl = new ConjuntosListFragment();
        ((BaseContainerFragment)getParentFragment()).replaceFragment(cl, true);
    }

}
