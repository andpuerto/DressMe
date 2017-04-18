package uoc.master.angel.dressme.modelo;

import android.util.SparseArray;

import java.io.Serializable;


/**
 * Created by angel on 30/03/2017.
 */

public class ColorPrenda implements Serializable{
    private int id;
    private String nombre;
    private String rgb;
    //Los colores combinados los almacenamos en un sparse array por su id para un acceso mas rapido
    private SparseArray<ColorPrenda> coloresCombinados;

    public ColorPrenda(){}

    public ColorPrenda(int id, String nombre, String rgb, SparseArray<ColorPrenda> coloresCombinados){
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

    public SparseArray<ColorPrenda> getColoresCombinados() {
        return coloresCombinados;
    }

    public void setColoresCombinados(SparseArray<ColorPrenda> coloresCombinados) {
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
