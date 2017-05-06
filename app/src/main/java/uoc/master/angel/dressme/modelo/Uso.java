package uoc.master.angel.dressme.modelo;

import java.io.Serializable;

/**
 * Created by angel on 01/04/2017.
 */

public class Uso implements Serializable{
    private int id;
    private String nombre;


    public Uso(int id, String nombre){
        this.id = id;
        this.nombre = nombre;
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
}
