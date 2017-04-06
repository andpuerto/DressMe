package uoc.master.angel.dressme.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uoc.master.angel.dressme.R;
import uoc.master.angel.dressme.modelo.Prenda;

/**
 * Created by angel on 05/04/2017.
 */

public class PrendaDetailFragment extends Fragment {

    @Override

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            Bundle bundle = this.getArguments();
            Prenda prenda = (Prenda)bundle.getSerializable(getString(R.string.prenda_bundle_key));
            //Realizaremos las inicializaciones para mostrar todos los datos de la prenda
            Log.i("PrendaDetail", "Identificador de la prenda: " + prenda.getId());
        }else{
            //Aqui llegariamos si se esta creando una nueva prenda
        }



    }



    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,

                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.prenda_detail, container, false);

    }

}
