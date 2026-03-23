    document.addEventListener("DOMContentLoaded", () => {

        cargarCategorias();

        const btn = document.getElementById("cargarCategoriaBtn");
        btn?.addEventListener("click", () => abrirAbmCategoria());

        const buscador = document.getElementById("categoriaNombre");
        buscador?.addEventListener("input", filtrarCategorias);

    });

    const ELEMENTOS_POR_PAGINA = 5;
    let paginaActual = 1;
    let categoriasCache = [];


    async function cargarCategorias(){

    try{

    const res = await fetch("http://localhost:8080/categorias");

    if(!res.ok) throw new Error("Error cargando categorías");

    categoriasCache = await res.json();

    filtrarCategorias();

    }catch(err){

    console.error(err);

    Swal.fire({
    icon:"error",
    title:"Error",
    text:"No se pudieron cargar las categorías"
    });

    }

    }


    function renderCategorias(categorias){

    const lista = document.getElementById("lista-categorias");
    const total = document.getElementById("totalCount");

    const encabezado = lista.querySelector(".encabezado");

    lista.innerHTML="";
    lista.appendChild(encabezado);

    const totalPaginas = Math.max(1, Math.ceil(categorias.length / ELEMENTOS_POR_PAGINA));
    if (paginaActual > totalPaginas) paginaActual = totalPaginas;

    const inicio = (paginaActual - 1) * ELEMENTOS_POR_PAGINA;
    const pagina = categorias.slice(inicio, inicio + ELEMENTOS_POR_PAGINA);

    pagina.forEach(c=>{

    const fila = document.createElement("div");
    fila.className="fila";

    fila.innerHTML=`
    <span>${c.nombre}</span>

    <span>
    <button onclick="abrirAbmCategoria(${c.id})">✏️</button>
    <button onclick="eliminarCategoria(${c.id})">🗑️</button>
    </span>
    `;

    lista.appendChild(fila);

    });

    total.textContent = categorias.length;
    renderizarPaginacionCategorias(categorias.length, totalPaginas);

    }


    function filtrarCategorias(){

    const nombre = document.getElementById("categoriaNombre").value.toLowerCase();

    const filtradas = categoriasCache.filter(c =>
    c.nombre.toLowerCase().includes(nombre)
    );

    paginaActual = 1;

    renderCategorias(filtradas);

    }


    function renderizarPaginacionCategorias(totalItems, totalPaginas) {

    const contenedor = document.getElementById("paginacion-categorias");
    if (!contenedor) return;

    contenedor.innerHTML = "";

    if (totalItems <= ELEMENTOS_POR_PAGINA) return;

    const prev = document.createElement("button");
    prev.textContent = "Anterior";
    prev.disabled = paginaActual === 1;
    prev.addEventListener("click", () => {
        paginaActual--;
        filtrarCategoriasSinReset();
    });
    contenedor.appendChild(prev);

    for (let i = 1; i <= totalPaginas; i++) {
        const btn = document.createElement("button");
        btn.textContent = i;
        if (i === paginaActual) btn.classList.add("activo");
        btn.addEventListener("click", () => {
            paginaActual = i;
            filtrarCategoriasSinReset();
        });
        contenedor.appendChild(btn);
    }

    const next = document.createElement("button");
    next.textContent = "Siguiente";
    next.disabled = paginaActual === totalPaginas;
    next.addEventListener("click", () => {
        paginaActual++;
        filtrarCategoriasSinReset();
    });
    contenedor.appendChild(next);

    }


    function filtrarCategoriasSinReset() {

    const nombre = document.getElementById("categoriaNombre").value.toLowerCase();

    const filtradas = categoriasCache.filter(c =>
    c.nombre.toLowerCase().includes(nombre)
    );

    renderCategorias(filtradas);

    }


async function abrirAbmCategoria(id = null) {
    let overlay;

    try {
        
        const res = await fetch('/abmCategorias.html');
        const html = await res.text();

        overlay = document.createElement('div');
        overlay.id = 'abmOverlayCategoria';
        overlay.innerHTML = html;
        document.body.appendChild(overlay);

    } catch (e) {
        console.error("No se pudo abrir el ABM de categoría", e);
        return;
    }

    const panel = overlay.querySelector('.abm-panel');
    const form = overlay.querySelector("#categoriaForm");
    const btnCerrar = overlay.querySelector("#btnCerrarCategoria");
    const btnCancelar = overlay.querySelector("#btnCancelarCategoria");
    const titulo = overlay.querySelector("#abmCategoriaTitle");

    
    const close = () => {
        overlay.remove();
        document.removeEventListener("keydown", onKeyDown);
    };

   
    const onKeyDown = (e) => {
        if (e.key === "Escape") close();
    };
    document.addEventListener("keydown", onKeyDown);

    
    if(btnCerrar) btnCerrar.addEventListener("click", close);
    if(btnCancelar) btnCancelar.addEventListener("click", close);
    
    overlay.addEventListener("click", (e) => {
        if (!panel.contains(e.target)) close();
    });

    
    if (id) {
        try {
            const res = await fetch(`http://localhost:8080/categorias/${id}`);
            if (!res.ok) throw new Error("No se pudo cargar la categoría");
            const categoria = await res.json();

            overlay.querySelector("#nombreCategoria").value = categoria.nombre || '';
            if (titulo) titulo.textContent = "Editar Categoría: " + categoria.nombre;

        } catch (err) {
            console.error("Error cargando categoría:", err);
            Swal.fire({
                icon: "error",
                title: "Error",
                text: "No se pudo cargar la categoría"
            });
        }
    } else {
        if (titulo) titulo.textContent = "Nueva Categoría";
    }

    // Guardar categoría
    form.addEventListener("submit", async e => {
        e.preventDefault();

        const data = Object.fromEntries(new FormData(form).entries());
        const categoria = { nombre: data.nombre };

        const url = id
            ? `http://localhost:8080/categorias/${id}`
            : `http://localhost:8080/categorias`;
        const method = id ? "PUT" : "POST";

        try {
            const res = await fetch(url, {
                method: method,
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(categoria)
            });

            if (!res.ok) {
                const text = await res.text();
                throw new Error(text || "Error al guardar categoría");
            }

            close();
            cargarCategorias(); 
        } catch (err) {
            Swal.fire({
                icon: "error",
                title: "Error",
                text: err.message
            });
        }
    });
}


    async function eliminarCategoria(id){

    const confirm = await Swal.fire({
    title:"¿Eliminar categoría?",
    icon:"warning",
    showCancelButton:true
    });

    if(!confirm.isConfirmed) return;

    await fetch(`http://localhost:8080/categorias/${id}`,{
    method:"DELETE"
    });

    cargarCategorias();

    }