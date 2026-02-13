document.getElementById('formReset1').addEventListener('submit', async (e) => {
    e.preventDefault();
    const username = document.getElementById('username').value;

    try {
        // Asumiendo que tienes un endpoint que verifica existencia
        const response = await fetch(`http://localhost:8080/usuarios/existe/${username}`);
        
        if (response.ok) {
            // Si el usuario existe, lo guardamos para el siguiente paso
            localStorage.setItem('usuarioParaReset', username);
            window.location.href = "resetPass2.html";
        } else {
            // Pop-up de error si no existe
            Swal.fire({
                title: 'Usuario no encontrado',
                text: 'El nombre de usuario ingresado no existe en nuestro sistema.',
                icon: 'error',
                confirmButtonText: 'Reintentar',
                confirmButtonColor: '#3085d6'
            });
        }
    } catch (error) {
        Swal.fire('Error', 'No se pudo conectar con el servidor.', 'error');
    }
});