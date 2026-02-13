document.getElementById('verificarForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const codigoInput = document.getElementById('codigo');
    const codigoVal = codigoInput.value;
    const usernameVal = localStorage.getItem('usuarioPendiente');

    // 1. Verificación básica antes de enviar
    if (!usernameVal) {
        Swal.fire({
            title: 'Error',
            text: 'No hay un usuario pendiente de verificación.',
            icon: 'error'
        });
        return;
    }

    const datos = {
        username: usernameVal,
        codigo: codigoVal
    };

    try {
        // 2. Llamada a tu controlador Java
        const response = await fetch('http://localhost:8080/usuarios/verificar', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(datos)
        });

        const resultado = await response.json();

        if (response.ok) {
            // --- CASO DE ÉXITO ---
            await Swal.fire({
                title: '¡Verificación Exitosa!',
                text: resultado.mensaje, // "Usuario verificado exitosamente..."
                icon: 'success',
                confirmButtonText: 'Ir al Login',
                confirmButtonColor: '#3085d6',
                allowOutsideClick: false
            });

            // Limpiamos el dato temporal y redirigimos
            localStorage.removeItem('usuarioPendiente');
            window.location.href = "login.html";

        } else {
            // --- CASO DE ERROR (Código incorrecto, expirado, etc.) ---
            Swal.fire({
                title: 'Error de Verificación',
                text: resultado.mensaje || 'El código ingresado es incorrecto.',
                icon: 'error',
                confirmButtonText: 'Intentar de nuevo',
                confirmButtonColor: '#d33'
            });
            
            // Opcional: limpiar el input para que lo intente de nuevo
            codigoInput.value = '';
            codigoInput.focus();
        }

    } catch (error) {
        // --- ERROR DE CONEXIÓN ---
        console.error("Error:", error);
        Swal.fire({
            title: 'Error de Servidor',
            text: 'No se pudo conectar con el sistema. Inténtalo más tarde.',
            icon: 'error'
        });
    }
});