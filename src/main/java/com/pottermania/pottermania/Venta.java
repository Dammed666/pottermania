package com.pottermania.pottermania;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "venta")
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    private LocalDate fechaVenta;

    @Column(name = "iva_venta", columnDefinition = "DECIMAL(10, 2)")
    private double impuestoIva;

    @Column(name = "total_venta", columnDefinition = "DECIMAL(10, 2)")
    private double importeTotal;

    public Venta() {
    }

    public Venta(Cliente cliente, LocalDate fechaVenta, double impuestoIva, double importeTotal) {
        this.cliente = cliente;
        this.fechaVenta = fechaVenta;
        this.impuestoIva = impuestoIva;
        this.importeTotal = importeTotal;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public LocalDate getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(LocalDate fechaVenta) {
        this.fechaVenta = fechaVenta;
    }
    
    public double getImpuestoIva() {
        return impuestoIva;
    }

    public void setImpuestoIva(double impuestoIva) {
        this.impuestoIva = impuestoIva;
    }

    public double getImporteTotal() {
        return importeTotal;
    }

    public void setImporteTotal(double importeTotal) {
        this.importeTotal = importeTotal;
    }
}