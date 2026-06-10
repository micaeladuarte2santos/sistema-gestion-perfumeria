document.getElementById('signupForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const btnRegister = document.getElementById('btnRegister');
    const loaderContainer = document.getElementById('loaderContainer');

    const usernameVal = document.getElementById('username').value;
    const passwordVal = document.getElementById('password').value;
    const nombreVal = document.getElementById('nombre').value;
    const apellidoVal = document.getElementById('apellido').value;
    const emailVal = document.getElementById('email').value;
    const fechaVal = document.getElementById('fechaNacimiento').value;

 
    const datos = {
        username: usernameVal,
        password: passwordVal,
        nombre: nombreVal,
        apellido: apellidoVal,
        email: emailVal,
        fechaNacimiento: fechaVal 
    };

    // Mostrar loader y deshabilitar botón
    btnRegister.disabled = true;
    loaderContainer.classList.add('active');

    try {
        const response = await fetch('http://localhost:8080/usuarios', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(datos)
        });

        const resultado = await response.json();

        if (response.ok) {
           
            Swal.fire({
                title: '¡Registro Exitoso!',
                text: resultado.mensaje || 'Te hemos enviado un código de verificación.',
                icon: 'success',
                confirmButtonText: 'Ir a verificar',
                confirmButtonColor: '#3085d6',
                allowOutsideClick: false 
            }).then((result) => {
                if (result.isConfirmed) {
                    
                    localStorage.setItem('usuarioPendiente', datos.username);
                    window.location.href = "verificarUsuario.html";
                }
            });
        } else {
            Swal.fire({
                title: 'Error',
                text: resultado.mensaje || 'Datos incorrectos',
                icon: 'error',
                confirmButtonText: 'Reintentar'
            });
            
            // Ocultar loader y habilitar botón
            btnRegister.disabled = false;
            loaderContainer.classList.remove('active');
        } 
    } catch (error) {
        console.error("Error de red:", error);
        alert("No se pudo conectar con el servidor.");
        
        // Ocultar loader y habilitar botón
        btnRegister.disabled = false;
        loaderContainer.classList.remove('active');
    }
});