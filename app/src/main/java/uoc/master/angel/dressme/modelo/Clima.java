package uoc.master.angel.dressme.modelo;

import java.io.Serializable;


/**
 * Created by angel on 30/03/2017.
 */

public class Clima implements Serializable{
    private int id;
    private String nombre;
    private float minTemp;
    private float maxTemp;
    private boolean lluvia;


    public Clima(int id, String nombre, float minTemp, float maxTemp, boolean lluvia){
        this.id = id;
        this.nombre = nombre;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.lluvia = lluvia;
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
