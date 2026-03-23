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

import com.perfumeria.exception.ProveedorEmailAlreadyExistsException;
import com.perfumeria.exception.ProveedorNotFoundException;
import com.perfumeria.models.Proveedor;
import com.perfumeria.repositories.ProveedorRepository;

@ExtendWith(MockitoExtension.class)
class ProveedorServiceImplTest {

    @Mock
    private ProveedorRepository proveedorRepository;

    @InjectMocks
    private ProveedorServiceImpl proveedorService;

    private Proveedor proveedor;

    @BeforeEach
    void setUp() {
        proveedor = new Proveedor();
        proveedor.setId(1L);
        proveedor.setNombre("Proveedor Test");
        proveedor.setTelefono("123456789");
        proveedor.setEmail("proveedor@test.com");
        proveedor.setActivo(true);
    }

    @Test
    void testAgregarProveedor_Exitoso() {
        when(proveedorRepository.findByNombre(anyString())).thenReturn(Optional.empty());
        when(proveedorRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
        when(proveedorRepository.save(any(Proveedor.class))).thenReturn(proveedor);
        Proveedor resultado = proveedorService.agregarProveedor(proveedor);
        assertNotNull(resultado);
        assertEquals("Proveedor Test", resultado.getNombre());
        assertTrue(resultado.getActivo());
        verify(proveedorRepository, times(1)).findByNombre(proveedor.getNombre());
        verify(proveedorRepository, times(1)).existsByEmailIgnoreCase(proveedor.getEmail());
        verify(proveedorRepository, times(1)).save(any(Proveedor.class));
    }

    @Test
    void testAgregarProveedor_LanzaExcepcionCuandoNombreYaExiste() {
        when(proveedorRepository.findByNombre(anyString())).thenReturn(Optional.of(proveedor));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            proveedorService.agregarProveedor(proveedor);
        });

        assertTrue(exception.getMessage().contains("Ya existe un proveedor"));
        verify(proveedorRepository, times(1)).findByNombre(proveedor.getNombre());
        verify(proveedorRepository, never()).existsByEmailIgnoreCase(anyString());
        verify(proveedorRepository, never()).save(any(Proveedor.class));
    }

    @Test
    void testAgregarProveedor_LanzaExcepcionCuandoEmailYaExiste() {
        when(proveedorRepository.findByNombre(anyString())).thenReturn(Optional.empty());
        when(proveedorRepository.existsByEmailIgnoreCase(anyString())).thenReturn(true);

        ProveedorEmailAlreadyExistsException exception = assertThrows(ProveedorEmailAlreadyExistsException.class, () -> {
            proveedorService.agregarProveedor(proveedor);
        });

        assertTrue(exception.getMessage().contains("Ya existe un proveedor con el email"));
        verify(proveedorRepository, times(1)).findByNombre(proveedor.getNombre());
        verify(proveedorRepository, times(1)).existsByEmailIgnoreCase(proveedor.getEmail());
        verify(proveedorRepository, never()).save(any(Proveedor.class));
    }

    @Test
    void testListarProveedores_RetornaListaDeProveedoresActivos() {
        Proveedor proveedor2 = new Proveedor();
        proveedor2.setId(2L);
        proveedor2.setNombre("Proveedor Test 2");
        proveedor2.setActivo(true);

        List<Proveedor> proveedores = Arrays.asList(proveedor, proveedor2);
        when(proveedorRepository.findByActivoTrue()).thenReturn(proveedores);
        List<Proveedor> resultado = proveedorService.listarProveedores();
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Proveedor Test", resultado.get(0).getNombre());
        assertEquals("Proveedor Test 2", resultado.get(1).getNombre());
        verify(proveedorRepository, times(1)).findByActivoTrue();
    }

    @Test
    void testListarProveedores_RetornaListaVacia() {
        when(proveedorRepository.findByActivoTrue()).thenReturn(Arrays.asList());
        List<Proveedor> resultado = proveedorService.listarProveedores();
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(proveedorRepository, times(1)).findByActivoTrue();
    }

    @Test
    void testEliminarProveedor_DesactivaProveedor() {
        when(proveedorRepository.findById(anyLong())).thenReturn(Optional.of(proveedor));
        when(proveedorRepository.save(any(Proveedor.class))).thenReturn(proveedor);
        proveedorService.eliminarProveedor(1L);
        verify(proveedorRepository, times(1)).findById(1L);
        verify(proveedorRepository, times(1)).save(any(Proveedor.class));
    }

    @Test
    void testEliminarProveedor_LanzaExcepcionCuandoNoExiste() {
        when(proveedorRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ProveedorNotFoundException.class, () -> {
            proveedorService.eliminarProveedor(999L);
        });

        verify(proveedorRepository, times(1)).findById(999L);
        verify(proveedorRepository, never()).save(any(Proveedor.class));
    }

    @Test
    void testAgregarProveedor_EstableceActivoEnTrue() {
        Proveedor nuevoProveedor = new Proveedor();
        nuevoProveedor.setNombre("Nuevo Proveedor");
        nuevoProveedor.setEmail("nuevo@proveedor.com");
        nuevoProveedor.setActivo(false); // Initially false

        when(proveedorRepository.findByNombre(anyString())).thenReturn(Optional.empty());
        when(proveedorRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
        when(proveedorRepository.save(any(Proveedor.class))).thenAnswer(invocation -> {
            Proveedor p = invocation.getArgument(0);
            assertTrue(p.getActivo(), "El proveedor debe ser marcado como activo");
            return p;
        });
        proveedorService.agregarProveedor(nuevoProveedor);
        verify(proveedorRepository, times(1)).save(any(Proveedor.class));
    }
}
