package Views;

import Modelos.Producto;
import conexiondb.ConexionSQLServer;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
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

    JPanel panelTitulo = new JPanel();
    panelTitulo.setBackground(new Color(200, 50, 50));
    panelTitulo.setPreferredSize(new Dimension(getWidth(), 60));

    JLabel lblTitulo = new JLabel("📦 GESTIÓN DE PRODUCTOS");
    lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
    lblTitulo.setForeground(Color.WHITE);
    panelTitulo.add(lblTitulo);

    add(panelTitulo, BorderLayout.NORTH);

    crearTabla();

    crearPanelBotones();

    getContentPane().setBackground(Color.WHITE);

    JTextField txtBuscar = new JTextField(20);  
    JButton btnBuscar = new JButton("Buscar Producto");
    btnBuscar.setBackground(new Color(40, 167, 69));  
    btnBuscar.setForeground(Color.WHITE);
    btnBuscar.setPreferredSize(new Dimension(200, 45));

    btnBuscar.addActionListener(e -> buscarProducto(txtBuscar.getText().trim()));  

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
        
        JScrollPane scrollPane = new JScrollPane(tablaProductos);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void crearPanelBotones() {
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panelBotones.setBackground(Color.WHITE);
        panelBotones.setPreferredSize(new Dimension(getWidth(), 80));
        
        btnAgregar = crearBoton("➕ Agregar Producto", new Color(40, 167, 69));
        btnEditar = crearBoton("✏️ Editar Producto", new Color(237, 159, 33));
        btnEliminar = crearBoton("🗑️ Eliminar Producto", new Color(220, 53, 69));
        btnAgregarCampaña = crearBoton("Agregar Campaña", new Color(40, 167, 69));
        
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

    JTextField txtNombre = new JTextField(20);
    JTextField txtDescuento = new JTextField(5);

    JTextField txtInicio = new JTextField("2025-12-01");
    JTextField txtFin = new JTextField("2025-12-25");

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    panel.add(new JLabel("Nombre de la campaña:"));
    panel.add(txtNombre);

    panel.add(new JLabel("Descuento (%):"));
    panel.add(txtDescuento);

    panel.add(new JLabel("Fecha inicio (YYYY-MM-DD):"));
    panel.add(txtInicio);

    panel.add(new JLabel("Fecha fin (YYYY-MM-DD):"));
    panel.add(txtFin);

    int result = JOptionPane.showConfirmDialog(
        null, panel, "Registrar campaña",
        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
    );

    if (result == JOptionPane.OK_OPTION) {
        registrarCampaña(
                txtNombre.getText(),
                txtDescuento.getText(),
                txtInicio.getText(),
                txtFin.getText()
        );
    }
}
    private void registrarCampaña(String nombre, String descuento, String inicio, String fin) {
    String query = "INSERT INTO CAMPAÑA_DESCUENTO(nombre, fecha_inicio, fecha_fin, descuento) VALUES (?,?,?,?)";

    try (Connection conn = ConexionSQLServer.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setString(1, nombre);
        stmt.setDate(2, java.sql.Date.valueOf(inicio));
        stmt.setDate(3, java.sql.Date.valueOf(fin));
        stmt.setDouble(4, Double.parseDouble(descuento));

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
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(color.darker());
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(color);
            }
        });
        
        return boton;
    }
    
    public void cargarDatosTabla() {
    String query = "SELECT * FROM PRODUCTO WHERE estado = 'Activo'";

    try (Connection con = ConexionSQLServer.getConnection(); 
         PreparedStatement ps = con.prepareStatement(query); 
         ResultSet rs = ps.executeQuery()) {

        if (listaProductos == null) {
            listaProductos = new ArrayList<>();
        }
        modeloTabla.setRowCount(0);  

        while (rs.next()) {
            int id_producto = rs.getInt("id_producto");
            String nombre = rs.getString("nombre");
            String categoria = rs.getString("categoria");
            double precio = rs.getDouble("precio");
            int stock = rs.getInt("stock");

            Producto producto = new Producto(id_producto,nombre, precio, categoria, "", stock);
            listaProductos.add(producto);

            Object[] fila = {
                nombre,
                String.format("%.2f", precio),
                categoria,
                stock
            };
            modeloTabla.addRow(fila);
        }
        
        if (ventanaPrincipal != null) {
            ventanaPrincipal.mostrarProductos("Todos los Productos");
        }

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al cargar los productos desde la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    
    private void buscarProducto(String nombreProducto) {
    if (nombreProducto.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Por favor ingresa el nombre del producto.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    String query = "SELECT * FROM PRODUCTO WHERE nombre LIKE ? AND estado = 'Activo'";

    try (Connection con = ConexionSQLServer.getConnection(); 
         PreparedStatement ps = con.prepareStatement(query)) {

        ps.setString(1, "%" + nombreProducto + "%"); 

        ResultSet rs = ps.executeQuery();

        modeloTabla.setRowCount(0);  

        while (rs.next()) {
            String nombre = rs.getString("nombre");
            double precio = rs.getDouble("precio");
            String categoria = rs.getString("categoria");
            int stock = rs.getInt("stock");

            
            Object[] fila = {
                nombre,
                String.format("%.2f", precio),
                categoria,
                stock
            };

            modeloTabla.addRow(fila);  
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al buscar el producto", "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    private void agregarProducto() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField txtNombre = new JTextField();
        JTextField txtPrecio = new JTextField();
        JTextField txtStock = new JTextField();

        String[] categorias = {"Libros", "Cuadernos", "Escritura", "Mochilas", "Geometría", "Arte", "Oficina", "Útiles"};
        JComboBox<String> cmbCategoria = new JComboBox<>(categorias);

        panel.add(new JLabel("Nombre del Producto:"));
        panel.add(txtNombre);
        panel.add(new JLabel("Precio (S/):"));
        panel.add(txtPrecio);
        panel.add(new JLabel("Categoría:"));
        panel.add(cmbCategoria);
        panel.add(new JLabel("Stock:"));
        panel.add(txtStock);

        int resultado = JOptionPane.showConfirmDialog(this, panel, "Agregar Nuevo Producto", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (resultado == JOptionPane.OK_OPTION) {
            try {
                String nombre = txtNombre.getText().trim();
                String precioStr = txtPrecio.getText().trim().replace(",", ".");
                double precio = Double.parseDouble(precioStr);
                String categoria = (String) cmbCategoria.getSelectedItem();
                int stock = Integer.parseInt(txtStock.getText().trim());

                if (nombre.isEmpty() || precio <= 0 || stock <= 0) {
                    JOptionPane.showMessageDialog(this, "Todos los campos deben ser válidos", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String connectionString = "jdbc:sqlserver://localhost:1433;databaseName=BD_TPOO;encrypt=false";
                String query = "SELECT COUNT(*) FROM PRODUCTO WHERE nombre = ?";  

                try (Connection conn = DriverManager.getConnection(connectionString, "lucianoadm", "hilario123");
                     PreparedStatement stmt = conn.prepareStatement(query)) {

                    stmt.setString(1, nombre); 

                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        int count = rs.getInt(1);
                        if (count > 0) {
                            JOptionPane.showMessageDialog(this, "Ya existe un producto con el mismo nombre.", "Error", JOptionPane.ERROR_MESSAGE);
                            return;  
                        }
                    }

                    String insertQuery = "INSERT INTO PRODUCTO (nombre, categoria, precio, precio_descuento, stock) "
                            + "VALUES (?, ?, ?, ?, ?)";

                    try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                        insertStmt.setString(1, nombre);
                        insertStmt.setString(2, categoria); 
                        insertStmt.setDouble(3, precio);
                        insertStmt.setDouble(4, 0);
                        insertStmt.setInt(5, stock);

                        int filasAfectadas = insertStmt.executeUpdate();
                        if (filasAfectadas > 0) {
                            JOptionPane.showMessageDialog(this, "Producto agregado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                            cargarDatosTabla(); 
                            ventanaPrincipal.mostrarProductos("Todos los Productos");
                        } else {
                            JOptionPane.showMessageDialog(this, "Error al agregar el producto", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Error en la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error al verificar la existencia del producto", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "El precio debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    
   private void editarProducto() {
    int filaSeleccionada = tablaProductos.getSelectedRow();

    if (filaSeleccionada == -1) {
        JOptionPane.showMessageDialog(this, "Por favor, selecciona un producto para editar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        return;
    }
    String nombreProducto = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
    String categoriaProducto = (String) modeloTabla.getValueAt(filaSeleccionada, 2);
    String precioStr = (String) modeloTabla.getValueAt(filaSeleccionada, 1);
    int stockProducto = (int) modeloTabla.getValueAt(filaSeleccionada, 3);
    precioStr = precioStr.replace(",", ".");
    double precioProducto = 0;
    try {
        precioProducto = Double.parseDouble(precioStr);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "El precio ingresado no es válido", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    JPanel panelEditar = new JPanel(new GridLayout(5, 2, 10, 10));
    panelEditar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JTextField txtNombre = new JTextField(nombreProducto);
    JTextField txtPrecio = new JTextField(String.valueOf(precioProducto));
    JTextField txtStock = new JTextField(String.valueOf(stockProducto));

    String[] categorias = {"Libros", "Cuadernos", "Escritura", "Mochilas", "Geometría", "Arte", "Oficina", "Útiles"};
    JComboBox<String> cmbCategoria = new JComboBox<>(categorias);
    cmbCategoria.setSelectedItem(categoriaProducto);

    panelEditar.add(new JLabel("Nombre del Producto:"));
    panelEditar.add(txtNombre);
    panelEditar.add(new JLabel("Precio (S/):"));
    panelEditar.add(txtPrecio);
    panelEditar.add(new JLabel("Categoría:"));
    panelEditar.add(cmbCategoria);
    panelEditar.add(new JLabel("Stock:"));
    panelEditar.add(txtStock);

    int resultado = JOptionPane.showConfirmDialog(this, panelEditar, "Editar Producto", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (resultado == JOptionPane.OK_OPTION) {
        String nuevoNombre = txtNombre.getText().trim();
        String nuevoPrecioStr = txtPrecio.getText().trim().replace(",", ".");
        double nuevoPrecio = 0;
        try {
            nuevoPrecio = Double.parseDouble(nuevoPrecioStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El precio debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nuevaCategoria = (String) cmbCategoria.getSelectedItem();
        int nuevoStock = Integer.parseInt(txtStock.getText().trim());

        if (nuevoNombre.isEmpty() || nuevoPrecio <= 0 || nuevoStock <= 0) {
            JOptionPane.showMessageDialog(this, "Todos los campos deben ser válidos", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = "UPDATE PRODUCTO SET nombre = ?, categoria = ?, precio = ?, stock = ? WHERE nombre = ?";

        try (Connection con = ConexionSQLServer.getConnection(); 
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, nuevoNombre);
            ps.setString(2, nuevaCategoria);
            ps.setDouble(3, nuevoPrecio);
            ps.setInt(4, nuevoStock);
            ps.setString(5, nombreProducto);
            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(this, "Producto actualizado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);

                cargarDatosTabla();
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar el producto", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error en la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
    
private void eliminarProducto() {
    int filaSeleccionada = tablaProductos.getSelectedRow();

    if (filaSeleccionada == -1) {
        JOptionPane.showMessageDialog(this, "Por favor, selecciona un producto de la tabla", "Advertencia", JOptionPane.WARNING_MESSAGE);
        return;
    }

    Producto productoEliminar = listaProductos.get(filaSeleccionada);

    String verificarUso = "SELECT COUNT(*) FROM PRODUCTOS_VENDIDOS WHERE id_producto = ?";
    String eliminarProducto = "DELETE FROM PRODUCTO WHERE id_producto = ?";

    try (Connection con = ConexionSQLServer.getConnection()) {

        try (PreparedStatement psVerificar = con.prepareStatement(verificarUso)) {
            psVerificar.setInt(1, productoEliminar.getIdProducto());
            ResultSet rs = psVerificar.executeQuery();
            rs.next();

            int cantidadUsos = rs.getInt(1);

            if (cantidadUsos > 0) {
                JOptionPane.showMessageDialog(this,
                        "❗ No se puede eliminar este producto.\nEstá registrado en ventas.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        try (PreparedStatement psEliminar = con.prepareStatement(eliminarProducto)) {
            psEliminar.setInt(1, productoEliminar.getIdProducto());

            int filas = psEliminar.executeUpdate();
            if (filas > 0) {
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