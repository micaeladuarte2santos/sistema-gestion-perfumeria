document.addEventListener("DOMContentLoaded", () => {

    const nombre = localStorage.getItem("usuarioNombre") || "Usuario";
    const usuarioSpan = document.getElementById("usuarioBienvenida");

    if (usuarioSpan) {
        usuarioSpan.textContent = `Bienvenido, ${nombre}`;
    }
  
    const hoy = new Date().toISOString().split("T")[0];
    const mes = new Date().getMonth() + 1;
    const anio = new Date().getFullYear();

    
    fetch(`http://localhost:8080/ventas/recaudacion/dia?fecha=${hoy}`)
        .then(res => res.json())
        .then(data => {
            const el = document.getElementById("montoHoy");
            if (el) el.textContent = `$${data.toFixed(2)}`;
        })
        .catch(err => console.error("Error monto día:", err));

    
    fetch(`http://localhost:8080/ventas/recaudacion/mes?mes=${mes}&anio=${anio}`)
        .then(res => res.json())
        .then(data => {
            const el = document.getElementById("montoMes");
            if (el) el.textContent = `$${data.toFixed(2)}`;
        })
        .catch(err => console.error("Error monto mes:", err));

    
    fetch(`http://localhost:8080/ventas/mes?mes=${mes}&anio=${anio}`)
        .then(res => res.json())
        .then(data => {
            const el = document.getElementById("ventasMes");
            if (el) el.textContent = data.length;
        })
        .catch(err => console.error("Error ventas mes:", err));

    
    fetch(`http://localhost:8080/ventas/recaudacion/anio?anio=${anio}`)
        .then(res => res.json())
        .then(data => {
            const el = document.getElementById("montoAnio");
            if (el) el.textContent = `$${data.toFixed(2)}`;
        })
        .catch(err => console.error("Error monto año:", err));

    
    fetch(`http://localhost:8080/ventas/anio?anio=${anio}`)
        .then(res => res.json())
        .then(data => {
            const el = document.getElementById("ventasAnio");
            if (el) el.textContent = data.length;
        })
        .catch(err => console.error("Error ventas año:", err));

});