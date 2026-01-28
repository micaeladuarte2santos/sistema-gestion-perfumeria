package com.perfumeria.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

import com.perfumeria.exception.CategoriaAlreadyExistsException;
import com.perfumeria.models.CategoriaProducto;
import com.perfumeria.repositories.CategoriaProductoRepository;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceImplTest {

    @Mock
    private CategoriaProductoRepository categoriaRepository;

    @InjectMocks
    private CategoriaServiceImpl categoriaService;

    private CategoriaProducto categoria;

    @BeforeEach
    void setUp() {
        categoria = new CategoriaProducto();
        categoria.setId(1L);
        categoria.setNombre("Perfumes");
    }

    @Test
    void testCrearCategoria_Exitoso() {
        when(categoriaRepository.findByNombre(categoria.getNombre())).thenReturn(Optional.empty());
        when(categoriaRepository.save(any(CategoriaProducto.class))).thenReturn(categoria);
        CategoriaProducto resultado = categoriaService.crearCategoria(categoria);
        assertNotNull(resultado);
        assertEquals("Perfumes", resultado.getNombre());
        verify(categoriaRepository, times(1)).findByNombre(categoria.getNombre());
        verify(categoriaRepository, times(1)).save(categoria);
    }

    @Test
    void testCrearCategoria_LanzaExcepcionCuandoYaExiste() {
        when(categoriaRepository.findByNombre(categoria.getNombre())).thenReturn(Optional.of(categoria));
        assertThrows(CategoriaAlreadyExistsException.class, () -> {
            categoriaService.crearCategoria(categoria);
        });

        verify(categoriaRepository, times(1)).findByNombre(categoria.getNombre());
        verify(categoriaRepository, never()).save(any(CategoriaProducto.class));
    }

    @Test
    void testListarCategorias_RetornaListaCompleta() {
        CategoriaProducto categoria2 = new CategoriaProducto();
        categoria2.setId(2L);
        categoria2.setNombre("Cremas");

        List<CategoriaProducto> categorias = Arrays.asList(categoria, categoria2);
        when(categoriaRepository.findAll()).thenReturn(categorias);
        List<CategoriaProducto> resultado = categoriaService.listarCategorias();
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Perfumes", resultado.get(0).getNombre());
        assertEquals("Cremas", resultado.get(1).getNombre());
        verify(categoriaRepository, times(1)).findAll();
    }

    @Test
    void testListarCategorias_RetornaListaVacia() {
        when(categoriaRepository.findAll()).thenReturn(Arrays.asList());
        List<CategoriaProducto> resultado = categoriaService.listarCategorias();
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(categoriaRepository, times(1)).findAll();
    }
}
