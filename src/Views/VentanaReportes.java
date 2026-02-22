package Views;

import java.awt.*;
import conexiondb.ConexionSQLServer;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.*;

public class VentanaReportes extends JFrame {

    private JTabbedPane tabs;
    // Tab Ventas
    private JTextField txtDesde, txtHasta, txtVendedor;
    private JTable tblVentas;
    private DefaultTableModel modelVentas;
    // Tab Inventario
    private JComboBox<String> cmbFiltroInv;
    private JTable tblInventario;
    private DefaultTableModel modelInventario;
    // Tab Estadísticas
    private JTable tblEstadisticas;
    private DefaultTableModel modelEstadisticas;
    private JComboBox<String> cmbPeriodo;

    public VentanaReportes() {
        setTitle("Reportes - Librería Fanny");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Título
        JLabel lblTitulo = new JLabel("  📊 Módulo de Reportes", JLabel.LEFT);
        lblTitulo.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 20));
        lblTitulo.setForeground(java.awt.Color.WHITE);
        lblTitulo.setBackground(new java.awt.Color(0, 0, 51));
        lblTitulo.setOpaque(true);
        lblTitulo.setPreferredSize(new Dimension(0, 50));
        add(lblTitulo, BorderLayout.NORTH);

        tabs = new JTabbedPane();
        tabs.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        tabs.addTab("📈 Reporte de Ventas", crearTabVentas());
        tabs.addTab("📦 Reporte de Inventario", crearTabInventario());
        tabs.addTab("📊 Estadísticas", crearTabEstadisticas());
        add(tabs, BorderLayout.CENTER);
    }

    // ══════════════════════════════════════════════
    // TAB 1: VENTAS
    // ══════════════════════════════════════════════
    private JPanel crearTabVentas() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Filtros
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filtros.setBackground(new java.awt.Color(240, 240, 240));
        filtros.setBorder(BorderFactory.createTitledBorder("Filtros"));

        java.awt.Font f = new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13);
        JLabel l1 = new JLabel("Desde:"); l1.setFont(f);
        JLabel l2 = new JLabel("Hasta:");  l2.setFont(f);
        JLabel l3 = new JLabel("Vendedor:"); l3.setFont(f);

        txtDesde   = new JTextField("2026-01-01", 10); txtDesde.setFont(f);
        txtHasta   = new JTextField("2026-12-31", 10); txtHasta.setFont(f);
        txtVendedor = new JTextField(10); txtVendedor.setFont(f);

        JButton btnGenerar = new JButton("🔍 Generar");
        btnGenerar.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        btnGenerar.setBackground(new java.awt.Color(0, 0, 51));
        btnGenerar.setForeground(java.awt.Color.WHITE);
        btnGenerar.setFocusPainted(false);
        btnGenerar.addActionListener(e -> generarReporteVentas());

        JButton btnExcel = new JButton("📥 Exportar Excel");
        btnExcel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        btnExcel.setBackground(new java.awt.Color(34, 139, 34));
        btnExcel.setForeground(java.awt.Color.WHITE);
        btnExcel.setFocusPainted(false);
        btnExcel.addActionListener(e -> exportarExcelVentas());

        filtros.add(l1); filtros.add(txtDesde);
        filtros.add(l2); filtros.add(txtHasta);
        filtros.add(l3); filtros.add(txtVendedor);
        filtros.add(btnGenerar);
        filtros.add(btnExcel);

        // Tabla
        modelVentas = new DefaultTableModel(new String[]{"Cliente","Producto","Precio","Cantidad","Subtotal","Fecha","Vendedor"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblVentas = new JTable(modelVentas);
        tblVentas.setRowHeight(25);
        tblVentas.setFont(f);
        tblVentas.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));

        panel.add(filtros, BorderLayout.NORTH);
        panel.add(new JScrollPane(tblVentas), BorderLayout.CENTER);
        return panel;
    }

    private void generarReporteVentas() {
        String desde   = txtDesde.getText().trim();
        String hasta   = txtHasta.getText().trim();
        String vendedor = txtVendedor.getText().trim();

        StringBuilder q = new StringBuilder(
            "SELECT CONCAT(c.nombres,' ',c.apellidos) AS cliente, p.nombre AS producto, " +
            "pv.precio, pv.cantidad, pv.subtotal, DATE(v.fecha) AS fecha, v.vendedor " +
            "FROM ventas v " +
            "INNER JOIN cliente c ON v.id_cliente=c.id_cliente " +
            "INNER JOIN productos_vendidos pv ON v.id_venta=pv.id_venta " +
            "INNER JOIN producto p ON pv.id_producto=p.id_producto WHERE 1=1 ");
        if (!desde.isEmpty())   q.append("AND DATE(v.fecha) >= ? ");
        if (!hasta.isEmpty())   q.append("AND DATE(v.fecha) <= ? ");
        if (!vendedor.isEmpty()) q.append("AND v.vendedor LIKE ? ");
        q.append("ORDER BY v.fecha DESC");

        try (Connection con = ConexionSQLServer.getConnection();
             PreparedStatement ps = con.prepareStatement(q.toString())) {
            int i = 1;
            if (!desde.isEmpty())    ps.setString(i++, desde);
            if (!hasta.isEmpty())    ps.setString(i++, hasta);
            if (!vendedor.isEmpty()) ps.setString(i++, "%" + vendedor + "%");

            modelVentas.setRowCount(0);
            ResultSet rs = ps.executeQuery();
            boolean hay = false;
            while (rs.next()) {
                hay = true;
                modelVentas.addRow(new Object[]{
                    rs.getString("cliente"), rs.getString("producto"),
                    "S/. " + rs.getDouble("precio"), rs.getInt("cantidad"),
                    "S/. " + rs.getDouble("subtotal"), rs.getDate("fecha"),
                    rs.getString("vendedor")
                });
            }
            if (!hay) JOptionPane.showMessageDialog(this, "Sin registros encontrados.", "Info", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportarExcelVentas() {
        if (modelVentas.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Primero genera el reporte.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            XSSFWorkbook wb = new XSSFWorkbook();
            XSSFSheet sheet = wb.createSheet("Reporte de Ventas");

            // Estilo encabezado
            XSSFCellStyle estilo = wb.createCellStyle();
            estilo.setFillForegroundColor(new XSSFColor(new byte[]{0,0,51}, null));
            estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font font = wb.createFont();
            font.setBold(true);
            font.setColor(IndexedColors.WHITE.getIndex());
            estilo.setFont(font);

            // Encabezados
            Row header = sheet.createRow(0);
            String[] cols = {"Cliente","Producto","Precio","Cantidad","Subtotal","Fecha","Vendedor"};
            for (int c = 0; c < cols.length; c++) {
                Cell cell = header.createCell(c);
                cell.setCellValue(cols[c]);
                cell.setCellStyle(estilo);
                sheet.setColumnWidth(c, 5000);
            }

            // Datos
            for (int r = 0; r < modelVentas.getRowCount(); r++) {
                Row row = sheet.createRow(r + 1);
                for (int c = 0; c < modelVentas.getColumnCount(); c++)
                    row.createCell(c).setCellValue(modelVentas.getValueAt(r, c).toString());
            }

            // Guardar
            JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(new File("Reporte_Ventas.xlsx"));
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                FileOutputStream out = new FileOutputStream(fc.getSelectedFile());
                wb.write(out);
                out.close();
                JOptionPane.showMessageDialog(this, "✅ Excel guardado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
            wb.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al exportar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ══════════════════════════════════════════════
    // TAB 2: INVENTARIO
    // ══════════════════════════════════════════════
    private JPanel crearTabInventario() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filtros.setBackground(new java.awt.Color(240, 240, 240));
        filtros.setBorder(BorderFactory.createTitledBorder("Filtros"));

        java.awt.Font f = new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13);
        JLabel l1 = new JLabel("Filtro:"); l1.setFont(f);
        cmbFiltroInv = new JComboBox<>(new String[]{"Todos", "Stock Crítico (≤10)"});
        cmbFiltroInv.setFont(f);

        JButton btnGenerar = new JButton("🔍 Generar");
        btnGenerar.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        btnGenerar.setBackground(new java.awt.Color(0, 0, 51));
        btnGenerar.setForeground(java.awt.Color.WHITE);
        btnGenerar.setFocusPainted(false);
        btnGenerar.addActionListener(e -> generarReporteInventario());

        JButton btnExcel = new JButton("📥 Exportar Excel");
        btnExcel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        btnExcel.setBackground(new java.awt.Color(34, 139, 34));
        btnExcel.setForeground(java.awt.Color.WHITE);
        btnExcel.setFocusPainted(false);
        btnExcel.addActionListener(e -> exportarExcelInventario());

        filtros.add(l1); filtros.add(cmbFiltroInv);
        filtros.add(btnGenerar); filtros.add(btnExcel);

        modelInventario = new DefaultTableModel(new String[]{"ID","Producto","Categoría","Precio","Stock","Estado"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblInventario = new JTable(modelInventario);
        tblInventario.setRowHeight(25);
        tblInventario.setFont(f);
        tblInventario.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));

        panel.add(filtros, BorderLayout.NORTH);
        panel.add(new JScrollPane(tblInventario), BorderLayout.CENTER);
        return panel;
    }

    private void generarReporteInventario() {
        String filtro = (String) cmbFiltroInv.getSelectedItem();
        String q = "SELECT id_producto, nombre, categoria, precio, stock FROM producto WHERE activo=1 " +
                   (filtro.contains("Crítico") ? "AND stock <= 10 " : "") +
                   "ORDER BY stock ASC";
        try (Connection con = ConexionSQLServer.getConnection();
             PreparedStatement ps = con.prepareStatement(q);
             ResultSet rs = ps.executeQuery()) {
            modelInventario.setRowCount(0);
            boolean hay = false;
            while (rs.next()) {
                hay = true;
                int stock = rs.getInt("stock");
                String estado = stock <= 10 ? "⚠️ Crítico" : stock <= 20 ? "🟡 Bajo" : "✅ Normal";
                modelInventario.addRow(new Object[]{
                    rs.getInt("id_producto"), rs.getString("nombre"),
                    rs.getString("categoria"), "S/. " + rs.getDouble("precio"),
                    stock, estado
                });
            }
            if (!hay) JOptionPane.showMessageDialog(this, "No hay productos.", "Info", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportarExcelInventario() {
        if (modelInventario.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Primero genera el reporte.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            XSSFWorkbook wb = new XSSFWorkbook();
            XSSFSheet sheet = wb.createSheet("Inventario");

            XSSFCellStyle estilo = wb.createCellStyle();
            estilo.setFillForegroundColor(new XSSFColor(new byte[]{0,0,51}, null));
            estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font font = wb.createFont();
            font.setBold(true);
            font.setColor(IndexedColors.WHITE.getIndex());
            estilo.setFont(font);

            Row header = sheet.createRow(0);
            String[] cols = {"ID","Producto","Categoría","Precio","Stock","Estado"};
            for (int c = 0; c < cols.length; c++) {
                Cell cell = header.createCell(c);
                cell.setCellValue(cols[c]);
                cell.setCellStyle(estilo);
                sheet.setColumnWidth(c, 5000);
            }
            for (int r = 0; r < modelInventario.getRowCount(); r++) {
                Row row = sheet.createRow(r + 1);
                for (int c = 0; c < modelInventario.getColumnCount(); c++)
                    row.createCell(c).setCellValue(modelInventario.getValueAt(r, c).toString());
            }

            JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(new File("Reporte_Inventario.xlsx"));
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                FileOutputStream out = new FileOutputStream(fc.getSelectedFile());
                wb.write(out); out.close();
                JOptionPane.showMessageDialog(this, "✅ Excel guardado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
            wb.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al exportar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ══════════════════════════════════════════════
    // TAB 3: ESTADÍSTICAS
    // ══════════════════════════════════════════════
    private JPanel crearTabEstadisticas() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filtros.setBackground(new java.awt.Color(240, 240, 240));
        filtros.setBorder(BorderFactory.createTitledBorder("Periodo"));

        java.awt.Font f = new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13);
        JLabel l1 = new JLabel("Periodo:"); l1.setFont(f);
        cmbPeriodo = new JComboBox<>(new String[]{"Este mes", "Este año", "Todo el tiempo"});
        cmbPeriodo.setFont(f);

        JButton btnGenerar = new JButton("📊 Visualizar");
        btnGenerar.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        btnGenerar.setBackground(new java.awt.Color(0, 0, 51));
        btnGenerar.setForeground(java.awt.Color.WHITE);
        btnGenerar.setFocusPainted(false);
        btnGenerar.addActionListener(e -> generarEstadisticas());

        JButton btnExcel = new JButton("📥 Exportar Excel");
        btnExcel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        btnExcel.setBackground(new java.awt.Color(34, 139, 34));
        btnExcel.setForeground(java.awt.Color.WHITE);
        btnExcel.setFocusPainted(false);
        btnExcel.addActionListener(e -> exportarExcelEstadisticas());

        filtros.add(l1); filtros.add(cmbPeriodo);
        filtros.add(btnGenerar); filtros.add(btnExcel);

        modelEstadisticas = new DefaultTableModel(new String[]{"Métrica","Valor"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblEstadisticas = new JTable(modelEstadisticas);
        tblEstadisticas.setRowHeight(30);
        tblEstadisticas.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
        tblEstadisticas.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));

        panel.add(filtros, BorderLayout.NORTH);
        panel.add(new JScrollPane(tblEstadisticas), BorderLayout.CENTER);
        return panel;
    }

    private void generarEstadisticas() {
        String periodo = (String) cmbPeriodo.getSelectedItem();
        String condicion = "";
        if (periodo.equals("Este mes"))  condicion = "AND MONTH(v.fecha)=MONTH(NOW()) AND YEAR(v.fecha)=YEAR(NOW())";
        if (periodo.equals("Este año"))  condicion = "AND YEAR(v.fecha)=YEAR(NOW())";

        modelEstadisticas.setRowCount(0);
        try (Connection con = ConexionSQLServer.getConnection()) {

            // Total ventas
            ResultSet rs1 = con.createStatement().executeQuery(
                "SELECT COUNT(*) as total, COALESCE(SUM(total),0) as ingresos FROM ventas WHERE 1=1 " + condicion.replace("v.fecha","fecha"));
            if (rs1.next()) {
                modelEstadisticas.addRow(new Object[]{"📦 Total de ventas", rs1.getInt("total")});
                modelEstadisticas.addRow(new Object[]{"💰 Ingresos totales", "S/. " + rs1.getDouble("ingresos")});
            }

            // Producto más vendido
            ResultSet rs2 = con.createStatement().executeQuery(
                "SELECT p.nombre, SUM(pv.cantidad) as total FROM productos_vendidos pv " +
                "INNER JOIN ventas v ON pv.id_venta=v.id_venta " +
                "INNER JOIN producto p ON pv.id_producto=p.id_producto WHERE 1=1 " + condicion +
                " GROUP BY p.nombre ORDER BY total DESC LIMIT 1");
            if (rs2.next())
                modelEstadisticas.addRow(new Object[]{"🏆 Producto más vendido", rs2.getString("nombre") + " (" + rs2.getInt("total") + " unid.)"});
            else
                modelEstadisticas.addRow(new Object[]{"🏆 Producto más vendido", "Historial insuficiente"});

            // Vendedor top
            ResultSet rs3 = con.createStatement().executeQuery(
                "SELECT vendedor, COUNT(*) as total FROM ventas WHERE 1=1 " + condicion.replace("v.fecha","fecha") +
                " GROUP BY vendedor ORDER BY total DESC LIMIT 1");
            if (rs3.next())
                modelEstadisticas.addRow(new Object[]{"⭐ Vendedor top", rs3.getString("vendedor") + " (" + rs3.getInt("total") + " ventas)"});
            else
                modelEstadisticas.addRow(new Object[]{"⭐ Vendedor top", "Sin datos"});

            // Ticket promedio
            ResultSet rs4 = con.createStatement().executeQuery(
                "SELECT COALESCE(AVG(total),0) as promedio FROM ventas WHERE 1=1 " + condicion.replace("v.fecha","fecha"));
            if (rs4.next())
                modelEstadisticas.addRow(new Object[]{"🧾 Ticket promedio", "S/. " + String.format("%.2f", rs4.getDouble("promedio"))});

            // Total clientes
            ResultSet rs5 = con.createStatement().executeQuery("SELECT COUNT(*) as total FROM cliente");
            if (rs5.next())
                modelEstadisticas.addRow(new Object[]{"👥 Total clientes registrados", rs5.getInt("total")});

            // Stock crítico
            ResultSet rs6 = con.createStatement().executeQuery("SELECT COUNT(*) as total FROM producto WHERE stock <= 10 AND activo=1");
            if (rs6.next())
                modelEstadisticas.addRow(new Object[]{"⚠️ Productos con stock crítico", rs6.getInt("total")});

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportarExcelEstadisticas() {
        if (modelEstadisticas.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Primero genera las estadísticas.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            XSSFWorkbook wb = new XSSFWorkbook();
            XSSFSheet sheet = wb.createSheet("Estadísticas");

            XSSFCellStyle estilo = wb.createCellStyle();
            estilo.setFillForegroundColor(new XSSFColor(new byte[]{0,0,51}, null));
            estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font font = wb.createFont();
            font.setBold(true);
            font.setColor(IndexedColors.WHITE.getIndex());
            estilo.setFont(font);

            Row header = sheet.createRow(0);
            String[] cols = {"Métrica", "Valor"};
            for (int c = 0; c < cols.length; c++) {
                Cell cell = header.createCell(c);
                cell.setCellValue(cols[c]);
                cell.setCellStyle(estilo);
                sheet.setColumnWidth(c, 8000);
            }
            for (int r = 0; r < modelEstadisticas.getRowCount(); r++) {
                Row row = sheet.createRow(r + 1);
                for (int c = 0; c < modelEstadisticas.getColumnCount(); c++)
                    row.createCell(c).setCellValue(modelEstadisticas.getValueAt(r, c).toString());
            }

            JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(new File("Estadisticas_Negocio.xlsx"));
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                FileOutputStream out = new FileOutputStream(fc.getSelectedFile());
                wb.write(out); out.close();
                JOptionPane.showMessageDialog(this, "✅ Excel guardado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
            wb.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al exportar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}