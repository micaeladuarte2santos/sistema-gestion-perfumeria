package com.perfumeria.services;

import java.util.List;

import com.perfumeria.models.Proveedor;

public interface IProveedorService {

    Proveedor agregarProveedor(Proveedor proveedor);
    List<Proveedor> listarProveedores();
    void eliminarProveedor(Long id);

}
