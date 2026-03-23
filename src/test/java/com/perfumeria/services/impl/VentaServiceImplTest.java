package com.perfumeria.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
        venta.setTotal(200.0);
        venta.setEstado(EstadoVentaEnum.PENDIENTE);
        venta.setDetalles(Arrays.asList(detalleVenta));

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
        assertThrows(ProductoNotFoundException.class, () -> {
            ventaService.createVenta(ventaRequestDTO);
        });

        verify(productoRepository, times(1)).findById(producto.getId());
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void testCreateVenta_LanzaExcepcionCuandoStockInsuficiente() {
        producto.setStock(1);
        detalleVentaRequestDTO.setCantidad(5);

        when(ventaRepository.save(any(Venta.class))).thenReturn(venta);
        when(productoRepository.findById(anyLong())).thenReturn(Optional.of(producto));
        assertThrows(StockInsuficienteException.class, () -> {
            ventaService.createVenta(ventaRequestDTO);
        });

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
        assertThrows(VentaNotFoundException.class, () -> {
            ventaService.findById(999L);
        });

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
        assertThrows(VentaNotFoundException.class, () -> {
            ventaService.deleteById(999L);
        });

        verify(ventaRepository, times(1)).findById(999L);
        verify(ventaRepository, never()).deleteById(anyLong());
    }

    @Test
    void testFindByMes_RetornaVentasDelMes() {
        List<Venta> ventas = Arrays.asList(venta);
        when(ventaRepository.findByMes(anyInt(), anyInt())).thenReturn(ventas);
        List<Venta> resultado = ventaService.findByMes(1, 2026);
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(ventaRepository, times(1)).findByMes(1, 2026);
    }

    @Test
    void testFindByAnio_RetornaVentasDelAnio() {
        List<Venta> ventas = Arrays.asList(venta);
        when(ventaRepository.findByAnio(anyInt())).thenReturn(ventas);
        List<Venta> resultado = ventaService.findByAnio(2026);
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(ventaRepository, times(1)).findByAnio(2026);
    }

    @Test
    void testGetRecaudacionPorDia_RetornaRecaudacion() {
        LocalDate fecha = LocalDate.now();
        when(ventaRepository.getRecaudacionPorDia(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(500.0);
        Double resultado = ventaService.getRecaudacionPorDia(fecha);
        assertNotNull(resultado);
        assertEquals(500.0, resultado);
        verify(ventaRepository, times(1)).getRecaudacionPorDia(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void testGetRecaudacionPorDia_RetornaCeroCuandoEsNull() {
        LocalDate fecha = LocalDate.now();
        when(ventaRepository.getRecaudacionPorDia(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(null);
        Double resultado = ventaService.getRecaudacionPorDia(fecha);
        assertNotNull(resultado);
        assertEquals(0.0, resultado);
        verify(ventaRepository, times(1)).getRecaudacionPorDia(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void testGetRecaudacionPorMes_RetornaRecaudacion() {
        when(ventaRepository.getRecaudacionPorMes(anyInt(), anyInt())).thenReturn(1500.0);
        Double resultado = ventaService.getRecaudacionPorMes(1, 2026);
        assertNotNull(resultado);
        assertEquals(1500.0, resultado);
        verify(ventaRepository, times(1)).getRecaudacionPorMes(1, 2026);
    }

    @Test
    void testGetRecaudacionPorMes_RetornaCeroCuandoEsNull() {
        when(ventaRepository.getRecaudacionPorMes(anyInt(), anyInt())).thenReturn(null);
        Double resultado = ventaService.getRecaudacionPorMes(1, 2026);
        assertNotNull(resultado);
        assertEquals(0.0, resultado);
        verify(ventaRepository, times(1)).getRecaudacionPorMes(1, 2026);
    }

    @Test
    void testGetRecaudacionPorAnio_RetornaRecaudacion() {
        when(ventaRepository.getRecaudacionPorAnio(anyInt())).thenReturn(18000.0);
        Double resultado = ventaService.getRecaudacionPorAnio(2026);
        assertNotNull(resultado);
        assertEquals(18000.0, resultado);
        verify(ventaRepository, times(1)).getRecaudacionPorAnio(2026);
    }

    @Test
    void testGetRecaudacionPorAnio_RetornaCeroCuandoEsNull() {
        when(ventaRepository.getRecaudacionPorAnio(anyInt())).thenReturn(null);
        Double resultado = ventaService.getRecaudacionPorAnio(2026);
        assertNotNull(resultado);
        assertEquals(0.0, resultado);
        verify(ventaRepository, times(1)).getRecaudacionPorAnio(2026);
    }

    @Test
    void testActualizarEstado_ActualizaCorrectamente() {
        when(ventaRepository.findById(anyLong())).thenReturn(Optional.of(venta));
        when(ventaRepository.save(any(Venta.class))).thenReturn(venta);
        Venta resultado = ventaService.actualizarEstado(1L, EstadoVentaEnum.ABONADA);
        assertNotNull(resultado);
        verify(ventaRepository, times(1)).findById(1L);
        verify(ventaRepository, times(1)).save(any(Venta.class));
    }

    @Test
    void testActualizarEstado_LanzaExcepcionCuandoVentaNoExiste() {
        when(ventaRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(VentaNotFoundException.class, () -> {
            ventaService.actualizarEstado(999L, EstadoVentaEnum.ABONADA);
        });

        verify(ventaRepository, times(1)).findById(999L);
        verify(ventaRepository, never()).save(any(Venta.class));
    }

    @Test
    void testCreateVenta_CalculaCorrectamenteElTotal() {
        Producto producto2 = new Producto();
        producto2.setId(2L);
        producto2.setPrecio(50.0);
        producto2.setStock(20);

        DetalleVentaRequestDTO detalle2 = new DetalleVentaRequestDTO();
        detalle2.setProductoId(2L);
        detalle2.setCantidad(3);

        ventaRequestDTO.setDetalles(Arrays.asList(detalleVentaRequestDTO, detalle2));

        when(ventaRepository.save(any(Venta.class))).thenReturn(venta);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.findById(2L)).thenReturn(Optional.of(producto2));
        when(productoRepository.save(any(Producto.class))).thenAnswer(i -> i.getArguments()[0]);
        when(detalleVentaRepository.save(any(DetalleVenta.class))).thenAnswer(i -> i.getArguments()[0]);
        Venta resultado = ventaService.createVenta(ventaRequestDTO);
        assertNotNull(resultado);
        verify(ventaRepository, times(2)).save(any(Venta.class));
        verify(detalleVentaRepository, times(2)).save(any(DetalleVenta.class));
    }
}
