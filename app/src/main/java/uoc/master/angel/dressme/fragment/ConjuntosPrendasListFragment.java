package uoc.master.angel.dressme.fragment;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uoc.master.angel.dressme.R;

import uoc.master.angel.dressme.db.PrendaDA;

import uoc.master.angel.dressme.fragment.container.BaseContainerFragment;
import uoc.master.angel.dressme.modelo.Clima;
import uoc.master.angel.dressme.modelo.Conjunto;

import uoc.master.angel.dressme.modelo.ParteConjunto;
import uoc.master.angel.dressme.modelo.Prenda;
import uoc.master.angel.dressme.modelo.TipoParteConjunto;
import uoc.master.angel.dressme.modelo.Uso;
import uoc.master.angel.dressme.util.ImageUtil;

/**
 * Created by angel on 29/03/2017.
 */

public class ConjuntosPrendasListFragment extends Fragment {


    //Lista con todos los tipos de partes de conjunto
    private List<TipoParteConjunto> tiposParteConjunto = new ArrayList<>();

    //El tipoParteConjunto para el que queremos obtener las prendas
    private TipoParteConjunto tipoParteConjunto;

    //El conjunto que estamos editando
    private Conjunto conjunto;

    @Override

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //Obtenemos la lista de TipoParteConjunto de la base de datos
        //tiposParteConjunto = new TipoParteConjuntoDA(this.getContext()).getAllTipoParteConjunto();

    }



    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Realizamos las inicializaciones en onCreateView en lugar de onCreate porque
        //si no, no se puede acceder a los elementos de la vista.
        View rootView =  inflater.inflate(R.layout.conjuntos_prendas_list, container, false);

        //Obtenemos los datos del tipoParteConjunto y el conjunto, en caso de tenerlos
        //En principio, siempre se deberia obtener un tipoParteConjunto, pero lo comprobamos
        if(getArguments() != null){
            Bundle bundle = this.getArguments();
            tipoParteConjunto = (TipoParteConjunto) bundle.getSerializable(getString(
                    R.string.tipo_parte_conjunto_bundle_key));
            conjunto = (Conjunto) bundle.getSerializable(getString(R.string.conjunto_bundle_key));
        }

        setViews(rootView);

        return rootView;

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }


    //Este metodo establece las vistas iniciales de esta actividad
    private void setViews(View rootView) {

        //Establecemos el adaptador del recycleView
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(
                R.id.conjuntos_prendas_recycle_view);
        //Obtenemos las prendas
        List<Prenda> prendas = new PrendaDA(getContext()).getAllPrendas(tipoParteConjunto, true);
        //Agregamos un elemento null al comienzo de la lista. Esto proporcionara al usuario la opcion
        //de desasignar la prenda o no seleccionar ninguna
        prendas.add(0, null);
        ConjuntosPrendasListAdapter adapter = new ConjuntosPrendasListAdapter(prendas);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));

    }



    /**
     * Asigna la prenda al conjunto y vuelve a la pantalla de detalles del conjunto
     * @param prenda La prenda seleccionada
     */
    public void asignarPrenda(Prenda prenda){

        //Intentamos obtener la parte conjunto para el tipoParteConjunto que estamos editando
        //Si existe, cambiamos la asignacion de la prenda. Si no, insertamos una parteConjunto nueva
        //con la prenda
        ParteConjunto pc = conjunto.getPartesConjunto().get(tipoParteConjunto.getId());
        //Tambien hay que considerar si la prenda el null. En ese caso, el usuario, ha seleccionado
        //que no quiere ninguna prenda para esa parte.
        //Si la parteConjunto es null y la prenda tambien no la insertamos
        if(pc == null){
            if(prenda != null) {
                pc = new ParteConjunto(-1, tipoParteConjunto, prenda);
                conjunto.getPartesConjunto().put(tipoParteConjunto.getId(), pc);
            }
        }else{
            //Si ya existe la parteConjunto y la prenda no es null, se la asignamos
            if(prenda != null) {
                pc.setPrendaAsignada(prenda);
            }else{
                //Si la prenda es null, el usuario quiere desasignarla, asi que la eliminamos
                conjunto.getPartesConjunto().remove(tipoParteConjunto.getId());
            }
        }

//        //Creamos el fragmento
//        ConjuntoDetailFragment cd = new ConjuntoDetailFragment();
//
//        //Creamos y llenamos el bundle con los datos del conjunto
//        Bundle bundle = new Bundle();
//        bundle.putSerializable(getString(R.string.conjunto_bundle_key),conjunto);
//        //Pasamos el bundle al fragment
//        cd.setArguments(bundle);

        //Utilizamos el metodo de cambio de fragmento del fragmento padre
       // ((BaseContainerFragment)getParentFragment()).replaceFragment(cd, true);
        ((BaseContainerFragment)getParentFragment()).popFragment();
    }



    /**
     * Clase interna para el adaptador del RecyclerView
     */
    public class ConjuntosPrendasListAdapter extends
            RecyclerView.Adapter<ConjuntosPrendasListFragment.ConjuntosPrendasListAdapter.ViewHolder> {
        //Constantes indicando los valores de par e impar
        private final static int EVEN = 0;
        private final static int ODD = 1;
        //Lista de conjuntos
        private List<Prenda> mValues;

        //Constructor. Recibe unicamente la lista de conjuntos
        ConjuntosPrendasListAdapter(List<Prenda> items) {
            mValues = items;
        }

        //Actualizamos la lista con los datos recibidos
        public void setItems(List<Prenda> items) {
            mValues = items;

        }


        @Override
        public ConjuntosPrendasListFragment.ConjuntosPrendasListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            //Segun el tipo de vista que recibamos como parametro, inflamos el layout
            //para los items pares o para los impares
            switch (viewType) {
                case ConjuntosPrendasListFragment.ConjuntosPrendasListAdapter.EVEN:
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.list_item_conjunto_prenda_par, parent, false);
                    break;
                case ConjuntosPrendasListFragment.ConjuntosPrendasListAdapter.ODD:
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.list_item_conjunto_prenda_impar, parent, false);
                    break;
            }
            //Devolvemos un ViewHolder con la vista que tendra el layout adecuado
            return new ConjuntosPrendasListFragment.ConjuntosPrendasListAdapter.ViewHolder(view);
        }


        @Override
        public void onBindViewHolder(final ConjuntosPrendasListFragment.ConjuntosPrendasListAdapter.ViewHolder holder, final int position) {
            //Establecemos los valores del viewHolder
            //Tomamos el elemento de la lista usando la posicion recibida como parametro
            //Tomamos la posicion. Podria hacerse directamente usando el parametro position pero da
            //lugar a un warning y el compilador no lo recomienda porque la posicion puede cambiar
            final int posicion = holder.getAdapterPosition();
            holder.mItem = mValues.get(position);


            //Establecemos el clickListener para el holder, para que cada elemento responda
            //a los clicks
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    asignarPrenda(holder.mItem);
                }

            });


            //Si el elemento es null mostramos solo un texto indicando que no se seleccione la prenda
            if(holder.mItem == null){
                holder.mNoPrendaTextView.setVisibility(View.VISIBLE);
                holder.mContentLayout.setVisibility(View.GONE);
                return;
            }

            //Establecemos la imagen
            holder.mImageView.setImageBitmap(ImageUtil.toBitmap(holder.mItem.getFoto()));
            //Establecemos el color del cuadro
            holder.mColorImageView.setBackgroundColor(
                    Color.parseColor("#"+holder.mItem.getColor().getRgb()));
            //Establecemos el nombre del color
            holder.mColorTextView.setText(holder.mItem.getColor().getNombre());
            //Establecemos la lista de climas y de usos como valores separados por comas
            String values = "";
            List<Clima> climas = holder.mItem.getClimasAdecuados();
            if(climas!=null && climas.size()>0){
                values += climas.get(0).getNombre();
                for(int i=1; i<climas.size(); i++){
                    values += ", " + climas.get(i).getNombre();
                }
            }
            holder.mClimasTextView.setText(values);
            //Ahora la de usos
            values = "";
            List<Uso> usos = holder.mItem.getUsosAdecuados();
            if(usos!=null && usos.size()>0){
                values += usos.get(0).getNombre();
                for(int i=1; i<usos.size(); i++){
                    values += ", " + usos.get(i).getNombre();
                }
            }
            holder.mUsosTextView.setText(values);


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
            final ImageView mColorImageView;
            final TextView mColorTextView;
            final TextView mUsosTextView;
            final TextView mClimasTextView;
            final TextView mNoPrendaTextView;
            final LinearLayout mContentLayout;

            Prenda mItem;

            //Constructor. Establece la vista que recibe
            ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (ImageView) view.findViewById(R.id.conjunto_prenda_image);
                mColorImageView = (ImageView) view.findViewById(R.id.conjunto_prenda_color_square);
                mColorTextView = (TextView) view.findViewById(R.id.conjunto_prenda_color_text);
                mUsosTextView = (TextView) view.findViewById(R.id.conjunto_prenda_usos_text);
                mClimasTextView = (TextView) view.findViewById(R.id.conjunto_prenda_climas_text);
                mNoPrendaTextView = (TextView) view.findViewById(R.id.no_prenda_text);
                mContentLayout = (LinearLayout) view.findViewById(R.id.prenda_chose_layout);

            }

            @Override
            public String toString() {
                return super.toString() + " '" + mView.getId() + "'";
            }
        }

    }

}
