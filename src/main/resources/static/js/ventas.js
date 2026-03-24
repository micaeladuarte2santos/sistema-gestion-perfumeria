document.addEventListener("DOMContentLoaded", () => {
  cargarVentas();

  document
    .getElementById("clienteNombre")
    .addEventListener("input", aplicarFiltros);

  document
    .getElementById("estadoVenta")
    .addEventListener("input", aplicarFiltros);

  document
    .getElementById("metodoPagoFiltro")
    .addEventListener("input", aplicarFiltros);

  document
    .getElementById("cargarVentaBtn")
    .addEventListener("click", () => abrirAbmVenta());

  document.addEventListener("click", function (e) {
    if (e.target.classList.contains("btn-editar")) {
      abrirAbmVenta(e.target.dataset.id);
    }

    if (e.target.classList.contains("btn-ticket")) {
      imprimirTicket(e.target.dataset.id);
    }

    if (e.target.classList.contains("btn-eliminar")) {
      eliminarVenta(e.target.dataset.id);
    }
  });
});

let ventasGlobal = [];
const ELEMENTOS_POR_PAGINA = 5;
let paginaActual = 1;


function cargarVentas() {
  return fetch("http://localhost:8080/ventas")
    .then(res => res.json())
    .then(data => {
      ventasGlobal = data;

      paginaActual = 1;
      aplicarFiltros();
      actualizarResumenVentas(data); // 🔥 AGREGAR ESTA LÍNEA
    })
    .catch(err => console.error("Error:", err));
}

function renderizarPaginacion(totalItems, totalPaginas) {
  const contenedor = document.getElementById("paginacion-ventas");
  if (!contenedor) return;

  contenedor.innerHTML = "";

  // botón anterior
  const prev = document.createElement("button");
  prev.textContent = "Anterior";
  prev.disabled = paginaActual === 1;
  prev.onclick = () => {
    paginaActual--;
    aplicarFiltrosSinReset();
  };

  contenedor.appendChild(prev);

  // páginas
  for (let i = 1; i <= totalPaginas; i++) {
    const btn = document.createElement("button");
    btn.textContent = i;

    if (i === paginaActual) btn.classList.add("activo");

    btn.onclick = () => {
      paginaActual = i;
      aplicarFiltrosSinReset();
    };

    contenedor.appendChild(btn);
  }

  // botón siguiente
  const next = document.createElement("button");
  next.textContent = "Siguiente";
  next.disabled = paginaActual === totalPaginas;
  next.onclick = () => {
    paginaActual++;
    aplicarFiltrosSinReset();
  };

  contenedor.appendChild(next);
}

function actualizarContadorVentas(ventas) {

    let pendientes = 0;
    let devueltas = 0;
    let canceladas = 0;
    let abonadas = 0;
    let indefinidas = 0;

    ventas.forEach(v => {

        switch (v.estado) {
            case "DEVUELTA":
                devueltas++;
                break;

            case "CANCELADA":
                canceladas++;
                break;

            case "ABONADA":
                abonadas++;
                break;
            case "PENDIENTE":
                pendientes++;
            default:
                indefinidas++; 
                break;
        }
    });

    document.getElementById("contPendientes").textContent = pendientes;
    document.getElementById("contDevueltas").textContent = devueltas;
    document.getElementById("contCanceladas").textContent = canceladas;
    document.getElementById("contAbonadas").textContent = abonadas  ;
}


function mostrarVentas(ventas) {
  const lista = document.getElementById("lista-ventas");
  const total = document.getElementById("totalCount");

  lista.innerHTML = `
        <div class="fila encabezado">
            <span>CLIENTE</span>
            <span>FECHA Y HORA</span>
            <span>TOTAL</span>
            <span>MÉTODO DE PAGO</span>
            <span>ESTADO</span>
        <span>TICKET</span>
            <span>ACCIONES</span>
        </div>
    `;

  if (!ventas || ventas.length === 0) {
    total.textContent = "0";
    renderizarPaginacion(0, 1);
    return;
  }

  total.textContent = ventas.length;

  const totalPaginas = Math.max(1, Math.ceil(ventas.length / ELEMENTOS_POR_PAGINA));
  if (paginaActual > totalPaginas) paginaActual = totalPaginas;

  const inicio = (paginaActual - 1) * ELEMENTOS_POR_PAGINA;
  const ventasPagina = ventas.slice(inicio, inicio + ELEMENTOS_POR_PAGINA);

  ventasPagina.forEach((v) => {
    const fila = document.createElement("div");
    fila.className = "fila";

    const botonTicket =
      v.estado === "PENDIENTE"
        ? `<button class="btn-ticket" data-id="${v.id}" data-estado="${v.estado}">Imprimir</button>`
        : `<span class="ticket-vacio">-</span>`;

    fila.innerHTML = `
            <span>${v.nombreCliente}</span>
            <span>${new Date(v.fecha).toLocaleString()}</span>
            <span>$ ${v.total?.toFixed(2)}</span>
            <span>${v.metodoPago}</span>
            <span>${v.estado}</span>
            <span class="celda-ticket">
                ${botonTicket}
            </span>
            <span class="celda-acciones">
                <button class="btn-editar" data-id="${v.id}">✏️</button>
            </span>
        `;

    lista.appendChild(fila);
  });

  renderizarPaginacion(ventas.length, totalPaginas);
}

function aplicarFiltros() {
  paginaActual = 1;
  aplicarFiltrosSinReset();
}

function aplicarFiltrosSinReset() {
  const cliente = document.getElementById("clienteNombre").value.toLowerCase();
  const estado = document.getElementById("estadoVenta").value.toLowerCase();
  const metodo = document
    .getElementById("metodoPagoFiltro")
    .value.toLowerCase();

  const filtradas = ventasGlobal.filter((v) => {
    const coincideCliente =
      !cliente || v.nombreCliente.toLowerCase().includes(cliente);
    const coincideEstado = !estado || v.estado.toLowerCase().includes(estado);
    const coincideMetodo =
      !metodo || v.metodoPago.toLowerCase().includes(metodo);

    return coincideCliente && coincideEstado && coincideMetodo;
  });

  mostrarVentas(filtradas);
}

let productos = [];

async function cargarProductos() {
  const res = await fetch("http://localhost:8080/productos");
  productos = await res.json();
}

function buscarProductoPorCodigo(codigoBarras) {
  if (!codigoBarras) return null;

  const codigoNormalizado = codigoBarras.toString().trim().toLowerCase();

  return (
    productos.find(
      (p) =>
        p.codigoBarras &&
        p.codigoBarras.toString().trim().toLowerCase() === codigoNormalizado
    ) || null
  );
}

function renderizarEncabezadoDetalle(contenedor) {
  contenedor.innerHTML = `
        <div class="fila-producto encabezado">
            <span>Código de barras</span>
            <span>Producto</span>
            <span>Cantidad</span>
            <span>Precio</span>
            <span>Subtotal</span>
            <span></span>
        </div>
    `;
}

function inicializarDetalleProductos(contenedor, totalInput, detalles = []) {
  renderizarEncabezadoDetalle(contenedor);

  if (detalles.length > 0) {
    detalles.forEach((det) => crearFilaProducto(contenedor, totalInput, det));
  } else {
    crearFilaProducto(contenedor, totalInput);
  }

  const primerCodigo = contenedor.querySelector(".codigo-barras");
  if (primerCodigo) {
    primerCodigo.focus();
  }
}

function crearFilaProducto(contenedor, totalInput, detalle = null) {
  const div = document.createElement("div");
  div.className = "fila-producto";

  const productoDetalle = detalle?.producto || null;
  const productoId = productoDetalle?.id || detalle?.productoId || "";
  const codigoBarras = productoDetalle?.codigoBarras || "";
  const nombreProducto = productoDetalle?.nombre || "";
  const cantidadInicial = detalle?.cantidad || 1;
  const precioInicial = productoDetalle?.precio || 0;
  const subtotalInicial = precioInicial * cantidadInicial;

  div.innerHTML = `
        <input type="text" class="codigo-barras" placeholder="Código de barras" value="${codigoBarras}">
        <input type="hidden" class="producto-id" value="${productoId}">
      <input type="text" class="nombre-producto" placeholder="Nombre del producto" value="${nombreProducto}" readonly>
        <input type="number" class="cantidad" min="1" value="${cantidadInicial}">
        <input type="number" class="precio" readonly value="${precioInicial}">
        <input type="number" class="subtotal" readonly value="${subtotalInicial}">
        <button type="button" class="eliminar">X</button>
    `;

  contenedor.appendChild(div);

  const codigoInput = div.querySelector(".codigo-barras");
  const productoIdInput = div.querySelector(".producto-id");
  const nombreInput = div.querySelector(".nombre-producto");
  const cantidadInput = div.querySelector(".cantidad");
  const precioInput = div.querySelector(".precio");
  const subtotalInput = div.querySelector(".subtotal");

  function actualizarFila(mostrarError = false) {
    const producto = buscarProductoPorCodigo(codigoInput.value);

    if (!producto) {
      productoIdInput.value = "";
      nombreInput.value = "";
      precioInput.value = 0;
      subtotalInput.value = 0;
      recalcularTotal(contenedor, totalInput);

      if (mostrarError && codigoInput.value.trim()) {
        Swal.fire("No se encontró producto para ese código", "", "warning");
      }

      return;
    }

    productoIdInput.value = producto.id;
    nombreInput.value = producto.nombre;
    precioInput.value = producto.precio;
    subtotalInput.value = producto.precio * (parseInt(cantidadInput.value, 10) || 0);

    codigoInput.title = producto.nombre;

    recalcularTotal(contenedor, totalInput);
  }

  codigoInput.addEventListener("change", () => actualizarFila(true));
  codigoInput.addEventListener("blur", () => actualizarFila(true));
  cantidadInput.addEventListener("input", () => actualizarFila(false));

  div.querySelector(".eliminar").onclick = () => {
    div.remove();
    recalcularTotal(contenedor, totalInput);
  };

  actualizarFila(false);
}

  async function abrirAbmVenta(id = null) {
    await cargarProductos();

    // 📦 Crear overlay
    const tpl = document.getElementById("abmVentas");
    const overlay = document.createElement("div");

    overlay.id = "abmOverlay";
    overlay.appendChild(tpl.content.cloneNode(true));
    document.body.appendChild(overlay);

    const form = overlay.querySelector("#ventaForm");
    const contenedor = overlay.querySelector("#detalleProductos");
    const totalInput = overlay.querySelector("#totalVenta");
    const estadoContainer = overlay.querySelector("#estadoContainer");

    const selectMetodo = overlay.querySelector('[name="metodoPago"]');
    const selectEstado = overlay.querySelector('[name="estado"]');

    

    await cargarMetodosPago(selectMetodo);
    await cargarEstados(selectEstado);

    if (!id) {
      estadoContainer.style.display = "none";
    } else {
      estadoContainer.style.display = "block";
    }

    inicializarDetalleProductos(contenedor, totalInput);

    const close = () => overlay.remove();

    overlay.querySelector("#btnCerrar").onclick = close;
    overlay.querySelector("#btnCancelar").onclick = close;

    overlay.querySelector("#btnAgregarProducto").onclick = () => {
      agregarFilaProducto(contenedor, totalInput);
    };

    // =========================
    // 🔥 CARGAR VENTA (EDICIÓN)
    // =========================
    if (id) {
      try {
        const res = await fetch(`http://localhost:8080/ventas/${id}`);
        const venta = await res.json();

        console.log("VENTA:", venta);
        console.log("DETALLES:", venta.detalles);

        form.nombreCliente.value = venta.nombreCliente || "";
        form.metodoPago.value = venta.metodoPago || "";
        form.estado.value = venta.estado || "";
        totalInput.value = venta.total || 0;
        console.log("HTML generado:", overlay.innerHTML);
        inicializarDetalleProductos(contenedor, totalInput, venta.detalles || []);
      } catch (error) {
        console.error("Error cargando venta:", error);
        Swal.fire("Error al cargar la venta", "", "error");
      }
    }

    // =========================
    // 💾 GUARDAR
    // =========================
    form.addEventListener("submit", async (e) => {
      e.preventDefault();

      const data = Object.fromEntries(new FormData(form).entries());
      const detalles = [];

      contenedor.querySelectorAll(".fila-producto").forEach((fila) => {
        const productoIdInput = fila.querySelector(".producto-id");
        const cantidadInput = fila.querySelector(".cantidad");

        if (!productoIdInput || !cantidadInput) return;

        const productoId = productoIdInput.value;
        const cantidad = cantidadInput.value;

        if (productoId && cantidad > 0) {
          detalles.push({
            productoId: parseInt(productoId),
            cantidad: parseInt(cantidad),
          });
        }
      });

      if (detalles.length === 0) {
        Swal.fire("Debe agregar al menos un producto", "", "warning");
        return;
      }

      const venta = {
        nombreCliente: data.nombreCliente,
        metodoPago: data.metodoPago,
        detalles: detalles,
      };

      if (id) {
        venta.estado = data.estado;
      }

      const url = id
        ? `http://localhost:8080/ventas/${id}`
        : "http://localhost:8080/ventas";

      const method = id ? "PUT" : "POST";

      try {
        await fetch(url, {
          method: method,
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(venta),
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

function actualizarResumenVentas(ventas) {

    let pendientes = 0;
    let devueltas = 0;
    let canceladas = 0;
    let abonadas = 0;

    ventas.forEach(v => {

        switch (v.estado) {

            case "PENDIENTE":
                pendientes++;
                break;

            case "DEVUELTA":
                devueltas++;
                break;

            case "CANCELADA":
            case "ANULADA":
                canceladas++;
                break;

            case "ABONADA":
                abonadas++;
                break;
        }
    });

    document.getElementById("contPendientes").textContent = pendientes;
    document.getElementById("contDevueltas").textContent = devueltas;
    document.getElementById("contCanceladas").textContent = canceladas;
    document.getElementById("contAbonadas").textContent = abonadas;
}

function agregarFilaProducto(contenedor, totalInput) {
  crearFilaProducto(contenedor, totalInput);
}

function recalcularTotal(contenedor, totalInput) {
  let total = 0;

  const subtotales = contenedor.querySelectorAll(".subtotal");

  if (subtotales.length === 0) {
    totalInput.value = 0;
    return;
  }

  subtotales.forEach((input) => {
    total += parseFloat(input.value) || 0;
  });

  totalInput.value = total;
}

async function cargarMetodosPago(select) {
  const res = await fetch("http://localhost:8080/metodos-pago");
  const metodos = await res.json();

  select.innerHTML = '<option value="">Seleccione</option>';

  metodos.forEach((m) => {
    const option = document.createElement("option");
    option.value = m;
    option.textContent = m;
    select.appendChild(option);
  });
}

async function cargarEstados(select) {
  try {
    const res = await fetch("http://localhost:8080/estados-venta");

    if (!res.ok) {
      throw new Error("No se pudo cargar estados");
    }

    const estados = await res.json();

    if (!Array.isArray(estados)) {
      throw new Error("Formato inválido de estados");
    }

    select.innerHTML = '<option value="">--Seleccione--</option>';

    estados.forEach((e) => {
      const option = document.createElement("option");
      option.value = e;
      option.textContent = e;
      select.appendChild(option);
    });

    console.log("ESTADOS:", estados);
  } catch (error) {
    console.error("Error cargando estados:", error);
    Swal.fire("Error al cargar estados", "", "error");
  }
}

async function imprimirTicket(id) {
  try {
    const res = await fetch(`http://localhost:8080/ventas/${id}/ticket`);

    if (!res.ok) {
      throw new Error("No se pudo obtener el ticket");
    }

    const ticket = await res.text();
    const printArea = document.getElementById("printArea");

    printArea.innerHTML = `<pre>${ticket}</pre>`;
    printArea.style.display = "block";

    window.print();

    printArea.style.display = "none";
  } catch (error) {
    console.error("Error imprimiendo ticket:", error);
    Swal.fire("Error al imprimir ticket", "", "error");
  }
}
