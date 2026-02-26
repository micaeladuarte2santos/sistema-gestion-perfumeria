# Presentación: Sistema de Gestión - Perfumería

## Slide 1 - Portada
**Título:** Sistema de Gestión para Perfumería  
**Subtítulo:** Plataforma web para administrar productos, ventas, proveedores y usuarios  
**Pie:** Spring Boot + MySQL + Frontend web

**Notas del presentador:**
- Presentar el proyecto como una solución integral para la operación diaria de una perfumería.

---

## Slide 2 - Problema y contexto
**Título:** ¿Qué problema resolvemos?

- Gestión manual o dispersa de inventario y ventas.
- Falta de trazabilidad en recaudación diaria/mensual/anual.
- Dificultad para controlar proveedores y stock actualizado.
- Necesidad de registro/login y recuperación de contraseña para usuarios.

**Mensaje clave:** Centralizamos la operación comercial en una sola aplicación.

---

## Slide 3 - Objetivo del proyecto
**Título:** Objetivo

- Construir un sistema web para gestionar:
  - Catálogo de productos y categorías.
  - Proveedores.
  - Ventas y métricas de recaudación.
  - Usuarios con verificación por correo.
- Asegurar una base técnica escalable para futuras mejoras.

---

## Slide 4 - Stack tecnológico
**Título:** Tecnologías utilizadas

- **Backend:** Java 17 + Spring Boot 3.5.6.
- **Persistencia:** Spring Data JPA + Hibernate.
- **Base de datos:** MySQL.
- **API y documentación:** REST + OpenAPI/Swagger (`springdoc-openapi`).
- **Seguridad:** Spring Security + BCrypt.
- **Email:** Spring Mail (SMTP) para verificación.
- **Build:** Maven.

---

## Slide 5 - Arquitectura de la solución
**Título:** Arquitectura en capas

- **Capa Controller:** expone endpoints REST.
- **Capa Service:** lógica de negocio.
- **Capa Repository:** acceso a datos con JPA.
- **Capa DTO + Mapper:** contrato de entrada/salida limpio.
- **Modelos de dominio:** `Producto`, `Venta`, `Usuario`, `Proveedor`, etc.

**Flujo:** Cliente web -> Controllers -> Services -> Repositories -> MySQL.

---

## Slide 6 - Módulos funcionales
**Título:** Funcionalidades principales

- **Productos:** alta, listado, búsqueda por código, filtro por categoría, actualización de precio/stock, baja.
- **Categorías:** alta y listado.
- **Proveedores:** alta, listado y baja.
- **Ventas:** alta, consulta por id/listado, cambio de estado, borrado.
- **Métricas de ventas:** recaudación por día, mes y año.
- **Usuarios:** registro, login, verificación, reenvío de código y cambio de contraseña.

---

## Slide 7 - Seguridad y autenticación
**Título:** Seguridad implementada

- Encriptación de contraseñas con **BCrypt**.
- Configuración de **CORS** para acceso desde frontend.
- Flujo de **verificación por código** para habilitar usuarios.
- Recuperación de acceso mediante actualización de contraseña.

**Nota técnica actual:** configuración con `permitAll` en HTTP requests (base lista para endurecer reglas por roles/endpoints).

---

## Slide 8 - Base de datos y entidades
**Título:** Modelo de datos

Entidades principales:
- `Usuario`
- `CodigoVerificacion`
- `Producto`
- `CategoriaProducto`
- `Proveedor`
- `Venta`
- `DetalleVenta`

**Valor:** se cubre el ciclo completo desde catálogo hasta operación de venta y análisis de ingresos.

---

## Slide 9 - Frontend y experiencia de uso
**Título:** Interfaz web

- Páginas estáticas para operación rápida:
  - `home`, `productos`, `login`, `signup`, `reset password`, `verificar usuario`.
- JavaScript para interacción con endpoints REST.
- CSS dedicado por pantalla para mantener estilos modulares.

---

## Slide 10 - Puesta en marcha local
**Título:** ¿Cómo se ejecuta?

1. Crear base de datos `perfumeria` en MySQL.
2. Configurar `application.properties` (usuario/clave DB).
3. Ejecutar la app con Maven/Spring Boot.
4. Cargar datos iniciales con `insert-Perfumeria.sql`.

**Requisitos:** Java 17+, MySQL.

---

## Slide 11 - Logros y valor de negocio
**Título:** Resultados esperados

- Mayor control de stock y catálogo.
- Registro estructurado de ventas.
- Visibilidad de recaudación para toma de decisiones.
- Gestión centralizada de usuarios y proveedores.
- Base sólida para evolucionar a un sistema más robusto.

---

## Slide 12 - Próximos pasos
**Título:** Roadmap

- Implementar autorización por roles (admin/vendedor).
- Agregar JWT/sesiones seguras para login.
- Mejorar auditoría y reportes (exportables).
- Incorporar pruebas automatizadas de integración.
- Preparar despliegue en nube y CI/CD.

---

## Slide 13 - Cierre
**Título:** Gracias

**Texto sugerido:**
¿Preguntas o sugerencias para la siguiente versión del sistema?

---

## Guía rápida para pasarlo a Google Slides

1. Crear una presentación nueva en Google Slides.
2. Copiar cada bloque `Slide X` como una diapositiva.
3. Usar el título en la cabecera y los bullets como contenido.
4. Opcional: usar notas del presentador donde estén incluidas.

### Estilo visual sugerido (rápido)
- Tema limpio (fondo claro).
- Color principal: azul oscuro/gris.
- 1 idea por diapositiva.
- Máximo 5-6 bullets por slide.