package uoc.master.angel.dressme.fragment;


import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.nfc.FormatException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import uoc.master.angel.dressme.R;

/**
 * Created by angel on 29/03/2017.
 */

public class PlanificacionFragment extends Fragment {

    private CaldroidFragment caldroidFragment;


    @Override

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }



    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,

                             Bundle savedInstanceState) {




        //Creamos el calendario
        caldroidFragment = new CaldroidFragment();
        //Marcamos los dias que esten planificados
        marcarDiasPlanificados();

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


    private void marcarDiasPlanificados(){
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {

            caldroidFragment.setBackgroundDrawableForDate(
                    new ColorDrawable(getResources().getColor(R.color.lightGreen)),
                    format.parse("01/05/2017"));
        }catch(ParseException e){}
    }


    private void showConjunto(Date date){
        Log.i("Dia seleccionado", date.toString());
    }

}
