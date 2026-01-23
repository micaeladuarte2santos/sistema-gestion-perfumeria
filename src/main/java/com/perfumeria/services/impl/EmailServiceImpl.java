package com.perfumeria.services.impl;

import com.perfumeria.services.IEmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements IEmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Override
    public void enviarCodigoVerificacion(String destinatario, String codigo, String nombreUsuario) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");
            
            helper.setTo(destinatario);
            helper.setSubject("Verificación de Cuenta - Perfumería Flowers");
            
            String contenidoHtml = String.format("""
                <html>
                <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;">
                    <div style="max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 5px rgba(0,0,0,0.1);">
                        <h2 style="color: #333; text-align: center;">¡Bienvenido al Sistema de Gestión de Pefumeria Flowers!</h2>
                        <p style="color: #666; font-size: 16px;">Hola <strong>%s</strong>,</p>
                        <p style="color: #666; font-size: 16px;">
                            Gracias por registrarte en nuestro sistema. Para completar tu registro, 
                            por favor utiliza el siguiente código de verificación:
                        </p>
                        <div style="background-color: #f8f9fa; padding: 20px; margin: 20px 0; text-align: center; border-radius: 5px;">
                            <h1 style="color: #007bff; margin: 0; font-size: 36px; letter-spacing: 5px;">%s</h1>
                        </div>
                        <p style="color: #666; font-size: 14px;">
                            Este código expirará en 15 minutos.
                        </p>
                        <p style="color: #999; font-size: 12px; margin-top: 30px; text-align: center;">
                            Si no solicitaste este registro, por favor ignora este correo.
                        </p>
                    </div>
                </body>
                </html>
                """, nombreUsuario, codigo);
            
            helper.setText(contenidoHtml, true);
            
            mailSender.send(mensaje);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el correo de verificación", e);
        }
    }
}
