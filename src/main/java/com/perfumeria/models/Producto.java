package com.perfumeria.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "productos")
public class Producto {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String codigoBarras;
    private String nombre;
    private double precio;
    private double precioCosto;
    private int stock;
    
    @Column(nullable = false)
    private boolean activo = true;
    
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private CategoriaProducto categoria;

    @ManyToOne
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor;

}
