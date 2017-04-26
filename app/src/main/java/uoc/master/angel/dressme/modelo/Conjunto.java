package uoc.master.angel.dressme.modelo;


import java.io.Serializable;
import java.util.HashMap;


/**
 * Created by angel on 30/03/2017.
 */

public class Conjunto implements Serializable{
    private int id;
    //Lo hacemos como un hashmap array para poder acceder a ellas por el tipoParteConjunto
    private HashMap<Integer,ParteConjunto> partesConjunto;

    public Conjunto(){
        this.partesConjunto = new HashMap<>();
    }

    public Conjunto(int id, HashMap<Integer,ParteConjunto> partesConjunto){
        this.id = id;
        if(partesConjunto != null) {
            this.partesConjunto = partesConjunto;
        }else{
            this.partesConjunto = new HashMap<>();
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public HashMap<Integer,ParteConjunto> getPartesConjunto() {
        return partesConjunto;
    }

    public void setPartesConjunto(HashMap<Integer,ParteConjunto> partesConjunto) {
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
                                   HashMap<Integer,ColorPrenda> combinaciones){
        //Antes de nada, comprobamos que no haya ya una ParteConjunto de ese tipo insertada
        if(partesConjunto.get(tpc.getId()) != null){
            return false;
        }
        boolean combina = true;
        ParteConjunto[] partesConjuntoValues = partesConjunto.values().toArray(new ParteConjunto[0]);
        //Recorremos las prendas asignadas a las ParteConjunto y, para cada una de ellas, comprobamos
        //si las prendas asignadas combinan con el color de la prenda a insertar
        for(int i=0; i<partesConjuntoValues.length && combina; i++){
            Prenda prendaBase = partesConjuntoValues[i].getPrendaAsignada();
            if (combinaciones.get(prendaBase.getColor().getId()).
                    getColoresCombinados().get(prenda.getId()) == null) {
                combina = false;
            }
        }
        //Solamente insertaremos el elemento si todas las prendas ya asignadas combinan con la nueva
        if(combina){
            //Insertamos poniendo como clave el id del tipo. Como la parteconjunto es nueva, le
            //asignamos id=-1
            partesConjunto.put(tpc.getId(), new ParteConjunto(-1, tpc, prenda));
        }
        return combina;
    }
}
