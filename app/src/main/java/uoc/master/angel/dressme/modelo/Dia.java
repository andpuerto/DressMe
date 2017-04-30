package uoc.master.angel.dressme.modelo;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by angel on 30/03/2017.
 */

public class Dia implements Serializable{
    private int id;
    private Date fecha;
    private Conjunto conjuntoAsignado;

    public Dia(int id, Date fecha, Conjunto conjuntoAsignado){
        this.id = id;
        this.fecha = fecha;
        this.conjuntoAsignado = conjuntoAsignado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Conjunto getConjuntoAsignado() {
        return conjuntoAsignado;
    }

    public void setConjuntoAsignado(Conjunto conjuntoAsignado) {
        this.conjuntoAsignado = conjuntoAsignado;
    }
}
