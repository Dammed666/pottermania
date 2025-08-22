package com.pottermania.pottermania;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    // Consulta para encontrar las 3 publicaciones más recientes, ordenadas por fecha de publicación
    List<BlogPost> findTop3ByOrderByFechaPublicacionDesc();
}