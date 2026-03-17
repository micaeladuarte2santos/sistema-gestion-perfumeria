document.addEventListener("DOMContentLoaded", () => {

    cargarVentas();

    document.getElementById('cargarVentaBtn')
        .addEventListener('click', () => abrirAbmVenta());

    document.addEventListener("click", function(e) {

        if (e.target.classList.contains("btn-editar")) {
            abrirAbmVenta(e.target.dataset.id);
        }

        if (e.target.classList.contains("btn-eliminar")) {
            eliminarVenta(e.target.dataset.id);
        }

    });

});


function cargarVentas() {
    fetch('http://localhost:8080/ventas')
        .then(res => res.json())
        .then(data => mostrarVentas(data))
        .catch(err => console.error("Error:", err));
}

function mostrarVentas(ventas) {

    const lista = document.getElementById('lista-ventas');
    const total = document.getElementById('totalCount');

    lista.innerHTML = `
        <div class="fila encabezado">
            <span>CLIENTE</span>
            <span>FECHA Y HORA</span>
            <span>TOTAL</span>
            <span>MÉTODO DE PAGO</span>
            <span>ESTADO</span>
            <span>ACCIONES</span>
        </div>
    `;

    if (!ventas || ventas.length === 0) {
        total.textContent = "0";
        return;
    }

    total.textContent = ventas.length;

    ventas.forEach(v => {

        const fila = document.createElement('div');
        fila.className = 'fila';

        fila.innerHTML = `
            <span>${v.nombreCliente}</span>
            <span>${new Date(v.fecha).toLocaleString()}</span>
            <span>$ ${v.total?.toFixed(2)}</span>
            <span>${v.metodoPago}</span>
            <span>${v.estado}</span>
            <span>
                <button class="btn-editar" data-id="${v.id}">✏️</button>
            </span>
        `;

        lista.appendChild(fila);
    });
}

async function eliminarVenta(id) {

    const result = await Swal.fire({
        title: "¿Cancelar venta?",
        icon: "warning",
        showCancelButton: true,
        confirmButtonText: "Sí"
    });

    if (!result.isConfirmed) return;

    await fetch(`http://localhost:8080/ventas/${id}`, {
        method: "DELETE"
    });

    cargarVentas();
}


let productos = [];

async function cargarProductos() {
    const res = await fetch('http://localhost:8080/productos');
    productos = await res.json();
}


async function abrirAbmVenta(id = null) {

    await cargarProductos();

    // 📦 Crear overlay
    const tpl = document.getElementById('abmVentas');
    const overlay = document.createElement('div');

    overlay.id = 'abmOverlay';
    overlay.appendChild(tpl.content.cloneNode(true));
    document.body.appendChild(overlay);

    
    const form = overlay.querySelector('#ventaForm');
    const contenedor = overlay.querySelector('#detalleProductos');
    const totalInput = overlay.querySelector('#totalVenta');
    const estadoContainer = overlay.querySelector('#estadoContainer');

    const selectMetodo = overlay.querySelector('[name="metodoPago"]');
    const selectEstado = overlay.querySelector('[name="estado"]');

    
    await cargarMetodosPago(selectMetodo);
    await cargarEstados(selectEstado);

    
    if (!id) {
        estadoContainer.style.display = "none"; // nueva venta
    } else {
        estadoContainer.style.display = "block"; // edición
    }

    
    const close = () => overlay.remove();

    overlay.querySelector('#btnCerrar').onclick = close;
    overlay.querySelector('#btnCancelar').onclick = close;

    
    overlay.querySelector('#btnAgregarProducto').onclick = () => {
        agregarFilaProducto(contenedor, totalInput);
    };

    
    if (id) {
        try {
            const res = await fetch(`http://localhost:8080/ventas/${id}`);
            const venta = await res.json();

            console.log("VENTA:", venta);

            form.nombreCliente.value = venta.nombreCliente || "";
            form.metodoPago.value = venta.metodoPago || "";
            form.estado.value = venta.estado || "";
         
            contenedor.innerHTML = "";
           
            venta.detalles.forEach(det => {

                const div = document.createElement('div');
                div.className = 'fila-producto';

                div.innerHTML = `
                    <select class="producto-select">
                        <option value="">--Producto--</option>
                        ${productos.map(p => `
                            <option value="${p.id}" 
                                data-precio="${p.precio}"
                                ${p.id === det.producto.id ? "selected" : ""}>
                                ${p.nombre}
                            </option>
                        `).join('')}
                    </select>

                    <input type="number" class="cantidad" value="${det.cantidad}" min="1">
                    <input type="number" class="precio" readonly>
                    <input type="number" class="subtotal" readonly>

                    <button type="button" class="eliminar">X</button>
                `;

                contenedor.appendChild(div);

                const select = div.querySelector('.producto-select');
                const cantidad = div.querySelector('.cantidad');
                const precio = div.querySelector('.precio');
                const subtotal = div.querySelector('.subtotal');

                function actualizar() {
                    const selected = select.options[select.selectedIndex];
                    const p = parseFloat(selected.dataset.precio || 0);

                    precio.value = p;

                    const sub = p * (cantidad.value || 0);
                    subtotal.value = sub;

                    recalcularTotal(contenedor, totalInput);
                }

                select.onchange = actualizar;
                cantidad.oninput = actualizar;

                div.querySelector('.eliminar').onclick = () => {
                    div.remove();
                    recalcularTotal(contenedor, totalInput);
                };
               
                actualizar();
            });

        } catch (error) {
            console.error("Error cargando venta:", error);
            Swal.fire("Error al cargar la venta", "", "error");
        }
    }

   
    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const data = Object.fromEntries(new FormData(form).entries());
        const detalles = [];

        contenedor.querySelectorAll('.fila-producto').forEach(fila => {

            const select = fila.querySelector('.producto-select');
            const cantidadInput = fila.querySelector('.cantidad');

            if (!select || !cantidadInput) return;

            const productoId = select.value;
            const cantidad = cantidadInput.value;

            if (productoId && cantidad > 0) {
                detalles.push({
                    producto: { id: parseInt(productoId) }, 
                    cantidad: parseInt(cantidad)
                });
            }
        });

        // ⚠️ Validación mínima
        if (detalles.length === 0) {
            Swal.fire("Debe agregar al menos un producto", "", "warning");
            return;
        }

        const venta = {
            nombreCliente: data.nombreCliente,
            metodoPago: data.metodoPago,
            detalles: detalles
        };

        // 🔥 SOLO EN EDICIÓN
        if (id) {
            venta.estado = data.estado;
        }

        const url = id
            ? `http://localhost:8080/ventas/${id}`
            : 'http://localhost:8080/ventas';

        const method = id ? 'PUT' : 'POST';

        try {
            await fetch(url, {
                method: method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(venta)
            });

            Swal.fire("Guardado!", "", "success");

            cargarVentas();
            close();

        } catch (error) {
            console.error("Error guardando:", error);
            Swal.fire("Error al guardar la venta", "", "error");
        }
    });
}


function agregarFilaProducto(contenedor, totalInput) {

    const div = document.createElement('div');
    div.className = 'fila-producto';

    div.innerHTML = `
        <select class="producto-select">
            <option value="">--Producto--</option>
            ${productos.map(p => `<option value="${p.id}" data-precio="${p.precio}">${p.nombre}</option>`).join('')}
        </select>

        <input type="number" class="cantidad" min="1" value="1">
        <input type="number" class="precio" readonly>
        <input type="number" class="subtotal" readonly>

        <button type="button" class="eliminar">X</button>
    `;

    contenedor.appendChild(div);

    const select = div.querySelector('.producto-select');
    const cantidad = div.querySelector('.cantidad');
    const precio = div.querySelector('.precio');
    const subtotal = div.querySelector('.subtotal');

    function actualizar() {
        const selected = select.options[select.selectedIndex];
        const p = parseFloat(selected.dataset.precio || 0);

        precio.value = p;

        const sub = p * (cantidad.value || 0);
        subtotal.value = sub;

        recalcularTotal(contenedor, totalInput);
    }

    select.onchange = actualizar;
    cantidad.oninput = actualizar;

    div.querySelector('.eliminar').onclick = () => {
        div.remove();
        recalcularTotal(contenedor, totalInput);
    };
}


function recalcularTotal(contenedor, totalInput) {

    let total = 0;

    contenedor.querySelectorAll('.subtotal').forEach(input => {
        total += parseFloat(input.value) || 0;
    });

    totalInput.value = total;
}


async function cargarMetodosPago(select) {
    const res = await fetch('http://localhost:8080/metodos-pago');
    const metodos = await res.json();

    select.innerHTML = '<option value="">Seleccione</option>';

    metodos.forEach(m => {
        const option = document.createElement('option');
        option.value = m;
        option.textContent = m;
        select.appendChild(option);
    });
}

async function cargarEstados(select) {
    try {
        const res = await fetch('http://localhost:8080/estados-venta');

        if (!res.ok) {
            throw new Error("No se pudo cargar estados");
        }

        const estados = await res.json();

        // 🔥 validación clave
        if (!Array.isArray(estados)) {
            throw new Error("Formato inválido de estados");
        }

        select.innerHTML = '<option value="">--Seleccione--</option>';

        estados.forEach(e => {
            const option = document.createElement('option');
            option.value = e;
            option.textContent = e;
            select.appendChild(option);
        });

    } catch (error) {
        console.error("Error cargando estados:", error);
        Swal.fire("Error al cargar estados", "", "error");
    }
}