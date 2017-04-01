package uoc.master.angel.dressme;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by angel on 29/03/2017.
 */

public class PrendasList extends Fragment {

    @Override

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }


    //Este metodo establece las vistas iniciales de esta actividad
    private void setViews() {
        //Obtenemos el recyclerView
        RecyclerView recyclerView = (RecyclerView) this.getView().findViewById(R.id.prendas_recycle_view);
        //Establecemos el adaptador para el recyclerView
        //recyclerView.setAdapter(adapter);
        //Creamos un layoutManager para el recyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
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

}