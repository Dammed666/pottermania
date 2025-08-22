package com.pottermania.pottermania;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class TiendaController {

    @Autowired
    private final ArticuloRepository articuloRepository;
    private final CategoriaRepository categoriaRepository;

    public TiendaController(ArticuloRepository articuloRepository, CategoriaRepository categoriaRepository) {
        this.articuloRepository = articuloRepository;
        this.categoriaRepository = categoriaRepository;
    }

    // Método para mostrar la página de inicio de la tienda con las categorías
    @GetMapping("/tienda")
    public String showTiendaHome(Model model) {
        List<Categoria> categorias = categoriaRepository.findAll();
        model.addAttribute("categorias", categorias);
        model.addAttribute("modo", "categorias"); // Indicador para la vista
        return "tienda";
    }

    // Método para mostrar los artículos de una categoría específica
    @GetMapping("/tienda/categoria/{id}")
    public String showArticulosPorCategoria(@PathVariable("id") Long id, Model model) {
        List<Articulo> articulos = articuloRepository.findByCategoriaId(id);
        model.addAttribute("articulos", articulos);
        model.addAttribute("modo", "articulos"); // Indicador para la vista
        return "tienda";
    }
}