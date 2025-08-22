package com.pottermania.pottermania;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
//import com.pottermania.pottermania.Articulo;

@Component
@SessionScope
public class Carrito{

    private final List<Articulo> articulos = new ArrayList<>();

    public void addArticulo(Articulo articulo){
        this.articulos.add(articulo);
    }

    public List<Articulo> getArticulos(){
        return articulos;
    }

    public double getSubtotal(){
        return articulos.stream()
                .mapToDouble(Articulo::getPrecio)
                .sum();
    }
}