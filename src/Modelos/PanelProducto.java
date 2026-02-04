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
    private JLabel lblStock;
    
    public PanelProducto(Producto producto) {
        this.producto = producto;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(5, 5));
        setPreferredSize(new Dimension(200, 280));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
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
        
        lblNombre = new JLabel(producto.getNombre());
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNombre.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        lblPrecio = new JLabel("S/ " + String.format("%.2f", producto.getPrecio()));
        lblPrecio.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblPrecio.setForeground(new Color(200, 50, 50));
        lblPrecio.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        lblStock = new JLabel("Stock: " + producto.getStock());
        lblStock.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStock.setForeground(new Color(100, 100, 100));
        lblStock.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panelInfo.add(Box.createVerticalStrut(5));
        panelInfo.add(lblNombre);
        panelInfo.add(Box.createVerticalStrut(5));
        panelInfo.add(lblPrecio);
        panelInfo.add(Box.createVerticalStrut(3));
        panelInfo.add(lblStock);
        
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