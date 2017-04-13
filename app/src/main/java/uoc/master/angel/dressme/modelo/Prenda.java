package uoc.master.angel.dressme.modelo;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import uoc.master.angel.dressme.db.UsoDA;

/**
 * Created by angel on 30/03/2017.
 */

public class Prenda implements Serializable{
    private int id;
    private byte[] foto;
    private String marca;
    private String material;
    private ColorPrenda color;
    private List<Clima> climasAdecuados;
    private List<Uso> usosAdecuados;
    //Indica la parte del conjunto para la que es válida la prenda
    private TipoParteConjunto tipoParteConjunto;

    public Prenda(){
        this.id = -1;
    }

    public Prenda(int id, byte[] foto, String marca, String material, ColorPrenda color, List<Clima> climas,
                  List<Uso> usos, TipoParteConjunto tipoParteConjunto){
        this.id = id;
        this.foto = foto;
        this.marca = marca;
        this.material = material;
        this.color = color;
        this.climasAdecuados = climas;
        this.usosAdecuados = usos;
        this.tipoParteConjunto = tipoParteConjunto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getFoto() {
        return foto;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public ColorPrenda getColor() {
        return color;
    }

    public void setColor(ColorPrenda color) {
        this.color = color;
    }

    public List<Clima> getClimasAdecuados() {
        return climasAdecuados;
    }

    public void setClimasAdecuados(List<Clima> climasAdecuados) {
        this.climasAdecuados = climasAdecuados;
    }

    public TipoParteConjunto getTipoParteConjunto() {
        return tipoParteConjunto;
    }

    public void setTipoParteConjunto(TipoParteConjunto tipoParteConjunto) {
        this.tipoParteConjunto = tipoParteConjunto;
    }

    public List<Uso> getUsosAdecuados() {
        return usosAdecuados;
    }

    public void setUsosAdecuados(List<Uso> usosAdecuados) {
        this.usosAdecuados = usosAdecuados;
    }

    /**
     * Devuelve true en caso de que contenga el uso con id idUso en su lista de usos adecuados
     * @param idUso
     * @return
     */
    public boolean hasUso(int idUso){
        if(usosAdecuados == null){
            return false;
        }
        for(Uso uso : usosAdecuados){
            if(uso.getId() == idUso){
                return true;
            }
        }
        return false;
    }


    /**
     * Devuelve true en caso de que contenga el clima con id idClima en su lista de climas adecuados
     * @param idClima
     * @return
     */
    public boolean hasClima(int idClima){
        if(climasAdecuados == null){
            return false;
        }
        for(Clima clima : climasAdecuados){
            if(clima.getId() == idClima){
                return true;
            }
        }
        return false;
    }


    /**
     * Agrega un uso, en caso de no estar ya en la lista
     * @param uso
     */
    public void addUso(Uso uso){
        if(usosAdecuados == null){
            usosAdecuados = new ArrayList<>();
        }
        if(!this.hasUso(uso.getId())) {
            usosAdecuados.add(uso);
        }
    }


    /**
     * Elimina un uso adecuado de la lista a partir de su id
     * @param usoId
     */
    public void removeUso(int usoId){
        if(usosAdecuados != null){
            for(Uso uso : usosAdecuados){
                if(uso.getId() == usoId){
                    usosAdecuados.remove(uso);
                    return;
                }
            }
        }
    }


    /**
     * Agrega un clima, en caso de no estar ya en la lista
     * @param clima
     */
    public void addClima(Clima clima){
        if(climasAdecuados == null){
            climasAdecuados = new ArrayList<>();
        }
        if(!this.hasClima(clima.getId())) {
            climasAdecuados.add(clima);
        }
    }


    /**
     * Elimina un clima adecuado de la lista a partir de su id
     * @param climaId
     */
    public void removeClima(int climaId){
        if(climasAdecuados != null){
            for(Clima clima : climasAdecuados){
                if(clima.getId() == climaId){
                    climasAdecuados.remove(clima);
                    return;
                }
            }
        }
    }


}
