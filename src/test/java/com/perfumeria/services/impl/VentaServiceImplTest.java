package com.perfumeria.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.perfumeria.dto.DetalleVentaRequestDTO;
import com.perfumeria.dto.VentaRequestDTO;
import com.perfumeria.exception.ProductoNotFoundException;
import com.perfumeria.exception.StockInsuficienteException;
import com.perfumeria.exception.VentaNotFoundException;
import com.perfumeria.models.DetalleVenta;
import com.perfumeria.models.EstadoVentaEnum;
import com.perfumeria.models.MetodoPagoEnum;
import com.perfumeria.models.Producto;
import com.perfumeria.models.Venta;
import com.perfumeria.repositories.DetalleVentaRepository;
import com.perfumeria.repositories.ProductoRepository;
import com.perfumeria.repositories.VentaRepository;

@ExtendWith(MockitoExtension.class)
class VentaServiceImplTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private DetalleVentaRepository detalleVentaRepository;

    @InjectMocks
    private VentaServiceImpl ventaService;

    private Venta venta;
    private Producto producto;
    private DetalleVenta detalleVenta;
    private VentaRequestDTO ventaRequestDTO;
    private DetalleVentaRequestDTO detalleVentaRequestDTO;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Perfume Test");
        producto.setPrecio(100.0);
        producto.setStock(10);
        producto.setActivo(true);

        detalleVenta = new DetalleVenta();
        detalleVenta.setId(1L);
        detalleVenta.setProducto(producto);
        detalleVenta.setCantidad(2);
        detalleVenta.setSubtotal(200.0);

        venta = new Venta();
        venta.setId(1L);
        venta.setFecha(LocalDateTime.now());
        venta.setNombreCliente("Cliente Test");
        venta.setTotal(200.0);
        venta.setEstado(EstadoVentaEnum.PENDIENTE);
        venta.setDetalles(new ArrayList<>(Arrays.asList(detalleVenta)));

        detalleVentaRequestDTO = new DetalleVentaRequestDTO();
        detalleVentaRequestDTO.setProductoId(1L);
        detalleVentaRequestDTO.setCantidad(2);

        ventaRequestDTO = new VentaRequestDTO();
        ventaRequestDTO.setNombreCliente("Cliente Test");
        ventaRequestDTO.setMetodoPago(MetodoPagoEnum.EFECTIVO);
        ventaRequestDTO.setEstado(EstadoVentaEnum.PENDIENTE);
        ventaRequestDTO.setDetalles(Arrays.asList(detalleVentaRequestDTO));
    }

    @Test
    void testCreateVenta_Exitoso() {
        when(ventaRepository.save(any(Venta.class))).thenReturn(venta);
        when(productoRepository.findById(anyLong())).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        when(detalleVentaRepository.save(any(DetalleVenta.class))).thenReturn(detalleVenta);

        Venta resultado = ventaService.createVenta(ventaRequestDTO);

        assertNotNull(resultado);
        assertEquals(EstadoVentaEnum.PENDIENTE, resultado.getEstado());
        verify(ventaRepository, times(2)).save(any(Venta.class));
        verify(productoRepository, times(1)).findById(producto.getId());
        verify(productoRepository, times(1)).save(any(Producto.class));
        verify(detalleVentaRepository, times(1)).save(any(DetalleVenta.class));
    }

    @Test
    void testCreateVenta_LanzaExcepcionCuandoProductoNoExiste() {
        when(ventaRepository.save(any(Venta.class))).thenReturn(venta);
        when(productoRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ProductoNotFoundException.class, () -> ventaService.createVenta(ventaRequestDTO));

        verify(productoRepository, times(1)).findById(producto.getId());
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void testCreateVenta_LanzaExcepcionCuandoStockInsuficiente() {
        producto.setStock(1);
        detalleVentaRequestDTO.setCantidad(5);

        when(ventaRepository.save(any(Venta.class))).thenReturn(venta);
        when(productoRepository.findById(anyLong())).thenReturn(Optional.of(producto));

        assertThrows(StockInsuficienteException.class, () -> ventaService.createVenta(ventaRequestDTO));

        verify(productoRepository, times(1)).findById(producto.getId());
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void testFindById_Exitoso() {
        when(ventaRepository.findById(anyLong())).thenReturn(Optional.of(venta));

        Venta resultado = ventaService.findById(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(ventaRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_LanzaExcepcionCuandoNoExiste() {
        when(ventaRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(VentaNotFoundException.class, () -> ventaService.findById(999L));

        verify(ventaRepository, times(1)).findById(999L);
    }

    @Test
    void testFindAll_RetornaListaDeVentas() {
        Venta venta2 = new Venta();
        venta2.setId(2L);
        List<Venta> ventas = Arrays.asList(venta, venta2);

        when(ventaRepository.findAll()).thenReturn(ventas);

        List<Venta> resultado = ventaService.findAll();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(ventaRepository, times(1)).findAll();
    }

    @Test
    void testDeleteById_EliminaYRestableceStock() {
        when(ventaRepository.findById(anyLong())).thenReturn(Optional.of(venta));
        when(productoRepository.findById(anyLong())).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        doNothing().when(ventaRepository).deleteById(anyLong());

        ventaService.deleteById(1L);

        verify(ventaRepository, times(1)).findById(1L);
        verify(productoRepository, times(1)).findById(producto.getId());
        verify(productoRepository, times(1)).save(any(Producto.class));
        verify(ventaRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteById_LanzaExcepcionCuandoVentaNoExiste() {
        when(ventaRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(VentaNotFoundException.class, () -> ventaService.deleteById(999L));

        verify(ventaRepository, times(1)).findById(999L);
        verify(ventaRepository, never()).deleteById(anyLong());
    }

    @Test
    void testActualizarEstado_ActualizaCorrectamente() {
        when(ventaRepository.findById(anyLong())).thenReturn(Optional.of(venta));
        when(ventaRepository.save(any(Venta.class))).thenReturn(venta);

        Venta resultado = ventaService.actualizarEstado(1L, EstadoVentaEnum.ABONADA);

        assertNotNull(resultado);
        assertEquals(EstadoVentaEnum.ABONADA, resultado.getEstado());
        verify(ventaRepository, times(1)).findById(1L);
        verify(ventaRepository, times(1)).save(any(Venta.class));
    }

    @Test
    void testActualizarEstado_LanzaExcepcionCuandoVentaNoExiste() {
        when(ventaRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(VentaNotFoundException.class, () -> ventaService.actualizarEstado(999L, EstadoVentaEnum.ABONADA));

        verify(ventaRepository, times(1)).findById(999L);
        verify(ventaRepository, never()).save(any(Venta.class));
    }

    @Test
    void testUpdateVenta_ActualizaDatosYStock() {
        when(ventaRepository.findByIdConDetalles(anyLong())).thenReturn(Optional.of(venta));
        when(productoRepository.findById(anyLong())).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(ventaRepository.save(any(Venta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VentaRequestDTO updateRequest = new VentaRequestDTO();
        updateRequest.setNombreCliente("Cliente Actualizado");
        updateRequest.setMetodoPago(MetodoPagoEnum.CREDITO);
        updateRequest.setEstado(EstadoVentaEnum.ABONADA);

        DetalleVentaRequestDTO nuevoDetalle = new DetalleVentaRequestDTO();
        nuevoDetalle.setProductoId(1L);
        nuevoDetalle.setCantidad(1);
        updateRequest.setDetalles(Arrays.asList(nuevoDetalle));

        Venta resultado = ventaService.updateVenta(1L, updateRequest);

        assertNotNull(resultado);
        assertEquals("Cliente Actualizado", resultado.getNombreCliente());
        assertEquals(EstadoVentaEnum.ABONADA, resultado.getEstado());
        assertEquals(1, resultado.getDetalles().size());
        assertEquals(100.0, resultado.getTotal());
        verify(ventaRepository, times(1)).findByIdConDetalles(1L);
        verify(productoRepository, times(1)).findById(1L);
        verify(productoRepository, times(2)).save(any(Producto.class));
        verify(detalleVentaRepository, times(1)).deleteAll(anyList());
        verify(detalleVentaRepository, times(1)).saveAll(anyList());
        verify(ventaRepository, times(1)).save(any(Venta.class));
    }

    @Test
    void testFindByDia_RetornaVentasDelDia() {
        LocalDate fecha = LocalDate.now();
        List<Venta> ventas = Arrays.asList(venta);
        when(ventaRepository.findByFechaBetween(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(ventas);

        List<Venta> resultado = ventaService.findByDia(fecha);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(ventaRepository, times(1)).findByFechaBetween(any(LocalDateTime.class), any(LocalDateTime.class));
    }
}
