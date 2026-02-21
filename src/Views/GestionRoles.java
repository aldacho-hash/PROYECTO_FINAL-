package Views;

import conexiondb.ConexionSQLServer;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class GestionRoles extends JFrame {

    private JTable tabla;
    private DefaultTableModel modelo;
    private JButton btnGuardar, btnCerrar;
    private String[] cargos = {"administrador", "vendedor", "logistica"};
    private String[] modulos = {"Vendedor", "Logística", "Administrador"};

    public GestionRoles() {
        setTitle("Gestión de Roles y Permisos - Librería Fanny");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel bg = new JPanel(new BorderLayout(10, 10));
        bg.setBackground(new Color(0, 0, 51));
        bg.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título
        JLabel titulo = new JLabel("GESTIÓN DE ROLES Y PERMISOS", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI Black", Font.BOLD, 16));
        titulo.setForeground(Color.WHITE);
        bg.add(titulo, BorderLayout.NORTH);

        // Tabla
        modelo = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int col) {
                return col == 0 ? String.class : Boolean.class;
            }
            @Override
            public boolean isCellEditable(int row, int col) {
                // No editar cargo administrador en módulo Administrador (última protección)
                return col != 0;
            }
        };

        modelo.addColumn("Cargo");
        for (String modulo : modulos) modelo.addColumn(modulo);

        cargarPermisos();

        tabla = new JTable(modelo);
        tabla.setRowHeight(35);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabla.getTableHeader().setBackground(new Color(0, 102, 204));
        tabla.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(tabla);
        bg.add(scroll, BorderLayout.CENTER);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setBackground(new Color(0, 0, 51));

        btnGuardar = new JButton("GUARDAR CAMBIOS");
        btnGuardar.setBackground(new Color(0, 153, 76));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnGuardar.setFocusPainted(false);
        btnGuardar.setBorderPainted(false);

        btnCerrar = new JButton("CERRAR");
        btnCerrar.setBackground(new Color(153, 0, 0));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCerrar.setFocusPainted(false);
        btnCerrar.setBorderPainted(false);

        btnGuardar.addActionListener(e -> guardarPermisos());
        btnCerrar.addActionListener(e -> dispose());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCerrar);
        bg.add(panelBotones, BorderLayout.SOUTH);

        setContentPane(bg);
    }

    private void cargarPermisos() {
        modelo.setRowCount(0);
        String query = "SELECT cargo, modulo, puede_acceder FROM permisos ORDER BY cargo, modulo";
        try (Connection conn = ConexionSQLServer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            // Organizar por cargo
            java.util.Map<String, Boolean[]> mapa = new java.util.LinkedHashMap<>();
            for (String cargo : cargos) mapa.put(cargo, new Boolean[]{false, false, false});

            while (rs.next()) {
                String cargo = rs.getString("cargo");
                String modulo = rs.getString("modulo");
                boolean acceso = rs.getInt("puede_acceder") == 1;
                if (mapa.containsKey(cargo)) {
                    for (int i = 0; i < modulos.length; i++) {
                        if (modulos[i].equals(modulo)) {
                            mapa.get(cargo)[i] = acceso;
                        }
                    }
                }
            }

            for (String cargo : cargos) {
                Boolean[] permisos = mapa.get(cargo);
                modelo.addRow(new Object[]{cargo, permisos[0], permisos[1], permisos[2]});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar permisos: " + e.getMessage());
        }
    }

    private void guardarPermisos() {
        // Protección: el administrador siempre debe tener acceso a Administrador
        for (int fila = 0; fila < modelo.getRowCount(); fila++) {
            String cargo = (String) modelo.getValueAt(fila, 0);
            if (cargo.equals("administrador")) {
                Boolean accesoAdmin = (Boolean) modelo.getValueAt(fila, 3);
                if (accesoAdmin == null || !accesoAdmin) {
                    JOptionPane.showMessageDialog(this,
                        "⛔ No puedes revocar el acceso de Administrador al módulo Administrador.\nDebe quedar al menos un rol con ese acceso.",
                        "Acción no permitida", JOptionPane.WARNING_MESSAGE);
                    modelo.setValueAt(true, fila, 3);
                    return;
                }
            }
        }

        try (Connection conn = ConexionSQLServer.getConnection()) {
            String query = "UPDATE permisos SET puede_acceder = ? WHERE cargo = ? AND modulo = ?";
            PreparedStatement stmt = conn.prepareStatement(query);

            for (int fila = 0; fila < modelo.getRowCount(); fila++) {
                String cargo = (String) modelo.getValueAt(fila, 0);
                for (int col = 0; col < modulos.length; col++) {
                    Boolean valor = (Boolean) modelo.getValueAt(fila, col + 1);
                    stmt.setInt(1, valor != null && valor ? 1 : 0);
                    stmt.setString(2, cargo);
                    stmt.setString(3, modulos[col]);
                    stmt.executeUpdate();
                }
            }

            JOptionPane.showMessageDialog(this,
                "✅ Permisos actualizados correctamente.",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al guardar permisos: " + e.getMessage());
        }
    }
}