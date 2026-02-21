package Views;

import conexiondb.ConexionSQLServer;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Time;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


public class Sistema extends javax.swing.JFrame {
    private String nombreEmpleado;
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Sistema.class.getName());
    private VentanaPrincipal ventanaPrincipal;
       
    public Sistema(String nombreEmpleado,String cargo) {
    initComponents(); 

    String[] columnNamesEmpleados = {"ID", "Nombre", "DNI","Correo","Fecha de Nacimiento" ,"Dirección", "Teléfono"};
    DefaultTableModel modelEmpleados = new DefaultTableModel(columnNamesEmpleados, 0);
    tblEmpleados.setModel(modelEmpleados);  
    
    String[] columnNamesClientes = {"ID", "Nombre", "DNI","Correo", "Dirección", "Teléfono"};
    DefaultTableModel modelClientes = new DefaultTableModel(columnNamesClientes, 0);
    tblClientes.setModel(modelClientes); 
    
        DefaultTableModel modelVentas = new DefaultTableModel();
        modelVentas.addColumn("CLIENTE");
        modelVentas.addColumn("PRODUCTO");
        modelVentas.addColumn("PRECIO");
        modelVentas.addColumn("CANTIDAD");
        modelVentas.addColumn("SUBTOTAL");
        modelVentas.addColumn("FECHA");
        modelVentas.addColumn("HORA");
        modelVentas.addColumn("VENDEDOR");

        tblHistorialVentas.setModel(modelVentas);
    
    this.nombreEmpleado = nombreEmpleado;

    String nombreCliente = "x"; 
    ventanaPrincipal = new VentanaPrincipal(nombreCliente);
    btnBuscarCliente.addActionListener(e -> clientesRegistrados(txtBuscarCliente.getText().trim()));
    btnBuscarEmpleado.addActionListener(e -> empleadosRegistrados(txtBuscarEmpleado.getText().trim()));
    btnBuscarHistorialVentas.addActionListener(e -> buscarCliente(txtHistorialVentas.getText().trim()));
    
    txtHistorialVentas.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
    @Override
    public void insertUpdate(javax.swing.event.DocumentEvent e) {}
    @Override
    public void removeUpdate(javax.swing.event.DocumentEvent e) {
        if (txtHistorialVentas.getText().trim().isEmpty()) {
            cargarVentasEnTabla((DefaultTableModel) tblHistorialVentas.getModel());
        }
    }
    @Override
    public void changedUpdate(javax.swing.event.DocumentEvent e) {}
});
    btnGestionProductos.addActionListener(e -> abrirGestionProductos());
    if (cargo.equalsIgnoreCase("administrador")) {
    btnGestionRoles = new javax.swing.JButton("Gestión de Roles");
    btnGestionRoles.setFont(new java.awt.Font("Segoe UI", 1, 14));
    btnGestionRoles.setBackground(new java.awt.Color(238, 238, 238));
    btnGestionRoles.setForeground(new java.awt.Color(0, 0, 0));
    btnGestionRoles.setOpaque(true);
    btnGestionRoles.setFocusPainted(false);
    btnGestionRoles.setBounds(380, 625, 180, 32);
    jPanel1.add(btnGestionRoles);
    jPanel1.setComponentZOrder(btnGestionRoles, 0);
    btnGestionRoles.addActionListener(e -> {
    GestionRoles gr = new GestionRoles();
    gr.setVisible(true);
    });
}

    pnlPrincipal.addChangeListener(e -> {
    cargarVentasEnTabla((DefaultTableModel) tblHistorialVentas.getModel());
    cargarClientesEnTabla((DefaultTableModel) tblClientes.getModel());
    cargarEmpleadosEnTabla((DefaultTableModel) tblEmpleados.getModel());
});

    cargarEmpleadosEnTabla(modelEmpleados);
    cargarClientesEnTabla(modelClientes);
    cargarVentasEnTabla(modelVentas);  
}
    
    private void abrirGestionProductos() {
        VentanaGestionProductos ventanaGestion = new VentanaGestionProductos(ventanaPrincipal);
        ventanaGestion.setVisible(true);
    }
    
    private void buscarCliente(String nombreCliente) {
    if (nombreCliente.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Por favor ingrese el nombre del cliente.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    String query = "SELECT v.id_venta, CONCAT(c.nombres, ' ', c.apellidos) AS cliente, " +
               "p.nombre AS producto, pv.precio, pv.cantidad, pv.subtotal, v.fecha " +
               "FROM ventas v " +
               "INNER JOIN cliente c ON v.id_cliente = c.id_cliente " +
               "INNER JOIN productos_vendidos pv ON v.id_venta = pv.id_venta " +
               "INNER JOIN producto p ON pv.id_producto = p.id_producto " +
               "WHERE c.nombres LIKE ? OR c.apellidos LIKE ? OR p.nombre LIKE ? " + 
               "ORDER BY v.fecha DESC";

    try (Connection conn = ConexionSQLServer.getConnection(); 
         PreparedStatement ps = conn.prepareStatement(query)) {

        ps.setString(1, "%" + nombreCliente + "%");
        ps.setString(2, "%" + nombreCliente + "%");
        ps.setString(3, "%" + nombreCliente + "%");
        ResultSet rs = ps.executeQuery();

        DefaultTableModel modelVentas = (DefaultTableModel) tblHistorialVentas.getModel();
        modelVentas.setRowCount(0);
        if (!rs.isBeforeFirst()) {
        cargarVentasEnTabla((DefaultTableModel) tblHistorialVentas.getModel());
        JOptionPane.showMessageDialog(this, "No se encontraron resultados.");
        return;
}

       
        while (rs.next()) {
            String cliente = rs.getString("cliente");  
            String producto = rs.getString("producto");
            double precio = rs.getDouble("precio");
            int cantidad = rs.getInt("cantidad");
            double subtotal = rs.getDouble("subtotal");
            Date fecha = rs.getDate("fecha");
            Time hora = rs.getTime("hora");
            String vendedor = rs.getString("vendedor");
          
            modelVentas.addRow(new Object[]{cliente, producto,"S/. "+precio, cantidad, subtotal, fecha});
        }

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al buscar el cliente", "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    
    
    private void clientesRegistrados(String nombreCliente) {
    if (nombreCliente.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Por favor ingrese el nombre del cliente.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    String query = "SELECT id_cliente, CONCAT(nombres, ' ', apellidos) AS nombre_completo, dni, correo, direccion, telefono " +
                   "FROM cliente " +
                   "WHERE nombres LIKE ? OR apellidos LIKE ?"; 

    try (Connection conn = ConexionSQLServer.getConnection(); 
         PreparedStatement ps = conn.prepareStatement(query)) {

        ps.setString(1, "%" + nombreCliente + "%");  
        ps.setString(2, "%" + nombreCliente + "%"); 

        ResultSet rs = ps.executeQuery();

        DefaultTableModel modelClientes = (DefaultTableModel) tblClientes.getModel();
        modelClientes.setRowCount(0);  

      
        while (rs.next()) {
            int idCliente = rs.getInt("id_cliente");
            String nombreCompleto = rs.getString("nombre_completo");
            String dni = rs.getString("dni");
            String correo = rs.getString("correo");
            String direccion = rs.getString("direccion");
            String telefono = rs.getString("telefono");

            
            modelClientes.addRow(new Object[]{idCliente, nombreCompleto, dni, correo, direccion, telefono});
        }

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al buscar los clientes registrados", "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    
    private void empleadosRegistrados(String nombreEmpleado) {
    if (nombreEmpleado.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Por favor ingrese el nombre del empleado.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    String query = "SELECT empleado_id, CONCAT(nombres, ' ', apellidos) AS nombre_completo, dni, correo_electronico,fecha_nacimiento, direccion, telefono " +
                   "FROM empleado    " +
                   "WHERE nombres LIKE ? OR apellidos LIKE ?";  

    try (Connection conn = ConexionSQLServer.getConnection(); 
         PreparedStatement ps = conn.prepareStatement(query)) {

        ps.setString(1, "%" + nombreEmpleado + "%");  
        ps.setString(2, "%" + nombreEmpleado + "%");  

        ResultSet rs = ps.executeQuery();

        DefaultTableModel modelEmpleados = (DefaultTableModel) tblEmpleados.getModel();
        modelEmpleados.setRowCount(0); 

       
        while (rs.next()) {
            int idEmpleado = rs.getInt("empleado_id");
            String nombreCompleto = rs.getString("nombre_completo");
            String dni = rs.getString("dni");
            String correo = rs.getString("correo_electronico");
            String fecha_nacimiento = rs.getString("fecha_nacimiento");
            String direccion = rs.getString("direccion");
            String telefono = rs.getString("telefono");

          
            modelEmpleados.addRow(new Object[]{idEmpleado, nombreCompleto, dni, correo,fecha_nacimiento, direccion, telefono});
        }

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al buscar los empleados registrados", "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    
    
    
    private void cargarEmpleadosEnTabla(DefaultTableModel tableModel) {
    String query = "SELECT empleado_id, CONCAT(nombres, ' ', apellidos) AS nombre_completo, dni, correo_electronico, fecha_nacimiento, direccion, telefono FROM empleado";
    
    try (Connection conn = ConexionSQLServer.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query);
         ResultSet rs = stmt.executeQuery()) {

        tableModel.setRowCount(0);

        while (rs.next()) {
            int idEmpleado = rs.getInt("empleado_id");
            String nombre = rs.getString("nombre_completo");
            String dni = rs.getString("dni");
            String correo = rs.getString("correo_electronico");
            String fecha_nacimiento = rs.getString("fecha_nacimiento");
            String direccion = rs.getString("direccion");
            String telefono = rs.getString("telefono");

            tableModel.addRow(new Object[]{idEmpleado, nombre, dni, correo, fecha_nacimiento, direccion, telefono});
        }
    } catch (SQLException e) {
        e.printStackTrace();
        System.out.println("Error al cargar los empleados: " + e.getMessage());
    }
}
   
  private void cargarClientesEnTabla(DefaultTableModel tableModel) {
    String query = "SELECT id_cliente, CONCAT(nombres, ' ', apellidos) AS nombre_completo, dni, correo, direccion, telefono FROM cliente";
    
    try (Connection conn = ConexionSQLServer.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query);
         ResultSet rs = stmt.executeQuery()) {

        tableModel.setRowCount(0);

        while (rs.next()) {
            int idCliente = rs.getInt("id_cliente");
            String nombreCompleto = rs.getString("nombre_completo"); 
            String dni = rs.getString("dni");
            String correo = rs.getString("correo");
            String direccion = rs.getString("direccion");
            String telefono = rs.getString("telefono");

            tableModel.addRow(new Object[]{idCliente, nombreCompleto, dni, correo, direccion, telefono});
        }
    } catch (SQLException e) {
    e.printStackTrace();
    JOptionPane.showMessageDialog(this, "Error clientes: " + e.getMessage());
}
}
   
private void cargarVentasEnTabla(DefaultTableModel tableModel) {
    String query = "SELECT v.id_venta, CONCAT(c.nombres, ' ', c.apellidos) AS cliente, " +
               "p.nombre AS producto, pv.precio, pv.cantidad, pv.subtotal, " +
               "DATE(v.fecha) AS fecha, TIME(v.fecha) AS hora, " +
               "CONCAT(e.nombres, ' ', e.apellidos) AS vendedor " +
               "FROM ventas v " +
               "INNER JOIN cliente c ON v.id_cliente = c.id_cliente " +
               "INNER JOIN productos_vendidos pv ON v.id_venta = pv.id_venta " +
               "INNER JOIN producto p ON pv.id_producto = p.id_producto " +
               "LEFT JOIN empleado e ON v.id_empleado = e.empleado_id " +
               "ORDER BY v.fecha DESC";
    try (Connection conn = ConexionSQLServer.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query);
         ResultSet rs = stmt.executeQuery()) {  

        tableModel.setRowCount(0);  

        while (rs.next()) {
            String cliente = rs.getString("cliente");  
            String producto = rs.getString("producto");
            double precio = rs.getDouble("precio");
            int cantidad = rs.getInt("cantidad");
            double subtotal = rs.getDouble("subtotal");
            Date fecha = rs.getDate("fecha");
            Time hora = rs.getTime("hora");
            String vendedor = rs.getString("vendedor");
            
            tableModel.addRow(new Object[]{cliente, producto, "S/. "+precio, cantidad, subtotal, fecha, hora, vendedor});
        }
    } catch (SQLException e) {
        e.printStackTrace();
        System.out.println("Error al cargar el historial de ventas: " + e.getMessage());
    }
}
  
       
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        imagenLogo = new javax.swing.JLabel();
        btnGestionProductos = new javax.swing.JButton();
        pnlPrincipal = new javax.swing.JTabbedPane();
        pnlClientes = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblClientes = new javax.swing.JTable();
        btnBuscarCliente = new javax.swing.JButton();
        txtBuscarCliente = new javax.swing.JTextField();
        pnlEmpleados = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblEmpleados = new javax.swing.JTable();
        txtBuscarEmpleado = new javax.swing.JTextField();
        btnBuscarEmpleado = new javax.swing.JButton();
        pnlHistorial = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblHistorialVentas = new javax.swing.JTable();
        txtHistorialVentas = new javax.swing.JTextField();
        btnBuscarHistorialVentas = new javax.swing.JButton();
        btnCerrarSesion = new javax.swing.JButton();
        btnSalir = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(0, 0, 51));

        imagenLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imágenes/logo.png"))); // NOI18N

        btnGestionProductos.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnGestionProductos.setText("Gestion de Productos");
        btnGestionProductos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGestionProductosActionPerformed(evt);
            }
        });

        tblClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "CLIENTE_ID", "NOMBRE", "DNI", "CORREO", "TELEFONO", "DIRECCION"
            }
        ));
        jScrollPane1.setViewportView(tblClientes);

        btnBuscarCliente.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnBuscarCliente.setText("BUSCAR");
        btnBuscarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarClienteActionPerformed(evt);
            }
        });

        txtBuscarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarClienteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlClientesLayout = new javax.swing.GroupLayout(pnlClientes);
        pnlClientes.setLayout(pnlClientesLayout);
        pnlClientesLayout.setHorizontalGroup(
            pnlClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlClientesLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 911, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlClientesLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtBuscarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(btnBuscarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41))
        );
        pnlClientesLayout.setVerticalGroup(
            pnlClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlClientesLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBuscarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBuscarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 16, Short.MAX_VALUE))
        );

        pnlPrincipal.addTab("Clientes", pnlClientes);

        tblEmpleados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "EMPLEADO_ID", "NOMBRE", "DNI", "CORREO", "FECHA DE NACIMIENTO", "DIRECCION", "TELEFONO"
            }
        ));
        jScrollPane2.setViewportView(tblEmpleados);

        txtBuscarEmpleado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarEmpleadoActionPerformed(evt);
            }
        });

        btnBuscarEmpleado.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnBuscarEmpleado.setText("BUSCAR");
        btnBuscarEmpleado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarEmpleadoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlEmpleadosLayout = new javax.swing.GroupLayout(pnlEmpleados);
        pnlEmpleados.setLayout(pnlEmpleadosLayout);
        pnlEmpleadosLayout.setHorizontalGroup(
            pnlEmpleadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 911, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlEmpleadosLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtBuscarEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47)
                .addComponent(btnBuscarEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );
        pnlEmpleadosLayout.setVerticalGroup(
            pnlEmpleadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEmpleadosLayout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlEmpleadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtBuscarEmpleado, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                    .addComponent(btnBuscarEmpleado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 27, Short.MAX_VALUE))
        );

        pnlPrincipal.addTab("Empleados", pnlEmpleados);

        tblHistorialVentas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "CLIENTE", "PRODUCTO", "CANTIDAD", "PRECIO", "SUBTOTAL", "FECHA"
            }
        ));
        jScrollPane3.setViewportView(tblHistorialVentas);

        txtHistorialVentas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtHistorialVentasActionPerformed(evt);
            }
        });

        btnBuscarHistorialVentas.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnBuscarHistorialVentas.setText("BUSCAR");
        btnBuscarHistorialVentas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarHistorialVentasActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlHistorialLayout = new javax.swing.GroupLayout(pnlHistorial);
        pnlHistorial.setLayout(pnlHistorialLayout);
        pnlHistorialLayout.setHorizontalGroup(
            pnlHistorialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 911, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlHistorialLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtHistorialVentas, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(btnBuscarHistorialVentas, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
        );
        pnlHistorialLayout.setVerticalGroup(
            pnlHistorialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHistorialLayout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(pnlHistorialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtHistorialVentas, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscarHistorialVentas, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16))
        );

        pnlPrincipal.addTab("Historial de Ventas", pnlHistorial);

        btnCerrarSesion.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnCerrarSesion.setText("Cerrar sesión");
        btnCerrarSesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarSesionActionPerformed(evt);
            }
        });

        btnSalir.setBackground(new java.awt.Color(0, 0, 51));
        btnSalir.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        btnSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imágenes/icono_X.png"))); // NOI18N
        btnSalir.setBorder(null);
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 21, Short.MAX_VALUE)
                        .addComponent(pnlPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(imagenLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(310, 310, 310)
                        .addComponent(btnSalir)))
                .addGap(18, 18, 18))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(96, 96, 96)
                .addComponent(btnGestionProductos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCerrarSesion, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(105, 105, 105))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnSalir))
                    .addComponent(imagenLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlPrincipal)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCerrarSesion, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGestionProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 950, 690));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnGestionProductosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGestionProductosActionPerformed
        
    }//GEN-LAST:event_btnGestionProductosActionPerformed

    private void btnCerrarSesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarSesionActionPerformed

        String mensaje = "¿Deseas cerrar sesión de: " + nombreEmpleado + "?";

        int respuesta = JOptionPane.showOptionDialog(
            Sistema.this, 
            mensaje, 
            "Cerrar Sesión",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE, 
            null, 
            new Object[] {"Cerrar sesión", "Atrás"},  
            "Cerrar sesión"  
        );

        if (respuesta == JOptionPane.YES_OPTION) {
            LoginUsuarios login = new LoginUsuarios();  
            login.setVisible(true); 
            Sistema.this.setVisible(false);  
        }
    }//GEN-LAST:event_btnCerrarSesionActionPerformed

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        int respuesta=JOptionPane.showOptionDialog(this,"¿Estas seguro de salir?","Mensaje de Confirmación",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,null,null);
        if(respuesta==0) System.exit(0);
    }//GEN-LAST:event_btnSalirActionPerformed

    private void btnBuscarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarClienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnBuscarClienteActionPerformed

    private void txtBuscarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarClienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscarClienteActionPerformed

    private void txtHistorialVentasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtHistorialVentasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtHistorialVentasActionPerformed

    private void btnBuscarHistorialVentasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarHistorialVentasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnBuscarHistorialVentasActionPerformed

    private void txtBuscarEmpleadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarEmpleadoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscarEmpleadoActionPerformed

    private void btnBuscarEmpleadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarEmpleadoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnBuscarEmpleadoActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
    }
    private javax.swing.JButton btnGestionRoles;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscarCliente;
    private javax.swing.JButton btnBuscarEmpleado;
    private javax.swing.JButton btnBuscarHistorialVentas;
    private javax.swing.JButton btnCerrarSesion;
    private javax.swing.JButton btnGestionProductos;
    private javax.swing.JButton btnSalir;
    private javax.swing.JLabel imagenLogo;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPanel pnlClientes;
    private javax.swing.JPanel pnlEmpleados;
    private javax.swing.JPanel pnlHistorial;
    private javax.swing.JTabbedPane pnlPrincipal;
    private javax.swing.JTable tblClientes;
    private javax.swing.JTable tblEmpleados;
    private javax.swing.JTable tblHistorialVentas;
    private javax.swing.JTextField txtBuscarCliente;
    private javax.swing.JTextField txtBuscarEmpleado;
    private javax.swing.JTextField txtHistorialVentas;
    // End of variables declaration//GEN-END:variables
}
