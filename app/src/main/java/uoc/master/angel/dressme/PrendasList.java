package uoc.master.angel.dressme;


import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import uoc.master.angel.dressme.db.PrendaDA;
import uoc.master.angel.dressme.modelo.Prenda;

/**
 * Created by angel on 29/03/2017.
 */

public class PrendasList extends Fragment {

    //Adaptador para la lista de libros
    private PrendasListAdapter adapter = new PrendasListAdapter(new ArrayList<Prenda>());


    @Override

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Context context = this.getContext();
        adapter.setItems(new PrendaDA(this.getContext()).getAllPrendas());
      }


    //Este metodo establece las vistas iniciales de esta actividad
    private void setViews() {

        //Obtenemos el recyclerView
        //RecyclerView recyclerView = (RecyclerView) this.getView().findViewById(R.id.prendas_recycle_view);
        //Establecemos el adaptador para el recyclerView
        //recyclerView.setAdapter(adapter);
        //Creamos un layoutManager para el recyclerView
        //recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        //**********************************
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false);

        RecyclerView recyclerView = (RecyclerView) this.getView().findViewById(R.id.prendas_recycle_view);
        //Establecemos el adaptador para el recyclerView
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
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
        //Lista de libros
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

//            //DE MOMENTO, DEJO LA IMAGEN VACIA
//            holder.mImageView.setText(mValues.get(position).getTitle());
            holder.mTextView.setText("Prenda nº: " + mValues.get(position).getId());

//            //DE MOMENTO, EL CLICK_LISTENER QUEDA COMENTADO
//            //Tambien establecemos el clickListener para el holder, para que cada elemento responda
//            //a los clicks
//            holder.mView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mostrarDetalles(posicion);
//                }
//
//            });
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
            //Almacenamos La vista, titulo autor y el elemento (libro)
            //Dejamos la visibilidad por defecto (package) para poder acceder directamente a los
            //atributos como se muestra en el enunciado de la PEC
            final View mView;
            final ImageView mImageView;
            final TextView mTextView;
            Prenda mItem;

            //Constructor. Establece la vista que recibe y busca los TextView
            ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (ImageView) view.findViewById(R.id.prenda_list_image);
                mTextView = (TextView) view.findViewById(R.id.prenda_list_text);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mTextView.getText() + "'";
            }
        }

    }

}