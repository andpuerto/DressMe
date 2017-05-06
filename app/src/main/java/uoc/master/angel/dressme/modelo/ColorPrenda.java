package uoc.master.angel.dressme.modelo;


import java.io.Serializable;
import java.util.HashMap;


/**
 * Created by angel on 30/03/2017.
 */

public class ColorPrenda implements Serializable{
    private int id;
    private String nombre;
    private String rgb;
    //Los colores combinados los almacenamos en un hashmap por su id para un acceso mas rapido
    private HashMap<Integer,ColorPrenda> coloresCombinados;

    public ColorPrenda(int id, String nombre, String rgb, HashMap<Integer, ColorPrenda> coloresCombinados){
        this.id = id;
        this.nombre = nombre;
        this.rgb = rgb;
        this.coloresCombinados = coloresCombinados;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRgb() {
        return rgb;
    }

    public void setRgb(String rgb) {
        this.rgb = rgb;
    }

    public HashMap<Integer,ColorPrenda> getColoresCombinados() {
        return coloresCombinados;
    }

    public void setColoresCombinados(HashMap<Integer, ColorPrenda> coloresCombinados) {
        this.coloresCombinados = coloresCombinados;
    }


    //Sobrescribimos el metodo equals para comparaciones y busquedas en listas
    @Override
    public boolean equals(Object v) {
        boolean retVal = false;

        if (v instanceof ColorPrenda){
            ColorPrenda ptr = (ColorPrenda) v;
            retVal = ptr.getId() == this.id;
        }

        return retVal;
    }
}
