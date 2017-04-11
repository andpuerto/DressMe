package uoc.master.angel.dressme.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.List;

import uoc.master.angel.dressme.R;
import uoc.master.angel.dressme.db.ColorPrendaDA;
import uoc.master.angel.dressme.db.PrendaDA;
import uoc.master.angel.dressme.modelo.ColorPrenda;
import uoc.master.angel.dressme.modelo.Prenda;
import uoc.master.angel.dressme.util.ImageUtil;

/**
 * Created by angel on 05/04/2017.
 */

public class PrendaDetailFragment extends Fragment {

    private Prenda prenda;
    private ImageView fotoView;
//    private View colorSquareView;
    private Spinner colorSpinner;
    private List<ColorPrenda> coloresPrenda;
    private SpinnerAdapter adapter;

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
        //Obtenemos los elementos de la vista que vamos a usar
        fotoView = (ImageView) rootView.findViewById(R.id.prenda_detail_foto);
//        colorSquareView = rootView.findViewById(R.id.color_square);
        colorSpinner = (Spinner) rootView.findViewById(R.id.color_spinner);

        //Inicializamos el spinner con los colores
        this.initializeColores();

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
//            if(colorSquareView != null){
//                colorSquareView.setBackgroundColor(Color.parseColor("#"+prenda.getColor().getRgb()));
//            }
        }
    }

    private void initializeColores(){
        //Obtenemos la lista de todos los colores
        if(this.coloresPrenda == null) {
            this.coloresPrenda = new ColorPrendaDA(getContext()).getAllColorPrenda();
        }
        adapter = new ColorPrendaSpinnerAdapter(getContext());
        this.colorSpinner.setAdapter(adapter);

    }




    /***** Adapter class extends with ArrayAdapter ******/
    public class ColorPrendaSpinnerAdapter extends ArrayAdapter<ColorPrenda> {

//        private Activity activity;
//        private ArrayList data;
//        public Resources res;
        ColorPrenda tempValues=null;
        LayoutInflater inflater;

        /*************  CustomAdapter Constructor *****************/
        public ColorPrendaSpinnerAdapter(Context context){
            super(context,R.layout.color_spiner_item, coloresPrenda);
//            super(activitySpinner, textViewResourceId, objects);
//
//            /********** Take passed values **********/
//            activity = activitySpinner;
//            data     = objects;
//            res      = resLocal;


            /***********  Layout inflator to call external xml layout () **********************/
            inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        }

        @Override
        public View getDropDownView(int position, View convertView,ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        // This funtion called for each row ( Called data.size() times )
        public View getCustomView(int position, View convertView, ViewGroup parent) {

            /********** Inflate spinner_rows.xml file for each row ( Defined below ) ************/
            View row = inflater.inflate(R.layout.color_spiner_item, parent, false);

            /***** Get each Model object from Arraylist ********/
            tempValues = null;
            tempValues = coloresPrenda.get(position);

            TextView label        = (TextView)row.findViewById(R.id.item_color_label);
            ImageView colorImage = (ImageView)row.findViewById(R.id.item_color_square);

//            if(position==0){
//
//                // Default selected Spinner item
//                label.setText("Selecciona un color");
//
//            }
//            else
//            {
                // Set values for spinner each row
                label.setText(tempValues.getNombre());
                colorImage.setBackgroundColor(Color.parseColor("#"+tempValues.getRgb()));

//            }

            return row;
        }
    }

}
