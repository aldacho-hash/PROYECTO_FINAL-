package Views;

import conexiondb.ConexionSQLServer;
import javax.swing.*;
import java.awt.*;

public class SeleccionRol extends javax.swing.JFrame {

    private String usuario;
    private String cargo;

    public SeleccionRol(String usuario, String cargo) {
        this.usuario = usuario;
        this.cargo = cargo.toLowerCase().trim();
        initComponents();
        lblBienvenida.setText("Bienvenido: " + usuario + "  |  Cargo: " + cargo.toUpperCase());
    }

    private void initComponents() {
        // Componentes
        JPanel bg = new JPanel();
        lblBienvenida = new JLabel();
        lblTitulo = new JLabel("SELECCIONA TU ÁREA DE ACCESO");
        btnVendedor = new JButton("🛒  VENDEDOR");
        btnLogistica = new JButton("📦  LOGÍSTICA");
        btnAdministrador = new JButton("⚙️  ADMINISTRADOR");
        btnSalir = new JButton("SALIR");

        // Ventana
        setTitle("Selección de Rol - Librería Fanny");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(600, 450);
        setLocationRelativeTo(null);

        // Panel fondo
        bg.setBackground(new Color(0, 0, 51));
        bg.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        // Título
        lblTitulo.setFont(new Font("Segoe UI Black", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        bg.add(lblTitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 40, 500, 40));

        // Bienvenida
        lblBienvenida.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblBienvenida.setForeground(new Color(180, 180, 180));
        lblBienvenida.setHorizontalAlignment(SwingConstants.CENTER);
        bg.add(lblBienvenida, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 90, 500, 25));

        // Botón Vendedor
        btnVendedor.setFont(new Font("Segoe UI Black", Font.BOLD, 16));
        btnVendedor.setBackground(new Color(0, 153, 76));
        btnVendedor.setForeground(Color.WHITE);
        btnVendedor.setFocusPainted(false);
        btnVendedor.setBorderPainted(false);
        btnVendedor.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bg.add(btnVendedor, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 150, 300, 55));

        // Botón Logística
        btnLogistica.setFont(new Font("Segoe UI Black", Font.BOLD, 16));
        btnLogistica.setBackground(new Color(0, 102, 204));
        btnLogistica.setForeground(Color.WHITE);
        btnLogistica.setFocusPainted(false);
        btnLogistica.setBorderPainted(false);
        btnLogistica.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bg.add(btnLogistica, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 225, 300, 55));

        // Botón Administrador
        btnAdministrador.setFont(new Font("Segoe UI Black", Font.BOLD, 16));
        btnAdministrador.setBackground(new Color(153, 0, 0));
        btnAdministrador.setForeground(Color.WHITE);
        btnAdministrador.setFocusPainted(false);
        btnAdministrador.setBorderPainted(false);
        btnAdministrador.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bg.add(btnAdministrador, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 300, 300, 55));

        // Botón Salir
        btnSalir.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnSalir.setBackground(new Color(80, 80, 80));
        btnSalir.setForeground(Color.WHITE);
        btnSalir.setFocusPainted(false);
        btnSalir.setBorderPainted(false);
        bg.add(btnSalir, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 390, 80, 30));

        btnVendedor.addActionListener(e -> accederRol("Vendedor"));
        btnLogistica.addActionListener(e -> accederRol("Logística"));
        btnAdministrador.addActionListener(e -> accederRol("Administrador"));
        btnSalir.addActionListener(e -> {
            int r = JOptionPane.showConfirmDialog(this, "¿Estás seguro de salir?", "Salir", JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION) System.exit(0);
        });

        getContentPane().add(bg);
        bg.setPreferredSize(new Dimension(600, 450));
        pack();
    }

    private void accederRol(String rolSeleccionado) {
        if (!ConexionSQLServer.tienePermiso(cargo, rolSeleccionado)) {
            String cargoMostrar = cargo.substring(0, 1).toUpperCase() + cargo.substring(1);
            JOptionPane.showMessageDialog(
                this,
                "⛔ Acceso denegado.\n\nTu cargo es: " + cargoMostrar +
                "\nNo tienes permiso para ingresar al área de " + rolSeleccionado + ".",
                "Acceso Denegado",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Acceso permitido — abrir la ventana correspondiente
        this.setVisible(false);
        switch (rolSeleccionado.toLowerCase()) {
            case "vendedor":
            VentanaPrincipal ventanaVendedor = new VentanaPrincipal(usuario, usuario);
            ventanaVendedor.setVisible(true);
            break;
            
            case "logística":
                Sistema sistemaLogistica = new Sistema(usuario, "logistica");
                sistemaLogistica.setVisible(true);
            break;

            case "administrador":
            Sistema sistemaFrame = new Sistema(usuario, "administrador");
            sistemaFrame.setVisible(true);
            break;
        }
    }

    // Variables
    private JLabel lblBienvenida;
    private JLabel lblTitulo;
    private JButton btnVendedor;
    private JButton btnLogistica;
    private JButton btnAdministrador;
    private JButton btnSalir;
}