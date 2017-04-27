package uoc.master.angel.dressme.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import uoc.master.angel.dressme.R;
import uoc.master.angel.dressme.db.PrendaDA;
import uoc.master.angel.dressme.db.TipoParteConjuntoDA;
import uoc.master.angel.dressme.fragment.container.BaseContainerFragment;
import uoc.master.angel.dressme.modelo.Prenda;
import uoc.master.angel.dressme.modelo.TipoParteConjunto;
import uoc.master.angel.dressme.util.ImageUtil;

/**
 * Created by angel on 29/03/2017.
 */

public class PrendasListFragment extends Fragment {

    //Adaptadores para cada una de las listas de prendas, una por cada tipo de parte de conjunto
    private ArrayList<PrendasListAdapter> adapters = new ArrayList<>();

    //Lista con todos los tipos de partes de conjunto
    List<TipoParteConjunto> tiposParteConjunto = new ArrayList<>();

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = this.getContext();
        //Obtener la lista de TipoParteConjunto de la base de datos
        tiposParteConjunto = new TipoParteConjuntoDA(this.getContext()).getAllTipoParteConjunto();
        //Crear tantos adaptadores como TipoParteConjunto haya
        for(TipoParteConjunto tpc : tiposParteConjunto){
            //Creamos un nuevo adapter, con un array vacio
            PrendasListAdapter adapterTemp = new PrendasListAdapter(new ArrayList<Prenda>());
            //Establecemos los elementos del adapter con las prendas obtenidas de la consulta
            //Buscaremos la prendas del TipoParteConjunto actual
            adapterTemp.setItems(new PrendaDA(this.getContext()).getAllPrendas(tpc, false));
            //Agregamos el adapter a la lista de adapters
            adapters.add(adapterTemp);
        }

      }


    //Este metodo establece las vistas iniciales de esta actividad
    private void setViews() {


        //Vamos a establecer los adaptadores a cada RecycleView
        //En esta version, lo hacemos de forma estatica. Para hacerlo generico habria que crea cada
        //recycleview aquí por programa en lugar de tenerlo en el layout
        RecyclerView recyclerView = (RecyclerView) this.getView().findViewById(R.id.prendas_recycle_view1);
        recyclerView.setAdapter(adapters.get(0));
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));

        recyclerView = (RecyclerView) this.getView().findViewById(R.id.prendas_recycle_view2);
        recyclerView.setAdapter(adapters.get(1));
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));

        recyclerView = (RecyclerView) this.getView().findViewById(R.id.prendas_recycle_view3);
        recyclerView.setAdapter(adapters.get(2));
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));

        recyclerView = (RecyclerView) this.getView().findViewById(R.id.prendas_recycle_view4);
        recyclerView.setAdapter(adapters.get(3));
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



        //************************************
        //Establecemos el listener para el refreshLayout
        //final SwipeRefreshLayout swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
//        swipeContainer.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                //Al refrescar, agregamos el valueEventListener de nuevo
//                //El propio listener quitara el estado de actualizando del swipe, ya que se
//                //debe hacer una vez esten realmente actualizados los datos
//                database.getReference().addValueEventListener(new BookListListener());
//            }
//        });
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