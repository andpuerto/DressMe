package uoc.master.angel.dressme.fragment;


import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.util.Date;
import java.util.HashMap;

import uoc.master.angel.dressme.R;
import uoc.master.angel.dressme.db.DiaDA;
import uoc.master.angel.dressme.fragment.container.BaseContainerFragment;
import uoc.master.angel.dressme.modelo.Dia;


/**
 * Created by angel on 29/03/2017.
 */

public class PlanificacionFragment extends Fragment {

    //Hashmap con los días
    private HashMap<Date, Dia> diasAsignados;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Obtenemos los dias asignados
        diasAsignados = new DiaDA(getContext()).getAllDia();
        //Establecemos el mapa para los colores
        HashMap<Date, Drawable> diasColoreados = new HashMap<>(diasAsignados.size());
        for (Date fecha : diasAsignados.keySet()) {
            diasColoreados.put(fecha, new ColorDrawable(
                    getResources().getColor(R.color.diaAsignado)));
        }

        //Creamos el calendario
        CaldroidFragment caldroidFragment = new CaldroidFragment();
        //Marcamos los dias que esten planificados
        caldroidFragment.setBackgroundDrawableForDates(diasColoreados);

        //Establecemos un listener para cuando se seleccionen los dias
        caldroidFragment.setCaldroidListener(new CaldroidListener() {
            @Override
            public void onSelectDate(Date date, View view) {
                showConjunto(date);
            }
        });

        FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar1, caldroidFragment);
        t.commit();

        return inflater.inflate(R.layout.planifiacion, container, false);
    }


    /**
     * Muestra el conjunto para la fecha recibida
     * @param fechaSeleccionada fecha para la que se desea ver el conjunto
     */
    private void showConjunto(Date fechaSeleccionada) {
        Log.i("Dia seleccionado", fechaSeleccionada.toString());

        //Dependiendo de si el dia seleccionado esta en la lista de los dias con conjunto asignado
        //iremos a una pantalla u otra
        Dia diaAsignado = diasAsignados.get(fechaSeleccionada);

        //Fragmento
        Fragment fragment;

        if (diaAsignado != null) {
            //Vamos a la pantalla que muestra los detalles del conjunto de ese dia
            fragment = new PlanificarConjuntoDetailFragment();
        } else {
            //Vamos a la pantalla de seleccion de conjunto para ese dia
            fragment = new PlanificarConjuntosListFragment();
            //Creamos un nuevo dia para la fecha seleccionada
            diaAsignado = new Dia(-1, fechaSeleccionada, null);
        }

        //Creamos y llenamos el bundle con los datos del TipoParteConjunto
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.dia_bundle_key), diaAsignado);
        //Pasamos el bundle al fragment
        fragment.setArguments(bundle);

        //Utilizamos el metodo de cambio de fragmento del fragmento padre
        ((BaseContainerFragment) getParentFragment()).replaceFragment(fragment, true);
    }

}
