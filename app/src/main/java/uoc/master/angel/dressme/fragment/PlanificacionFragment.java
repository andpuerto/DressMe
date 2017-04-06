package uoc.master.angel.dressme.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uoc.master.angel.dressme.R;

/**
 * Created by angel on 29/03/2017.
 */

public class PlanificacionFragment extends Fragment {

    @Override

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }



    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,

                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.planifiacion, container, false);

    }

}
