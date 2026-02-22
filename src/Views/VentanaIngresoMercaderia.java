package Views;

import java.sql.Statement;
import conexiondb.ConexionSQLServer;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class VentanaIngresoMercaderia extends JFrame {

    private JComboBox<String> cmbOrdenes;
    private JTable tblProductos;
    private DefaultTableModel modeloTabla;
    private int idOrdenSeleccionada = -1;

    public VentanaIngresoMercaderia() {
        setTitle("Ingreso de Mercadería");
        setSize(950, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // ── Panel superior ──
        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelTop.setBackground(new Color(0, 0, 51));

        JLabel lblTitulo = new JLabel("Ingreso de Mercadería");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblOrden = new JLabel("Orden de Compra:");
        lblOrden.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblOrden.setForeground(Color.WHITE);

        cmbOrdenes = new JComboBox<>();
        cmbOrdenes.addActionListener(e -> cargarProductosOrden());
        cmbOrdenes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbOrdenes.setPreferredSize(new Dimension(250, 30));

        JButton btnCargar = new JButton("📋 Cargar Productos");
        btnCargar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCargar.setBackground(Color.WHITE);
        btnCargar.setFocusPainted(false);
        btnCargar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCargar.addActionListener(e -> cargarProductosOrden());

        panelTop.add(lblTitulo);
        panelTop.add(Box.createHorizontalStrut(20));
        panelTop.add(lblOrden);
        panelTop.add(cmbOrdenes);
        panelTop.add(btnCargar);

        // ── Tabla de productos esperados ──
        String[] columnas = {"ID Producto", "Producto", "Cant. Esperada", "Cant. Recibida", "Diferencia"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int row, int col) {
                return col == 3; // Solo editable la columna "Cant. Recibida"
            }
        };
        tblProductos = new JTable(modeloTabla);
        tblProductos.setRowHeight(28);
        tblProductos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblProductos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Al cambiar cant. recibida, actualizar diferencia
        modeloTabla.addTableModelListener(e -> {
            if (e.getColumn() == 3) {
                int fila = e.getFirstRow();
                try {
                    int esperada = (int) modeloTabla.getValueAt(fila, 2);
                    int recibida = Integer.parseInt(modeloTabla.getValueAt(fila, 3).toString());
                    modeloTabla.setValueAt(recibida - esperada, fila, 4);
                } catch (NumberFormatException ex) {
                    modeloTabla.setValueAt(0, fila, 4);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tblProductos);
        scroll.setBorder(BorderFactory.createTitledBorder("Productos de la Orden"));

        // ── Panel inferior ──
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        panelBoton.setBackground(Color.WHITE);

        JButton btnRegistrar = new JButton("✅ Registrar Ingreso");
        btnRegistrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRegistrar.setBackground(new Color(50, 150, 50));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setPreferredSize(new Dimension(200, 40));
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegistrar.addActionListener(e -> registrarIngreso());

        JButton btnNuevaOrden = new JButton("➕ Nueva Orden");
        btnNuevaOrden.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnNuevaOrden.setBackground(new Color(0, 0, 51));
        btnNuevaOrden.setForeground(Color.WHITE);
        btnNuevaOrden.setPreferredSize(new Dimension(160, 40));
        btnNuevaOrden.setFocusPainted(false);
        btnNuevaOrden.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNuevaOrden.addActionListener(e -> crearNuevaOrden());

        panelBoton.add(btnNuevaOrden);
        panelBoton.add(btnRegistrar);

        add(panelTop, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(panelBoton, BorderLayout.SOUTH);

        cargarOrdenes();
    }

    private void cargarOrdenes() {
        cmbOrdenes.removeAllItems();
        String query = "SELECT oc.id_orden, p.nombre, oc.estado, oc.fecha_orden " +
                       "FROM orden_compra oc " +
                       "INNER JOIN proveedores p ON oc.id_proveedor = p.id_proveedor " +
                       "WHERE oc.estado = 'Pendiente' ORDER BY oc.fecha_orden DESC";
        try (Connection con = ConexionSQLServer.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                cmbOrdenes.addItem("Orden #" + rs.getInt("id_orden") +
                    " - " + rs.getString("nombre") +
                    " (" + rs.getString("fecha_orden").substring(0, 10) + ")");
            }
            if (cmbOrdenes.getItemCount() == 0)
                cmbOrdenes.addItem("No hay órdenes pendientes");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cargarProductosOrden() {
        String seleccion = (String) cmbOrdenes.getSelectedItem();
        if (seleccion == null || seleccion.equals("No hay órdenes pendientes")) {
            JOptionPane.showMessageDialog(this, "No hay orden seleccionada.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        idOrdenSeleccionada = Integer.parseInt(seleccion.split("#")[1].split(" ")[0]);

        String query = "SELECT doc.id_producto, p.nombre, doc.cantidad_esperada, doc.cantidad_recibida " +
                       "FROM detalle_orden_compra doc " +
                       "INNER JOIN producto p ON doc.id_producto = p.id_producto " +
                       "WHERE doc.id_orden = ?";
        try (Connection con = ConexionSQLServer.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, idOrdenSeleccionada);
            ResultSet rs = ps.executeQuery();
            modeloTabla.setRowCount(0);
            while (rs.next()) {
                int esperada = rs.getInt("cantidad_esperada");
                int recibida = rs.getInt("cantidad_recibida");
                modeloTabla.addRow(new Object[]{
                    rs.getInt("id_producto"),
                    rs.getString("nombre"),
                    esperada,
                    recibida,
                    recibida - esperada
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar productos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrarIngreso() {
        if (idOrdenSeleccionada == -1 || modeloTabla.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Primero carga una orden.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Confirmar el ingreso de mercadería?\nEl stock se actualizará.",
            "Confirmar Ingreso", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        boolean hayFaltantes = false;

        try (Connection con = ConexionSQLServer.getConnection()) {
            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                int idProducto  = (int) modeloTabla.getValueAt(i, 0);
                int esperada    = (int) modeloTabla.getValueAt(i, 2);
                int recibida    = Integer.parseInt(modeloTabla.getValueAt(i, 3).toString());
                int diferencia  = recibida - esperada;

                // Actualizar cantidad recibida en detalle
                PreparedStatement ps1 = con.prepareStatement(
                    "UPDATE detalle_orden_compra SET cantidad_recibida = ? WHERE id_orden = ? AND id_producto = ?");
                ps1.setInt(1, recibida);
                ps1.setInt(2, idOrdenSeleccionada);
                ps1.setInt(3, idProducto);
                ps1.executeUpdate();

                // Actualizar stock del producto
                if (recibida > 0) {
                    PreparedStatement ps2 = con.prepareStatement(
                        "UPDATE producto SET stock = stock + ? WHERE id_producto = ?");
                    ps2.setInt(1, recibida);
                    ps2.setInt(2, idProducto);
                    ps2.executeUpdate();
                }

                // Si hay faltantes, registrar incidencia
                if (diferencia < 0) {
                    hayFaltantes = true;
                    PreparedStatement ps3 = con.prepareStatement(
                        "INSERT INTO incidencias (id_orden, id_producto, cantidad_faltante, descripcion) VALUES (?,?,?,?)");
                    ps3.setInt(1, idOrdenSeleccionada);
                    ps3.setInt(2, idProducto);
                    ps3.setInt(3, Math.abs(diferencia));
                    ps3.setString(4, "Faltante detectado en recepción de mercadería");
                    ps3.executeUpdate();
                }
            }

            // Actualizar estado de la orden
            PreparedStatement psOrden = con.prepareStatement(
                "UPDATE orden_compra SET estado = ? WHERE id_orden = ?");
            psOrden.setString(1, hayFaltantes ? "Con Incidencias" : "Completada");
            psOrden.setInt(2, idOrdenSeleccionada);
            psOrden.executeUpdate();

            String msg = "✅ Ingreso registrado correctamente.\n";
            if (hayFaltantes) msg += "⚠️ Se generaron incidencias por faltantes.";
            JOptionPane.showMessageDialog(this, msg, "Éxito", JOptionPane.INFORMATION_MESSAGE);

            idOrdenSeleccionada = -1;
            modeloTabla.setRowCount(0);
            cargarOrdenes();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al registrar ingreso.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void crearNuevaOrden() {
        // Cargar proveedores
        JComboBox<String> cmbProv = new JComboBox<>();
        String queryProv = "SELECT id_proveedor, nombre FROM proveedores";
        try (Connection con = ConexionSQLServer.getConnection();
             PreparedStatement ps = con.prepareStatement(queryProv);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                cmbProv.addItem(rs.getInt("id_proveedor") + " - " + rs.getString("nombre"));
        } catch (SQLException e) { e.printStackTrace(); }

        JTextField txtObs = new JTextField();
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.add(new JLabel("Proveedor:")); panel.add(cmbProv);
        panel.add(new JLabel("Observaciones:")); panel.add(txtObs);

        int res = JOptionPane.showConfirmDialog(this, panel, "Nueva Orden de Compra",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        String provSel = (String) cmbProv.getSelectedItem();
        if (provSel == null) return;
        int idProv = Integer.parseInt(provSel.split(" - ")[0]);

        try (Connection con = ConexionSQLServer.getConnection();
             PreparedStatement ps = con.prepareStatement(
                "INSERT INTO orden_compra (id_proveedor, observaciones) VALUES (?,?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idProv);
            ps.setString(2, txtObs.getText().trim());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int idNuevaOrden = keys.getInt(1);
                agregarProductosAOrden(con, idNuevaOrden);
            }
            cargarOrdenes();
            JOptionPane.showMessageDialog(this, "Orden creada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al crear la orden.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarProductosAOrden(Connection con, int idOrden) throws SQLException {
        boolean seguir = true;
        while (seguir) {
            JComboBox<String> cmbProd = new JComboBox<>();
            String q = "SELECT id_producto, nombre FROM producto WHERE activo = 1 ORDER BY nombre";
            PreparedStatement ps = con.prepareStatement(q);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                cmbProd.addItem(rs.getInt("id_producto") + " - " + rs.getString("nombre"));

            JSpinner spnCant = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
            JPanel p = new JPanel(new GridLayout(2, 2, 10, 10));
            p.add(new JLabel("Producto:")); p.add(cmbProd);
            p.add(new JLabel("Cantidad esperada:")); p.add(spnCant);

            int res = JOptionPane.showConfirmDialog(this, p, "Agregar Producto a la Orden",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (res != JOptionPane.OK_OPTION) break;

            String prodSel = (String) cmbProd.getSelectedItem();
            int idProd = Integer.parseInt(prodSel.split(" - ")[0]);
            int cant   = (int) spnCant.getValue();

            PreparedStatement psIns = con.prepareStatement(
                "INSERT INTO detalle_orden_compra (id_orden, id_producto, cantidad_esperada) VALUES (?,?,?)");
            psIns.setInt(1, idOrden); psIns.setInt(2, idProd); psIns.setInt(3, cant);
            psIns.executeUpdate();

            int otro = JOptionPane.showConfirmDialog(this, "¿Agregar otro producto?",
                "Continuar", JOptionPane.YES_NO_OPTION);
            seguir = (otro == JOptionPane.YES_OPTION);
        }
    }
}