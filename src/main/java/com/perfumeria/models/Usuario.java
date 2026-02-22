package com.perfumeria.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "usuarios")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {
    
    @Id
    private String username;
    private String password;
    private String nombre;
    private String apellido;
    private String email;
    private LocalDate fechaNacimiento;
    @Column(nullable = false)
    private boolean verificado = false;
}
