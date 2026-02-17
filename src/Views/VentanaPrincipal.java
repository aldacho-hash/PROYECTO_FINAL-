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
import java.sql.DriverManager;
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
                "¿Deseas cerrar sesión?",
                "Cerrar Sesión",
                JOptionPane.YES_NO_OPTION);
            
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
        
        JLabel lblUsuario = new JLabel(nombreCliente.toUpperCase() + "!");
        lblUsuario.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panelFoto.add(Box.createVerticalStrut(20));
        panelFoto.add(lblFoto);
        panelFoto.add(Box.createVerticalStrut(10));
        panelFoto.add(lblNombre);
        panelFoto.add(lblUsuario);
        
        JPanel panelDatos = new JPanel(new GridLayout(0, 2, 10, 15));
        panelDatos.setBorder(BorderFactory.createTitledBorder("Datos personales"));
        
        String query = "SELECT * FROM cliente WHERE usuario = ?";
        try (Connection con = ConexionSQLServer.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            
            ps.setString(1, nombreCliente);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                panelDatos.add(new JLabel("Nombre:"));
                panelDatos.add(new JLabel(rs.getString("nombres")));
                
                panelDatos.add(new JLabel("Apellido:"));
                panelDatos.add(new JLabel(rs.getString("apellidos")));
                
                panelDatos.add(new JLabel("Email:"));
                panelDatos.add(new JLabel(rs.getString("correo")));
                
                panelDatos.add(new JLabel("DNI:"));
                panelDatos.add(new JLabel(rs.getString("dni")));
                
                panelDatos.add(new JLabel("Teléfono:"));
                panelDatos.add(new JLabel(rs.getString("telefono")));
                
                panelDatos.add(new JLabel("Fecha de Nacimiento:"));
                String fechaNac = rs.getString("fecha_nacimiento");
                panelDatos.add(new JLabel(fechaNac != null ? fechaNac : "No especificado"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
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
                       "FROM ventas v " +
                       "LEFT JOIN pedidos p ON v.id_venta = p.id_venta " +
                       "INNER JOIN cliente c ON v.id_cliente = c.id_cliente " +
                       "WHERE c.usuario = ? " +
                       "ORDER BY v.fecha DESC";
        
        try (Connection con = ConexionSQLServer.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            
            ps.setString(1, nombreCliente);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("id_venta"),
                    rs.getTimestamp("fecha"),
                    String.format("S/ %.2f", rs.getDouble("total")),
                    rs.getString("estado"),
                    rs.getString("metodo_pago")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        JTable tabla = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabla);
        
        ventanaPedidos.add(scroll);
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
                String direccion = String.format("%s%s - %s %s, %s %s (%s)",
                    rs.getInt("es_principal") == 1 ? "⭐ " : "",
                    rs.getString("alias"),
                    rs.getString("calle"),
                    rs.getString("numero"),
                    rs.getString("distrito"),
                    rs.getString("ciudad"),
                    rs.getString("referencia")
                );
                modeloLista.addElement(direccion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        JScrollPane scroll = new JScrollPane(listaDirecciones);
        
        JPanel panelBotones = new JPanel(new FlowLayout());
        JButton btnAgregar = new JButton("➕ Agregar Dirección");
        btnAgregar.addActionListener(e -> agregarNuevaDireccion(idCliente, modeloLista));
        panelBotones.add(btnAgregar);
        
        ventanaDirecciones.add(scroll, BorderLayout.CENTER);
        ventanaDirecciones.add(panelBotones, BorderLayout.SOUTH);
        ventanaDirecciones.setVisible(true);
    }
    
    private void agregarNuevaDireccion(int idCliente, DefaultListModel<String> modelo) {
        JPanel panel = new JPanel(new GridLayout(8, 2, 10, 10));
        
        JTextField txtAlias = new JTextField();
        JTextField txtCalle = new JTextField();
        JTextField txtNumero = new JTextField();
        JTextField txtDistrito = new JTextField();
        JTextField txtCiudad = new JTextField("Lima");
        JTextField txtDepartamento = new JTextField("Lima");
        JTextField txtReferencia = new JTextField();
        JCheckBox chkPrincipal = new JCheckBox();
        
        panel.add(new JLabel("Alias (Casa, Trabajo, etc.):"));
        panel.add(txtAlias);
        panel.add(new JLabel("Calle/Avenida:"));
        panel.add(txtCalle);
        panel.add(new JLabel("Número:"));
        panel.add(txtNumero);
        panel.add(new JLabel("Distrito:"));
        panel.add(txtDistrito);
        panel.add(new JLabel("Ciudad:"));
        panel.add(txtCiudad);
        panel.add(new JLabel("Departamento:"));
        panel.add(txtDepartamento);
        panel.add(new JLabel("Referencia:"));
        panel.add(txtReferencia);
        panel.add(new JLabel("¿Es dirección principal?"));
        panel.add(chkPrincipal);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Nueva Dirección", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            String query = "INSERT INTO direcciones (id_cliente, alias, calle, numero, distrito, ciudad, departamento, referencia, es_principal) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (Connection con = ConexionSQLServer.getConnection();
                 PreparedStatement ps = con.prepareStatement(query)) {
                
                ps.setInt(1, idCliente);
                ps.setString(2, txtAlias.getText());
                ps.setString(3, txtCalle.getText());
                ps.setString(4, txtNumero.getText());
                ps.setString(5, txtDistrito.getText());
                ps.setString(6, txtCiudad.getText());
                ps.setString(7, txtDepartamento.getText());
                ps.setString(8, txtReferencia.getText());
                ps.setInt(9, chkPrincipal.isSelected() ? 1 : 0);
                
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Dirección agregada correctamente");
                
                String direccion = String.format("%s%s - %s %s, %s %s (%s)",
                    chkPrincipal.isSelected() ? "⭐ " : "",
                    txtAlias.getText(),
                    txtCalle.getText(),
                    txtNumero.getText(),
                    txtDistrito.getText(),
                    txtCiudad.getText(),
                    txtReferencia.getText()
                );
                modelo.addElement(direccion);
                
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
            if (rs.next()) {
                return rs.getDouble("max_desc") / 100.0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
    
    private void mostrarTotalCompra() {
        if (carrito.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay productos en el carrito.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double total = 0;
        for (CarritoProducto cp : carrito) {
            total += cp.getSubtotal();  
        }
        double descuento = obtenerDescuentoActivo();

        double totalConDescuento = total;
        if (descuento > 0) {
            totalConDescuento = total * (1 - descuento);
        }
        String mensaje = String.format(
            "Total sin descuento: S/ %.2f\n" +
            "Descuento aplicado: %.0f%%\n" +
            "TOTAL A PAGAR: S/ %.2f",
            total, descuento*100, totalConDescuento
        );
        int respuesta = JOptionPane.showConfirmDialog(this, mensaje + "\n¿Desea confirmar la compra?", "Confirmación", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (respuesta == JOptionPane.YES_OPTION) {
            realizarCompra(total);
        }
    }
    
    private void realizarCompra(double total) {

        JPanel panelOpciones = new JPanel();
        panelOpciones.setLayout(new BoxLayout(panelOpciones, BoxLayout.Y_AXIS));
        panelOpciones.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel filaPago = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblPago = new JLabel("Método de Pago:");
        JComboBox<String> comboPago = new JComboBox<>(new String[]{"Efectivo", "Yape", "VISA"});
        filaPago.add(lblPago);
        filaPago.add(comboPago);

        JLabel avisoPago = new JLabel(" ");
        avisoPago.setForeground(Color.RED);

        JPanel filaDelivery = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblDelivery = new JLabel("Delivery:");
        JComboBox<String> comboDelivery = new JComboBox<>(new String[]{"Sí", "No"});
        filaDelivery.add(lblDelivery);
        filaDelivery.add(comboDelivery);

        JLabel avisoDelivery = new JLabel(" ");
        avisoDelivery.setForeground(new Color(0, 100, 255));

        panelOpciones.add(filaPago);
        panelOpciones.add(avisoPago);
        panelOpciones.add(Box.createVerticalStrut(8));

        panelOpciones.add(filaDelivery);
        panelOpciones.add(avisoDelivery);

        comboPago.addActionListener(e -> {
            if (comboPago.getSelectedItem().equals("VISA")) {
                avisoPago.setText("+3% al total.");
            } else {
                avisoPago.setText("Sin Costos Adicionales");
            }
        });

        comboDelivery.addActionListener(e -> {
            if (comboDelivery.getSelectedItem().equals("Sí")) {
                avisoDelivery.setText("+S/. 5.00");
            } else {
                avisoDelivery.setText("-----");
            }
        });

        int opcion = JOptionPane.showConfirmDialog(
                this, panelOpciones, "Opciones de Compra",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (opcion != JOptionPane.OK_OPTION) return;

        String metodoPago = comboPago.getSelectedItem().toString();
        String delivery = comboDelivery.getSelectedItem().toString();
        String usuario = nombreCliente;

        CampañaDescuentoDAO dao = new CampañaDescuentoDAO();
        CampañaDescuento camp = dao.obtenerCampañaActiva();

        double descuentoPorcentaje = 0;
        String nombreCampaña = "Ninguna";

        if (camp != null) {
            descuentoPorcentaje = camp.getPorcentajeDescuento();
            nombreCampaña = camp.getNombre();
        }

        double descuentoMonto = total * descuentoPorcentaje;
        double totalConDescuento = total - descuentoMonto;

        List<CarritoProducto> carritoCopia = new ArrayList<>(carrito);

        insertarVenta(usuario, carrito, totalConDescuento, metodoPago, delivery);

        for (CarritoProducto carritoProducto : carrito) {
            Producto producto = carritoProducto.getProducto();
            int cantidad = carritoProducto.getCantidad();

            actualizarStockEnBaseDeDatos(producto, cantidad);
            producto.setStock(producto.getStock() - cantidad);
        }

        carrito.clear();
        contadorCarrito = 0;
        btnCarrito.setText("🛒 Carrito (0)");

        JOptionPane.showMessageDialog(
                this,
                "¡Compra realizada con éxito!\n" +
                "Total antes del descuento: S/ " + String.format("%.2f", total) + "\n" +
                "Descuento aplicado: S/ " + String.format("%.2f", descuentoMonto) + "\n" +
                "Total a pagar: S/ " + String.format("%.2f", totalConDescuento)
        );

        GenerarPDF.generarBoletaDeVenta(
            usuario,
            carritoCopia,
            total,
            metodoPago,
            delivery,
            nombreCampaña,
            descuentoPorcentaje * 100 
        );
    }

    private void actualizarStockEnBaseDeDatos(Producto producto, int cantidadComprada) {
        String query = "UPDATE producto SET stock = stock - ? WHERE id_producto = ?";

        try (Connection conn = ConexionSQLServer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, cantidadComprada);
            stmt.setInt(2, producto.getIdProducto()); 

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Stock actualizado correctamente para el producto: " + producto.getNombre());
            } else {
                System.out.println("No se pudo actualizar el stock para el producto: " + producto.getNombre());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al actualizar el stock: " + e.getMessage());
        }
    }
      
    private boolean actualizarUsuario(String usuario, String nuevoUsuario) {

        usuario = nombreCliente;

        String queryValidar = "SELECT * FROM cliente WHERE usuario = ?";
        String queryUpdate  = "UPDATE cliente SET usuario = ? WHERE usuario = ?";
        String queryUpdateUsuario = "UPDATE usuarios SET usuario = ? WHERE usuario = ?";

        try (Connection conn = ConexionSQLServer.getConnection();
             PreparedStatement validarStmt = conn.prepareStatement(queryValidar)) {

            validarStmt.setString(1, usuario);

            ResultSet rs = validarStmt.executeQuery();

            if (!rs.next()) {
                return false;
            }

            PreparedStatement updateCliente = conn.prepareStatement(queryUpdate);
            updateCliente.setString(1, nuevoUsuario);
            updateCliente.setString(2, usuario);

            PreparedStatement updateUsuario = conn.prepareStatement(queryUpdateUsuario);
            updateUsuario.setString(1, nuevoUsuario);
            updateUsuario.setString(2, usuario);

            int filasCliente = updateCliente.executeUpdate();
            int filasUsuario = updateUsuario.executeUpdate();

            return filasCliente > 0 && filasUsuario > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private int obtenerIdCliente(String usuario) {
        String query = "SELECT id_cliente FROM cliente WHERE usuario = ?";

        try (Connection conn = ConexionSQLServer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            usuario = usuario.trim();
            System.out.println("Buscando usuario: '" + usuario + "'");

            stmt.setString(1, usuario);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_cliente");
            } else {
                System.out.println("Usuario no encontrado: " + usuario);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al obtener el id_cliente: " + e.getMessage());
        }
        return -1;
    }
   
    public void insertarVenta(String usuario, List<CarritoProducto> carrito, double total,
                          String metodoPago, String delivery) {
        String queryVenta = "INSERT INTO ventas (id_cliente, fecha, total, metodo_pago, delivery) VALUES (?, ?, ?, ?, ?)";
        String queryProductoVendido = "INSERT INTO productos_vendidos (id_venta, id_producto, precio, cantidad, subtotal) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConexionSQLServer.getConnection();
             PreparedStatement stmtVenta = conn.prepareStatement(queryVenta, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement stmtProductoVendido = conn.prepareStatement(queryProductoVendido)) {

            int idCliente = obtenerIdCliente(usuario);

            stmtVenta.setInt(1, idCliente);  
            stmtVenta.setDate(2, java.sql.Date.valueOf(java.time.LocalDate.now())); 
            stmtVenta.setDouble(3, total); 
            stmtVenta.setString(4, metodoPago);
            stmtVenta.setString(5, delivery);

            stmtVenta.executeUpdate();  

            ResultSet generatedKeys = stmtVenta.getGeneratedKeys();
            int idVenta = 0;
            if (generatedKeys.next()) {
                idVenta = generatedKeys.getInt(1);  
            }

            for (CarritoProducto carritoProducto : carrito) {
                Producto producto = carritoProducto.getProducto();
                double precio = producto.getPrecio();
                int cantidad = carritoProducto.getCantidad();
                double subtotal = precio * cantidad;

                stmtProductoVendido.setInt(1, idVenta);  
                stmtProductoVendido.setInt(2, producto.getIdProducto());  
                stmtProductoVendido.setDouble(3, precio);  
                stmtProductoVendido.setInt(4, cantidad); 
                stmtProductoVendido.setDouble(5, subtotal);  

                stmtProductoVendido.executeUpdate();
            }

            System.out.println("Venta registrada correctamente.");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al registrar la venta: " + e.getMessage());
        }
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
        
        JButton btnTodos = crearBotonCategoria("🏠 Todos los Productos", "Todos");
        panelMenu.add(btnTodos);
        panelMenu.add(Box.createVerticalStrut(8));
        
        String[][] categorias = {
            {"📖 Libros", "Libros"},
            {"✏️ Útiles Escolares", "Útiles Escolares"},
            {"🎨 Arte y Manualidades", "Arte"},
            {"💼 Oficina", "Oficina"},
            {"🎒 Mochilas", "Mochilas"},
            {"📐 Geometría", "Geometría"},
            {"🖊️ Escritura", "Escritura"},
            {"📋 Cuadernos", "Cuadernos"}
        };
        
        for (String[] categoria : categorias) {
            JButton btnCategoria = crearBotonCategoria(categoria[0], categoria[1]);
            panelMenu.add(btnCategoria);
            panelMenu.add(Box.createVerticalStrut(8));
        }
        
        add(panelMenu, BorderLayout.WEST);
    }
    
    private JButton crearBotonCategoria(String texto, String categoria) {
        JButton btnCategoria = new JButton(texto);
        btnCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnCategoria.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCategoria.setMaximumSize(new Dimension(200, 40));
        btnCategoria.setBackground(Color.WHITE);
        btnCategoria.setHorizontalAlignment(SwingConstants.LEFT);
        btnCategoria.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        btnCategoria.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCategoria.setFocusPainted(false);
        
        if (!categoria.equals("Gestion")) {
            btnCategoria.addActionListener(e -> filtrarPorCategoria(categoria));
        }
        
        btnCategoria.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnCategoria.setBackground(new Color(200, 50, 50));
                btnCategoria.setForeground(Color.WHITE);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (categoria.equals("Gestion")) {
                    btnCategoria.setBackground(new Color(255, 193, 7));
                } else {
                    btnCategoria.setBackground(Color.WHITE);
                }
                btnCategoria.setForeground(Color.BLACK);
            }
        });
        
        return btnCategoria;
    }
    
    private void crearPanelContenido() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(Color.WHITE);
        
        lblTituloSeccion = new JLabel("Todos los Productos");
        lblTituloSeccion.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTituloSeccion.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        panelPrincipal.add(lblTituloSeccion, BorderLayout.NORTH);
        
        panelContenido = new JPanel();
        panelContenido.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));
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
        String query = "SELECT * FROM producto WHERE estado = 'Activo'"; 

        try (Connection con = ConexionSQLServer.getConnection(); 
             PreparedStatement ps = con.prepareStatement(query); 
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id_producto = rs.getInt("id_producto");
                String nombre = rs.getString("nombre");
                String categoria = rs.getString("categoria");
                double precio = rs.getDouble("precio");
                int stock = rs.getInt("stock");

                Producto producto = new Producto(id_producto, nombre, precio, categoria, "", stock);
                productos.add(producto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar los productos desde la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void filtrarPorCategoria(String categoria) {
        List<Producto> productosFiltrados = new ArrayList<>();

        try (Connection con = ConexionSQLServer.getConnection()) {
            String query = "SELECT * FROM producto WHERE estado = 'Activo'";  
            
            if (categoria != null && !categoria.equals("Todos")) {
                query += " AND categoria = ?"; 
            }

            try (PreparedStatement pst = con.prepareStatement(query)) {
                if (categoria != null && !categoria.equals("Todos")) {
                    pst.setString(1, categoria);
                }

                ResultSet rs = pst.executeQuery();
                while (rs.next()) {
                    int id_producto = rs.getInt("id_producto");
                    String nombre = rs.getString("nombre");
                    String categoriaProducto = rs.getString("categoria");
                    double precio = rs.getDouble("precio");
                    int stock = rs.getInt("stock");

                    Producto producto = new Producto(id_producto, nombre, precio, categoriaProducto, "", stock);
                    productosFiltrados.add(producto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (categoria.equals("Todos")) {
            lblTituloSeccion.setText("Todos los Productos");
        } else {
            lblTituloSeccion.setText("Categoría: " + categoria);
        }

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
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        agregarAlCarrito(producto);
                    }
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

        if (busqueda.isEmpty()) {
            mostrarProductos("Todos los Productos");
            return;
        }
        String query = "SELECT * FROM producto WHERE estado = 'Activo' " +
                       "AND (LOWER(nombre) LIKE ? OR LOWER(categoria) LIKE ?)";

        List<Producto> resultados = new ArrayList<>();

        try (Connection con = ConexionSQLServer.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setString(1, "%" + busqueda + "%"); 
            pst.setString(2, "%" + busqueda + "%");

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                int id_producto = rs.getInt("id_producto");
                String nombre = rs.getString("nombre");
                String categoria = rs.getString("categoria");
                double precio = rs.getDouble("precio");
                int stock = rs.getInt("stock");

                Producto producto = new Producto(id_producto, nombre, precio, categoria, "", stock);
                resultados.add(producto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al buscar productos en la base de datos.", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        lblTituloSeccion.setText("Resultados para: \"" + txtBuscar.getText() + "\"");
        mostrarProductos(resultados);
        if (resultados.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No se encontraron productos con: " + txtBuscar.getText(),
                "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void mostrarProductos(String titulo) {
        panelContenido.removeAll();

        if (titulo != null) {
            lblTituloSeccion.setText(titulo);
        }

        String query = "SELECT * FROM producto WHERE estado = 'Activo'";

        if (titulo != null && !titulo.equals("Todos los Productos")) {
            query += " AND categoria = ?";
        }

        try (Connection con = ConexionSQLServer.getConnection(); 
             PreparedStatement ps = con.prepareStatement(query)) {

            if (titulo != null && !titulo.equals("Todos los Productos")) {
                String categoria = titulo.replace("Categoría: ", "");
                ps.setString(1, categoria);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    JLabel lblVacio = new JLabel("No hay productos en esta categoría");
                    lblVacio.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                    lblVacio.setForeground(Color.GRAY);
                    panelContenido.add(lblVacio); 
                } else {
                    do {
                        int id_producto = rs.getInt("id_producto");
                        String nombre = rs.getString("nombre");
                        String categoriaProducto = rs.getString("categoria");
                        double precio = rs.getDouble("precio");
                        int stock = rs.getInt("stock");
                        Producto producto = new Producto(id_producto, nombre, precio, categoriaProducto, "", stock);
                        PanelProducto panelProd = new PanelProducto(producto);
                        panelProd.addMouseListener(new java.awt.event.MouseAdapter() {
                            @Override
                            public void mouseClicked(java.awt.event.MouseEvent evt) {
                                agregarAlCarrito(producto);
                            }
                        });
                        panelContenido.add(panelProd);
                    } while (rs.next());
                }

                panelContenido.revalidate();
                panelContenido.repaint();
                scrollProductos.getVerticalScrollBar().setValue(0); 

            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar los productos desde la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void agregarAlCarrito(Producto producto) {
        int respuesta = JOptionPane.showConfirmDialog(this,
            "¿Desea agregar al carrito?\n\n" +
            producto.getNombre() + "\n" +
            "Precio: S/ " + String.format("%.2f", producto.getPrecio()),
            "Agregar al Carrito",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (respuesta == JOptionPane.YES_OPTION) {
            String cantidadStr = JOptionPane.showInputDialog(this, 
                "Ingrese la cantidad de " + producto.getNombre() + " que desea comprar:");

            if (cantidadStr != null && !cantidadStr.isEmpty()) {
                try {
                    int cantidad = Integer.parseInt(cantidadStr);

                    if (cantidad > 0) {
                        if (cantidad > producto.getStock()) {
                            JOptionPane.showMessageDialog(this,
                                "No hay suficiente stock de " + producto.getNombre() + ". Solo hay " + producto.getStock() + " unidad(es) disponible(s).",
                                "Stock insuficiente",
                                JOptionPane.WARNING_MESSAGE);
                            return;  
                        }

                        contadorCarrito += cantidad;
                        btnCarrito.setText("🛒 Carrito (" + contadorCarrito + ")");

                        boolean encontrado = false;
                        for (CarritoProducto cp : carrito) {
                            if (cp.getProducto().equals(producto)) {
                                cp.setCantidad(cp.getCantidad() + cantidad);
                                encontrado = true;
                                break;
                            }
                        }

                        if (!encontrado) {
                            carrito.add(new CarritoProducto(producto, cantidad));
                        }

                        JOptionPane.showMessageDialog(this,
                            "✓ " + cantidad + " unidad(es) de " + producto.getNombre() + " agregadas al carrito.",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Por favor ingrese una cantidad válida mayor a 0.",
                            "Cantidad no válida",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this,
                        "Por favor ingrese un número válido para la cantidad.",
                        "Error en cantidad",
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "No se ha ingresado una cantidad.",
                    "Operación cancelada",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    // =====================================================================
    // MÉTODO verCarrito() MODIFICADO:
    // - Columna "Eliminar" REMOVIDA de la tabla
    // - Botón "🗑 Eliminar Seleccionado" agregado junto a "💳 Comprar"
    // - Para eliminar: selecciona una fila y presiona el botón Eliminar
    // =====================================================================
    private void verCarrito() {
        if (carrito.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El carrito está vacío.", "Carrito Vacío", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JFrame ventanaCarrito = new JFrame("Carrito de Compras");
            ventanaCarrito.setSize(700, 500);
            ventanaCarrito.setLocationRelativeTo(this);
            ventanaCarrito.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            ventanaCarrito.setLayout(new BorderLayout());

            // Sin columna "Eliminar" en la tabla
            String[] columnas = {"Producto", "Precio", "Cantidad", "Subtotal"};
            DefaultTableModel modeloTabla = new DefaultTableModel(columnas, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Ninguna celda es editable
                }
            };

            double descuento = obtenerDescuentoActivo();
            double total = 0;

            for (CarritoProducto cp : carrito) {
                String nombreProducto = cp.getProducto().getNombre();
                double precioOriginal = cp.getProducto().getPrecio();

                double precioConDesc = precioOriginal;
                if (descuento > 0) {
                    precioConDesc = precioOriginal - (precioOriginal * descuento);
                }

                int cantidad = cp.getCantidad();
                double subtotal = precioConDesc * cantidad;

                modeloTabla.addRow(new Object[]{
                    nombreProducto,
                    String.format("S/ %.2f", precioConDesc),
                    cantidad,
                    String.format("S/ %.2f", subtotal)
                });

                total += subtotal;
            }

            JTable tablaCarrito = new JTable(modeloTabla);
            tablaCarrito.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            tablaCarrito.getTableHeader().setReorderingAllowed(false);

            JScrollPane scrollPane = new JScrollPane(tablaCarrito);

            // Panel inferior con total y botones
            JPanel panelInferior = new JPanel(new BorderLayout());

            // Total
            JPanel panelTotal = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            final double totalFinal = total;
            JLabel lblTotal = new JLabel(String.format("Total a Pagar: S/ %.2f", total));
            lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
            panelTotal.add(lblTotal);

            // Botones: Eliminar + Comprar juntos
            JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

            // Botón Eliminar seleccionado
            JButton btnEliminar = new JButton("🗑 Eliminar");
            btnEliminar.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btnEliminar.setBackground(new Color(200, 50, 50));
            btnEliminar.setForeground(Color.WHITE);
            btnEliminar.setPreferredSize(new Dimension(150, 40));
            btnEliminar.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnEliminar.setFocusPainted(false);
            btnEliminar.addActionListener(e -> {
                int filaSeleccionada = tablaCarrito.getSelectedRow();
                if (filaSeleccionada == -1) {
                    JOptionPane.showMessageDialog(ventanaCarrito,
                        "Selecciona un producto de la tabla para eliminarlo.",
                        "Selección requerida",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int respuesta = JOptionPane.showConfirmDialog(ventanaCarrito,
                    "¿Eliminar \"" + modeloTabla.getValueAt(filaSeleccionada, 0) + "\" del carrito?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION);
                if (respuesta == JOptionPane.YES_OPTION) {
                    CarritoProducto productoEliminar = carrito.get(filaSeleccionada);
                    contadorCarrito -= productoEliminar.getCantidad();
                    carrito.remove(filaSeleccionada);
                    btnCarrito.setText("🛒 Carrito (" + contadorCarrito + ")");
                    ventanaCarrito.dispose();
                    verCarrito(); // Reabre el carrito actualizado
                }
            });

            // Botón Comprar
            JButton btnComprarCarrito = new JButton("💳 Comprar");
            btnComprarCarrito.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btnComprarCarrito.setBackground(new Color(50, 150, 50));
            btnComprarCarrito.setForeground(Color.WHITE);
            btnComprarCarrito.setPreferredSize(new Dimension(150, 40));
            btnComprarCarrito.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnComprarCarrito.setFocusPainted(false);
            btnComprarCarrito.addActionListener(e -> {
                ventanaCarrito.dispose();
                mostrarTotalCompra();
            });

            panelBotones.add(btnEliminar);
            panelBotones.add(btnComprarCarrito);

            panelInferior.add(panelTotal, BorderLayout.NORTH);
            panelInferior.add(panelBotones, BorderLayout.CENTER);

            ventanaCarrito.add(scrollPane, BorderLayout.CENTER);
            ventanaCarrito.add(panelInferior, BorderLayout.SOUTH);

            if (descuento > 0) {
                lblTotal.setText(String.format(
                    "<html>Total a Pagar (con %.0f%% desc.): <b>S/ %.2f</b></html>",
                    descuento * 100,
                    totalFinal
                ));
            }

            ventanaCarrito.setVisible(true);
        }
    }

    public void actualizarProductos() {
        mostrarProductos("Todos los Productos");
    }
    
    public List<Producto> getProductos() {
        return productos;
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
        }
    }
    
    public void agregarProductoAlCarrito(Producto producto, int cantidad) {
        CarritoProducto carritoProducto = new CarritoProducto(producto, cantidad);
        carrito.add(carritoProducto);
    }
}