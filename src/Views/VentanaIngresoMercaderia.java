package Views;

import conexiondb.ConexionSQLServer;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class VentanaIngresoMercaderia extends JFrame {

    private JTable tblProductosSinStock;
    private DefaultTableModel modeloTabla;
    private JTextArea txtRazon;
    private JLabel lblEstado;
    private JButton btnSolicitar;
    private int ordenActualId = -1;

    public VentanaIngresoMercaderia() {
        setTitle("Ingreso de Mercadería - Librería Fanny");
        setSize(900, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(0, 0, 51));

        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.setBackground(new Color(0, 0, 51));
        panelTop.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        JLabel lblTitulo = new JLabel("📦 Productos Sin Stock");
        lblTitulo.setFont(new Font("Segoe UI Black", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);
        panelTop.add(lblTitulo, BorderLayout.WEST);

        String[] columnas = {"ID", "Producto", "Categoría", "Stock Actual", "Cantidad a Solicitar"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int row, int col) {
                return col == 4;
            }
            public Class<?> getColumnClass(int col) {
                return col == 3 || col == 4 ? Integer.class : String.class;
            }
        };

        tblProductosSinStock = new JTable(modeloTabla);
        tblProductosSinStock.setRowHeight(30);
        tblProductosSinStock.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblProductosSinStock.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblProductosSinStock.getTableHeader().setBackground(new Color(0, 102, 204));
        tblProductosSinStock.getTableHeader().setForeground(Color.WHITE);
        tblProductosSinStock.getColumnModel().getColumn(0).setPreferredWidth(40);
        tblProductosSinStock.getColumnModel().getColumn(1).setPreferredWidth(300);
        tblProductosSinStock.getColumnModel().getColumn(2).setPreferredWidth(150);
        tblProductosSinStock.getColumnModel().getColumn(3).setPreferredWidth(100);
        tblProductosSinStock.getColumnModel().getColumn(4).setPreferredWidth(150);

        JScrollPane scroll = new JScrollPane(tblProductosSinStock);
        scroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE),
            "Productos con stock menor a 5",
            0, 0,
            new Font("Segoe UI", Font.BOLD, 12),
            Color.WHITE
        ));

        JPanel panelSur = new JPanel(new BorderLayout(10, 10));
        panelSur.setBackground(new Color(0, 0, 51));
        panelSur.setBorder(BorderFactory.createEmptyBorder(5, 15, 15, 15));

        JPanel panelRazon = new JPanel(new BorderLayout(5, 5));
        panelRazon.setBackground(new Color(0, 0, 51));
        JLabel lblRazon = new JLabel("Razón de solicitud:");
        lblRazon.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblRazon.setForeground(Color.WHITE);
        txtRazon = new JTextArea(3, 40);
        txtRazon.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtRazon.setLineWrap(true);
        txtRazon.setWrapStyleWord(true);
        txtRazon.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panelRazon.add(lblRazon, BorderLayout.NORTH);
        panelRazon.add(new JScrollPane(txtRazon), BorderLayout.CENTER);

        JPanel panelAccion = new JPanel(new BorderLayout(10, 10));
        panelAccion.setBackground(new Color(0, 0, 51));

        lblEstado = new JLabel("Estado: Sin solicitud activa");
        lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblEstado.setForeground(new Color(200, 200, 200));
        lblEstado.setHorizontalAlignment(SwingConstants.CENTER);

        btnSolicitar = new JButton("📨 Enviar Solicitud al Proveedor");
        btnSolicitar.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnSolicitar.setBackground(new Color(0, 153, 76));
        btnSolicitar.setForeground(Color.WHITE);
        btnSolicitar.setFocusPainted(false);
        btnSolicitar.setPreferredSize(new Dimension(300, 45));
        btnSolicitar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSolicitar.addActionListener(e -> enviarSolicitud());

        panelAccion.add(lblEstado, BorderLayout.CENTER);
        panelAccion.add(btnSolicitar, BorderLayout.SOUTH);

        panelSur.add(panelRazon, BorderLayout.CENTER);
        panelSur.add(panelAccion, BorderLayout.EAST);

        add(panelTop, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(panelSur, BorderLayout.SOUTH);

        cargarProductosSinStock();
    }

    private void cargarProductosSinStock() {
        modeloTabla.setRowCount(0);
        String query = "SELECT id_producto, nombre, categoria, stock FROM producto WHERE stock <= 5 AND activo = 1";
        try (Connection conn = ConexionSQLServer.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                modeloTabla.addRow(new Object[]{
                    rs.getInt("id_producto"),
                    rs.getString("nombre"),
                    rs.getString("categoria"),
                    rs.getInt("stock"),
                    1
                });
            }
            if (modeloTabla.getRowCount() == 0) {
                lblEstado.setText("✅ Todos los productos tienen stock disponible.");
                lblEstado.setForeground(new Color(0, 200, 100));
                btnSolicitar.setEnabled(false);
            } else {
                lblEstado.setText("Estado: Sin solicitud activa");
                lblEstado.setForeground(new Color(200, 200, 200));
                btnSolicitar.setEnabled(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar productos: " + e.getMessage());
        }
    }

    private void enviarSolicitud() {
        if (modeloTabla.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay productos sin stock para solicitar.");
            return;
        }

        String razon = txtRazon.getText().trim();
        if (razon.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese una razón de solicitud.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            Object cantObj = modeloTabla.getValueAt(i, 4);
            int cant = cantObj != null ? Integer.parseInt(cantObj.toString()) : 0;
            if (cant <= 0) {
                JOptionPane.showMessageDialog(this, "La cantidad para '" + modeloTabla.getValueAt(i, 1) + "' debe ser mayor a 0.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        // Cargar proveedores desde BD
        List<String> nombresProv = new ArrayList<>();
        List<Integer> idsProv = new ArrayList<>();
        try (Connection conn = ConexionSQLServer.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id_proveedor, nombre FROM proveedores");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                idsProv.add(rs.getInt("id_proveedor"));
                nombresProv.add(rs.getString("nombre"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (nombresProv.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay proveedores registrados.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ── Ventana de confirmación ──
        JDialog dialogo = new JDialog(this, "Confirmar Solicitud", true);
        dialogo.setSize(520, 450);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout(10, 10));
        dialogo.getContentPane().setBackground(new Color(0, 0, 51));

        JLabel lblTitulo = new JLabel("  Confirmar Solicitud al Proveedor");
        lblTitulo.setFont(new Font("Segoe UI Black", Font.BOLD, 16));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));
        dialogo.add(lblTitulo, BorderLayout.NORTH);

        JPanel panelCentro = new JPanel(new GridBagLayout());
        panelCentro.setBackground(new Color(0, 0, 51));
        panelCentro.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ComboBox proveedor
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblProv = new JLabel("Seleccionar Proveedor:");
        lblProv.setForeground(Color.WHITE);
        lblProv.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panelCentro.add(lblProv, gbc);

        gbc.gridx = 1;
        JComboBox<String> cmbProveedores = new JComboBox<>(nombresProv.toArray(new String[0]));
        cmbProveedores.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbProveedores.setPreferredSize(new Dimension(250, 30));
        panelCentro.add(cmbProveedores, gbc);

        // Tabla resumen
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        JLabel lblResumen = new JLabel("Productos a solicitar:");
        lblResumen.setForeground(Color.WHITE);
        lblResumen.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panelCentro.add(lblResumen, gbc);

        gbc.gridy = 2;
        String[] cols = {"Producto", "Stock Actual", "Cantidad a Solicitar"};
        DefaultTableModel modeloResumen = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return c == 2; }
            public Class<?> getColumnClass(int c) { return c != 0 ? Integer.class : String.class; }
        };
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            modeloResumen.addRow(new Object[]{
                modeloTabla.getValueAt(i, 1),
                modeloTabla.getValueAt(i, 3),
                modeloTabla.getValueAt(i, 4)
            });
        }
        JTable tablaResumen = new JTable(modeloResumen);
        tablaResumen.setRowHeight(28);
        tablaResumen.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaResumen.getTableHeader().setBackground(new Color(0, 102, 204));
        tablaResumen.getTableHeader().setForeground(Color.WHITE);
        JScrollPane scrollResumen = new JScrollPane(tablaResumen);
        scrollResumen.setPreferredSize(new Dimension(460, 140));
        panelCentro.add(scrollResumen, gbc);

        dialogo.add(panelCentro, BorderLayout.CENTER);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBotones.setBackground(new Color(0, 0, 51));

        JButton btnConfirmar = new JButton("✅ Confirmar y Enviar");
        btnConfirmar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnConfirmar.setBackground(new Color(0, 153, 76));
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setFocusPainted(false);
        btnConfirmar.setPreferredSize(new Dimension(200, 38));

        JButton btnCancelar = new JButton("❌ Cancelar");
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCancelar.setBackground(new Color(180, 0, 0));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setPreferredSize(new Dimension(130, 38));

        btnCancelar.addActionListener(e -> dialogo.dispose());

        btnConfirmar.addActionListener(e -> {
            // Verificar cantidades en tabla del diálogo
            for (int i = 0; i < modeloResumen.getRowCount(); i++) {
                Object cantObj = modeloResumen.getValueAt(i, 2);
                int cant = cantObj != null ? Integer.parseInt(cantObj.toString()) : 0;
                if (cant <= 0) {
                    JOptionPane.showMessageDialog(dialogo, "La cantidad para '" + modeloResumen.getValueAt(i, 0) + "' debe ser mayor a 0.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                // Actualizar cantidad en tabla principal
                modeloTabla.setValueAt(cantObj, i, 4);
            }
            int idProvSeleccionado = idsProv.get(cmbProveedores.getSelectedIndex());
            dialogo.dispose();
            procesarOrden(idProvSeleccionado, razon);
        });

        panelBotones.add(btnConfirmar);
        panelBotones.add(btnCancelar);
        dialogo.add(panelBotones, BorderLayout.SOUTH);

        dialogo.setVisible(true);
    }

    private void procesarOrden(int idProveedor, String razon) {
        try (Connection conn = ConexionSQLServer.getConnection()) {
            PreparedStatement psOrden = conn.prepareStatement(
                "INSERT INTO orden_compra (id_proveedor, estado, razon_solicitud) VALUES (?, 'En Proceso', ?)",
                Statement.RETURN_GENERATED_KEYS);
            psOrden.setInt(1, idProveedor);
            psOrden.setString(2, razon);
            psOrden.executeUpdate();
            ResultSet keys = psOrden.getGeneratedKeys();
            keys.next();
            ordenActualId = keys.getInt(1);

            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                int idProducto = (int) modeloTabla.getValueAt(i, 0);
                int cantidad = Integer.parseInt(modeloTabla.getValueAt(i, 4).toString());

                PreparedStatement psDetalle = conn.prepareStatement(
                    "INSERT INTO detalle_orden_compra (id_orden, id_producto, cantidad_esperada, cantidad_recibida) VALUES (?,?,?,0)");
                psDetalle.setInt(1, ordenActualId);
                psDetalle.setInt(2, idProducto);
                psDetalle.setInt(3, cantidad);
                psDetalle.executeUpdate();
            }

            btnSolicitar.setEnabled(false);
            txtRazon.setEnabled(false);
            actualizarEstado("⏳ En Proceso...", new Color(255, 200, 0));

            Timer timer = new Timer(6000, ev -> simularRecepcion());
            timer.setRepeats(false);
            timer.start();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al enviar solicitud: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void simularRecepcion() {
        try (Connection conn2 = ConexionSQLServer.getConnection()) {
            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                int idProducto = (int) modeloTabla.getValueAt(i, 0);
                int cantidad = Integer.parseInt(modeloTabla.getValueAt(i, 4).toString());

                PreparedStatement ps1 = conn2.prepareStatement(
                    "UPDATE producto SET stock = stock + ? WHERE id_producto = ?");
                ps1.setInt(1, cantidad);
                ps1.setInt(2, idProducto);
                ps1.executeUpdate();

                PreparedStatement ps2 = conn2.prepareStatement(
                    "UPDATE detalle_orden_compra SET cantidad_recibida = ? WHERE id_orden = ? AND id_producto = ?");
                ps2.setInt(1, cantidad);
                ps2.setInt(2, ordenActualId);
                ps2.setInt(3, idProducto);
                ps2.executeUpdate();
            }

            PreparedStatement psOrden = conn2.prepareStatement(
                "UPDATE orden_compra SET estado = 'Recibido' WHERE id_orden = ?");
            psOrden.setInt(1, ordenActualId);
            psOrden.executeUpdate();

            actualizarEstado("✅ Recibido — Stock actualizado correctamente", new Color(0, 200, 100));
            JOptionPane.showMessageDialog(this,
                "✅ Mercadería recibida.\nEl stock ha sido actualizado correctamente.",
                "Recepción Completada", JOptionPane.INFORMATION_MESSAGE);

            cargarProductosSinStock();
            txtRazon.setText("");
            txtRazon.setEnabled(true);
            ordenActualId = -1;

        } catch (SQLException e) {
            e.printStackTrace();
            actualizarEstado("❌ Error al recibir mercadería", Color.RED);
        }
    }

    private void actualizarEstado(String texto, Color color) {
        SwingUtilities.invokeLater(() -> {
            lblEstado.setText("Estado: " + texto);
            lblEstado.setForeground(color);
        });
    }
}