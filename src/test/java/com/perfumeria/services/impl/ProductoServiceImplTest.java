package com.perfumeria.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.perfumeria.exception.CategoriaNotFoundException;
import com.perfumeria.exception.ProductoCodigoBarrasAlreadyExistsException;
import com.perfumeria.exception.ProductoNotFoundException;
import com.perfumeria.exception.ProveedorNotFoundException;
import com.perfumeria.models.CategoriaProducto;
import com.perfumeria.models.Producto;
import com.perfumeria.models.Proveedor;
import com.perfumeria.repositories.CategoriaProductoRepository;
import com.perfumeria.repositories.ProductoRepository;
import com.perfumeria.repositories.ProveedorRepository;

@ExtendWith(MockitoExtension.class)
class ProductoServiceImplTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaProductoRepository categoriaRepository;

    @Mock
    private ProveedorRepository proveedorRepository;

    @InjectMocks
    private ProductoServiceImpl productoService;

    private Producto producto;
    private CategoriaProducto categoria;
    private Proveedor proveedor;

    @BeforeEach
    void setUp() {
        categoria = new CategoriaProducto();
        categoria.setId(1L);
        categoria.setNombre("Perfumes");

        proveedor = new Proveedor();
        proveedor.setId(1L);
        proveedor.setNombre("Proveedor Test");

        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Perfume Test");
        producto.setCodigoBarras("123456789");
        producto.setPrecio(100.0);
        producto.setPrecioCosto(50.0);
        producto.setStock(10);
        producto.setCategoria(categoria);
        producto.setProveedor(proveedor);
        producto.setActivo(true);
    }

    @Test
    void testCrearProducto_Exitoso() {
        when(productoRepository.findByCodigoBarras(anyString())).thenReturn(Optional.empty());
        when(categoriaRepository.findById(anyLong())).thenReturn(Optional.of(categoria));
        when(proveedorRepository.findById(anyLong())).thenReturn(Optional.of(proveedor));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        Producto resultado = productoService.crearProducto(producto);
        assertNotNull(resultado);
        assertEquals("Perfume Test", resultado.getNombre());
        assertTrue(resultado.isActivo());
        verify(productoRepository, times(1)).findByCodigoBarras(producto.getCodigoBarras());
        verify(categoriaRepository, times(1)).findById(categoria.getId());
        verify(proveedorRepository, times(1)).findById(proveedor.getId());
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void testCrearProducto_LanzaExcepcionCuandoCodigoBarrasYaExiste() {
        when(productoRepository.findByCodigoBarras(anyString())).thenReturn(Optional.of(producto));
        assertThrows(ProductoCodigoBarrasAlreadyExistsException.class, () -> {
            productoService.crearProducto(producto);
        });

        verify(productoRepository, times(1)).findByCodigoBarras(producto.getCodigoBarras());
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void testCrearProducto_LanzaExcepcionCuandoCategoriaNoExiste() {
        when(productoRepository.findByCodigoBarras(anyString())).thenReturn(Optional.empty());
        when(categoriaRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(CategoriaNotFoundException.class, () -> {
            productoService.crearProducto(producto);
        });

        verify(categoriaRepository, times(1)).findById(categoria.getId());
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void testCrearProducto_LanzaExcepcionCuandoProveedorNoExiste() {
        when(productoRepository.findByCodigoBarras(anyString())).thenReturn(Optional.empty());
        when(categoriaRepository.findById(anyLong())).thenReturn(Optional.of(categoria));
        when(proveedorRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ProveedorNotFoundException.class, () -> {
            productoService.crearProducto(producto);
        });

        verify(proveedorRepository, times(1)).findById(proveedor.getId());
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void testListarProductos_RetornaListaDeProductosActivos() {
        Producto producto2 = new Producto();
        producto2.setId(2L);
        producto2.setNombre("Perfume Test 2");
        producto2.setActivo(true);

        List<Producto> productos = Arrays.asList(producto, producto2);
        when(productoRepository.findByActivoTrue()).thenReturn(productos);
        List<Producto> resultado = productoService.listarProductos();
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(productoRepository, times(1)).findByActivoTrue();
    }

    @Test
    void testObtenerPorCodigoBarras_Exitoso() {
        when(productoRepository.findByCodigoBarrasAndActivoTrue(anyString())).thenReturn(Optional.of(producto));
        Producto resultado = productoService.obtenerPorCodigoBarras("123456789");
        assertNotNull(resultado);
        assertEquals("123456789", resultado.getCodigoBarras());
        verify(productoRepository, times(1)).findByCodigoBarrasAndActivoTrue("123456789");
    }

    @Test
    void testObtenerPorCodigoBarras_LanzaExcepcionCuandoNoExiste() {
        when(productoRepository.findByCodigoBarrasAndActivoTrue(anyString())).thenReturn(Optional.empty());
        assertThrows(ProductoNotFoundException.class, () -> {
            productoService.obtenerPorCodigoBarras("999999999");
        });

        verify(productoRepository, times(1)).findByCodigoBarrasAndActivoTrue("999999999");
    }

    @Test
    void testListarPorCategoria_RetornaProductosDeLaCategoria() {
        List<Producto> productos = Arrays.asList(producto);
        when(productoRepository.findByCategoriaIdAndActivoTrue(anyLong())).thenReturn(productos);
        List<Producto> resultado = productoService.listarPorCategoria(1L);
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(productoRepository, times(1)).findByCategoriaIdAndActivoTrue(1L);
    }

    @Test
    void testEliminarProducto_DesactivaProducto() {
        when(productoRepository.findById(anyLong())).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        productoService.eliminarProducto(1L);
        verify(productoRepository, times(1)).findById(1L);
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void testEliminarProducto_LanzaExcepcionCuandoNoExiste() {
        when(productoRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ProductoNotFoundException.class, () -> {
            productoService.eliminarProducto(999L);
        });

        verify(productoRepository, times(1)).findById(999L);
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void testActualizarPrecioYStock_ActualizaTodosLosValores() {
        when(productoRepository.findById(anyLong())).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        Producto resultado = productoService.actualizarPrecioYStock(1L, 150.0, 75.0, 20);
        assertNotNull(resultado);
        verify(productoRepository, times(1)).findById(1L);
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void testActualizarPrecioYStock_ActualizaSoloPrecio() {
        when(productoRepository.findById(anyLong())).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        Producto resultado = productoService.actualizarPrecioYStock(1L, 150.0, null, null);
        assertNotNull(resultado);
        verify(productoRepository, times(1)).findById(1L);
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void testActualizarPrecioYStock_LanzaExcepcionCuandoProductoNoExiste() {
        when(productoRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ProductoNotFoundException.class, () -> {
            productoService.actualizarPrecioYStock(999L, 150.0, 75.0, 20);
        });

        verify(productoRepository, times(1)).findById(999L);
        verify(productoRepository, never()).save(any(Producto.class));
    }
}
