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

    if (e.target.classList.contains("btn-eliminar")) {
      eliminarVenta(e.target.dataset.id);
    }
  });
});

let ventasGlobal = [];


function cargarVentas() {
  fetch("http://localhost:8080/ventas")
    .then(res => res.json())
    .then(data => {
      ventasGlobal = data;

      mostrarVentas(data);
      actualizarResumenVentas(data); // 🔥 AGREGAR ESTA LÍNEA
    })
    .catch(err => console.error("Error:", err));
}

function renderizarPaginacion(data) {
  const contenedor = document.getElementById("paginacion");
  contenedor.innerHTML = "";

  // botón anterior
  const prev = document.createElement("button");
  prev.textContent = "←";
  prev.disabled = data.first;
  prev.onclick = () => {
    paginaActual--;
    cargarVentas();
  };

  contenedor.appendChild(prev);

  // páginas
  for (let i = 0; i < data.totalPages; i++) {
    const btn = document.createElement("button");
    btn.textContent = i + 1;

    if (i === data.number) {
      btn.style.fontWeight = "bold";
    }

    btn.onclick = () => {
      paginaActual = i;
      cargarVentas();
    };

    contenedor.appendChild(btn);
  }

  // botón siguiente
  const next = document.createElement("button");
  next.textContent = "→";
  next.disabled = data.last;
  next.onclick = () => {
    paginaActual++;
    cargarVentas();
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
            <span>ACCIONES</span>
        </div>
    `;

  if (!ventas || ventas.length === 0) {
    total.textContent = "0";
    return;
  }

  total.textContent = ventas.length;

  ventas.forEach((v) => {
    const fila = document.createElement("div");
    fila.className = "fila";

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

function aplicarFiltros() {
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

        contenedor.innerHTML = "";

        (venta.detalles || []).forEach((det) => {
          const div = document.createElement("div");
          div.className = "fila-producto";

          const productoId = det.producto?.id || det.productoId;

          div.innerHTML = `
            <select class="producto-select">
              <option value="">--Producto--</option>
              ${productos
                .map(
                  (p) => `
                  <option value="${p.id}" 
                    data-precio="${p.precio}"
                    ${p.id === productoId ? "selected" : ""}>
                    ${p.nombre}
                  </option>
                `
                )
                .join("")}
            </select>

            <input type="number" class="cantidad" value="${det.cantidad}" min="1">
            <input type="number" class="precio" readonly>
            <input type="number" class="subtotal" readonly>

            <button type="button" class="eliminar">X</button>
          `;

          contenedor.appendChild(div);

          const select = div.querySelector(".producto-select");
          const cantidad = div.querySelector(".cantidad");
          const precio = div.querySelector(".precio");
          const subtotal = div.querySelector(".subtotal");

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

          div.querySelector(".eliminar").onclick = () => {
            div.remove();
            recalcularTotal(contenedor, totalInput);
          };

          actualizar(); // 🔥 CLAVE
        });
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
        const select = fila.querySelector(".producto-select");
        const cantidadInput = fila.querySelector(".cantidad");

        if (!select || !cantidadInput) return;

        const productoId = select.value;
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
  const div = document.createElement("div");
  div.className = "fila-producto";

  div.innerHTML = `
        <select class="producto-select">
            <option value="">--Producto--</option>
            ${productos.map((p) => `<option value="${p.id}" data-precio="${p.precio}">${p.nombre}</option>`).join("")}
        </select>

        <input type="number" class="cantidad" min="1" value="1">
        <input type="number" class="precio" readonly>
        <input type="number" class="subtotal" readonly>

        <button type="button" class="eliminar">X</button>
    `;

  contenedor.appendChild(div);

  const select = div.querySelector(".producto-select");
  const cantidad = div.querySelector(".cantidad");
  const precio = div.querySelector(".precio");
  const subtotal = div.querySelector(".subtotal");

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

  div.querySelector(".eliminar").onclick = () => {
    div.remove();
    recalcularTotal(contenedor, totalInput);
  };
}

function recalcularTotal(contenedor, totalInput) {
  let total = 0;

  const subtotales = contenedor.querySelectorAll(".subtotal");

  if (subtotales.length === 0) return; // 🔥 evita pisar el total

  subtotales.forEach((input) => {
    total += parseFloat(input.value) || 0;
  });

  if (total > 0) {
    totalInput.value = total;
  }
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

    // 🔥 validación clave
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
