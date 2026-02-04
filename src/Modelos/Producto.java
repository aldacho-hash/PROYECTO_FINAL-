
package Modelos;


public class Producto {

    private int id_producto; 
    private String nombre;
    private double precio;
    private String categoria;
    private String imagen;
    private int stock;

    public Producto(int id_producto, String nombre, double precio, String categoria, String imagen, int stock) {
        this.id_producto = id_producto;
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
        this.imagen = imagen;
        this.stock = stock;
    }

    public int getIdProducto() {
        return id_producto;
    }

    public void setIdProducto(int id_producto) {
        this.id_producto = id_producto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    @Override
    public String toString() {
        return nombre + " - S/ " + precio;
    }
}