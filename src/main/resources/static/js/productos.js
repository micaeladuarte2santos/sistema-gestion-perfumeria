document.addEventListener("DOMContentLoaded", () => {
    cargarProductos();

    document.getElementById('buscarProductoBtn').addEventListener('click', buscarProductos);
    document.getElementById('cargarProductoBtn').addEventListener('click', abrirAbmProducto);
    document.addEventListener("click", function(e) {
        if (e.target.classList.contains("btn-editar")) {
            const id = e.target.dataset.id;
            abrirAbmProducto(id);
        }
    });

});

function cargarProductos() {
    fetch('http://localhost:8080/productos/listado-productos')
        .then(response => {
            if (!response.ok) throw new Error('Error al cargar los productos');
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

    listaProductos.innerHTML = `
        <div class="fila encabezado">
            <span>NOMBRE</span>
            <span>PROVEEDOR</span>
            <span>PRECIO</span>
            <span>STOCK</span>
            <span>ACCIONES</span>
        </div>
    `;

    if (!productos || productos.length === 0) {
        listaProductos.insertAdjacentHTML('beforeend', '<p style="padding: 20px;">No hay productos cargados.</p>');
        totalCount.textContent = "0";
        return;
    }

    totalCount.textContent = productos.length;

    productos.forEach(producto => {
        const fila = document.createElement('div');
        fila.className = 'fila';

        const nombre = producto.nombre || 'Sin nombre';
        const proveedor = producto.proveedor ? producto.proveedor.nombre : 'N/A';
        const precio = producto.precio !== undefined ? Number(producto.precio).toFixed(2) : '0.00';
        const stock = producto.stock !== undefined ? producto.stock : 0;

        fila.innerHTML = `
            <span>${escapeHtml(nombre)}</span>
            <span>${escapeHtml(proveedor)}</span>
            <span>$ ${escapeHtml(precio)}</span>
            <span>${escapeHtml(stock)} unidades</span>
            <span>
              <button class="btn-editar" data-id="${producto.id}">✏️</button>
            </span>
        `;

        listaProductos.appendChild(fila);
    });
}

function buscarProductos() {
    const nombre = document.getElementById('productoNombre').value;
    const proveedor = document.getElementById('proveedorNombre').value;

    let query = `http://localhost:8080/productos/listado-productos?`;
    if (nombre) query += `nombre=${encodeURIComponent(nombre)}&`;
    if (proveedor) query += `proveedor=${encodeURIComponent(proveedor)}&`;
    query = query.replace(/&$/, '');

    fetch(query)
        .then(response => {
            if (!response.ok) throw new Error('Error al buscar productos');
            return response.json();
        })
        .then(data => mostrarProductos(data))
        .catch(error => console.error('Error:', error));
}


async function abrirAbmProducto(id = null) {
  let tpl = document.getElementById('abmProductos');
  let overlay;

  if (tpl) {
    const clone = tpl.content.cloneNode(true);
    overlay = document.createElement('div');
    overlay.id = 'abmOverlay';
    overlay.appendChild(clone);
    document.body.appendChild(overlay);
  } else {
    try {
      const res = await fetch('/abmProductos.html');
      if (!res.ok) throw new Error('archivo no encontrado');
      const html = await res.text();
      overlay = document.createElement('div');
      overlay.id = 'abmOverlay';
      overlay.innerHTML = html;
      document.body.appendChild(overlay);
    } catch (err) {
      console.error('No se pudo abrir el ABM:', err);
      return;
    }
  }

  const panel = overlay.querySelector('.abm-panel');
  const btnCerrar = overlay.querySelector('#btnCerrar');
  const btnCancelar = overlay.querySelector('#btnCancelar');
  const form = overlay.querySelector('#productoForm');

  const close = () => {
    if (overlay && overlay.parentNode) overlay.parentNode.removeChild(overlay);
    document.removeEventListener('keydown', onKeyDown);
  };

  if (panel) {
    overlay.addEventListener('click', (e) => { if (!panel.contains(e.target)) close(); });
  } else {
    overlay.addEventListener('click', (e) => { if (e.target === overlay) close(); });
  }

  const onKeyDown = (e) => { if (e.key === 'Escape') close(); };
  document.addEventListener('keydown', onKeyDown);

  btnCerrar?.addEventListener('click', close);
  btnCancelar?.addEventListener('click', close);

  // poblar selects desde backend
  const selCat = overlay.querySelector('#categoria');
  const selProv = overlay.querySelector('#proveedor');

  if (selCat) {
    try {
      const r = await fetch('http://localhost:8080/categorias');
      if (!r.ok) throw new Error('No se pudieron cargar categorías');
      const categorias = await r.json();
      selCat.innerHTML = '<option value="">--Seleccione--</option>';
      categorias.forEach(c => {
        const opt = document.createElement('option');
        opt.value = c.id;
        opt.textContent = c.nombre;
        selCat.appendChild(opt);
      });
    } catch (e) {
      console.error('Error al cargar categorías:', e);
      selCat.innerHTML = '<option value="">No se pudieron cargar categorías</option>';
    }
  }

  if (selProv) {
    try {
      const r2 = await fetch('http://localhost:8080/proveedores');
      if (!r2.ok) throw new Error('No se pudieron cargar proveedores');
      const proveedores = await r2.json();
      selProv.innerHTML = '<option value="">--Seleccione--</option>';
      proveedores.forEach(p => {
        const opt = document.createElement('option');
        opt.value = p.id;
        opt.textContent = p.nombre;
        selProv.appendChild(opt);
      });
    } catch (e) {
      console.error('Error al cargar proveedores:', e);
      selProv.innerHTML = '<option value="">No se pudieron cargar proveedores</option>';
    }
  }

  if (!form) {
    console.warn('No se encontró formulario #productoForm en el template/modal.');
    return;
  }

  if (id) {
    try {
        const res = await fetch(`http://localhost:8080/productos/${id}`);
        if (!res.ok) throw new Error("No se pudo cargar el producto");

        const producto = await res.json();

        overlay.querySelector('#codigoBarras').value = producto.codigoBarras || '';
        overlay.querySelector('#nombre').value = producto.nombre || '';
        overlay.querySelector('#precio').value = producto.precio || 0;
        overlay.querySelector('#precioCosto').value = producto.precioCosto || 0;
        overlay.querySelector('#stock').value = producto.stock || 0;
        overlay.querySelector('#categoria').value = producto.categoriaId || '';
        overlay.querySelector('#proveedor').value = producto.proveedorId || '';

        const titulo = overlay.querySelector('#abmTitle');
        if (titulo) titulo.textContent = "Editar Producto";

    } catch (err) {
        console.error("Error cargando producto:", err);
        alert("No se pudo cargar el producto para editar");
    }
}

  form.addEventListener('submit', async (e) => {
    e.preventDefault();

    const formData = new FormData();

    // Datos normales del formulario
    const data = Object.fromEntries(new FormData(form).entries());

    const categoriaId = data.categoria ? Number(data.categoria) : null;
    const proveedorId = data.proveedor ? Number(data.proveedor) : null;

    if (!categoriaId) {
        alert('Seleccione una categoría válida.');
        return;
    }

    const producto = {
        codigoBarras: data.codigoBarras,
        nombre: data.nombre,
        precio: parseFloat(data.precio) || 0,
        precioCosto: parseFloat(data.precioCosto) || 0,
        stock: parseInt(data.stock, 10) || 0,
        categoriaId: categoriaId,
        proveedorId: proveedorId
    };

    // Agregar JSON como texto
    formData.append("producto", new Blob(
        [JSON.stringify(producto)], 
        { type: "application/json" }
    ));

    // Agregar imagen si existe
    const inputImagen = form.querySelector('#imagen');
    if (inputImagen && inputImagen.files.length > 0) {
        formData.append("imagen", inputImagen.files[0]);
    }

    console.log("Enviando producto con imagen...");

    try {
        const url = id ? `http://localhost:8080/productos/${id}`: 'http://localhost:8080/productos';
        const method = id ? 'PUT' : 'POST';

        const res = await fetch(url, {
            method: method,
            body: formData
        });

        if (!res.ok) {
            const errText = await res.text().catch(() => null);
            throw new Error(`Error del servidor: ${res.status} ${errText || res.statusText}`);
        }

        const nuevoProducto = await res.json().catch(() => null);
        console.log('Producto creado:', nuevoProducto);

        cargarProductos();
        close();

    } catch (err) {
        console.error('No se pudo guardar el producto:', err);
        alert('Error al guardar el producto.');
    }
  });
}




/* Util: evitar inyección simple al insertar texto en option */
function escapeHtml(str) {
    if (str === null || str === undefined) return '';
    return String(str).replace(/[&<>"']/g, s => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[s]));
}
