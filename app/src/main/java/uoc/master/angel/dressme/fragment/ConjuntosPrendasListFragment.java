package uoc.master.angel.dressme.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import uoc.master.angel.dressme.R;

import uoc.master.angel.dressme.db.da.ClimaDA;
import uoc.master.angel.dressme.db.da.ColorPrendaDA;
import uoc.master.angel.dressme.db.da.PrendaDA;

import uoc.master.angel.dressme.db.da.UsoDA;
import uoc.master.angel.dressme.fragment.container.BaseContainerFragment;
import uoc.master.angel.dressme.modelo.Clima;
import uoc.master.angel.dressme.modelo.ColorPrenda;
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


    //El tipoParteConjunto para el que queremos obtener las prendas
    private TipoParteConjunto tipoParteConjunto;

    //El conjunto que estamos editando
    private Conjunto conjunto;

    //RecycleView con las prendas
    private RecyclerView recyclerView;

    //Arrays con los elementos de filtro y arrays con los nombres para los Spinner
    private List<Uso> usos = new ArrayList<>();
    private String[] usosStrings;
    private List<Clima> climas = new ArrayList<>();
    private String[] climasStrings;
    private List<ColorPrenda> colores = new ArrayList<>();
    private String[] coloresStrings;

    //Elementos seleccionados para los spinners
    private int usoSeleccionado;
    private int climaSeleccionado;
    private int colorSeleccionado;

    //Booleans para evitar que los spinneractualicen las vistas al cargarse
    private boolean climaInicializado = false;
    private boolean usoInicializado = false;
    private boolean colorInicializado = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Obtenemos la lista de usos
        usos = new UsoDA(getContext()).getAllUso();
        //Añadimos un uso con un valor null para hacer referencia a todos los usos
        usos.add(0, null);
        //Creamos la lista de las strings de los usos
        usosStrings = new String[usos.size()];
        //Insertamos un primer elemento en el array con la opcion por defectop
        usosStrings[0] = getString(R.string.todos);
        //Insertamos el resto de elementos en el array
        for (int i = 1; i < usos.size(); i++) {
            usosStrings[i] = usos.get(i).getNombre();
        }

        //Hacemos lo mismo con las otras dos listas
        climas = new ClimaDA(getContext()).getAllClima();
        climas.add(0, null);
        climasStrings = new String[climas.size()];
        climasStrings[0] = getString(R.string.todos);
        for (int i = 1; i < climas.size(); i++) {
            climasStrings[i] = climas.get(i).getNombre();
        }

        colores = new ColorPrendaDA(getContext()).getAllColorPrenda();
        colores.add(0, null);
        coloresStrings = new String[colores.size()];
        coloresStrings[0] = getString(R.string.todos);
        for (int i = 1; i < colores.size(); i++) {
            coloresStrings[i] = colores.get(i).getNombre();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Realizamos las inicializaciones en onCreateView en lugar de onCreate porque
        //si no, no se puede acceder a los elementos de la vista.
        View rootView = inflater.inflate(R.layout.conjuntos_prendas_list, container, false);

        //Obtenemos los datos del tipoParteConjunto y el conjunto, en caso de tenerlos
        //En principio, siempre se deberia obtener un tipoParteConjunto, pero lo comprobamos
        if (getArguments() != null) {
            Bundle bundle = this.getArguments();
            tipoParteConjunto = (TipoParteConjunto) bundle.getSerializable(getString(
                    R.string.tipo_parte_conjunto_bundle_key));
            conjunto = (Conjunto) bundle.getSerializable(getString(R.string.conjunto_bundle_key));
        }
        setViews(rootView);
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    /**
     * Establece las vistas iniciales del fragmento
     * @param rootView la vista
     */
    private void setViews(View rootView) {
        //Ponemos a false los boolean de inicializado. Servira para evitar que se ejecuten
        //los eventos de los spinners al inicializar la vista;
        climaInicializado = usoInicializado = colorInicializado = false;
        //Establecemos el adaptador del recycleView
        recyclerView = (RecyclerView) rootView.findViewById(
                R.id.conjuntos_prendas_recycle_view);
        //Agregamos un elemento null al comienzo de la lista. Esto proporcionara al usuario la opcion
        //de desasignar la prenda o no seleccionar ninguna
        //prendas.add(0, null);
        ConjuntosPrendasListAdapter adapter = new ConjuntosPrendasListAdapter(new ArrayList<Prenda>());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        updateListView();

        //Establecemos los spinners
        Spinner usoSpinner = (Spinner) rootView.findViewById(R.id.conjuntos_prendas_usos_spinner);
        Spinner climaSpinner = (Spinner) rootView.findViewById(R.id.conjuntos_prendas_climas_spinner);
        Spinner colorSpinner = (Spinner) rootView.findViewById(R.id.conjuntos_prendas_colores_spinner);

        //Adapter y listener para los spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, usosStrings);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        usoSpinner.setAdapter(spinnerAdapter);
        usoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Filtramos los elementos
                usoSeleccionado = position;
                if(usoInicializado) {
                    updateListView();
                } else {
                    usoInicializado = true;
                }
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
                climaSeleccionado = position;
                if(climaInicializado) {
                    updateListView();
                } else {
                    climaInicializado = true;
                }
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
                colorSeleccionado = position;
                if(colorInicializado) {
                    updateListView();
                } else {
                    colorInicializado = true;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * Actualiza la lista
     */
    private void updateListView() {
        PrendaDA prendaDA = new PrendaDA(getContext());

        //Obtenemos la lista de prendas para la parteConjunto correspondiente, considerando
        //los parametros seleccionados en lso filtros
        List<Prenda> prendas = prendaDA.getAllPrendas(
                usos.get(usoSeleccionado), climas.get(climaSeleccionado),
                colores.get(colorSeleccionado), tipoParteConjunto, true);
        //Agregamos un elemento null al comienzo de la lista. Esto proporcionara al usuario la opcion
        //de desasignar la prenda o no seleccionar ninguna
        prendas.add(0, null);
        ConjuntosPrendasListAdapter adapter = (ConjuntosPrendasListAdapter) recyclerView.getAdapter();
        adapter.setItems(prendas);
        //Notificamos los cambios para que cambie la lista
        adapter.notifyDataSetChanged();
    }


    /**
     * Asigna la prenda al conjunto y vuelve a la pantalla de detalles del conjunto
     *
     * @param prenda La prenda seleccionada
     */
    public void asignarPrenda(Prenda prenda) {

        //Intentamos obtener la parte conjunto para el tipoParteConjunto que estamos editando
        //Si existe, cambiamos la asignacion de la prenda. Si no, insertamos una parteConjunto nueva
        //con la prenda
        ParteConjunto pc = conjunto.getPartesConjunto().get(tipoParteConjunto.getId());
        //Tambien hay que considerar si la prenda el null. En ese caso, el usuario, ha seleccionado
        //que no quiere ninguna prenda para esa parte.
        //Si la parteConjunto es null y la prenda tambien no la insertamos
        if (pc == null) {
            if (prenda != null) {
                pc = new ParteConjunto(-1, tipoParteConjunto, prenda);
                conjunto.getPartesConjunto().put(tipoParteConjunto.getId(), pc);
            }
        } else {
            //Si ya existe la parteConjunto y la prenda no es null, se la asignamos
            if (prenda != null) {
                pc.setPrendaAsignada(prenda);
            } else {
                //Si la prenda es null, el usuario quiere desasignarla, asi que la eliminamos
                conjunto.getPartesConjunto().remove(tipoParteConjunto.getId());
            }
        }

        //Utilizamos el metodo de cambio de fragmento del fragmento padre
        ((BaseContainerFragment) getParentFragment()).popFragment();
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
        void setItems(List<Prenda> items) {
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
            if (holder.mItem == null) {
                holder.mNoPrendaTextView.setVisibility(View.VISIBLE);
                holder.mContentLayout.setVisibility(View.GONE);
                return;
            }

            //Establecemos la imagen
            holder.mImageView.setImageBitmap(ImageUtil.toBitmap(holder.mItem.getFoto()));
            //Establecemos el color del cuadro
            holder.mColorImageView.setBackgroundColor(
                    Color.parseColor("#" + holder.mItem.getColor().getRgb()));
            //Establecemos el nombre del color
            holder.mColorTextView.setText(holder.mItem.getColor().getNombre());
            //Establecemos la lista de climas y de usos como valores separados por comas
            String values = "";
            List<Clima> climas = holder.mItem.getClimasAdecuados();
            if (climas != null && climas.size() > 0) {
                values += climas.get(0).getNombre();
                for (int i = 1; i < climas.size(); i++) {
                    values += ", " + climas.get(i).getNombre();
                }
            }
            holder.mClimasTextView.setText(values);
            //Ahora la de usos
            values = "";
            List<Uso> usos = holder.mItem.getUsosAdecuados();
            if (usos != null && usos.size() > 0) {
                values += usos.get(0).getNombre();
                for (int i = 1; i < usos.size(); i++) {
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
