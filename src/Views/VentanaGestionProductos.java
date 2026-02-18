package Views;

import Modelos.Producto;
import conexiondb.ConexionSQLServer;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;

public class VentanaGestionProductos extends JFrame {
    
    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;
    private JButton btnAgregar, btnEditar, btnEliminar, btnAgregarCampaña;
    private List<Producto> listaProductos;
    private final VentanaPrincipal ventanaPrincipal;
    private JTextField txtBuscar;
    
    private static final int STOCK_MINIMO = 5;

    // Controla que la alerta de stock solo aparezca UNA VEZ por sesión
    private static boolean alertaStockMostrada = false;

    public VentanaGestionProductos(VentanaPrincipal ventanaPrincipal) {
        this.ventanaPrincipal = ventanaPrincipal;
        this.listaProductos = new ArrayList<>();
        initComponents();
        cargarDatosTabla();
    }
    
    private void initComponents() {
        setTitle("Gestión de Productos - Librería");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        crearTabla();
        crearPanelBotones();

        getContentPane().setBackground(Color.WHITE);

        txtBuscar = new JTextField(20);
        JButton btnBuscar = new JButton("Buscar Producto");
        btnBuscar.setBackground(new Color(40, 167, 69));
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setPreferredSize(new Dimension(200, 45));

        btnBuscar.addActionListener(e -> buscarProducto(txtBuscar.getText().trim()));

        txtBuscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {}
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                if (txtBuscar.getText().trim().isEmpty()) {
                    cargarDatosTabla();
                }
            }
            public void changedUpdate(javax.swing.event.DocumentEvent e) {}
        });

        JPanel panelBusqueda = new JPanel();
        panelBusqueda.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelBusqueda.add(new JLabel("Buscar Producto:"));
        panelBusqueda.add(txtBuscar);
        panelBusqueda.add(btnBuscar);

        add(panelBusqueda, BorderLayout.NORTH);
    }
    
    private void crearTabla() {
        String[] columnas = {"Nombre", "Precio (S/)", "Categoría", "Stock"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaProductos.setRowHeight(30);
        tablaProductos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaProductos.getTableHeader().setBackground(new Color(240, 240, 240));
        tablaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        tablaProductos.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                try {
                    int stock = Integer.parseInt(table.getValueAt(row, 3).toString());
                    if (stock < STOCK_MINIMO) {
                        c.setBackground(new Color(255, 150, 150));
                        c.setForeground(Color.BLACK);
                    } else {
                        c.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
                        c.setForeground(Color.BLACK);
                    }
                } catch (NumberFormatException e) {
                    c.setBackground(Color.WHITE);
                }
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaProductos);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void crearPanelBotones() {
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panelBotones.setBackground(Color.WHITE);
        panelBotones.setPreferredSize(new Dimension(getWidth(), 80));
        
        btnAgregar        = crearBoton("➕ Agregar Producto",  new Color(40, 167, 69));
        btnEditar         = crearBoton("✏️ Editar Producto",   new Color(237, 159, 33));
        btnEliminar       = crearBoton("🗑️ Eliminar Producto", new Color(220, 53, 69));
        btnAgregarCampaña = crearBoton("Agregar Campaña",      new Color(40, 167, 69));
        
        btnAgregarCampaña.addActionListener(e -> abrirFormularioCampaña());
        btnAgregar.addActionListener(e -> agregarProducto());
        btnEditar.addActionListener(e -> editarProducto());
        btnEliminar.addActionListener(e -> eliminarProducto());
        
        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnAgregarCampaña);
        
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void abrirFormularioCampaña() {
        JTextField txtNombre    = new JTextField(20);
        JTextField txtDescuento = new JTextField(5);
        JTextField txtInicio    = new JTextField("2026-02-17");
        JTextField txtFin       = new JTextField("2026-12-31");

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Nombre de la campaña:"));
        panel.add(txtNombre);
        panel.add(new JLabel("Descuento (%) - solo el número, ej: 10:"));
        panel.add(txtDescuento);
        panel.add(new JLabel("Fecha inicio (YYYY-MM-DD):"));
        panel.add(txtInicio);
        panel.add(new JLabel("Fecha fin (YYYY-MM-DD):"));
        panel.add(txtFin);

        int result = JOptionPane.showConfirmDialog(null, panel, "Registrar campaña",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            registrarCampaña(txtNombre.getText(), txtDescuento.getText(),
                             txtInicio.getText(), txtFin.getText());
        }
    }

    private void registrarCampaña(String nombre, String descuento, String inicio, String fin) {
        String query = "INSERT INTO campana_descuento(nombre, fecha_inicio, fecha_fin, descuento, activo) VALUES (?,?,?,?,?)";
        try (Connection conn = ConexionSQLServer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nombre);
            stmt.setDate(2, java.sql.Date.valueOf(inicio));
            stmt.setDate(3, java.sql.Date.valueOf(fin));
            stmt.setDouble(4, Double.parseDouble(descuento.replace("%", "").trim()));
            stmt.setInt(5, 1);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Campaña registrada correctamente.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al registrar campaña: " + ex.getMessage());
        }
    }
    
    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setPreferredSize(new Dimension(200, 45));
        boton.setBorder(BorderFactory.createEmptyBorder());
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setFocusPainted(false);
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { boton.setBackground(color.darker()); }
            public void mouseExited(java.awt.event.MouseEvent evt)  { boton.setBackground(color); }
        });
        return boton;
    }
    
    public void cargarDatosTabla() {
        String query = "SELECT * FROM producto WHERE activo = 1";
        try (Connection con = ConexionSQLServer.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            if (listaProductos == null) listaProductos = new ArrayList<>();
            listaProductos.clear();
            modeloTabla.setRowCount(0);

            List<String> productosStockBajo = new ArrayList<>();

            while (rs.next()) {
                int    id_producto = rs.getInt("id_producto");
                String nombre      = rs.getString("nombre");
                String categoria   = rs.getString("categoria");
                double precio      = rs.getDouble("precio");
                int    stock       = rs.getInt("stock");

                listaProductos.add(new Producto(id_producto, nombre, precio, categoria, "", stock));
                modeloTabla.addRow(new Object[]{nombre, String.format("%.2f", precio), categoria, stock});

                if (stock < STOCK_MINIMO) {
                    productosStockBajo.add("⚠ " + nombre + " (Stock: " + stock + ")");
                }
            }

            // Alerta solo UNA VEZ por sesión
            if (!productosStockBajo.isEmpty() && !alertaStockMostrada) {
                alertaStockMostrada = true;
                StringBuilder mensaje = new StringBuilder(
                    "⚠️ ALERTA: Los siguientes productos tienen stock bajo (menos de "
                    + STOCK_MINIMO + " unidades):\n\n");
                for (String prod : productosStockBajo) {
                    mensaje.append(prod).append("\n");
                }
                JOptionPane.showMessageDialog(this, mensaje.toString(),
                        "⚠️ Stock Bajo", JOptionPane.WARNING_MESSAGE);
            }

            if (ventanaPrincipal != null) {
                ventanaPrincipal.mostrarProductos("Todos los Productos");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar los productos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void buscarProducto(String nombreProducto) {
        if (nombreProducto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingresa el nombre del producto.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String query = "SELECT * FROM producto WHERE nombre LIKE ? AND activo = 1";
        try (Connection con = ConexionSQLServer.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, "%" + nombreProducto + "%");
            ResultSet rs = ps.executeQuery();
            modeloTabla.setRowCount(0);
            if (!rs.isBeforeFirst()) {
                cargarDatosTabla();
                JOptionPane.showMessageDialog(this, "No se encontraron resultados.");
                return;
            }
            while (rs.next()) {
                modeloTabla.addRow(new Object[]{
                    rs.getString("nombre"),
                    String.format("%.2f", rs.getDouble("precio")),
                    rs.getString("categoria"),
                    rs.getInt("stock")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al buscar el producto", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarProducto() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField txtNombre = new JTextField();
        JTextField txtPrecio = new JTextField();
        JTextField txtStock  = new JTextField();

        // ── Categorías corregidas para coincidir con el menú lateral ──
        String[] categorias = {
            "Libros", "Cuadernos", "Escritura", "Mochilas",
            "Geometría", "Arte y Manualidades", "Oficina", "Útiles Escolares"
        };
        JComboBox<String> cmbCategoria = new JComboBox<>(categorias);

        panel.add(new JLabel("Nombre del Producto:")); panel.add(txtNombre);
        panel.add(new JLabel("Precio (S/):"));         panel.add(txtPrecio);
        panel.add(new JLabel("Categoría:"));           panel.add(cmbCategoria);
        panel.add(new JLabel("Stock:"));               panel.add(txtStock);

        if (JOptionPane.showConfirmDialog(this, panel, "Agregar Nuevo Producto",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;

        try {
            String nombre    = txtNombre.getText().trim();
            double precio    = Double.parseDouble(txtPrecio.getText().trim().replace(",", "."));
            String categoria = (String) cmbCategoria.getSelectedItem();
            int    stock     = Integer.parseInt(txtStock.getText().trim());

            if (nombre.isEmpty() || precio <= 0 || stock <= 0) {
                JOptionPane.showMessageDialog(this, "Todos los campos deben ser válidos", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = ConexionSQLServer.getConnection()) {
                try (PreparedStatement s = conn.prepareStatement("SELECT COUNT(*) FROM producto WHERE nombre = ?")) {
                    s.setString(1, nombre);
                    ResultSet rs = s.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(this, "Ya existe un producto con ese nombre.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                try (PreparedStatement s = conn.prepareStatement(
                        "INSERT INTO producto (nombre, categoria, precio, precio_descuento, stock) VALUES (?,?,?,?,?)")) {
                    s.setString(1, nombre); s.setString(2, categoria);
                    s.setDouble(3, precio); s.setDouble(4, 0); s.setInt(5, stock);
                    if (s.executeUpdate() > 0) {
                        JOptionPane.showMessageDialog(this, "Producto agregado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        cargarDatosTabla();
                        ventanaPrincipal.mostrarProductos("Todos los Productos");
                    }
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El precio debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error en la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarProducto() {
        int filaSeleccionada = tablaProductos.getSelectedRow();

        // ── Si no hay fila seleccionada → ventana para elegir producto ──
        if (filaSeleccionada == -1) {

            if (listaProductos == null || listaProductos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay productos disponibles.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String[] nombres = listaProductos.stream()
                    .map(Producto::getNombre)
                    .toArray(String[]::new);

            JComboBox<String> combo = new JComboBox<>(nombres);
            combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            combo.setPreferredSize(new Dimension(280, 30));

            JPanel panelSeleccion = new JPanel(new BorderLayout(5, 10));
            panelSeleccion.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            JLabel lbl = new JLabel("Escoge el producto a editar:");
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
            panelSeleccion.add(lbl, BorderLayout.NORTH);
            panelSeleccion.add(combo, BorderLayout.CENTER);

            int opcion = JOptionPane.showConfirmDialog(
                    this, panelSeleccion,
                    "Seleccionar Producto para Editar",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (opcion != JOptionPane.OK_OPTION) return;

            filaSeleccionada = combo.getSelectedIndex();
            tablaProductos.setRowSelectionInterval(filaSeleccionada, filaSeleccionada);
        }

        // ── Datos actuales del producto ──
        String nombreActual    = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        String categoriaActual = (String) modeloTabla.getValueAt(filaSeleccionada, 2);
        int    stockActual     = (int)    modeloTabla.getValueAt(filaSeleccionada, 3);
        double precioActual;
        try {
            precioActual = Double.parseDouble(
                    modeloTabla.getValueAt(filaSeleccionada, 1).toString().replace(",", "."));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El precio del producto no es válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ── Formulario de edición ──
        JTextField txtNombre = new JTextField(nombreActual);
        JTextField txtPrecio = new JTextField(String.valueOf(precioActual));
        JTextField txtStock  = new JTextField(String.valueOf(stockActual));

        // ── Categorías corregidas para coincidir con el menú lateral ──
        String[] categorias = {
            "Libros", "Cuadernos", "Escritura", "Mochilas",
            "Geometría", "Arte y Manualidades", "Oficina", "Útiles Escolares"
        };
        JComboBox<String> cmbCategoria = new JComboBox<>(categorias);
        cmbCategoria.setSelectedItem(categoriaActual);

        JPanel panelEditar = new JPanel(new GridLayout(4, 2, 10, 10));
        panelEditar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelEditar.add(new JLabel("Nombre del Producto:")); panelEditar.add(txtNombre);
        panelEditar.add(new JLabel("Precio (S/):"));         panelEditar.add(txtPrecio);
        panelEditar.add(new JLabel("Categoría:"));           panelEditar.add(cmbCategoria);
        panelEditar.add(new JLabel("Stock:"));               panelEditar.add(txtStock);

        if (JOptionPane.showConfirmDialog(this, panelEditar,
                "Editar: " + nombreActual,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;

        try {
            String nuevoNombre    = txtNombre.getText().trim();
            double nuevoPrecio    = Double.parseDouble(txtPrecio.getText().trim().replace(",", "."));
            String nuevaCategoria = (String) cmbCategoria.getSelectedItem();
            int    nuevoStock     = Integer.parseInt(txtStock.getText().trim());

            if (nuevoNombre.isEmpty() || nuevoPrecio <= 0 || nuevoStock <= 0) {
                JOptionPane.showMessageDialog(this, "Todos los campos deben ser válidos", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String query = "UPDATE producto SET nombre = ?, categoria = ?, precio = ?, stock = ? WHERE nombre = ?";
            try (Connection con = ConexionSQLServer.getConnection();
                 PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, nuevoNombre);
                ps.setString(2, nuevaCategoria);
                ps.setDouble(3, nuevoPrecio);
                ps.setInt(4,    nuevoStock);
                ps.setString(5, nombreActual);

                if (ps.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(this, "Producto actualizado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarDatosTabla();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al actualizar el producto", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El precio debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error en la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void eliminarProducto() {
        int filaSeleccionada = tablaProductos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un producto de la tabla", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Producto productoEliminar = listaProductos.get(filaSeleccionada);

        try (Connection con = ConexionSQLServer.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT COUNT(*) FROM productos_vendidos WHERE id_producto = ?")) {
                ps.setInt(1, productoEliminar.getIdProducto());
                ResultSet rs = ps.executeQuery();
                rs.next();
                if (rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this,
                            "❗ No se puede eliminar este producto.\nEstá registrado en ventas.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            try (PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM producto WHERE id_producto = ?")) {
                ps.setInt(1, productoEliminar.getIdProducto());
                if (ps.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(this, "Producto eliminado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarDatosTabla();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al eliminar el producto", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}