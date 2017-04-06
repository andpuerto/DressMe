package uoc.master.angel.dressme.fragment.container;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uoc.master.angel.dressme.fragment.PrendasListFragment;
import uoc.master.angel.dressme.R;

/**
 * Created by angel on 06/04/2017.
 */

public class PrendaContainerFragment extends BaseContainerFragment {
    //Para controlar que solo se inicie la vista una vez
    private boolean mIsViewInited;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Log.e("test", "tab 1 oncreateview");
        //En todas las clases ContainerFragment, iniciamos primero el container_fragment
        return inflater.inflate(R.layout.container_fragment, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Log.e("test", "tab 1 container on activity created");
        //Iniciamos la vista "real". Nos aseguramos de que solo se inicie una vez
        if (!mIsViewInited) {
            mIsViewInited = true;
            initView();
        }
    }

    /**
     * Inserta el fragmento que realmente se quiere tener en primer lugar en la pestaña
     * sobre la vista contenedora
     * Solo debe hacerse una vez, puesto que después pueden haberse hecho transiciones a otros
     * fragmentos sobre la misma pestaña.
     */
    private void initView() {
        //Log.e("test", "tab 1 init view");
        replaceFragment(new PrendasListFragment(), false);
    }
}
