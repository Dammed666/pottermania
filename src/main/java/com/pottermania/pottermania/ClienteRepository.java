package com.pottermania.pottermania;

import java.util.Optional; // Importar la clase Optional

import org.springframework.data.jpa.repository.JpaRepository; 

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    // Método para buscar un cliente por su email
    Optional<Cliente> findByEmail(String email);
}