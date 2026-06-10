package com.perfumeria.services;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.perfumeria.models.Producto;

public interface IProductoService {

    Producto crearProducto(Producto producto);
    Producto crearProducto(Producto producto, MultipartFile imagen);
    List<Producto> listarProductos();
    Producto obtenerPorCodigoBarras(String codigoBarras);
    List<Producto> listarPorCategoria(Long categoriaId);
    void eliminarProducto(Long id);
    Producto actualizarPrecioYStock(Long id, Double precio, Double precioCosto, Integer stock);
    Producto obtenerPorId(Long id);
    void inactivarProducto(Long id);
    Producto actualizarProducto(Producto producto);
    Producto actualizarProducto(Producto producto, MultipartFile imagen);
}
