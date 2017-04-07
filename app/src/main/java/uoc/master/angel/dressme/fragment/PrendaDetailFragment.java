package uoc.master.angel.dressme.fragment;

import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import uoc.master.angel.dressme.R;
import uoc.master.angel.dressme.db.PrendaDA;
import uoc.master.angel.dressme.modelo.Prenda;
import uoc.master.angel.dressme.util.ImageUtil;

/**
 * Created by angel on 05/04/2017.
 */

public class PrendaDetailFragment extends Fragment {

    private Prenda prenda;
    private ImageView fotoView;
    private View colorSquareView;

    @Override

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }



    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Realizamos las inicializaciones en onCreateView en lugar de onCreate porque
        //si no, no se puede acceder a los elementos de la vista.
        View rootView =  inflater.inflate(R.layout.prenda_detail, container, false);
        fotoView = (ImageView) rootView.findViewById(R.id.prenda_detail_foto);
        colorSquareView = rootView.findViewById(R.id.color_square);
        if(getArguments() != null){
            Bundle bundle = this.getArguments();
            prenda = (Prenda)bundle.getSerializable(getString(R.string.prenda_bundle_key));
            showPredaData();

        }else{
            //Aqui llegariamos si se esta creando una nueva prenda
        }
        return rootView;

    }


    private void showPredaData (){
        if(prenda != null) {
            new PrendaDA(this.getContext()).fillPrendaDetails(prenda);
            //Realizaremos las inicializaciones para mostrar todos los datos de la prenda
            Log.i("PrendaDetail", "Identificador de la prenda: " + prenda.getId());

            if(fotoView != null){
                fotoView.setImageBitmap(ImageUtil.toBitmap(prenda.getFoto()));
            }
            if(colorSquareView != null){
                colorSquareView.setBackgroundColor(Color.parseColor("#"+prenda.getColor().getRgb()));
            }
        }
    }

}
