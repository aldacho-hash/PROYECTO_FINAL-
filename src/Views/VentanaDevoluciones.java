package Views;

import conexiondb.ConexionSQLServer;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class VentanaDevoluciones extends JFrame {
    private String nombreVendedor;
    private JTextField txtIdVenta;
    private JTable tblProductos;
    private DefaultTableModel modeloTabla;
    private JTextArea txtMotivo;
    private JSpinner spnCantidad;
    private int idVentaActual = -1;

    public VentanaDevoluciones(String nombreVendedor) {
        this.nombreVendedor = nombreVendedor;
        setTitle("Registrar Devolución");
        setSize(700, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // ── Panel superior: buscar venta ──
        JPanel panelBuscar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelBuscar.setBackground(new Color(200, 50, 50));
        JLabel lblTitulo = new JLabel("Registrar Devolución");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        JLabel lblId = new JLabel("N° de Venta:");
        lblId.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblId.setForeground(Color.WHITE);
        txtIdVenta = new JTextField(10);
        txtIdVenta.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JButton btnBuscar = new JButton("🔍 Buscar");
        btnBuscar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnBuscar.setBackground(Color.WHITE);
        btnBuscar.setFocusPainted(false);
        btnBuscar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBuscar.addActionListener(e -> buscarVenta());
        panelBuscar.add(lblTitulo);
        panelBuscar.add(Box.createHorizontalStrut(20));
        panelBuscar.add(lblId);
        panelBuscar.add(txtIdVenta);
        panelBuscar.add(btnBuscar);

        // ── Tabla de productos de la venta ──
        String[] columnas = {"ID Producto", "Producto", "Precio", "Cantidad Comprada", "Cant. a Devolver"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblProductos = new JTable(modeloTabla);
        tblProductos.setRowHeight(28);
        tblProductos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblProductos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tblProductos);
        scroll.setBorder(BorderFactory.createTitledBorder("Productos de la Venta"));

        // ── Panel inferior: motivo, cantidad y botón ──
        JPanel panelInferior = new JPanel(new GridLayout(3, 2, 10, 10));
        panelInferior.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        panelInferior.setBackground(Color.WHITE);

        JLabel lblCantidad = new JLabel("Cantidad a devolver:");
        lblCantidad.setFont(new Font("Segoe UI", Font.BOLD, 13));
        spnCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        spnCantidad.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JLabel lblMotivo = new JLabel("Motivo de devolución:");
        lblMotivo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtMotivo = new JTextArea(2, 20);
        txtMotivo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtMotivo.setLineWrap(true);
        JScrollPane scrollMotivo = new JScrollPane(txtMotivo);

        JLabel lblEstado = new JLabel("Estado del producto:");
        lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JComboBox<String> cmbEstado = new JComboBox<>(new String[]{"Buen estado", "Mal estado (rechazar)"});
        cmbEstado.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        panelInferior.add(lblCantidad);   panelInferior.add(spnCantidad);
        panelInferior.add(lblMotivo);     panelInferior.add(scrollMotivo);
        panelInferior.add(lblEstado);     panelInferior.add(cmbEstado);

        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBoton.setBackground(Color.WHITE);
        JButton btnRegistrar = new JButton("✅ Registrar Devolución");
        btnRegistrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRegistrar.setBackground(new Color(50, 150, 50));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setPreferredSize(new Dimension(220, 40));
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegistrar.addActionListener(e -> registrarDevolucion(cmbEstado));
        panelBoton.add(btnRegistrar);

        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.setBackground(Color.WHITE);
        panelSur.add(panelInferior, BorderLayout.CENTER);
        panelSur.add(panelBoton, BorderLayout.SOUTH);

        add(panelBuscar, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(panelSur, BorderLayout.SOUTH);
    }

    private void buscarVenta() {
        String idTexto = txtIdVenta.getText().trim();
        if (idTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el número de venta.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            idVentaActual = Integer.parseInt(idTexto);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El número de venta debe ser un número.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = "SELECT pv.id_producto, p.nombre, pv.precio, pv.cantidad " +
                       "FROM productos_vendidos pv " +
                       "INNER JOIN producto p ON pv.id_producto = p.id_producto " +
                       "WHERE pv.id_venta = ?";
        try (Connection con = ConexionSQLServer.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, idVentaActual);
            ResultSet rs = ps.executeQuery();
            modeloTabla.setRowCount(0);
            boolean hayResultados = false;
            while (rs.next()) {
                hayResultados = true;
                int cantComprada = rs.getInt("cantidad");
                // Calcular cuánto ya fue devuelto
                int yaDevuelto = cantidadYaDevuelta(idVentaActual, rs.getInt("id_producto"));
                int disponible = cantComprada - yaDevuelto;
                modeloTabla.addRow(new Object[]{
                    rs.getInt("id_producto"),
                    rs.getString("nombre"),
                    "S/. " + rs.getDouble("precio"),
                    cantComprada,
                    disponible > 0 ? disponible : 0
                });
            }
            if (!hayResultados) {
                idVentaActual = -1;
                JOptionPane.showMessageDialog(this, "No se encontró ninguna venta con ID: " + idTexto, "Sin resultados", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al buscar la venta.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int cantidadYaDevuelta(int idVenta, int idProducto) {
        String q = "SELECT COALESCE(SUM(cantidad_devuelta),0) FROM devoluciones WHERE id_venta=? AND id_producto=? AND estado='Aprobada'";
        try (Connection con = ConexionSQLServer.getConnection();
             PreparedStatement ps = con.prepareStatement(q)) {
            ps.setInt(1, idVenta); ps.setInt(2, idProducto);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private void registrarDevolucion(JComboBox<String> cmbEstado) {
        if (idVentaActual == -1) {
            JOptionPane.showMessageDialog(this, "Primero busca una venta válida.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int fila = tblProductos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto de la tabla.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (txtMotivo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa el motivo de la devolución.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int idProducto      = (int) modeloTabla.getValueAt(fila, 0);
        int disponible      = (int) modeloTabla.getValueAt(fila, 4);
        int cantDevolver    = (int) spnCantidad.getValue();
        String estadoProd   = (String) cmbEstado.getSelectedItem();
        boolean rechazado   = estadoProd.contains("rechazar");

        if (rechazado) {
            JOptionPane.showMessageDialog(this,
                "Devolución rechazada: el producto no está en buen estado.\nNo se actualizará el stock.",
                "Devolución Rechazada", JOptionPane.WARNING_MESSAGE);
            registrarEnBD(idProducto, cantDevolver, txtMotivo.getText().trim(), "Rechazada", false);
            return;
        }

        if (cantDevolver > disponible) {
            JOptionPane.showMessageDialog(this,
                "La cantidad a devolver (" + cantDevolver + ") supera lo disponible (" + disponible + ").",
                "Cantidad inválida", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Confirmar devolución de " + cantDevolver + " unidad(es)?\nEl stock se actualizará.",
            "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        registrarEnBD(idProducto, cantDevolver, txtMotivo.getText().trim(), "Aprobada", true);
    }

    private void registrarEnBD(int idProducto, int cantidad, String motivo, String estado, boolean actualizarStock) {
        try (Connection con = ConexionSQLServer.getConnection()) {
            // Insertar devolución
            PreparedStatement ps1 = con.prepareStatement(
                "INSERT INTO devoluciones (id_venta, id_producto, cantidad_devuelta, motivo, estado, vendedor) VALUES (?,?,?,?,?,?)");
            ps1.setInt(1, idVentaActual);
            ps1.setInt(2, idProducto);
            ps1.setInt(3, cantidad);
            ps1.setString(4, motivo);
            ps1.setString(5, estado);
            ps1.setString(6, nombreVendedor);
            ps1.executeUpdate();

            // Actualizar stock si fue aprobada
            if (actualizarStock) {
                PreparedStatement ps2 = con.prepareStatement(
                    "UPDATE producto SET stock = stock + ? WHERE id_producto = ?");
                ps2.setInt(1, cantidad);
                ps2.setInt(2, idProducto);
                ps2.executeUpdate();
            }

            JOptionPane.showMessageDialog(this,
                "Devolución registrada correctamente.\n" +
                (actualizarStock ? "Stock actualizado: +" + cantidad + " unidades." : "Stock no modificado (producto rechazado)."),
                "Éxito", JOptionPane.INFORMATION_MESSAGE);

            // Refrescar tabla
            buscarVenta();
            txtMotivo.setText("");
            spnCantidad.setValue(1);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al registrar la devolución.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}