package Views;

import conexiondb.ConexionSQLServer;
import javax.swing.*;
import java.awt.*;

public class RecuperarContrasenaDialog extends JFrame {

    private JTextField txtCorreoRecuperar, txtCodigoVerificacion;
    private JPasswordField txtNuevaContrasena;
    private JButton btnEnviarCodigo, btnCambiarContrasena;

    public RecuperarContrasenaDialog() {
        setTitle("Recuperar Contraseña");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel lblCorreo = new JLabel("Ingresa tu correo registrado:");
        lblCorreo.setBounds(50, 30, 300, 25);
        add(lblCorreo);

        txtCorreoRecuperar = new JTextField();
        txtCorreoRecuperar.setBounds(50, 60, 280, 30);
        add(txtCorreoRecuperar);

        btnEnviarCodigo = new JButton("Enviar código");
        btnEnviarCodigo.setBounds(50, 100, 280, 35);
        add(btnEnviarCodigo);

        JLabel lblCodigo = new JLabel("Código de verificación:");
        lblCodigo.setBounds(50, 150, 280, 25);
        add(lblCodigo);

        txtCodigoVerificacion = new JTextField();
        txtCodigoVerificacion.setBounds(50, 175, 280, 30);
        add(txtCodigoVerificacion);

        JLabel lblNueva = new JLabel("Nueva contraseña:");
        lblNueva.setBounds(50, 215, 280, 25);
        add(lblNueva);

        txtNuevaContrasena = new JPasswordField();
        txtNuevaContrasena.setBounds(50, 240, 280, 30);
        add(txtNuevaContrasena);

        btnCambiarContrasena = new JButton("Cambiar contraseña");
        btnCambiarContrasena.setBounds(50, 285, 280, 35);
        add(btnCambiarContrasena);

        // Lógica botón Enviar código
        btnEnviarCodigo.addActionListener(e -> {
            String correo = txtCorreoRecuperar.getText().trim();
            if (correo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingresa tu correo.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String codigo = ConexionSQLServer.generarCodigo();
            ConexionSQLServer.guardarCodigoRecuperacion(correo, codigo);
            ConexionSQLServer.enviarCorreoRecuperacion(correo, codigo);
            JOptionPane.showMessageDialog(this, "Código enviado a tu correo. Expira en 10 minutos.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        });

        // Lógica botón Cambiar contraseña
        btnCambiarContrasena.addActionListener(e -> {
            String correo = txtCorreoRecuperar.getText().trim();
            String codigo = txtCodigoVerificacion.getText().trim();
            String nuevaContrasena = new String(txtNuevaContrasena.getPassword());

            if (correo.isEmpty() || codigo.isEmpty() || nuevaContrasena.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Completa todos los campos.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (nuevaContrasena.length() < 8) {
                JOptionPane.showMessageDialog(this, "La contraseña debe tener al menos 8 caracteres.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (ConexionSQLServer.verificarCodigo(correo, codigo)) {
                ConexionSQLServer.cambiarContrasena(correo, nuevaContrasena);
                JOptionPane.showMessageDialog(this, "Contraseña cambiada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Código incorrecto o expirado.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}