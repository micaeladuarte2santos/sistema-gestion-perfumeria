document.getElementById('formReset1').addEventListener('submit', async (e) => {
    e.preventDefault();
    const username = document.getElementById('username').value;
    const submitBtn = document.querySelector('#formReset1 .btn-submit');

    submitBtn.disabled = true;
    const textoOriginalBoton = submitBtn.textContent;
    submitBtn.textContent = 'Aguarde un instante...';

    Swal.fire({
        title: 'Aguarde un instante...',
        text: 'Estamos enviando el codigo a su correo.',
        allowOutsideClick: false,
        didOpen: () => {
            Swal.showLoading();
        }
    });

    try {
        const response = await fetch('http://localhost:8080/usuarios/password-reset/solicitar-codigo', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username })
        });
        
        if (response.ok) {
            Swal.close();
            localStorage.setItem('usuarioParaReset', username);
            await Swal.fire({
                title: 'Codigo enviado',
                text: 'Te enviamos un codigo al correo asociado al usuario.',
                icon: 'success',
                confirmButtonText: 'Continuar'
            });
            window.location.href = 'resetPass2.html';
        } else {
            const resultado = await response.json();
            Swal.fire({
                title: 'No se pudo enviar el codigo',
                text: resultado.mensaje || 'Verifica el usuario ingresado e intenta nuevamente.',
                icon: 'error',
                confirmButtonText: 'Reintentar',
                confirmButtonColor: '#3085d6'
            });
        }
    } catch (error) {
        Swal.close();
        Swal.fire('Error', 'No se pudo conectar con el servidor.', 'error');
    } finally {
        submitBtn.disabled = false;
        submitBtn.textContent = textoOriginalBoton;
    }
});