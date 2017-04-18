package uoc.master.angel.dressme.modelo;

import android.util.SparseArray;


/**
 * Created by angel on 30/03/2017.
 */

public class Conjunto {
    private int id;
    //Lo hacemos como un sparse array para poder acceder a ellas por el tipoParteConjunto
    private SparseArray<ParteConjunto> partesConjunto;

    public Conjunto(){
        this.partesConjunto = new SparseArray<>();
    }

    public Conjunto(int id, SparseArray<ParteConjunto> partesConjunto){
        this.id = id;
        this.partesConjunto = partesConjunto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SparseArray<ParteConjunto> getPartesConjunto() {
        return partesConjunto;
    }

    public void setPartesConjunto(SparseArray<ParteConjunto> partesConjunto) {
        this.partesConjunto = partesConjunto;
    }

    /**
     *
     * @return Numero de ParteConjunto asignadas al conjunto
     */
    public int getNumeroPartesConjunto(){
        return partesConjunto==null ? 0 : partesConjunto.size();
    }
}
