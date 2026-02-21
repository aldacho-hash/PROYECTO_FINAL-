package Views;

import Clases.CampañaDescuento;
import Clases.CampañaDescuentoDAO;
import Modelos.CarritoProducto;
import Modelos.PanelProducto;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import Modelos.Producto;
import conexiondb.ConexionSQLServer;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import Clases.GenerarPDF;
import java.sql.Statement;

public class VentanaPrincipal extends JFrame {
    private String nombreCliente;
    private String usuario;
    private JPanel panelSuperior;
    private JPanel panelMenu;
    private JPanel panelContenido;
    private JScrollPane scrollProductos;
    private JTextField txtBuscar;
    private JLabel lblUsuario;
    private JLabel lblTituloSeccion;
    private JButton btnCarrito;
    private List<Producto> productos;
    private int contadorCarrito = 0;
    private List<CarritoProducto> carrito = new ArrayList<>();
    private List<CampañaDescuento> campanias = new ArrayList<>();
    private VentanaGestionProductos ventanaGestionProductos;

    public VentanaPrincipal(String nombreCliente) {
        initComponents();
        this.nombreCliente = nombreCliente;
        cargarProductosEjemplo();
        mostrarProductos("Todos los Productos");
        btnCarrito.addActionListener(e -> verCarrito());
        ventanaGestionProductos = new VentanaGestionProductos(this);
        ventanaGestionProductos.cargarDatosTabla();
    }

    private void initComponents() {
        setTitle("LIBRERIA FANNYSTORE");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        lblUsuario = new JLabel("Bienvenido: ");
        crearPanelSuperior();
        crearPanelMenu();
        crearPanelContenido();
        getContentPane().setBackground(Color.WHITE);
    }

    private void crearPanelSuperior() {
        panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(new Color(200, 50, 50));
        panelSuperior.setPreferredSize(new Dimension(getWidth(), 80));
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lblTitulo = new JLabel("📚 LIBRERÍA FANNYSTORE");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(Color.WHITE);

        JPanel panelBuscador = new JPanel(new BorderLayout(0, 0));
        panelBuscador.setBackground(Color.WHITE);
        panelBuscador.setPreferredSize(new Dimension(500, 40));
        panelBuscador.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));

        JLabel lblLupa = new JLabel();
        try {
            ImageIcon iconoLupa = new ImageIcon(getClass().getResource("/Imagenes/lupa.png"));
            Image img = iconoLupa.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            lblLupa.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            lblLupa.setText("🔍");
            lblLupa.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        }
        lblLupa.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 5));

        txtBuscar = new JTextField();
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtBuscar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 10));
        txtBuscar.addActionListener(e -> buscarProductos());

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnBuscar.setPreferredSize(new Dimension(80, 40));
        btnBuscar.setBackground(new Color(255, 193, 7));
        btnBuscar.setForeground(Color.BLACK);
        btnBuscar.setBorder(BorderFactory.createEmptyBorder());
        btnBuscar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBuscar.setFocusPainted(false);
        btnBuscar.addActionListener(e -> buscarProductos());

        panelBuscador.add(lblLupa, BorderLayout.WEST);
        panelBuscador.add(txtBuscar, BorderLayout.CENTER);
        panelBuscador.add(btnBuscar, BorderLayout.EAST);

        JPanel panelUsuario = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelUsuario.setBackground(new Color(200, 50, 50));

        btnCarrito = new JButton("🛒 Carrito (" + contadorCarrito + ")");
        btnCarrito.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCarrito.setForeground(Color.WHITE);
        btnCarrito.setBackground(new Color(200, 50, 50));
        btnCarrito.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        btnCarrito.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCarrito.setFocusPainted(false);

        JButton btnCuenta = new JButton("👤 Mi Cuenta");
        btnCuenta.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCuenta.setForeground(Color.WHITE);
        btnCuenta.setBackground(new Color(200, 50, 50));
        btnCuenta.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        btnCuenta.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCuenta.setFocusPainted(false);
        btnCuenta.addActionListener(e -> mostrarMenuCuenta(btnCuenta));

        panelUsuario.add(btnCarrito);
        panelUsuario.add(btnCuenta);

        panelSuperior.add(lblTitulo, BorderLayout.WEST);
        panelSuperior.add(panelBuscador, BorderLayout.CENTER);
        panelSuperior.add(panelUsuario, BorderLayout.EAST);
        add(panelSuperior, BorderLayout.NORTH);
    }

    private void mostrarMenuCuenta(JButton btnCuenta) {
        JPopupMenu menuCuenta = new JPopupMenu();
        menuCuenta.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        JPanel panelHeader = new JPanel();
        panelHeader.setBackground(new Color(240, 240, 240));
        panelHeader.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        JLabel lblNombreUsuario = new JLabel("¡Bienvenido, " + nombreCliente + "!");
        lblNombreUsuario.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panelHeader.add(lblNombreUsuario);
        menuCuenta.add(panelHeader);
        menuCuenta.addSeparator();

        JMenuItem itemPerfil = new JMenuItem("👤  Mi Perfil");
        itemPerfil.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        itemPerfil.addActionListener(e -> abrirPerfil());
        menuCuenta.add(itemPerfil);

        JMenuItem itemPedidos = new JMenuItem("📦  Mis Pedidos");
        itemPedidos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        itemPedidos.addActionListener(e -> abrirMisPedidos());
        menuCuenta.add(itemPedidos);

        JMenuItem itemDirecciones = new JMenuItem("📍  Mis Direcciones");
        itemDirecciones.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        itemDirecciones.addActionListener(e -> abrirMisDirecciones());
        menuCuenta.add(itemDirecciones);
        menuCuenta.addSeparator();

        JMenuItem itemCerrarSesion = new JMenuItem("🚪  Cerrar Sesión");
        itemCerrarSesion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        itemCerrarSesion.setForeground(Color.RED);
        itemCerrarSesion.addActionListener(e -> {
            int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Deseas cerrar sesión?", "Cerrar Sesión", JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {
                this.setVisible(false);
                LoginUsuarios loginFrame = new LoginUsuarios();
                loginFrame.setVisible(true);
            }
        });
        menuCuenta.add(itemCerrarSesion);
        menuCuenta.show(btnCuenta, 0, btnCuenta.getHeight());
    }

    private void abrirPerfil() {
        JFrame ventanaPerfil = new JFrame("Mi Perfil");
        ventanaPerfil.setSize(600, 500);
        ventanaPerfil.setLocationRelativeTo(this);
        ventanaPerfil.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panelPrincipal = new JPanel(new BorderLayout(15, 15));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel panelFoto = new JPanel();
        panelFoto.setLayout(new BoxLayout(panelFoto, BoxLayout.Y_AXIS));
        panelFoto.setBackground(Color.WHITE);

        JLabel lblFoto = new JLabel("👤");
        lblFoto.setFont(new Font("Segoe UI", Font.PLAIN, 80));
        lblFoto.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lblNombre = new JLabel("Hola,");
        lblNombre.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lblUsuarioLabel = new JLabel(nombreCliente.toUpperCase() + "!");
        lblUsuarioLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblUsuarioLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelFoto.add(Box.createVerticalStrut(20));
        panelFoto.add(lblFoto);
        panelFoto.add(Box.createVerticalStrut(10));
        panelFoto.add(lblNombre);
        panelFoto.add(lblUsuarioLabel);

        JPanel panelDatos = new JPanel(new GridLayout(0, 2, 10, 15));
        panelDatos.setBorder(BorderFactory.createTitledBorder("Datos personales"));

        String query = "SELECT * FROM cliente WHERE usuario = ?";
        try (Connection con = ConexionSQLServer.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, nombreCliente);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                panelDatos.add(new JLabel("Nombre:"));    panelDatos.add(new JLabel(rs.getString("nombres")));
                panelDatos.add(new JLabel("Apellido:"));  panelDatos.add(new JLabel(rs.getString("apellidos")));
                panelDatos.add(new JLabel("Email:"));     panelDatos.add(new JLabel(rs.getString("correo")));
                panelDatos.add(new JLabel("DNI:"));       panelDatos.add(new JLabel(rs.getString("dni")));
                panelDatos.add(new JLabel("Teléfono:")); panelDatos.add(new JLabel(rs.getString("telefono")));
                panelDatos.add(new JLabel("Fecha Nac.:"));
                String fechaNac = rs.getString("fecha_nacimiento");
                panelDatos.add(new JLabel(fechaNac != null ? fechaNac : "No especificado"));
            }
        } catch (SQLException e) { e.printStackTrace(); }

        panelPrincipal.add(panelFoto, BorderLayout.WEST);
        panelPrincipal.add(panelDatos, BorderLayout.CENTER);
        ventanaPerfil.add(panelPrincipal);
        ventanaPerfil.setVisible(true);
    }

    private void abrirMisPedidos() {
        JFrame ventanaPedidos = new JFrame("Mis Pedidos");
        ventanaPedidos.setSize(800, 500);
        ventanaPedidos.setLocationRelativeTo(this);
        String[] columnas = {"ID Pedido", "Fecha", "Total", "Estado", "Método Pago"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);
        String query = "SELECT v.id_venta, v.fecha, v.total, v.metodo_pago, " +
                       "COALESCE(p.estado_pedido, 'Completado') as estado " +
                       "FROM ventas v LEFT JOIN pedidos p ON v.id_venta = p.id_venta " +
                       "INNER JOIN cliente c ON v.id_cliente = c.id_cliente " +
                       "WHERE c.usuario = ? ORDER BY v.fecha DESC";
        try (Connection con = ConexionSQLServer.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, nombreCliente);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("id_venta"), rs.getTimestamp("fecha"),
                    String.format("S/ %.2f", rs.getDouble("total")),
                    rs.getString("estado"), rs.getString("metodo_pago")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        ventanaPedidos.add(new JScrollPane(new JTable(modelo)));
        ventanaPedidos.setVisible(true);
    }

    private void abrirMisDirecciones() {
        JFrame ventanaDirecciones = new JFrame("Mis Direcciones");
        ventanaDirecciones.setSize(750, 500);
        ventanaDirecciones.setLocationRelativeTo(this);
        ventanaDirecciones.setLayout(new BorderLayout());

        JPanel panelLista = new JPanel();
        panelLista.setLayout(new BoxLayout(panelLista, BoxLayout.Y_AXIS));
        panelLista.setBackground(Color.WHITE);
        JScrollPane scroll = new JScrollPane(panelLista);

        int idCliente = obtenerIdCliente(nombreCliente);
        cargarDirecciones2(panelLista, idCliente, ventanaDirecciones);

        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBoton.setBackground(Color.WHITE);
        JButton btnAgregar = new JButton("➕ Agregar Dirección");
        btnAgregar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAgregar.setBackground(new Color(200, 50, 50));
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.setFocusPainted(false);
        btnAgregar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAgregar.addActionListener(e -> {
            agregarNuevaDireccion(idCliente, null);
            cargarDirecciones2(panelLista, idCliente, ventanaDirecciones);
        });
        panelBoton.add(btnAgregar);

        ventanaDirecciones.add(scroll, BorderLayout.CENTER);
        ventanaDirecciones.add(panelBoton, BorderLayout.SOUTH);
        ventanaDirecciones.setVisible(true);
    }

    private void cargarDirecciones2(JPanel panelLista, int idCliente, JFrame ventana) {
        panelLista.removeAll();
        String query = "SELECT * FROM direcciones WHERE id_cliente = ? ORDER BY es_principal DESC";
        try (Connection con = ConexionSQLServer.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, idCliente);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int idDir         = rs.getInt("id_direccion");
                String calle      = rs.getString("calle");
                String distrito   = rs.getString("distrito");
                String provincia  = rs.getString("ciudad");
                String depto      = rs.getString("departamento");
                boolean principal = rs.getInt("es_principal") == 1;

                JPanel card = new JPanel(new BorderLayout(0, 6));
                card.setBackground(Color.WHITE);
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
                    BorderFactory.createEmptyBorder(14, 18, 14, 18)));
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

                JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
                panelTop.setBackground(Color.WHITE);
                if (principal) {
                    JLabel lblTag = new JLabel("Predeterminado");
                    lblTag.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    lblTag.setForeground(Color.WHITE);
                    lblTag.setBackground(new Color(255, 140, 0));
                    lblTag.setOpaque(true);
                    lblTag.setBorder(BorderFactory.createEmptyBorder(2, 7, 2, 7));
                    panelTop.add(lblTag);
                }
                JLabel lblNombre = new JLabel(nombreCliente);
                lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 13));
                panelTop.add(lblNombre);

                JLabel lblCalle = new JLabel(calle);
                lblCalle.setFont(new Font("Segoe UI", Font.PLAIN, 13));

                JLabel lblUbicacion = new JLabel(distrito + ", " + provincia + ", " + depto + ", Perú");
                lblUbicacion.setFont(new Font("Segoe UI", Font.PLAIN, 13));

                JPanel panelInfo = new JPanel();
                panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
                panelInfo.setBackground(Color.WHITE);
                panelInfo.add(panelTop);
                panelInfo.add(Box.createVerticalStrut(4));
                panelInfo.add(lblCalle);
                panelInfo.add(Box.createVerticalStrut(2));
                panelInfo.add(lblUbicacion);

                JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
                panelBotones.setBackground(Color.WHITE);

                JButton btnEliminar = new JButton("Eliminar");
                btnEliminar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                btnEliminar.setForeground(new Color(180, 50, 50));
                btnEliminar.setBorderPainted(false);
                btnEliminar.setContentAreaFilled(false);
                btnEliminar.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btnEliminar.addActionListener(e -> {
                    int resp = JOptionPane.showConfirmDialog(ventana, "¿Eliminar esta dirección?", "Confirmar", JOptionPane.YES_NO_OPTION);
                    if (resp == JOptionPane.YES_OPTION) {
                        try (Connection con2 = ConexionSQLServer.getConnection();
                             PreparedStatement ps2 = con2.prepareStatement("DELETE FROM direcciones WHERE id_direccion = ?")) {
                            ps2.setInt(1, idDir);
                            ps2.executeUpdate();
                        } catch (SQLException ex) { ex.printStackTrace(); }
                        cargarDirecciones2(panelLista, idCliente, ventana);
                    }
                });

                JLabel sep = new JLabel("|");
                sep.setForeground(Color.GRAY);

                JButton btnEditar = new JButton("Editar");
                btnEditar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                btnEditar.setForeground(new Color(0, 100, 200));
                btnEditar.setBorderPainted(false);
                btnEditar.setContentAreaFilled(false);
                btnEditar.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btnEditar.addActionListener(e ->
                    JOptionPane.showMessageDialog(ventana, "Función de editar próximamente.", "Editar", JOptionPane.INFORMATION_MESSAGE));

                panelBotones.add(btnEliminar);
                panelBotones.add(sep);
                panelBotones.add(btnEditar);

                card.add(panelInfo, BorderLayout.CENTER);
                card.add(panelBotones, BorderLayout.SOUTH);
                panelLista.add(card);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        panelLista.revalidate();
        panelLista.repaint();
    }

    private void agregarNuevaDireccion(int idCliente, DefaultListModel<String> modelo) {
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblPais = new JLabel("Perú");
        lblPais.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JTextField txtDireccion           = new JTextField();
        JComboBox<String> cmbDepartamento = new JComboBox<>();
        JComboBox<String> cmbProvincia    = new JComboBox<>();
        JComboBox<String> cmbDistrito     = new JComboBox<>();

        cmbDepartamento.addItem("Seleccionar");
        cmbProvincia.addItem("Seleccionar");
        cmbDistrito.addItem("Seleccionar");

        try (Connection con = ConexionSQLServer.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT nombre FROM departamento ORDER BY nombre");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) cmbDepartamento.addItem(rs.getString("nombre"));
        } catch (SQLException e) { e.printStackTrace(); }

        cmbDepartamento.addActionListener(e -> {
            cmbProvincia.removeAllItems(); cmbDistrito.removeAllItems();
            cmbProvincia.addItem("Seleccionar"); cmbDistrito.addItem("Seleccionar");
            String dep = (String) cmbDepartamento.getSelectedItem();
            if (dep == null || dep.equals("Seleccionar")) return;
            try (Connection con = ConexionSQLServer.getConnection();
                 PreparedStatement ps = con.prepareStatement(
                    "SELECT p.nombre FROM provincia p INNER JOIN departamento d ON p.id_departamento=d.id_departamento WHERE d.nombre=? ORDER BY p.nombre")) {
                ps.setString(1, dep);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) cmbProvincia.addItem(rs.getString("nombre"));
            } catch (SQLException ex) { ex.printStackTrace(); }
        });

        cmbProvincia.addActionListener(e -> {
            cmbDistrito.removeAllItems(); cmbDistrito.addItem("Seleccionar");
            String prov = (String) cmbProvincia.getSelectedItem();
            if (prov == null || prov.equals("Seleccionar")) return;
            try (Connection con = ConexionSQLServer.getConnection();
                 PreparedStatement ps = con.prepareStatement(
                    "SELECT d.nombre FROM distrito d INNER JOIN provincia p ON d.id_provincia=p.id_provincia WHERE p.nombre=? ORDER BY d.nombre")) {
                ps.setString(1, prov);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) cmbDistrito.addItem(rs.getString("nombre"));
            } catch (SQLException ex) { ex.printStackTrace(); }
        });

        panel.add(new JLabel("País/Región:"));        panel.add(lblPais);
        panel.add(new JLabel("Dirección completa:")); panel.add(txtDireccion);
        panel.add(new JLabel("Departamento:"));       panel.add(cmbDepartamento);
        panel.add(new JLabel("Provincia:"));          panel.add(cmbProvincia);
        panel.add(new JLabel("Distrito:"));           panel.add(cmbDistrito);

        if (JOptionPane.showConfirmDialog(this, panel, "Nueva Dirección",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {

            if (txtDireccion.getText().trim().isEmpty() ||
                cmbDepartamento.getSelectedItem().equals("Seleccionar") ||
                cmbProvincia.getSelectedItem().equals("Seleccionar") ||
                cmbDistrito.getSelectedItem().equals("Seleccionar")) {
                JOptionPane.showMessageDialog(this, "Por favor completa todos los campos.", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String query = "INSERT INTO direcciones (id_cliente, alias, calle, numero, distrito, ciudad, departamento, referencia, es_principal) VALUES (?,?,?,?,?,?,?,?,?)";
            try (Connection con = ConexionSQLServer.getConnection();
                 PreparedStatement ps = con.prepareStatement(query)) {
                ps.setInt(1, idCliente);
                ps.setString(2, (String) cmbDistrito.getSelectedItem());
                ps.setString(3, txtDireccion.getText().trim());
                ps.setString(4, "S/N");
                ps.setString(5, (String) cmbDistrito.getSelectedItem());
                ps.setString(6, (String) cmbProvincia.getSelectedItem());
                ps.setString(7, (String) cmbDepartamento.getSelectedItem());
                ps.setString(8, "Perú");
                ps.setInt(9, 0);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Dirección agregada correctamente");
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al agregar dirección");
            }
        }
    }

    public double obtenerDescuentoActivo() {
        String query = "SELECT MAX(descuento) AS max_desc FROM campana_descuento " +
                       "WHERE fecha_inicio <= CURDATE() AND fecha_fin >= CURDATE() AND activo = 1";
        try (Connection conn = ConexionSQLServer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getDouble("max_desc") / 100.0;
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private void mostrarTotalCompra() {
        if (carrito.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay productos en el carrito.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        double total = 0;
        for (CarritoProducto cp : carrito) total += cp.getSubtotal();
        double descuento = obtenerDescuentoActivo();
        double totalConDescuento = descuento > 0 ? total * (1 - descuento) : total;
        String mensaje = String.format("Total sin descuento: S/ %.2f\nDescuento aplicado: %.0f%%\nTOTAL A PAGAR: S/ %.2f",
            total, descuento * 100, totalConDescuento);
        int respuesta = JOptionPane.showConfirmDialog(this, mensaje + "\n¿Desea confirmar la compra?",
            "Confirmación", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (respuesta == JOptionPane.YES_OPTION) realizarCompra(totalConDescuento);
    }

    private void realizarCompra(double total) {
        JDialog dialogPago = new JDialog(this, "Seleccionar Método de Pago", true);
        dialogPago.setSize(500, 550);
        dialogPago.setLocationRelativeTo(this);
        dialogPago.setLayout(new BorderLayout());

        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(new Color(200, 50, 50));
        panelTitulo.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        JLabel lblTitulo = new JLabel("Total a pagar: S/ " + String.format("%.2f", total));
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setForeground(Color.WHITE);
        panelTitulo.add(lblTitulo);

        JPanel panelCentral = new JPanel(new BorderLayout(10, 10));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        panelCentral.setBackground(Color.WHITE);

        JPanel panelMetodos = new JPanel(new GridLayout(1, 5, 8, 0));
        panelMetodos.setBackground(Color.WHITE);

        String[] metodos = {"💵 Efectivo", "💳 Tarjeta", "🔄 Transferencia", "📱 Yape", "📱 Plin"};
        final String[] keys = {"Efectivo", "Tarjeta", "Transferencia", "Yape", "Plin"};
        JButton[] btnMetodos = new JButton[metodos.length];
        Color colorActivo   = new Color(200, 50, 50);
        Color colorInactivo = new Color(240, 240, 240);

        CardLayout cardLayout = new CardLayout();
        JPanel panelContenido = new JPanel(cardLayout);
        panelContenido.setBackground(Color.WHITE);

        // ── Panel Efectivo ──
        JPanel panelEfectivo = new JPanel(new GridLayout(3, 2, 10, 10));
        panelEfectivo.setBackground(Color.WHITE);
        panelEfectivo.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        JTextField txtMonto = new JTextField();
        JLabel lblVuelto = new JLabel("Vuelto: S/ 0.00");
        lblVuelto.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblVuelto.setForeground(new Color(0, 130, 0));
        txtMonto.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            void actualizar() {
                try {
                    double monto = Double.parseDouble(txtMonto.getText().trim());
                    if (monto >= total) lblVuelto.setText("Vuelto: S/ " + String.format("%.2f", monto - total));
                    else lblVuelto.setText("⚠ Monto insuficiente");
                } catch (NumberFormatException ex) { lblVuelto.setText("Vuelto: S/ 0.00"); }
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { actualizar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { actualizar(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { actualizar(); }
        });
        panelEfectivo.add(new JLabel("Monto entregado (S/):"));
        panelEfectivo.add(txtMonto);
        panelEfectivo.add(new JLabel(""));
        panelEfectivo.add(lblVuelto);

        // ── Panel Tarjeta ──
        JPanel panelTarjeta = new JPanel(new GridLayout(5, 2, 10, 10));
        panelTarjeta.setBackground(Color.WHITE);
        panelTarjeta.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JTextField txtNumTarjeta  = new JTextField();
        JTextField txtNombreTarj  = new JTextField();
        JTextField txtVencimiento = new JTextField("MM/AA");
        JTextField txtCVV         = new JTextField();
        panelTarjeta.add(new JLabel("Número de tarjeta (16 dígitos):")); panelTarjeta.add(txtNumTarjeta);
        panelTarjeta.add(new JLabel("Nombre en tarjeta:"));              panelTarjeta.add(txtNombreTarj);
        panelTarjeta.add(new JLabel("Vencimiento (MM/AA):"));            panelTarjeta.add(txtVencimiento);
        panelTarjeta.add(new JLabel("CVV (3 dígitos):"));                panelTarjeta.add(txtCVV);
        JLabel lblNotaTarjeta = new JLabel("* Se aplicará un cargo adicional del 3%");
        lblNotaTarjeta.setForeground(Color.RED);
        lblNotaTarjeta.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        panelTarjeta.add(new JLabel("")); panelTarjeta.add(lblNotaTarjeta);

        // ── Panel Transferencia ──
        JPanel panelTransferencia = new JPanel();
        panelTransferencia.setLayout(new BoxLayout(panelTransferencia, BoxLayout.Y_AXIS));
        panelTransferencia.setBackground(Color.WHITE);
        panelTransferencia.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        for (JLabel lbl : new JLabel[]{
            new JLabel("Banco: BCP"),
            new JLabel("Cuenta: 123-456789-0-12"),
            new JLabel("CCI: 00212300456789012345"),
            new JLabel("Titular: Librería Fanny")}) {
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            panelTransferencia.add(lbl);
            panelTransferencia.add(Box.createVerticalStrut(8));
        }

        // ── Panel Yape ──
        JPanel panelYape = new JPanel();
        panelYape.setLayout(new BoxLayout(panelYape, BoxLayout.Y_AXIS));
        panelYape.setBackground(Color.WHITE);
        panelYape.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        JLabel lblYapeTitulo = new JLabel("📱 Paga con Yape");
        lblYapeTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblYapeTitulo.setForeground(new Color(100, 0, 150));
        lblYapeTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lblNumYape = new JLabel("Número: 999-888-777");
        lblNumYape.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblNumYape.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel qrYape = new JPanel();
        qrYape.setBackground(new Color(230, 220, 245));
        qrYape.setPreferredSize(new Dimension(150, 150));
        qrYape.setMaximumSize(new Dimension(150, 150));
        qrYape.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lblQRYape = new JLabel("[ QR Yape aquí ]");
        lblQRYape.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblQRYape.setForeground(new Color(100, 0, 150));
        qrYape.add(lblQRYape);
        panelYape.add(lblYapeTitulo);
        panelYape.add(Box.createVerticalStrut(10));
        panelYape.add(qrYape);
        panelYape.add(Box.createVerticalStrut(10));
        panelYape.add(lblNumYape);

        // ── Panel Plin ──
        JPanel panelPlin = new JPanel();
        panelPlin.setLayout(new BoxLayout(panelPlin, BoxLayout.Y_AXIS));
        panelPlin.setBackground(Color.WHITE);
        panelPlin.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        JLabel lblPlinTitulo = new JLabel("📱 Paga con Plin");
        lblPlinTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPlinTitulo.setForeground(new Color(0, 150, 100));
        lblPlinTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lblNumPlin = new JLabel("Número: 999-888-777");
        lblNumPlin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblNumPlin.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel qrPlin = new JPanel();
        qrPlin.setBackground(new Color(220, 245, 235));
        qrPlin.setPreferredSize(new Dimension(150, 150));
        qrPlin.setMaximumSize(new Dimension(150, 150));
        qrPlin.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lblQRPlin = new JLabel("[ QR Plin aquí ]");
        lblQRPlin.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblQRPlin.setForeground(new Color(0, 150, 100));
        qrPlin.add(lblQRPlin);
        panelPlin.add(lblPlinTitulo);
        panelPlin.add(Box.createVerticalStrut(10));
        panelPlin.add(qrPlin);
        panelPlin.add(Box.createVerticalStrut(10));
        panelPlin.add(lblNumPlin);

        panelContenido.add(panelEfectivo,      "Efectivo");
        panelContenido.add(panelTarjeta,       "Tarjeta");
        panelContenido.add(panelTransferencia, "Transferencia");
        panelContenido.add(panelYape,          "Yape");
        panelContenido.add(panelPlin,          "Plin");

        for (int i = 0; i < metodos.length; i++) {
            final int idx = i;
            btnMetodos[i] = new JButton(metodos[i]);
            btnMetodos[i].setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnMetodos[i].setFocusPainted(false);
            btnMetodos[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnMetodos[i].setBackground(i == 0 ? colorActivo : colorInactivo);
            btnMetodos[i].setForeground(i == 0 ? Color.WHITE : Color.BLACK);
            btnMetodos[i].addActionListener(e -> {
                cardLayout.show(panelContenido, keys[idx]);
                for (int j = 0; j < btnMetodos.length; j++) {
                    btnMetodos[j].setBackground(j == idx ? colorActivo : colorInactivo);
                    btnMetodos[j].setForeground(j == idx ? Color.WHITE : Color.BLACK);
                }
            });
            panelMetodos.add(btnMetodos[i]);
        }

        panelCentral.add(panelMetodos,   BorderLayout.NORTH);
        panelCentral.add(panelContenido, BorderLayout.CENTER);

        JPanel panelDelivery = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelDelivery.setBackground(Color.WHITE);
        JComboBox<String> comboDelivery = new JComboBox<>(new String[]{"No", "Sí (+S/ 5.00)"});
        panelDelivery.add(new JLabel("¿Delivery?"));
        panelDelivery.add(comboDelivery);
        panelCentral.add(panelDelivery, BorderLayout.SOUTH);

        JPanel panelConfirmar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelConfirmar.setBackground(Color.WHITE);
        panelConfirmar.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
        JButton btnConfirmar = new JButton("✅ Confirmar Compra");
        btnConfirmar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnConfirmar.setBackground(new Color(50, 150, 50));
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setFocusPainted(false);
        btnConfirmar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConfirmar.setPreferredSize(new Dimension(200, 40));

        btnConfirmar.addActionListener(e -> {
            String metodoActivo = "Efectivo";
            for (int i = 0; i < btnMetodos.length; i++) {
                if (btnMetodos[i].getBackground().equals(colorActivo)) {
                    metodoActivo = keys[i];
                    break;
                }
            }

            double totalFinal = total;

            if (metodoActivo.equals("Efectivo")) {
                try {
                    double monto = Double.parseDouble(txtMonto.getText().trim());
                    if (monto < totalFinal) {
                        JOptionPane.showMessageDialog(dialogPago, "El monto ingresado es menor al total a pagar.", "Monto insuficiente", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialogPago, "Ingrese un monto válido.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            if (metodoActivo.equals("Tarjeta")) {
                String numTarj = txtNumTarjeta.getText().trim().replaceAll("\\s", "");
                String cvv     = txtCVV.getText().trim();
                String venc    = txtVencimiento.getText().trim();
                if (numTarj.length() < 16) {
                    JOptionPane.showMessageDialog(dialogPago, "El número de tarjeta debe tener 16 dígitos.", "Tarjeta inválida", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (cvv.length() < 3) {
                    JOptionPane.showMessageDialog(dialogPago, "El CVV debe tener 3 dígitos.", "CVV inválido", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (!venc.matches("\\d{2}/\\d{2}")) {
                    JOptionPane.showMessageDialog(dialogPago, "El vencimiento debe tener el formato MM/AA.", "Vencimiento inválido", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (txtNombreTarj.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialogPago, "Ingrese el nombre en la tarjeta.", "Campo vacío", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                totalFinal = totalFinal * 1.03;
            }

            String delivery = comboDelivery.getSelectedIndex() == 1 ? "Sí" : "No";
            if (delivery.equals("Sí")) totalFinal += 5.0;

            final String metodoFinal = metodoActivo;
            final double totalPagar  = totalFinal;

            dialogPago.dispose();

            CampañaDescuentoDAO dao = new CampañaDescuentoDAO();
            CampañaDescuento camp   = dao.obtenerCampañaActiva();
            double descuentoPorcentaje = camp != null ? camp.getPorcentajeDescuento() : 0;
            String nombreCampaña       = camp != null ? camp.getNombre() : "Ninguna";

            List<CarritoProducto> carritoCopia = new ArrayList<>(carrito);
            insertarVenta(nombreCliente, carrito, totalPagar, metodoFinal, delivery);
            for (CarritoProducto cp : carrito) {
                actualizarStockEnBaseDeDatos(cp.getProducto(), cp.getCantidad());
                cp.getProducto().setStock(cp.getProducto().getStock() - cp.getCantidad());
            }
            carrito.clear();
            contadorCarrito = 0;
            btnCarrito.setText("🛒 Carrito (0)");
            mostrarProductos("Todos los Productos");

            JOptionPane.showMessageDialog(VentanaPrincipal.this,
                "¡Compra realizada con éxito!\n" +
                "Método de pago: " + metodoFinal + "\n" +
                "Total pagado: S/ " + String.format("%.2f", totalPagar),
                "Compra exitosa", JOptionPane.INFORMATION_MESSAGE);

            GenerarPDF.generarBoletaDeVenta(nombreCliente, carritoCopia, totalPagar, metodoFinal, delivery, nombreCampaña, descuentoPorcentaje * 100);
        });

        panelConfirmar.add(btnConfirmar);

        dialogPago.add(panelTitulo,    BorderLayout.NORTH);
        dialogPago.add(panelCentral,   BorderLayout.CENTER);
        dialogPago.add(panelConfirmar, BorderLayout.SOUTH);
        dialogPago.setVisible(true);
    }

    private void actualizarStockEnBaseDeDatos(Producto producto, int cantidadComprada) {
        String query = "UPDATE producto SET stock = stock - ? WHERE id_producto = ?";
        try (Connection conn = ConexionSQLServer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, cantidadComprada); stmt.setInt(2, producto.getIdProducto());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private boolean actualizarUsuario(String usuario, String nuevoUsuario) {
        usuario = nombreCliente;
        try (Connection conn = ConexionSQLServer.getConnection();
             PreparedStatement validarStmt = conn.prepareStatement("SELECT * FROM cliente WHERE usuario = ?")) {
            validarStmt.setString(1, usuario);
            if (!validarStmt.executeQuery().next()) return false;
            PreparedStatement u1 = conn.prepareStatement("UPDATE cliente SET usuario = ? WHERE usuario = ?");
            u1.setString(1, nuevoUsuario); u1.setString(2, usuario);
            PreparedStatement u2 = conn.prepareStatement("UPDATE usuarios SET usuario = ? WHERE usuario = ?");
            u2.setString(1, nuevoUsuario); u2.setString(2, usuario);
            return u1.executeUpdate() > 0 && u2.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private int obtenerIdCliente(String usuario) {
        try (Connection conn = ConexionSQLServer.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT id_cliente FROM cliente WHERE usuario = ?")) {
            stmt.setString(1, usuario.trim());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("id_cliente");
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    public void insertarVenta(String usuario, List<CarritoProducto> carrito, double total, String metodoPago, String delivery) {
        String queryVenta = "INSERT INTO ventas (id_cliente, fecha, total, metodo_pago, delivery) VALUES (?, ?, ?, ?, ?)";
        String queryPV    = "INSERT INTO productos_vendidos (id_venta, id_producto, precio, cantidad, subtotal) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexionSQLServer.getConnection();
             PreparedStatement stmtVenta = conn.prepareStatement(queryVenta, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement stmtPV    = conn.prepareStatement(queryPV)) {
            stmtVenta.setInt(1, obtenerIdCliente(usuario));
            stmtVenta.setDate(2, java.sql.Date.valueOf(java.time.LocalDate.now()));
            stmtVenta.setDouble(3, total); stmtVenta.setString(4, metodoPago); stmtVenta.setString(5, delivery);
            stmtVenta.executeUpdate();
            ResultSet keys = stmtVenta.getGeneratedKeys();
            int idVenta = keys.next() ? keys.getInt(1) : 0;
            for (CarritoProducto cp : carrito) {
                stmtPV.setInt(1, idVenta); stmtPV.setInt(2, cp.getProducto().getIdProducto());
                stmtPV.setDouble(3, cp.getProducto().getPrecio()); stmtPV.setInt(4, cp.getCantidad());
                stmtPV.setDouble(5, cp.getProducto().getPrecio() * cp.getCantidad());
                stmtPV.executeUpdate();
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void crearPanelMenu() {
        panelMenu = new JPanel();
        panelMenu.setLayout(new BoxLayout(panelMenu, BoxLayout.Y_AXIS));
        panelMenu.setBackground(new Color(245, 245, 245));
        panelMenu.setPreferredSize(new Dimension(220, getHeight()));
        panelMenu.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        JLabel lblCategorias = new JLabel("  CATEGORÍAS");
        lblCategorias.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblCategorias.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelMenu.add(lblCategorias);
        panelMenu.add(Box.createVerticalStrut(15));
        panelMenu.add(crearBotonCategoria("🏠 Todos los Productos", "Todos"));
        panelMenu.add(Box.createVerticalStrut(8));
        String[][] categorias = {
            {"📖 Libros",              "Libros"},
            {"✏️ Útiles Escolares",    "Útiles Escolares"},
            {"🎨 Arte y Manualidades", "Arte y Manualidades"},
            {"💼 Oficina",             "Oficina"},
            {"🎒 Mochilas",            "Mochilas"},
            {"📐 Geometría",           "Geometría"},
            {"🖊️ Escritura",           "Escritura"},
            {"📋 Cuadernos",           "Cuadernos"}
        };
        for (String[] cat : categorias) {
            panelMenu.add(crearBotonCategoria(cat[0], cat[1]));
            panelMenu.add(Box.createVerticalStrut(8));
        }
        add(panelMenu, BorderLayout.WEST);
    }

    private JButton crearBotonCategoria(String texto, String categoria) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setBackground(Color.WHITE);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.addActionListener(e -> filtrarPorCategoria(categoria));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(new Color(200, 50, 50)); btn.setForeground(Color.WHITE); }
            public void mouseExited(java.awt.event.MouseEvent evt)  { btn.setBackground(Color.WHITE); btn.setForeground(Color.BLACK); }
        });
        return btn;
    }

    private void crearPanelContenido() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(Color.WHITE);
        lblTituloSeccion = new JLabel("Todos los Productos");
        lblTituloSeccion.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTituloSeccion.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        panelPrincipal.add(lblTituloSeccion, BorderLayout.NORTH);
        panelContenido = new JPanel();
        panelContenido.setLayout(new GridLayout(0, 4, 15, 15));
        panelContenido.setBackground(Color.WHITE);
        panelContenido.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollProductos = new JScrollPane(panelContenido);
        scrollProductos.setBorder(BorderFactory.createEmptyBorder());
        scrollProductos.getVerticalScrollBar().setUnitIncrement(16);
        panelPrincipal.add(scrollProductos, BorderLayout.CENTER);
        add(panelPrincipal, BorderLayout.CENTER);
    }

    private void cargarProductosEjemplo() {
        productos = new ArrayList<>();
        String query = "SELECT * FROM producto WHERE activo = 1";
        try (Connection con = ConexionSQLServer.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                productos.add(new Producto(rs.getInt("id_producto"), rs.getString("nombre"),
                    rs.getDouble("precio"), rs.getString("categoria"), "", rs.getInt("stock")));
            }
        } catch (SQLException e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Error al cargar los productos.", "Error", JOptionPane.ERROR_MESSAGE); }
    }

    private void filtrarPorCategoria(String categoria) {
        List<Producto> productosFiltrados = new ArrayList<>();
        String query = "SELECT * FROM producto WHERE activo = 1" + (categoria != null && !categoria.equals("Todos") ? " AND categoria = ?" : "");
        try (Connection con = ConexionSQLServer.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            if (categoria != null && !categoria.equals("Todos")) pst.setString(1, categoria);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                productosFiltrados.add(new Producto(rs.getInt("id_producto"), rs.getString("nombre"),
                    rs.getDouble("precio"), rs.getString("categoria"), "", rs.getInt("stock")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        lblTituloSeccion.setText(categoria.equals("Todos") ? "Todos los Productos" : "Categoría: " + categoria);
        mostrarProductos(productosFiltrados);
    }

    public void mostrarProductos(List<Producto> productosFiltrados) {
        panelContenido.removeAll();
        if (productosFiltrados == null || productosFiltrados.isEmpty()) {
            JLabel lblVacio = new JLabel("No hay productos en esta categoría");
            lblVacio.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            lblVacio.setForeground(Color.GRAY);
            panelContenido.add(lblVacio);
        } else {
            for (Producto producto : productosFiltrados) {
                PanelProducto panelProd = new PanelProducto(producto);
                panelProd.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseClicked(java.awt.event.MouseEvent evt) { agregarAlCarrito(producto); }
                });
                panelContenido.add(panelProd);
            }
        }
        panelContenido.revalidate();
        panelContenido.repaint();
        scrollProductos.getVerticalScrollBar().setValue(0);
    }

    private void buscarProductos() {
        String busqueda = txtBuscar.getText().trim().toLowerCase();
        if (busqueda.isEmpty()) { mostrarProductos("Todos los Productos"); return; }
        String query = "SELECT * FROM producto WHERE activo = 1 AND (LOWER(nombre) LIKE ? OR LOWER(categoria) LIKE ?)";
        List<Producto> resultados = new ArrayList<>();
        try (Connection con = ConexionSQLServer.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, "%" + busqueda + "%"); pst.setString(2, "%" + busqueda + "%");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                resultados.add(new Producto(rs.getInt("id_producto"), rs.getString("nombre"),
                    rs.getDouble("precio"), rs.getString("categoria"), "", rs.getInt("stock")));
            }
        } catch (SQLException e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Error al buscar productos.", "Error", JOptionPane.ERROR_MESSAGE); }
        lblTituloSeccion.setText("Resultados para: \"" + txtBuscar.getText() + "\"");
        mostrarProductos(resultados);
        if (resultados.isEmpty()) JOptionPane.showMessageDialog(this, "No se encontraron productos con: " + txtBuscar.getText(), "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarProductos(String titulo) {
        panelContenido.removeAll();
        if (titulo != null) lblTituloSeccion.setText(titulo);
        String query = "SELECT * FROM producto WHERE activo = 1" + (titulo != null && !titulo.equals("Todos los Productos") ? " AND categoria = ?" : "");
        try (Connection con = ConexionSQLServer.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            if (titulo != null && !titulo.equals("Todos los Productos"))
                ps.setString(1, titulo.replace("Categoría: ", ""));
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    JLabel lblVacio = new JLabel("No hay productos en esta categoría");
                    lblVacio.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                    lblVacio.setForeground(Color.GRAY);
                    panelContenido.add(lblVacio);
                } else {
                    do {
                        Producto producto = new Producto(rs.getInt("id_producto"), rs.getString("nombre"),
                            rs.getDouble("precio"), rs.getString("categoria"), "", rs.getInt("stock"));
                        PanelProducto panelProd = new PanelProducto(producto);
                        panelProd.addMouseListener(new java.awt.event.MouseAdapter() {
                            public void mouseClicked(java.awt.event.MouseEvent evt) { agregarAlCarrito(producto); }
                        });
                        panelContenido.add(panelProd);
                    } while (rs.next());
                }
                panelContenido.revalidate();
                panelContenido.repaint();
                scrollProductos.getVerticalScrollBar().setValue(0);
            }
        } catch (SQLException e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Error al cargar los productos.", "Error", JOptionPane.ERROR_MESSAGE); }
    }

    private void agregarAlCarrito(Producto producto) {
        int respuesta = JOptionPane.showConfirmDialog(this,
            "¿Desea agregar al carrito?\n\n" + producto.getNombre() + "\nPrecio: S/ " + String.format("%.2f", producto.getPrecio()),
            "Agregar al Carrito", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (respuesta != JOptionPane.YES_OPTION) return;
        String cantidadStr = JOptionPane.showInputDialog(this, "Ingrese la cantidad de " + producto.getNombre() + " que desea comprar:");
        if (cantidadStr == null || cantidadStr.isEmpty()) { JOptionPane.showMessageDialog(this, "No se ha ingresado una cantidad.", "Operación cancelada", JOptionPane.INFORMATION_MESSAGE); return; }
        try {
            int cantidad = Integer.parseInt(cantidadStr);
            if (cantidad <= 0) { JOptionPane.showMessageDialog(this, "Ingrese una cantidad válida mayor a 0.", "Cantidad no válida", JOptionPane.ERROR_MESSAGE); return; }
            if (cantidad > producto.getStock()) { JOptionPane.showMessageDialog(this, "Solo hay " + producto.getStock() + " unidad(es) disponible(s).", "Stock insuficiente", JOptionPane.WARNING_MESSAGE); return; }
            contadorCarrito += cantidad;
            btnCarrito.setText("🛒 Carrito (" + contadorCarrito + ")");
            boolean encontrado = false;
            for (CarritoProducto cp : carrito) {
                if (cp.getProducto().equals(producto)) { cp.setCantidad(cp.getCantidad() + cantidad); encontrado = true; break; }
            }
            if (!encontrado) carrito.add(new CarritoProducto(producto, cantidad));
            JOptionPane.showMessageDialog(this, "✓ " + cantidad + " unidad(es) de " + producto.getNombre() + " agregadas al carrito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) { JOptionPane.showMessageDialog(this, "Ingrese un número válido.", "Error en cantidad", JOptionPane.ERROR_MESSAGE); }
    }

    private void verCarrito() {
        if (carrito.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El carrito está vacío.", "Carrito Vacío", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFrame ventanaCarrito = new JFrame("Carrito de Compras");
        ventanaCarrito.setSize(750, 500);
        ventanaCarrito.setLocationRelativeTo(this);
        ventanaCarrito.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ventanaCarrito.setLayout(new BorderLayout());

        String[] columnas = {"Producto", "Precio Unit.", "Cantidad", "Subtotal"};
        DefaultTableModel modeloTabla = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        double descuento = obtenerDescuentoActivo();
        double total = 0;

        for (CarritoProducto cp : carrito) {
            double precioOriginal = cp.getProducto().getPrecio();
            double precioConDesc  = descuento > 0 ? precioOriginal * (1 - descuento) : precioOriginal;
            int    cantidad       = cp.getCantidad();
            double subtotal       = precioConDesc * cantidad;
            modeloTabla.addRow(new Object[]{
                cp.getProducto().getNombre(),
                String.format("S/ %.2f", precioConDesc),
                cantidad,
                String.format("S/ %.2f", subtotal)
            });
            total += subtotal;
        }

        JTable tablaCarrito = new JTable(modeloTabla);
        tablaCarrito.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaCarrito.getTableHeader().setReorderingAllowed(false);
        tablaCarrito.setRowHeight(28);
        JScrollPane scrollPane = new JScrollPane(tablaCarrito);

        JPanel panelTotal = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        final double totalFinal = total;
        JLabel lblTotal = new JLabel(String.format("Total a Pagar: S/ %.2f", total));
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        if (descuento > 0) lblTotal.setText(String.format("<html>Total (con %.0f%% desc.): <b>S/ %.2f</b></html>", descuento * 100, totalFinal));
        panelTotal.add(lblTotal);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JButton btnModificar = new JButton("✏️ Modificar Cantidad");
        btnModificar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnModificar.setBackground(new Color(255, 193, 7));
        btnModificar.setForeground(Color.BLACK);
        btnModificar.setPreferredSize(new Dimension(190, 40));
        btnModificar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnModificar.setFocusPainted(false);
        btnModificar.addActionListener(e -> {
            int fila = tablaCarrito.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(ventanaCarrito, "Selecciona un producto para modificar su cantidad.", "Selección requerida", JOptionPane.WARNING_MESSAGE);
                return;
            }
            CarritoProducto cp = carrito.get(fila);
            String nuevaCantidadStr = JOptionPane.showInputDialog(ventanaCarrito,
                "Ingresa la nueva cantidad para:\n" + cp.getProducto().getNombre() +
                "\n(Stock disponible: " + cp.getProducto().getStock() + ")\nCantidad actual: " + cp.getCantidad(),
                "Modificar Cantidad", JOptionPane.PLAIN_MESSAGE);
            if (nuevaCantidadStr == null || nuevaCantidadStr.isEmpty()) return;
            try {
                int nuevaCantidad = Integer.parseInt(nuevaCantidadStr.trim());
                if (nuevaCantidad <= 0) {
                    JOptionPane.showMessageDialog(ventanaCarrito, "La cantidad debe ser mayor a 0.", "Cantidad inválida", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (nuevaCantidad > cp.getProducto().getStock()) {
                    JOptionPane.showMessageDialog(ventanaCarrito, "Solo hay " + cp.getProducto().getStock() + " unidad(es) disponible(s).", "Stock insuficiente", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                contadorCarrito = contadorCarrito - cp.getCantidad() + nuevaCantidad;
                cp.setCantidad(nuevaCantidad);
                btnCarrito.setText("🛒 Carrito (" + contadorCarrito + ")");
                ventanaCarrito.dispose();
                verCarrito();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(ventanaCarrito, "Ingresa un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnEliminar = new JButton("🗑 Eliminar");
        btnEliminar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnEliminar.setBackground(new Color(200, 50, 50));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setPreferredSize(new Dimension(140, 40));
        btnEliminar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEliminar.setFocusPainted(false);
        btnEliminar.addActionListener(e -> {
            int fila = tablaCarrito.getSelectedRow();
            if (fila == -1) { JOptionPane.showMessageDialog(ventanaCarrito, "Selecciona un producto para eliminarlo.", "Selección requerida", JOptionPane.WARNING_MESSAGE); return; }
            int resp = JOptionPane.showConfirmDialog(ventanaCarrito, "¿Eliminar \"" + modeloTabla.getValueAt(fila, 0) + "\" del carrito?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
            if (resp == JOptionPane.YES_OPTION) {
                contadorCarrito -= carrito.get(fila).getCantidad();
                carrito.remove(fila);
                btnCarrito.setText("🛒 Carrito (" + contadorCarrito + ")");
                ventanaCarrito.dispose();
                verCarrito();
            }
        });

        JButton btnComprar = new JButton("💳 Comprar");
        btnComprar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnComprar.setBackground(new Color(50, 150, 50));
        btnComprar.setForeground(Color.WHITE);
        btnComprar.setPreferredSize(new Dimension(140, 40));
        btnComprar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnComprar.setFocusPainted(false);
        btnComprar.addActionListener(e -> { ventanaCarrito.dispose(); mostrarTotalCompra(); });

        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnComprar);

        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.add(panelTotal, BorderLayout.NORTH);
        panelInferior.add(panelBotones, BorderLayout.CENTER);

        ventanaCarrito.add(scrollPane, BorderLayout.CENTER);
        ventanaCarrito.add(panelInferior, BorderLayout.SOUTH);
        ventanaCarrito.setVisible(true);
    }

    public void actualizarProductos() { mostrarProductos("Todos los Productos"); }
    public List<Producto> getProductos() { return productos; }
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
    }
    public void agregarProductoAlCarrito(Producto producto, int cantidad) {
        carrito.add(new CarritoProducto(producto, cantidad));
    }
}