document.addEventListener("DOMContentLoaded", async () => {
    // 1. Configuración de Variables y Constantes
    const API_BASE_URL = "http://localhost:8080/ventas";
    const hoy = new Date().toLocaleDateString('en-CA');
    const mes = new Date().getMonth() + 1;
    const anio = new Date().getFullYear();

    // 2. Inicialización de la UI
    inicializarUI();

    // 3. Carga de Datos (Ejecución)
    cargarDashboard(hoy, mes, anio, API_BASE_URL);
});

/** * Maneja todo lo relacionado con el DOM y eventos iniciales
 */
function inicializarUI() {
    // Nombre de usuario
    const nombre = localStorage.getItem("usuarioNombre") || "Usuario";
    const usuarioSpan = document.getElementById("usuarioBienvenida");
    if (usuarioSpan) usuarioSpan.textContent = `Bienvenido, ${nombre}`;

    // Eventos de navegación
    document.querySelectorAll(".card").forEach(card => {
        card.style.cursor = "pointer";
        card.addEventListener("click", () => window.location.href = "ventas.html");
    });
}

/**
 * Orquesta todas las llamadas a la API
 */
async function cargarDashboard(hoy, mes, anio, baseUrl) {
    // Helpers de formateo dentro del contexto
    const fmt = (val) => `$${(Number(val) || 0).toFixed(2)}`;
    const setT = (id, txt) => {
        const el = document.getElementById(id);
        if (el) el.textContent = txt;
    };

    // Agrupamos las promesas para que se disparen en paralelo (más rápido)
    const [ventasHoy, recHoy, devHoy, mDevHoy, vMes, rMes, dMes, mDMes, vAnio, rAnio, dAnio, mDAnio] = await Promise.all([
        fetchSeguro(`${baseUrl}/dia?fecha=${hoy}`),
        fetchSeguro(`${baseUrl}/recaudacion/dia?fecha=${hoy}`),
        fetchSeguro(`${baseUrl}/devoluciones/dia?fecha=${hoy}`),
        fetchSeguro(`${baseUrl}/devoluciones/monto/dia?fecha=${hoy}`),
        
        fetchSeguro(`${baseUrl}/mes?mes=${mes}&anio=${anio}`),
        fetchSeguro(`${baseUrl}/recaudacion/mes?mes=${mes}&anio=${anio}`),
        fetchSeguro(`${baseUrl}/devoluciones/mes?mes=${mes}&anio=${anio}`),
        fetchSeguro(`${baseUrl}/devoluciones/monto/mes?mes=${mes}&anio=${anio}`),
        
        fetchSeguro(`${baseUrl}/anio?anio=${anio}`),
        fetchSeguro(`${baseUrl}/recaudacion/anio?anio=${anio}`),
        fetchSeguro(`${baseUrl}/devoluciones/anio?anio=${anio}`),
        fetchSeguro(`${baseUrl}/devoluciones/monto/anio?anio=${anio}`)
    ]);

    // Asignación Masiva a la UI
    setT("ventasHoy", ventasHoy.length || 0);
    setT("montoHoy", fmt(recHoy));
    setT("devHoy", devHoy || 0);
    setT("montoDevHoy", fmt(mDevHoy));

    setT("ventasMes", vMes.length || 0);
    setT("montoMes", fmt(rMes));
    setT("devMes", dMes || 0);
    setT("montoDevMes", fmt(mDMes));

    setT("ventasAnio", vAnio.length || 0);
    setT("montoAnio", fmt(rAnio));
    setT("devAnio", dAnio || 0);
    setT("montoDevAnio", fmt(mDAnio));
}

/**
 * Fetch con manejo de errores centralizado
 */
async function fetchSeguro(url) {
    try {
        const res = await fetch(url);
        if (!res.ok) throw new Error(`Status: ${res.status}`);
        return await res.json();
    } catch (err) {
        console.error(`Error en fetch: ${url}`, err);
        return 0; 
    }
}