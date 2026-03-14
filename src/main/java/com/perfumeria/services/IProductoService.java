package com.perfumeria.services;

import java.util.List;
import com.perfumeria.models.Producto;

public interface IProductoService {

    Producto crearProducto(Producto producto);
    List<Producto> listarProductos();
    Producto obtenerPorCodigoBarras(String codigoBarras);
    List<Producto> listarPorCategoria(Long categoriaId);
    void eliminarProducto(Long id);
    Producto actualizarPrecioYStock(Long id, Double precio, Double precioCosto, Integer stock);
    Producto obtenerPorId(Long id);
    void inactivarProducto(Long id);
    Producto actualizarProducto(Producto producto);
}
