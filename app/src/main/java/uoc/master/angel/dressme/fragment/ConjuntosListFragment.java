package uoc.master.angel.dressme.fragment;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uoc.master.angel.dressme.R;
import uoc.master.angel.dressme.db.ConjuntoDA;
import uoc.master.angel.dressme.db.TipoParteConjuntoDA;
import uoc.master.angel.dressme.fragment.container.BaseContainerFragment;
import uoc.master.angel.dressme.modelo.Conjunto;
import uoc.master.angel.dressme.modelo.ParteConjunto;
import uoc.master.angel.dressme.modelo.TipoParteConjunto;
import uoc.master.angel.dressme.util.ImageUtil;

/**
 * Created by angel on 29/03/2017.
 */

public class ConjuntosListFragment extends Fragment {


    //Lista con todos los tipos de partes de conjunto
    private List<TipoParteConjunto> tiposParteConjunto = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Obtenemos la lista de TipoParteConjunto de la base de datos
        tiposParteConjunto = new TipoParteConjuntoDA(this.getContext()).getAllTipoParteConjunto();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.conjuntos_list, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.setViews();
    }


    /**
     * Establece las vistas iniciales del fragmento
     */
    private void setViews() {
        //Establecemos el adaptador del recycleView
        RecyclerView recyclerView = (RecyclerView) this.getView().findViewById(R.id.conjuntos_recycle_view);
        ConjuntosListAdapter adapter = new ConjuntosListAdapter(
                new ConjuntoDA(getContext()).getAllConjuntos());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));


        //Boton de añadir nuevo conjunto
        FloatingActionButton newConjuntoButton = (FloatingActionButton) this.getView().
                findViewById(R.id.new_conjunto_button);
        newConjuntoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDetalles(new Conjunto(-1, null));
            }
        });
        //Establecemos el clicklistener para el boton de crear nuevo conjunto
//        (this.getView().findViewById(R.id.prendas_add_button1)).setOnClickListener(
//                new PrendasListFragment.AddButtonListener(tiposParteConjunto.get(0)));


    }


    /**
     * Muestra los detalles de un conjunto
     */
    public void mostrarDetalles(Conjunto conjunto) {
        //Creamos el fragmento
        ConjuntoDetailFragment cd = new ConjuntoDetailFragment();

        //Creamos y llenamos el bundle con los datos del conjunto
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.conjunto_bundle_key), conjunto);
        //Pasamos el bundle al fragment
        cd.setArguments(bundle);

        //Utilizamos el metodo de cambio de fragmento del fragmento padre
        ((BaseContainerFragment) getParentFragment()).replaceFragment(cd, true);
    }


    /**
     * Clase interna para el adaptador del RecyclerView
     */
    public class ConjuntosListAdapter extends RecyclerView.Adapter<ConjuntosListFragment.ConjuntosListAdapter.ViewHolder> {
        //Constantes indicando los valores de par e impar
        private final static int EVEN = 0;
        private final static int ODD = 1;
        //Lista de conjuntos
        private List<Conjunto> mValues;

        //Constructor. Recibe unicamente la lista de conjuntos
        ConjuntosListAdapter(List<Conjunto> items) {
            mValues = items;
        }

//        //Actualizamos la lista con los datos recibidos
//        public void setItems(List<Conjunto> items) {
//            mValues = items;
//
//
//        }

        @Override
        public ConjuntosListFragment.ConjuntosListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            //Segun el tipo de vista que recibamos como parametro, inflamos el layout
            //para los items pares o para los impares
            switch (viewType) {
                case ConjuntosListFragment.ConjuntosListAdapter.EVEN:
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.list_item_conjunto_par, parent, false);
                    break;
                case ConjuntosListFragment.ConjuntosListAdapter.ODD:
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.list_item_conjunto_impar, parent, false);
                    break;
            }
            //Devolvemos un ViewHolder con la vista que tendra el layout adecuado
            return new ConjuntosListFragment.ConjuntosListAdapter.ViewHolder(view);
        }


        @Override
        public void onBindViewHolder(final ConjuntosListFragment.ConjuntosListAdapter.ViewHolder holder, final int position) {
            //Establecemos los valores del viewHolder
            //Tomamos el elemento de la lista usando la posicion recibida como parametro
            //Tomamos la posicion. Podria hacerse directamente usando el parametro position pero da
            //lugar a un warning y el compilador no lo recomienda porque la posicion puede cambiar
            holder.mItem = mValues.get(position);
            //Establecemos las imagenes
            Bitmap[] bms = new Bitmap[tiposParteConjunto.size()];
            HashMap<Integer, ParteConjunto> pcs = holder.mItem.getPartesConjunto();
            for (int i = 0; i < tiposParteConjunto.size(); i++) {
                bms[i] = pcs.get(i) != null && pcs.get(i).getPrendaAsignada() != null ?
                        ImageUtil.toBitmap(pcs.get(i).getPrendaAsignada().getFoto()) :
                        null;
            }

            holder.mImageView1.setImageBitmap(bms[0]);
            holder.mImageView2.setImageBitmap(bms[1]);
            holder.mImageView3.setImageBitmap(bms[2]);
            holder.mImageView4.setImageBitmap(bms[3]);


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
            final ImageView mImageView1;
            final ImageView mImageView2;
            final ImageView mImageView3;
            final ImageView mImageView4;
            Conjunto mItem;

            //Constructor. Establece la vista que recibe
            ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView1 = (ImageView) view.findViewById(R.id.conjunto_list_image1);
                mImageView2 = (ImageView) view.findViewById(R.id.conjunto_list_image2);
                mImageView3 = (ImageView) view.findViewById(R.id.conjunto_list_image3);
                mImageView4 = (ImageView) view.findViewById(R.id.conjunto_list_image4);

            }

            @Override
            public String toString() {
                return super.toString() + " '" + mView.getId() + "'";
            }
        }

    }

}
