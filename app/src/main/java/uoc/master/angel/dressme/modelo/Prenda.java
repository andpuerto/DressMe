package uoc.master.angel.dressme.modelo;


import java.io.Serializable;
import java.util.List;

/**
 * Created by angel on 30/03/2017.
 */

public class Prenda implements Serializable{
    private int id;
    private byte[] foto;
    private String marca;
    private String material;
    private ColorPrenda color;
    private transient List<Clima> climasAdecuados;
    //Indica la parte del conjunto para la que es válida la prenda
    private TipoParteConjunto tipoParteConjunto;

    public Prenda(){

    }

    public Prenda(int id, byte[] foto, String marca, String material, ColorPrenda color, List<Clima> climas,
                  TipoParteConjunto tipoParteConjunto){
        this.id = id;
        this.foto = foto;
        this.marca = marca;
        this.material = material;
        this.color = color;
        this.climasAdecuados = climas;
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
}
