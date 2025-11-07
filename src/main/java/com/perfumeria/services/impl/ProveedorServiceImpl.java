package com.perfumeria.services.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.perfumeria.exception.ProveedorNotFoundException;
import com.perfumeria.models.Proveedor;
import com.perfumeria.repositories.ProveedorRepository;
import com.perfumeria.services.IProveedorService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProveedorServiceImpl implements IProveedorService {

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Override
    @Transactional
    public Proveedor agregarProveedor(Proveedor proveedor) {
        proveedorRepository.findByNombre(proveedor.getNombre())
            .ifPresent(p -> { throw new RuntimeException("Ya existe un proveedor con el mismo nombre"); });

        proveedor.setActivo(true);
        return proveedorRepository.save(proveedor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Proveedor> listarProveedores() {
        return proveedorRepository.findByActivoTrue();
    }

    @Override
    @Transactional
    public void eliminarProveedor(Long id) {
        Proveedor proveedor = proveedorRepository.findById(id).orElseThrow(() -> new ProveedorNotFoundException(id));
        proveedor.setActivo(false);
        proveedorRepository.save(proveedor);
    }
}
