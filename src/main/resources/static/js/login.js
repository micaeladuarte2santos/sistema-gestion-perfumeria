document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault(); 

    const usernameInput = document.getElementById('username').value;
    const passwordInput = document.getElementById('password').value;

    try {
        const response = await fetch('http://localhost:8080/usuarios/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                username: usernameInput,
                password: passwordInput
            })
        });

        if (response.ok) {
            
            const data = await response.json();
            localStorage.setItem('usuarioLogueado', data.username);
            window.location.href = "home.html";
            

        } else {
            // Error de credenciales (401 Unauthorized)
            Swal.fire({
                icon: 'error',
                title: 'Acceso Denegado',
                text: 'Usuario o contraseña incorrectos',
                confirmButtonColor: '#d33',
                confirmButtonText: 'Reintentar'
            });
        }

    } catch (error) {
        console.error("Error de conexión:", error);
        // Error de servidor o conexión
        Swal.fire({
            icon: 'warning',
            title: 'Error de conexión',
            text: 'No se pudo conectar con el servidor. Intenta más tarde.',
            confirmButtonColor: '#f8bb86'
        });
    }
});