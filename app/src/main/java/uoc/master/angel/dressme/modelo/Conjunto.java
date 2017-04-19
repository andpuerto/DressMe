package uoc.master.angel.dressme.modelo;

import android.util.SparseArray;

import java.util.HashMap;


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

    /**
     * Inserta una prenda si combina todas las demas del conjunto
     * @param prenda Prenda a insertar
     * @param tpc TipoParteConjunto donde insertar
     * @param combinaciones SparseArray que contenga todos los colores. Las claves
     *                      seran los id de los colores. Contendra objetos ColorPrenda
     *                      cuyas listas de colores combinados esten rellenas
     * @return true si se ha insertado la prenda
     */
    public boolean insertPrendaSiCombina(Prenda prenda, TipoParteConjunto tpc,
                                   SparseArray<ColorPrenda> combinaciones){
        //Antes de nada, comprobamos que no haya ya una ParteConjunto de ese tipo insertada
        if(partesConjunto.get(tpc.getId()) != null){
            return false;
        }
        boolean combina = true;
        //Recorremos las prendas asignadas a las ParteConjunto y, para cada una de ellas, comprobamos
        //si las prendas asignadas combinan con el color de la prenda a insertar
        for(int i=0; i<partesConjunto.size() && combina; i++){
            Prenda prendaBase = partesConjunto.valueAt(i).getPrendaAsignada();
            if (combinaciones.get(prendaBase.getColor().getId()).
                    getColoresCombinados().get(prenda.getId()) == null) {
                combina = false;
            }
        }
        //Solamente insertaremos el elemento si todas las prendas ya asignadas combinan con la nueva
        if(combina){
            //Insertamos poniendo como clave el id del tipo. Como la parteconjunto es nueva, le
            //asignamos id=-1
            partesConjunto.append(tpc.getId(), new ParteConjunto(-1, tpc, prenda));
        }
        return combina;
    }
}
