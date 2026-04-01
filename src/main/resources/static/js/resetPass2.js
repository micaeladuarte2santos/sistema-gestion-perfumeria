document.getElementById('formReset2').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const codigo = document.getElementById('codigo').value;
    const pass1 = document.getElementById('pass1').value;
    const pass2 = document.getElementById('pass2').value;
    const username = localStorage.getItem('usuarioParaReset');

    if (!username) {
        window.location.href = "resetPass1.html";
        return;
    }

    // Validar que coincidan
    if (pass1 !== pass2) {
        Swal.fire('Error', 'Las contraseñas no coinciden.', 'warning');
        return;
    }

    try {
        const response = await fetch('http://localhost:8080/usuarios/password-reset/confirmar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                username: username,
                codigo: codigo,
                nuevoPassword: pass1
            })
        });

        if (response.ok) {
            await Swal.fire({
                title: '¡Clave actualizada!',
                text: 'Tu contraseña ha sido restablecida con éxito.',
                icon: 'success',
                confirmButtonText: 'Ir al Login'
            });
            localStorage.removeItem('usuarioParaReset');
            window.location.href = "login.html";
        } else {
            const resultado = await response.json();
            Swal.fire('Error', resultado.mensaje || 'No se pudo actualizar la contrasena.', 'error');
        }
    } catch (error) {
        Swal.fire('Error', 'Error de conexión.', 'error');
    }
});