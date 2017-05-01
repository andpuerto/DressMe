package uoc.master.angel.dressme.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import uoc.master.angel.dressme.R;
import uoc.master.angel.dressme.db.ClimaDA;
import uoc.master.angel.dressme.db.ColorPrendaDA;
import uoc.master.angel.dressme.db.PrendaDA;
import uoc.master.angel.dressme.db.TipoParteConjuntoDA;
import uoc.master.angel.dressme.db.UsoDA;
import uoc.master.angel.dressme.fragment.container.BaseContainerFragment;
import uoc.master.angel.dressme.modelo.Clima;
import uoc.master.angel.dressme.modelo.ColorPrenda;
import uoc.master.angel.dressme.modelo.Prenda;
import uoc.master.angel.dressme.modelo.TipoParteConjunto;
import uoc.master.angel.dressme.modelo.Uso;
import uoc.master.angel.dressme.util.ImageUtil;

/**
 * Created by angel on 29/03/2017.
 */

public class PrendasListFragment extends Fragment {


    //Lista con todos los tipos de partes de conjunto
    List<TipoParteConjunto> tiposParteConjunto = new ArrayList<>();

    //Arrays con los elementos de filtro y arrays con los nombres para los Spinner
    private List<Uso> usos = new ArrayList<>();
    private String[] usosStrings;
    private List<Clima> climas = new ArrayList<>();
    private String[] climasStrings;
    private List<ColorPrenda> colores = new ArrayList<>();
    private String[] coloresStrings;

    //Spinners
    Spinner usoSpinner;
    Spinner climaSpinner;
    Spinner colorSpinner;

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Obtener la lista de TipoParteConjunto de la base de datos
        tiposParteConjunto = new TipoParteConjuntoDA(this.getContext()).getAllTipoParteConjunto();

        //Obtenemos la lista de usos
        usos = new UsoDA(getContext()).getAllUso();
        //Añadimos un uso con un valor null para hacer referencia a todos los usos
        usos.add(0, null);
        //Creamos la lista de las strings de los usos
        usosStrings = new String[usos.size()];
        //Insertamos un primer elemento en el array con la opcion por defectop
        usosStrings[0] = getString(R.string.todos);
        //Insertamos el resto de elementos en el array
        for(int i=1; i<usos.size(); i++){
            usosStrings[i] = usos.get(i).getNombre();
        }

        //Hacemos lo mismo con las otras dos listas
        climas = new ClimaDA(getContext()).getAllClima();
        climas.add(0, null);
        climasStrings = new String[climas.size()];
        climasStrings[0] = getString(R.string.todos);
        for(int i=1; i<climas.size(); i++){
            climasStrings[i] = climas.get(i).getNombre();
        }

        colores = new ColorPrendaDA(getContext()).getAllColorPrenda();
        colores.add(0, null);
        coloresStrings = new String[colores.size()];
        coloresStrings[0] = getString(R.string.todos);
        for(int i=1; i<colores.size(); i++){
            coloresStrings[i] = colores.get(i).getNombre();
        }

      }


    //Este metodo establece las vistas iniciales de esta actividad
    private void setViews() {

        //Nos cercioramos de que haya cuatro tipoParteConjunto
        if(tiposParteConjunto.size() < 4){
            return;
        }

        //Vamos a establecer los adaptadores a cada RecycleView
        //En esta version, lo hacemos de forma estatica. Para hacerlo generico habria que crea cada
        //recycleview aquí por programa en lugar de tenerlo en el layout
        RecyclerView recyclerView = (RecyclerView) this.getView().findViewById(R.id.prendas_recycle_view1);
        recyclerView.setAdapter(new PrendasListAdapter(new PrendaDA(
                this.getContext()).getAllPrendas(tiposParteConjunto.get(0), false)));
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));

        recyclerView = (RecyclerView) this.getView().findViewById(R.id.prendas_recycle_view2);
        recyclerView.setAdapter(new PrendasListAdapter(new PrendaDA(
                this.getContext()).getAllPrendas(tiposParteConjunto.get(1), false)));
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));

        recyclerView = (RecyclerView) this.getView().findViewById(R.id.prendas_recycle_view3);
        recyclerView.setAdapter(new PrendasListAdapter(new PrendaDA(
                this.getContext()).getAllPrendas(tiposParteConjunto.get(2), false)));
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));

        recyclerView = (RecyclerView) this.getView().findViewById(R.id.prendas_recycle_view4);
        recyclerView.setAdapter(new PrendasListAdapter(new PrendaDA(
                this.getContext()).getAllPrendas(tiposParteConjunto.get(3), false)));
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));


        //Establecemos el clicklistener para los botones de crear nueva prenda.
        //Tambien esta hecho de forma estatica en esta version
        (this.getView().findViewById(R.id.prendas_add_button1)).setOnClickListener(
                new AddButtonListener(tiposParteConjunto.get(0)));
        (this.getView().findViewById(R.id.prendas_add_button2)).setOnClickListener(
                new AddButtonListener(tiposParteConjunto.get(1)));
        (this.getView().findViewById(R.id.prendas_add_button3)).setOnClickListener(
                new AddButtonListener(tiposParteConjunto.get(2)));
        (this.getView().findViewById(R.id.prendas_add_button4)).setOnClickListener(
                new AddButtonListener(tiposParteConjunto.get(3)));

        //Establecemos los spinners
        usoSpinner = (Spinner)getView().findViewById(R.id.prendas_usos_spinner);
        climaSpinner = (Spinner)getView().findViewById(R.id.prendas_climas_spinner);
        colorSpinner = (Spinner)getView().findViewById(R.id.prendas_colores_spinner);

        //Adapter y listener para los spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, usosStrings);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        usoSpinner.setAdapter(spinnerAdapter);
        usoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Filtramos los elementos
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, climasStrings);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        climaSpinner.setAdapter(spinnerAdapter);
        climaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Filtramos los elementos
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, coloresStrings);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorSpinner.setAdapter(spinnerAdapter);
        colorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Filtramos los elementos
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




    }


    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,

                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.prendas_list, container, false);

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);



        this.setViews();
    }

    /**
     * Clase interna para el adaptador del RecyclerView
     */
    public class PrendasListAdapter extends RecyclerView.Adapter<PrendasListAdapter.ViewHolder> {
        //Constantes indicando los valores de par e impar
        private final static int EVEN = 0;
        private final static int ODD = 1;
        //Lista de prendas
        private List<Prenda> mValues;

        //Constructor. Recibe unicamente la lista de prendas
        PrendasListAdapter(List<Prenda> items) {
            mValues = items;
        }

        //Actualizamos la lista con los datos recibidos
        public void setItems(List<Prenda> items) {
            mValues = items;


        }

        @Override
        public PrendasListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            //Segun el tipo de vista que recibamos como parametro, inflamos el layout
            //para los items pares o para los impares
            switch (viewType) {
                case PrendasListAdapter.EVEN:
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.list_item_prenda_par, parent, false);
                    break;
                case PrendasListAdapter.ODD:
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.list_item_prenda_impar, parent, false);
                    break;
            }
            //Devolvemos un ViewHolder con la vista que tendra el layout adecuado
            return new PrendasListAdapter.ViewHolder(view);
        }


        @Override
        public void onBindViewHolder(final PrendasListAdapter.ViewHolder holder, final int position) {
            //Establecemos los valores del viewHolder
            //Tomamos el elemento de la lista usando la posicion recibida como parametro
            //Tomamos la posicion. Podria hacerse directamente usando el parametro position pero da
            //lugar a un warning y el compilador no lo recomienda porque la posicion puede cambiar
            final int posicion = holder.getAdapterPosition();
            holder.mItem = mValues.get(position);
            //Establecemos la imagen
            holder.mImageView.setImageBitmap(ImageUtil.toBitmap(mValues.get(position).getFoto()));


            //Tambien establecemos el clickListener para el holder, para que cada elemento responda
            //a los clicks
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mostrarDetalles(holder.mItem);
                }

            });
        }




        //Indica si la posicion que recibe es par o impar
        @Override
        public int getItemViewType(int position) {
            return position % 2;
        }


        @Override
        public int getItemCount() {
            return mValues.size();
        }


        //ViewHolder
        class ViewHolder extends RecyclerView.ViewHolder {
            final View mView;
            final ImageView mImageView;
            Prenda mItem;

            //Constructor. Establece la vista que recibe y busca los TextView
            ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (ImageView) view.findViewById(R.id.prenda_list_image);
 //               mTextView = (TextView) view.findViewById(R.id.prenda_list_text);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mView.getId() + "'";
            }
        }

    }

    /**
     * Muestra los detalles de una prenda
     */
    public void mostrarDetalles(Prenda prenda){
        //Creamos el fragmento
        PrendaDetailFragment pd = new PrendaDetailFragment();

        //Creamos y llenamos el bundle con los datos de la prenda
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.prenda_bundle_key),prenda);
        //Pasamos el bundle al fragment
        pd.setArguments(bundle);

        //Utilizamos el metodo de cambio de fragmento del fragmento padre
        ((BaseContainerFragment)getParentFragment()).replaceFragment(pd, true);
    }


    /**
     * Clase listener para los botones de añadir conjunto
     */
    public class AddButtonListener implements View.OnClickListener{

        private TipoParteConjunto tpc;

        /**
         * Crea el listener para un tipoParteConjunto concreto
         * @param tpc TipoParteConjunto
         */
        public AddButtonListener(TipoParteConjunto tpc){
            this.tpc = tpc;
        }

        @Override
        public void onClick(View v) {
            //Creamos una prenda vacia y le establecemos el tipoParteConjunto
            Prenda prenda = new Prenda();
            prenda.setTipoParteConjunto(tpc);
            mostrarDetalles(prenda);
        }
    }


}