package Clases;

import java.time.LocalDate;

public class CampañaDescuento {

    private String nombre;
    private LocalDate inicio;
    private LocalDate fin;
    private double porcentajeDescuento;

    public CampañaDescuento(String nombre, LocalDate inicio, LocalDate fin, double porcentajeDescuento) {
        this.nombre = nombre;
        this.inicio = inicio;
        this.fin = fin;
        this.porcentajeDescuento = porcentajeDescuento;
    }

    public boolean estaActiva() {
        LocalDate hoy = LocalDate.now();
        return (hoy.isEqual(inicio) || hoy.isAfter(inicio)) &&
               (hoy.isEqual(fin) || hoy.isBefore(fin));
    }

    public String getNombre() {
        return nombre;
    }

    public LocalDate getInicio() {
        return inicio;
    }

    public LocalDate getFin() {
        return fin;
    }

    public double getPorcentajeDescuento() {
        return porcentajeDescuento;
    }
}
