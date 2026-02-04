
package Clases;

import Modelos.CarritoProducto;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

public class GenerarPDF {

    public static void generarBoletaDeVenta(String nombreCliente, 
                                            List<CarritoProducto> carrito, 
                                            double total,
                                            String metodoPago, 
                                            String delivery,
                                            String nombreCampaña,
                                            double porcentajeDescuento) {

        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream("comprobante_pago.pdf"));
            document.open();

            document.add(new Paragraph("Comprobante de Pago", new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD)));
            document.add(new Paragraph("Fecha: " + java.time.LocalDate.now())); 
            document.add(new Paragraph("Cliente: " + nombreCliente));
            document.add(new Paragraph("Método de Pago: " + metodoPago));
            document.add(new Paragraph("Delivery: " + delivery));
            document.add(new Paragraph(" "));

            if (porcentajeDescuento > 0) {
                document.add(new Paragraph("Campaña Activa: " + nombreCampaña));
                document.add(new Paragraph("Descuento aplicado: " + porcentajeDescuento + "%"));
            } else {
                document.add(new Paragraph("Campaña Activa: Ninguna"));
            }
            document.add(new Paragraph(" "));

            PdfPTable tabla = new PdfPTable(4);

            tabla.addCell("Producto");
            tabla.addCell("Precio");
            tabla.addCell("Cantidad");
            tabla.addCell("Subtotal");

            double totalCarrito = 0;

            for (CarritoProducto cp : carrito) {
                String nombreProducto = cp.getProducto().getNombre();
                double precio = cp.getProducto().getPrecio();
                int cantidad = cp.getCantidad();
                double subtotal = cp.getSubtotal();

                tabla.addCell(nombreProducto);
                tabla.addCell(String.format("S/ %.2f", precio));
                tabla.addCell(String.valueOf(cantidad));
                tabla.addCell(String.format("S/ %.2f", subtotal));

                totalCarrito += subtotal;
            }

            document.add(tabla);

            // =======================
            // DESCUENTO
            // =======================
            double montoDescuento = 0;
            double totalPostDescuento = totalCarrito;

            if (porcentajeDescuento > 0) {
                montoDescuento = totalCarrito * (porcentajeDescuento / 100);
                totalPostDescuento = totalCarrito - montoDescuento;

                document.add(new Paragraph(" "));
                document.add(new Paragraph("Subtotal: S/ " + String.format("%.2f", totalCarrito)));
                document.add(new Paragraph("Descuento: S/ " + String.format("%.2f", montoDescuento)+"-"));
                document.add(new Paragraph("OP. GRAVADA: S/ " + String.format("%.2f", totalPostDescuento)));
            } else {
                document.add(new Paragraph("Subtotal: S/ " + String.format("%.2f", totalCarrito)));
                document.add(new Paragraph("OP. GRAVADA: S/ " + String.format("%.2f", totalCarrito)));
            }

            // =======================
            // IGV (18%)
            // =======================
            double impuesto = totalPostDescuento * 0.18;
            double totalConImpuestos = totalPostDescuento + impuesto;

            // =======================
            // RECARGO VISA (3%)
            // =======================
            double recargoVisa = 0;
            if ("VISA".equalsIgnoreCase(metodoPago)) {
                recargoVisa = totalPostDescuento * 0.03;
            }

            // =======================
            // DELIVERY
            // =======================
            double costoDelivery = delivery.equalsIgnoreCase("Sí") ? 5 : 0;

            // =======================
            // TOTAL FINAL
            // =======================
            double totalFinal = totalConImpuestos + recargoVisa + costoDelivery;

            document.add(new Paragraph("IGV (18%): S/ " + String.format("%.2f", impuesto)+"+"));
            
            if (costoDelivery > 0)
                document.add(new Paragraph("Costo Delivery: S/ " + String.format("%.2f", costoDelivery)+"+"));
            
            if (recargoVisa > 0)
                document.add(new Paragraph("Recargo VISA (3%): S/ " + String.format("%.2f", recargoVisa)+"+"));
            document.add(new Paragraph("-----------------------------------------------------"));
            document.add(new Paragraph("TOTAL A PAGAR: S/ " + String.format("%.2f", totalFinal)));
            document.add(new Paragraph("-----------------------------------------------------"));

            document.close();
            System.out.println("Comprobante generado exitosamente.");

        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
