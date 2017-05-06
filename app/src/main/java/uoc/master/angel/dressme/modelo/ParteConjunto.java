package uoc.master.angel.dressme.modelo;


import java.io.Serializable;

/**
 * Created by angel on 30/03/2017.
 */

public class ParteConjunto implements Serializable {
    private int id;
    private TipoParteConjunto tipoParteConjunto;
    private Prenda prendaAsignada;


    public ParteConjunto(int id, TipoParteConjunto tipoParteConjunto, Prenda prendaAsignada){
        this.id = id;
        this.tipoParteConjunto = tipoParteConjunto;
        this.prendaAsignada = prendaAsignada;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TipoParteConjunto getTipoParteConjunto() {
        return tipoParteConjunto;
    }

    public void setTipoParteConjunto(TipoParteConjunto tipoParteConjunto) {
        this.tipoParteConjunto = tipoParteConjunto;
    }

    public Prenda getPrendaAsignada() {
        return prendaAsignada;
    }

    public void setPrendaAsignada(Prenda prendaAsignada) {
        this.prendaAsignada = prendaAsignada;
    }
}
