package uoc.master.angel.dressme.modelo;

import java.util.ArrayList;

/**
 * Created by angel on 30/03/2017.
 */

public class Prenda {
    private int id;
    private String foto;
    private String marca;
    private String material;
    private ColorPrenda color;
    private ArrayList<Clima> climasAdecuados;
    //Indica la parte del conjunto para la que es válida la prenda
    private TipoParteConjunto tipoParteConjunto;
}
