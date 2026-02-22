document.addEventListener("DOMContentLoaded", () => {
    cargarProductos();

    
    document.getElementById('buscarProductoBtn').addEventListener('click', buscarProductos);
});


function cargarProductos() {
    fetch('http://localhost:8080/productos/listado-productos')
        .then(response => {
            if (!response.ok) {
                throw new Error('Error al cargar los productos');
            }
            return response.json();
        })
        .then(data => {
            console.log('Datos recibidos:', data); 
            mostrarProductos(data); 
        })
        .catch(error => console.error('Error:', error));
}


function mostrarProductos(productos) {
    const listaProductos = document.getElementById('lista-productos');
    const totalCount = document.getElementById('totalCount');

    listaProductos.innerHTML = '';
    
    if (!productos || productos.length === 0) {
        listaProductos.innerHTML = '<p>No hay productos cargados.</p>';
        totalCount.textContent = "0";
        return;
    }

    totalCount.textContent = productos.length;

    productos.forEach(producto => {
        const fila = document.createElement('div');
        fila.className = 'fila';

  
        const nombre = producto.nombre || 'Sin nombre';
        const proveedor = producto.proveedor ? producto.proveedor.nombre : 'N/A';
        const precio = producto.precio ? producto.precio.toFixed(2) : '0.00';
        const stock = producto.stock !== undefined ? producto.stock : 0;

        fila.innerHTML = `
            <span>${nombre}</span>
            <span>${proveedor}</span>
            <span>$ ${precio}</span>
            <span>${stock} unidades</span>
        `;

        listaProductos.appendChild(fila);
    });
}

function buscarProductos() {
    const nombre = document.getElementById('productoNombre').value;
    const proveedor = document.getElementById('proveedorNombre').value;

 
    let query = `/productos/listado-productos?`;
    if (nombre) {
        query += `nombre=${encodeURIComponent(nombre)}&`; 
    }
    if (proveedor) {
        query += `proveedor=${encodeURIComponent(proveedor)}&`; 
    }
    query = query.slice(0, -1); 

    fetch(query)
        .then(response => {
            if (!response.ok) {
                throw new Error('Error al buscar productos');
            }
            return response.json();
        })
        .then(data => {
            mostrarProductos(data); 
        })
        .catch(error => console.error('Error:', error));
}
