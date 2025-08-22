package com.pottermania.pottermania;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;


@Entity
public class Articulo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String descripcion;
    private double precio;
    private String fotoarticulo; // Nuevo campo para la imagen

    @ManyToOne
    @JoinColumn(name = "categoria_id") // Esto mapea a la foreign key en la tabla de Articulo
    private Categoria categoria;

    public Articulo() {
    }

    public Articulo(String nombre, String descripcion, double precio, String fotoarticulo, Categoria categoria) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.fotoarticulo = fotoarticulo;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    // Nuevo getter y setter para la foto
    public String getFotoarticulo() {
        return fotoarticulo;
    }

    public void setFotoarticulo(String fotoarticulo) {
        this.fotoarticulo = fotoarticulo;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
}