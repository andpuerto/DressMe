package uoc.master.angel.dressme.fragment;


import android.graphics.Bitmap;
import android.os.Bundle;
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
import uoc.master.angel.dressme.db.DiaDA;
import uoc.master.angel.dressme.db.TipoParteConjuntoDA;
import uoc.master.angel.dressme.fragment.container.BaseContainerFragment;
import uoc.master.angel.dressme.modelo.Conjunto;
import uoc.master.angel.dressme.modelo.Dia;
import uoc.master.angel.dressme.modelo.ParteConjunto;
import uoc.master.angel.dressme.modelo.TipoParteConjunto;
import uoc.master.angel.dressme.util.ImageUtil;


/**
 * Created by angel on 29/03/2017.
 */

public class PlanificarConjuntosListFragment extends Fragment {
    //Lista con todos los tipos de partes de conjunto
    private List<TipoParteConjunto> tiposParteConjunto = new ArrayList<>();
    //Dia para el que se esta seleccionando un conjunto
    private Dia dia;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Obtenemos la lista de TipoParteConjunto de la base de datos
        tiposParteConjunto = new TipoParteConjuntoDA(this.getContext()).getAllTipoParteConjunto();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Realizamos las inicializaciones en onCreateView en lugar de onCreate porque
        //si no, no se puede acceder a los elementos de la vista.
        View rootView = inflater.inflate(R.layout.planificar_conjuntos_list, container, false);

        //Obtenemos el dia seleccionado del bundle
        if (getArguments() != null) {
            Bundle bundle = this.getArguments();
            dia = (Dia) bundle.getSerializable(getString(R.string.dia_bundle_key));
        }

        //Iniciamos las vistas
        this.setViews(rootView);

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    /**
     * Establece las vistas generales para este fragmento
     *
     * @param rootView vista
     */
    private void setViews(View rootView) {
        //Establecemos el adaptador del recycleView
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.planificar_conjuntos_recycle_view);
        ConjuntosListAdapter adapter = new ConjuntosListAdapter(
                new ConjuntoDA(getContext()).getAllConjuntos());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
    }


    /**
     * Asigna el conjunto que recibe al dia que estamos editando
     *
     * @param conjunto el conjunto a asignar
     */
    private void asignarConjunto(Conjunto conjunto) {
        //Asignamos el conjunto seleccionado al dia
        dia.setConjuntoAsignado(conjunto);
        //Guardamos el dia con los cambios, solamente si el dia es nuevo. Si no, delegamos en la
        //pantalla anterior para guardar los cambios
        if (dia.getId() < 0) {
            new DiaDA(getContext()).saveDia(dia);
        }
        //volvemos a la vista anterior
        ((BaseContainerFragment) getParentFragment()).popFragment();
    }


    /**
     * Clase interna para el adaptador del RecyclerView
     */
    public class ConjuntosListAdapter extends RecyclerView.Adapter<PlanificarConjuntosListFragment.ConjuntosListAdapter.ViewHolder> {
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
//        }

        @Override
        public PlanificarConjuntosListFragment.ConjuntosListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            //Segun el tipo de vista que recibamos como parametro, inflamos el layout
            //para los items pares o para los impares
            switch (viewType) {
                case PlanificarConjuntosListFragment.ConjuntosListAdapter.EVEN:
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.list_item_conjunto_par, parent, false);
                    break;
                case PlanificarConjuntosListFragment.ConjuntosListAdapter.ODD:
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.list_item_conjunto_impar, parent, false);
                    break;
            }
            //Devolvemos un ViewHolder con la vista que tendra el layout adecuado
            return new PlanificarConjuntosListFragment.ConjuntosListAdapter.ViewHolder(view);
        }


        @Override
        public void onBindViewHolder(final PlanificarConjuntosListFragment.ConjuntosListAdapter.ViewHolder holder, final int position) {
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

                    asignarConjunto(holder.mItem);
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
