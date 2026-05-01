document.getElementById('verificarForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const codigoInput = document.getElementById('codigo');
    const codigoVal = codigoInput.value;
    const usernameVal = localStorage.getItem('usuarioPendiente');

   
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
       
        const response = await fetch('http://localhost:8080/usuarios/verificar', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(datos)
        });

        const resultado = await response.json();

        if (response.ok) {
           
            await Swal.fire({
                title: '¡Verificación Exitosa!',
                text: resultado.mensaje, 
                icon: 'success',
                confirmButtonText: 'Ir al Login',
                confirmButtonColor: '#3085d6',
                allowOutsideClick: false
            });

          
            localStorage.removeItem('usuarioPendiente');
            window.location.href = "login.html";

        } else {
           
            Swal.fire({
                title: 'Error de Verificación',
                text: resultado.mensaje || 'El código ingresado es incorrecto.',
                icon: 'error',
                confirmButtonText: 'Intentar de nuevo',
                confirmButtonColor: '#d33'
            });
            
           
            codigoInput.value = '';
            codigoInput.focus();
        }

    } catch (error) {
      
        console.error("Error:", error);
        Swal.fire({
            title: 'Error de Servidor',
            text: 'No se pudo conectar con el sistema. Inténtalo más tarde.',
            icon: 'error'
        });
    }
});