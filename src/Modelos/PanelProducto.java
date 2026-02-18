package Modelos;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PanelProducto extends JPanel {
    
    private final Producto producto;
    private JLabel lblImagen;
    private JLabel lblNombre;
    private JLabel lblPrecio;
    
    public PanelProducto(Producto producto) {
        this.producto = producto;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(5, 5));
        setPreferredSize(new Dimension(200, 300));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Imagen / Emoji del producto
        lblImagen = new JLabel();
        lblImagen.setPreferredSize(new Dimension(180, 150));
        lblImagen.setHorizontalAlignment(SwingConstants.CENTER);
        lblImagen.setBackground(new Color(245, 245, 245));
        lblImagen.setOpaque(true);
        
        String emoji = obtenerEmoji(producto.getCategoria());
        lblImagen.setText(emoji);
        lblImagen.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        
        add(lblImagen, BorderLayout.NORTH);
        
        JPanel panelInfo = new JPanel();
        panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
        panelInfo.setBackground(Color.WHITE);
        
        // Nombre del producto
        lblNombre = new JLabel("<html><body style='width:170px'>" + producto.getNombre() + "</body></html>");
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblNombre.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Valoración con estrellas (fija en 4.5 por defecto)
        JLabel lblEstrellas = new JLabel("⭐⭐⭐⭐⭐");
        lblEstrellas.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        lblEstrellas.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Precio
        lblPrecio = new JLabel("S/ " + String.format("%.2f", producto.getPrecio()));
        lblPrecio.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblPrecio.setForeground(new Color(200, 50, 50));
        lblPrecio.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panelInfo.add(Box.createVerticalStrut(5));
        panelInfo.add(lblNombre);
        panelInfo.add(Box.createVerticalStrut(3));
        panelInfo.add(lblEstrellas);
        panelInfo.add(Box.createVerticalStrut(5));
        panelInfo.add(lblPrecio);
        panelInfo.add(Box.createVerticalStrut(3));

        // Mostrar alerta de stock bajo si hay menos de 5 unidades
        // NO se muestra el número de stock al cliente, solo la alerta
        if (producto.getStock() < 5 && producto.getStock() > 0) {
            JLabel lblStockBajo = new JLabel("⚠ ¡Últimas " + producto.getStock() + " unidades!");
            lblStockBajo.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lblStockBajo.setForeground(new Color(200, 50, 50));
            lblStockBajo.setAlignmentX(Component.LEFT_ALIGNMENT);
            panelInfo.add(lblStockBajo);
        } else if (producto.getStock() == 0) {
            JLabel lblAgotado = new JLabel("❌ Producto agotado");
            lblAgotado.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lblAgotado.setForeground(Color.GRAY);
            lblAgotado.setAlignmentX(Component.LEFT_ALIGNMENT);
            panelInfo.add(lblAgotado);
        }
        
        add(panelInfo, BorderLayout.CENTER);
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 50, 50), 2),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }
    
    private String obtenerEmoji(String categoria) {
        return switch (categoria.toLowerCase()) {
            case "libros" -> "📕";
            case "cuadernos" -> "📓";
            case "escritura" -> "✏️";
            case "mochilas" -> "🎒";
            case "geometría" -> "📐";
            case "arte" -> "🎨";
            case "oficina" -> "📁";
            case "útiles" -> "✂️";
            default -> "📚";
        };
    }
    
    public Producto getProducto() {
        return producto;
    }
}