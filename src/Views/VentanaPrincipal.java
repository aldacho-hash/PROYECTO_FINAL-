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
                panelDatos.add(new JLabel("Nombre:"));       panelDatos.add(new JLabel(rs.getString("nombres")));
                panelDatos.add(new JLabel("Apellido:"));     panelDatos.add(new JLabel(rs.getString("apellidos")));
                panelDatos.add(new JLabel("Email:"));        panelDatos.add(new JLabel(rs.getString("correo")));
                panelDatos.add(new JLabel("DNI:"));          panelDatos.add(new JLabel(rs.getString("dni")));
                panelDatos.add(new JLabel("Teléfono:"));     panelDatos.add(new JLabel(rs.getString("telefono")));
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
        ventanaDirecciones.setSize(700, 500);
        ventanaDirecciones.setLocationRelativeTo(this);
        ventanaDirecciones.setLayout(new BorderLayout());
        DefaultListModel<String> modeloLista = new DefaultListModel<>();
        JList<String> listaDirecciones = new JList<>(modeloLista);
        int idCliente = obtenerIdCliente(nombreCliente);
        String query = "SELECT * FROM direcciones WHERE id_cliente = ? ORDER BY es_principal DESC";
        try (Connection con = ConexionSQLServer.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, idCliente);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                modeloLista.addElement(String.format("%s%s - %s %s, %s %s (%s)",
                    rs.getInt("es_principal") == 1 ? "⭐ " : "",
                    rs.getString("alias"), rs.getString("calle"), rs.getString("numero"),
                    rs.getString("distrito"), rs.getString("ciudad"), rs.getString("referencia")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        JPanel panelBotones = new JPanel(new FlowLayout());
        JButton btnAgregar = new JButton("➕ Agregar Dirección");
        btnAgregar.addActionListener(e -> agregarNuevaDireccion(idCliente, modeloLista));
        panelBotones.add(btnAgregar);
        ventanaDirecciones.add(new JScrollPane(listaDirecciones), BorderLayout.CENTER);
        ventanaDirecciones.add(panelBotones, BorderLayout.SOUTH);
        ventanaDirecciones.setVisible(true);
    }
    
    private void agregarNuevaDireccion(int idCliente, DefaultListModel<String> modelo) {
        JPanel panel = new JPanel(new GridLayout(8, 2, 10, 10));
        JTextField txtAlias = new JTextField(), txtCalle = new JTextField(),
                   txtNumero = new JTextField(), txtDistrito = new JTextField(),
                   txtCiudad = new JTextField("Lima"), txtDepartamento = new JTextField("Lima"),
                   txtReferencia = new JTextField();
        JCheckBox chkPrincipal = new JCheckBox();
        panel.add(new JLabel("Alias:")); panel.add(txtAlias);
        panel.add(new JLabel("Calle/Avenida:")); panel.add(txtCalle);
        panel.add(new JLabel("Número:")); panel.add(txtNumero);
        panel.add(new JLabel("Distrito:")); panel.add(txtDistrito);
        panel.add(new JLabel("Ciudad:")); panel.add(txtCiudad);
        panel.add(new JLabel("Departamento:")); panel.add(txtDepartamento);
        panel.add(new JLabel("Referencia:")); panel.add(txtReferencia);
        panel.add(new JLabel("¿Es principal?")); panel.add(chkPrincipal);
        if (JOptionPane.showConfirmDialog(this, panel, "Nueva Dirección", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            String query = "INSERT INTO direcciones (id_cliente, alias, calle, numero, distrito, ciudad, departamento, referencia, es_principal) VALUES (?,?,?,?,?,?,?,?,?)";
            try (Connection con = ConexionSQLServer.getConnection();
                 PreparedStatement ps = con.prepareStatement(query)) {
                ps.setInt(1, idCliente); ps.setString(2, txtAlias.getText());
                ps.setString(3, txtCalle.getText()); ps.setString(4, txtNumero.getText());
                ps.setString(5, txtDistrito.getText()); ps.setString(6, txtCiudad.getText());
                ps.setString(7, txtDepartamento.getText()); ps.setString(8, txtReferencia.getText());
                ps.setInt(9, chkPrincipal.isSelected() ? 1 : 0);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Dirección agregada correctamente");
                modelo.addElement(String.format("%s%s - %s %s, %s %s (%s)",
                    chkPrincipal.isSelected() ? "⭐ " : "",
                    txtAlias.getText(), txtCalle.getText(), txtNumero.getText(),
                    txtDistrito.getText(), txtCiudad.getText(), txtReferencia.getText()));
            } catch (SQLException e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Error al agregar dirección"); }
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
        if (respuesta == JOptionPane.YES_OPTION) realizarCompra(total);
    }
    
    private void realizarCompra(double total) {
        JPanel panelOpciones = new JPanel();
        panelOpciones.setLayout(new BoxLayout(panelOpciones, BoxLayout.Y_AXIS));
        panelOpciones.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel filaPago = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> comboPago = new JComboBox<>(new String[]{"Efectivo", "Yape", "VISA"});
        filaPago.add(new JLabel("Método de Pago:")); filaPago.add(comboPago);
        JLabel avisoPago = new JLabel(" "); avisoPago.setForeground(Color.RED);
        JPanel filaDelivery = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> comboDelivery = new JComboBox<>(new String[]{"Sí", "No"});
        filaDelivery.add(new JLabel("Delivery:")); filaDelivery.add(comboDelivery);
        JLabel avisoDelivery = new JLabel(" "); avisoDelivery.setForeground(new Color(0, 100, 255));
        panelOpciones.add(filaPago); panelOpciones.add(avisoPago);
        panelOpciones.add(Box.createVerticalStrut(8));
        panelOpciones.add(filaDelivery); panelOpciones.add(avisoDelivery);
        comboPago.addActionListener(e -> avisoPago.setText(comboPago.getSelectedItem().equals("VISA") ? "+3% al total." : "Sin Costos Adicionales"));
        comboDelivery.addActionListener(e -> avisoDelivery.setText(comboDelivery.getSelectedItem().equals("Sí") ? "+S/. 5.00" : "-----"));
        int opcion = JOptionPane.showConfirmDialog(this, panelOpciones, "Opciones de Compra", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcion != JOptionPane.OK_OPTION) return;
        String metodoPago = comboPago.getSelectedItem().toString();
        String delivery   = comboDelivery.getSelectedItem().toString();
        CampañaDescuentoDAO dao = new CampañaDescuentoDAO();
        CampañaDescuento camp   = dao.obtenerCampañaActiva();
        double descuentoPorcentaje = camp != null ? camp.getPorcentajeDescuento() : 0;
        String nombreCampaña       = camp != null ? camp.getNombre() : "Ninguna";
        double descuentoMonto    = total * descuentoPorcentaje;
        double totalConDescuento = total - descuentoMonto;
        List<CarritoProducto> carritoCopia = new ArrayList<>(carrito);
        insertarVenta(nombreCliente, carrito, totalConDescuento, metodoPago, delivery);
        for (CarritoProducto cp : carrito) {
            actualizarStockEnBaseDeDatos(cp.getProducto(), cp.getCantidad());
            cp.getProducto().setStock(cp.getProducto().getStock() - cp.getCantidad());
        }
        carrito.clear(); contadorCarrito = 0; btnCarrito.setText("🛒 Carrito (0)");
        JOptionPane.showMessageDialog(this,
            "¡Compra realizada con éxito!\nTotal antes del descuento: S/ " + String.format("%.2f", total) +
            "\nDescuento aplicado: S/ " + String.format("%.2f", descuentoMonto) +
            "\nTotal a pagar: S/ " + String.format("%.2f", totalConDescuento));
        GenerarPDF.generarBoletaDeVenta(nombreCliente, carritoCopia, total, metodoPago, delivery, nombreCampaña, descuentoPorcentaje * 100);
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
    
    // ══════════════════════════════════════════════════════════
    // RF06 ─ Ver carrito con botón MODIFICAR CANTIDAD agregado
    // ══════════════════════════════════════════════════════════
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

        // ── Panel total ──
        JPanel panelTotal = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        final double totalFinal = total;
        JLabel lblTotal = new JLabel(String.format("Total a Pagar: S/ %.2f", total));
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        if (descuento > 0) lblTotal.setText(String.format("<html>Total (con %.0f%% desc.): <b>S/ %.2f</b></html>", descuento * 100, totalFinal));
        panelTotal.add(lblTotal);

        // ── Botones ──
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        // Botón Modificar Cantidad (RF06)
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
                // Actualizar contador del carrito
                contadorCarrito = contadorCarrito - cp.getCantidad() + nuevaCantidad;
                cp.setCantidad(nuevaCantidad);
                btnCarrito.setText("🛒 Carrito (" + contadorCarrito + ")");
                ventanaCarrito.dispose();
                verCarrito(); // Reabrir carrito actualizado
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(ventanaCarrito, "Ingresa un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Botón Eliminar
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

        // Botón Comprar
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