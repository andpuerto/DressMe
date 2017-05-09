package uoc.master.angel.dressme.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import uoc.master.angel.dressme.R;
import uoc.master.angel.dressme.db.da.ConjuntoDA;
import uoc.master.angel.dressme.db.da.DiaDA;
import uoc.master.angel.dressme.db.da.TipoParteConjuntoDA;
import uoc.master.angel.dressme.fragment.container.BaseContainerFragment;
import uoc.master.angel.dressme.modelo.Conjunto;
import uoc.master.angel.dressme.modelo.Dia;
import uoc.master.angel.dressme.modelo.ParteConjunto;
import uoc.master.angel.dressme.modelo.TipoParteConjunto;
import uoc.master.angel.dressme.util.DateUtil;
import uoc.master.angel.dressme.util.ImageUtil;

/**
 * Created by angel on 26/04/2017.
 */

public class PlanificarConjuntoDetailFragment extends Fragment {
    //Lista con las vistas de imagenes
    private List<ImageView> imageViews = new ArrayList<>();

    //Dia que estamos planificando
    private Dia dia;

    //Boton de desasignar el conjunto
    private ImageButton desasignarButton;

    //Boton de cambiar el conjunto
    private ImageButton changeButton;

    //Boton de guardar la prenda
    private FloatingActionButton guardarButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Realizamos las inicializaciones en onCreateView en lugar de onCreate porque
        //si no, no se puede acceder a los elementos de la vista.
        View rootView = inflater.inflate(R.layout.planificar_conjunto_detail, container, false);

        //Obtenemos las referencias a las ImageView
        //Aunque usamos una lista de ImageView para que sea mas generico, en esta parte estamos
        //cogiendolos estaticamente. Seria lo que habria que cambiar para hacerlo totalmente
        //generico
        imageViews.clear();
        imageViews.add((ImageView) rootView.findViewById(R.id.prenda_plan_img1));
        imageViews.add((ImageView) rootView.findViewById(R.id.prenda_plan_img2));
        imageViews.add((ImageView) rootView.findViewById(R.id.prenda_plan_img3));
        imageViews.add((ImageView) rootView.findViewById(R.id.prenda_plan_img4));

        //Establecemos las etiquetas para los nombres de partes de conjuntos
        List<TipoParteConjunto> tpcs = new TipoParteConjuntoDA(getContext()).getAllTipoParteConjunto();
        if (tpcs.size() >= 4) {
            ((TextView) rootView.findViewById(R.id.parte_plan_text1)).setText(tpcs.get(0).getNombre());
            ((TextView) rootView.findViewById(R.id.parte_plan_text2)).setText(tpcs.get(1).getNombre());
            ((TextView) rootView.findViewById(R.id.parte_plan_text3)).setText(tpcs.get(2).getNombre());
            ((TextView) rootView.findViewById(R.id.parte_plan_text4)).setText(tpcs.get(3).getNombre());
        }


        //Tomamos las referencias a otros elementos de la vista
        desasignarButton = (ImageButton) rootView.findViewById(R.id.unasign_conjunto_button);
        changeButton = (ImageButton) rootView.findViewById(R.id.change_conjunto_button);
        guardarButton = (FloatingActionButton) rootView.findViewById(R.id.save_plan_button);

        //Obtenemos los datos del dia prlanificado, en caso de tenerlo (que deberia ser siempre)
        if (getArguments() != null) {
            Bundle bundle = this.getArguments();
            dia = (Dia) bundle.getSerializable(getString(R.string.dia_bundle_key));
            showConjuntoData();
        }

        //Inicializamos los botones
        initializeButtons();

        return rootView;
    }


    /**
     * Muestra la informacion basica del conjunto
     */
    private void showConjuntoData() {
        Conjunto conjunto = dia.getConjuntoAsignado();
        if (conjunto != null) {
            //Obtenemos los detalles del conjunto
            new ConjuntoDA(getContext()).fillConjuntoDetails(conjunto);
            for (int i = 0; i < imageViews.size(); i++) {
                ImageView currentImageView = imageViews.get(i);
                ParteConjunto pc = conjunto.getPartesConjunto().get(i);
                if (currentImageView != null) {
                    //Establecemos la imagen de la prenda asignada, comprobando que sea correcto
                    //A esta pantalla, suponemos que el bundle ya llega con las imagenes asociadas
                    Bitmap bitmap = pc == null || pc.getPrendaAsignada() == null ||
                            pc.getPrendaAsignada().getFoto() == null ? null
                            : ImageUtil.toBitmap(pc.getPrendaAsignada().getFoto());
                    //Si obtenemos el bitmap, establecemos la imagen con el.
                    //Si la imagen esta vacia establecemos una imagen de un rectangulo transparente
                    //Es necesario para que el layout de las imagenes quede bien
                    if (bitmap != null) {
                        currentImageView.setImageBitmap(bitmap);
                    } else {
                        currentImageView.setImageResource(R.drawable.smalltransrect);
                    }

                }
            }
        }
    }


    /**
     * Inicializa los botones
     */
    private void initializeButtons() {
        //Listener para el boton de guardar
        guardarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Guardamos el dia
                new DiaDA(getContext()).saveDia(dia);
                //Volvemos a la vista anterior
                returnToPlanificacion();
            }
        });

        //Listener para el boton de eliminar
        desasignarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUnassignConfirmationDialog();
            }
        });

        //Listener para el boton de cambiar
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectConjunto();
            }
        });
    }


    /**
     * Crea el panel de confirmacion de desasignar conjunto
     */
    private void createUnassignConfirmationDialog() {
        AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(getActivity());
        deleteBuilder.setTitle(getString(R.string.unassign_conjunto_confirm_title));
        deleteBuilder.setMessage(String.format(
                getString(R.string.unassign_conjunto_confirm), DateUtil.
                        dateToPresentableString(dia.getFecha())));
        deleteBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        deleteBuilder.setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Eliminamos el el dia
                new DiaDA(getContext()).deleteDia(dia);
                returnToPlanificacion();
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
     * Vuelve a la pantalla de planificacion
     */
    private void returnToPlanificacion() {
        ((BaseContainerFragment) getParentFragment()).popFragment();
    }


    /**
     * Abre la vista de seleccion de un conjunto para el dia que estamos editando
     */
    private void selectConjunto() {
        //Creamos el fragmento
        PlanificarConjuntosListFragment cplf = new PlanificarConjuntosListFragment();
        //Creamos y llenamos el bundle con los datos del TipoParteConjunto
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.dia_bundle_key), dia);
        //Pasamos el bundle al fragment
        cplf.setArguments(bundle);
        //Utilizamos el metodo de cambio de fragmento del fragmento padre
        ((BaseContainerFragment) getParentFragment()).replaceFragment(cplf, true);
    }

}
