package com.pottermania.pottermania;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


public interface ArticuloRepository extends JpaRepository<Articulo, Long> {
    List<Articulo> findByCategoriaId(Long categoriaId);
}