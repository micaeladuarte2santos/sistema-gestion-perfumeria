document.addEventListener("DOMContentLoaded", () => {

    cargarProveedores();

    const buscarNombre = document.getElementById("proveedorNombre");
    const buscarEmail = document.getElementById("proveedorEmail");
    const buscarTelefono = document.getElementById("proveedorTelefono");

    buscarTelefono?.addEventListener("input", filtrarProveedores);

    buscarNombre?.addEventListener("input", filtrarProveedores);
    buscarEmail?.addEventListener("input", filtrarProveedores);

    const btn = document.getElementById("cargarProveedorBtn");
    if (btn) {
        btn.addEventListener("click", () => abrirAbmProveedor());
    }


});

const ELEMENTOS_POR_PAGINA = 5;
let paginaActual = 1;
let proveedoresCache = [];

function limpiarTelefono(telefono = "") {
    return telefono.replace(/\D/g, "");
}

async function obtenerMensajeError(res, mensajePorDefecto) {
    try {
        const text = await res.text();
        if (!text) return mensajePorDefecto;

        try {
            const data = JSON.parse(text);
            if (data?.mensaje) return data.mensaje;
            if (data?.message) return data.message;
        } catch {
            return text;
        }

        return text;
    } catch {
        return mensajePorDefecto;
    }
}

function filtrarProveedores(){

    const nombre = document.getElementById("proveedorNombre").value.toLowerCase();
    const email = document.getElementById("proveedorEmail").value.toLowerCase();
    const telefono = document.getElementById("proveedorTelefono").value.toLowerCase();

    const filtrados = proveedoresCache.filter(p => {

        return (
            p.nombre.toLowerCase().includes(nombre) &&
            (p.email || "").toLowerCase().includes(email) &&
            (p.telefono || "").toLowerCase().includes(telefono)
        );

    });

    paginaActual = 1;

    renderProveedores(filtrados);
}


function filtrarProveedoresSinReset() {

    const nombre = document.getElementById("proveedorNombre").value.toLowerCase();
    const email = document.getElementById("proveedorEmail").value.toLowerCase();
    const telefono = document.getElementById("proveedorTelefono").value.toLowerCase();

    const filtrados = proveedoresCache.filter(p => {

        return (
            p.nombre.toLowerCase().includes(nombre) &&
            (p.email || "").toLowerCase().includes(email) &&
            (p.telefono || "").toLowerCase().includes(telefono)
        );

    });

    renderProveedores(filtrados);
}



async function abrirAbmProveedor(id = null) {

    let overlay;

    try {

        const res = await fetch('/abmProveedor.html');
        const html = await res.text();

        overlay = document.createElement('div');
        overlay.id = 'abmOverlayProveedor';
        overlay.innerHTML = html;

        document.body.appendChild(overlay);

    } catch (e) {

        console.error("No se pudo abrir el ABM proveedor", e);
        return;

    }

    const panel = overlay.querySelector('.abm-panel');
    const form = overlay.querySelector("#proveedorForm");
    const telefonoInput = overlay.querySelector("#telefonoProveedor");
    const btnCerrar = overlay.querySelector("#btnCerrarProveedor");
    const btnCancelar = overlay.querySelector("#btnCancelarProveedor");

    const close = () => {
        overlay.remove();
        document.removeEventListener("keydown", onKeyDown);
    };

    const onKeyDown = (e) => {
        if (e.key === "Escape") close();
    };

    document.addEventListener("keydown", onKeyDown);

    telefonoInput?.addEventListener("input", () => {
        telefonoInput.value = limpiarTelefono(telefonoInput.value);
    });

    btnCerrar?.addEventListener("click", close);
    btnCancelar?.addEventListener("click", close);

    overlay.addEventListener("click", (e) => {
        if (!panel.contains(e.target)) close();
    });

    // EDITAR PROVEEDOR
    if (id) {

        try {

            const res = await fetch(`http://localhost:8080/proveedores/${id}`);

            if (!res.ok) {
                const mensaje = await obtenerMensajeError(res, "No se pudo cargar el proveedor");
                throw new Error(mensaje);
            }

            const proveedor = await res.json();

            overlay.querySelector("#nombreProveedor").value = proveedor.nombre || '';
            overlay.querySelector("#emailProveedor").value = proveedor.email || '';
            overlay.querySelector("#telefonoProveedor").value = proveedor.telefono || '';

            const titulo = overlay.querySelector("#abmProveedorTitle");
            if (titulo) titulo.textContent = "Editar Proveedor: " + proveedor.nombre;

        } catch (err) {

            console.error("Error cargando proveedor:", err);

            Swal.fire({
                icon: "error",
                title: "Error",
                text: err.message || "No se pudo cargar el proveedor"
            });

        }

    }

    // GUARDAR
    form.addEventListener("submit", async e => {

        e.preventDefault();

        const data = Object.fromEntries(new FormData(form).entries());

        const proveedor = {
            nombre: data.nombre,
            email: data.email,
            telefono: limpiarTelefono(data.telefono || "")
        };

        const url = id
            ? `http://localhost:8080/proveedores/${id}`
            : `http://localhost:8080/proveedores`;

        const method = id ? "PUT" : "POST";

        try {

            const res = await fetch(url, {
                method: method,
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(proveedor)
            });

            if (!res.ok) {
                const mensaje = await obtenerMensajeError(res, "Error al guardar proveedor");
                throw new Error(mensaje);
            }

            close();
            cargarProveedores();

        } catch (err) {

            Swal.fire({
                icon: "error",
                title: "Error",
                text: err.message
            });

        }

    });

}

function renderProveedores(proveedores){

const lista = document.getElementById("lista-proveedores");
const total = document.getElementById("totalCount");

const encabezado = lista.querySelector(".encabezado");

lista.innerHTML = "";
lista.appendChild(encabezado);

const totalPaginas = Math.max(1, Math.ceil(proveedores.length / ELEMENTOS_POR_PAGINA));
if (paginaActual > totalPaginas) paginaActual = totalPaginas;

const inicio = (paginaActual - 1) * ELEMENTOS_POR_PAGINA;
const pagina = proveedores.slice(inicio, inicio + ELEMENTOS_POR_PAGINA);

pagina.forEach(p => {

const fila = document.createElement("div");
fila.className = "fila";

fila.innerHTML = `
<span>${p.nombre}</span>
<span>${p.email || '-'}</span>
<span>${p.telefono || '-'}</span>
<span>
<button onclick="abrirAbmProveedor(${p.id})">✏️</button>
<button onclick="eliminarProveedor(${p.id})">🗑️</button>
</span>
`;

lista.appendChild(fila);

});

if(total) total.textContent = proveedores.length;
renderizarPaginacionProveedores(proveedores.length, totalPaginas);
}


function renderizarPaginacionProveedores(totalItems, totalPaginas) {

const contenedor = document.getElementById("paginacion-proveedores");
if (!contenedor) return;

contenedor.innerHTML = "";

const prev = document.createElement("button");
prev.textContent = "Anterior";
prev.disabled = paginaActual === 1;
prev.addEventListener("click", () => {
    paginaActual--;
    filtrarProveedoresSinReset();
});
contenedor.appendChild(prev);

for (let i = 1; i <= totalPaginas; i++) {
    const btn = document.createElement("button");
    btn.textContent = i;
    if (i === paginaActual) btn.classList.add("activo");
    btn.addEventListener("click", () => {
        paginaActual = i;
        filtrarProveedoresSinReset();
    });
    contenedor.appendChild(btn);
}

const next = document.createElement("button");
next.textContent = "Siguiente";
next.disabled = paginaActual === totalPaginas;
next.addEventListener("click", () => {
    paginaActual++;
    filtrarProveedoresSinReset();
});
contenedor.appendChild(next);
}

async function cargarProveedores() {

    try {

        const res = await fetch("http://localhost:8080/proveedores");

        if (!res.ok) throw new Error("Error al cargar proveedores");

        proveedoresCache = await res.json();

        filtrarProveedores();

    } catch (err) {

        console.error("Error cargando proveedores:", err);

        Swal.fire({
            icon: "error",
            title: "Error",
            text: "No se pudieron cargar los proveedores"
        });

    }

}

async function eliminarProveedor(id) {

    const confirm = await Swal.fire({
        title: "¿Eliminar proveedor?",
        icon: "warning",
        showCancelButton: true,
        confirmButtonText: "Eliminar"
    });

    if (!confirm.isConfirmed) return;

    try {

        const res = await fetch(`http://localhost:8080/proveedores/${id}`, {
            method: "DELETE"
        });

        if (!res.ok) {
            const mensaje = await obtenerMensajeError(res, "No se pudo eliminar");
            throw new Error(mensaje);
        }

        cargarProveedores();

    } catch (err) {

        Swal.fire({
            icon: "error",
            title: "Error",
            text: err.message
        });

    }

}