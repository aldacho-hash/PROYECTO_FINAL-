package Clases;

import Modelos.CarritoProducto;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

public class GenerarPDF {

    // ── Colores ──
    private static final BaseColor COLOR_NEGRO     = new BaseColor(30, 30, 30);
    private static final BaseColor COLOR_BEIGE     = new BaseColor(245, 240, 230);
    private static final BaseColor COLOR_GRIS      = new BaseColor(180, 180, 180);
    private static final BaseColor COLOR_TABLA_CAB = new BaseColor(30, 30, 30);
    private static final BaseColor COLOR_FILA_PAR  = new BaseColor(250, 248, 244);
    private static final BaseColor COLOR_BLANCO    = BaseColor.WHITE;

    public static String generarBoletaDeVenta(String nombreCliente,
                                              List<CarritoProducto> carrito,
                                              double total,
                                              String metodoPago,
                                              String delivery,
                                              String nombreCampaña,
                                              double porcentajeDescuento,
                                              String tipoComprobante,
                                              String numeroComprobante,
                                              String ruc,
                                              String razonSocial) {

        String rutaArchivo = tipoComprobante + "_" + numeroComprobante.replace("/", "-") + ".pdf";
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);

        try {
            PdfWriter.getInstance(document, new FileOutputStream(rutaArchivo));
            document.open();

            // ── Fuentes ──
            Font fTitulo   = new Font(Font.FontFamily.HELVETICA, 36, Font.BOLD,  COLOR_NEGRO);
            Font fNumero   = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD,  COLOR_NEGRO);
            Font fSeccion  = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD,  COLOR_NEGRO);
            Font fNormal   = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL,COLOR_NEGRO);
            Font fGris     = new Font(Font.FontFamily.HELVETICA,  9, Font.NORMAL,COLOR_GRIS);
            Font fTabHead  = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD,  COLOR_BLANCO);
            Font fTabBody  = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL,COLOR_NEGRO);
            Font fTotalLbl = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD,  COLOR_BLANCO);
            Font fTotalVal = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD,  COLOR_BLANCO);

            // ══════════════════════════════════════════
            // ENCABEZADO: Título + Nombre empresa
            // ══════════════════════════════════════════
            PdfPTable tablaHeader = new PdfPTable(2);
            tablaHeader.setWidthPercentage(100);
            tablaHeader.setWidths(new float[]{60f, 40f});
            tablaHeader.setSpacingAfter(20f);

            // Celda izquierda: FACTURA / BOLETA + número
            PdfPCell celdaTitulo = new PdfPCell();
            celdaTitulo.setBorder(Rectangle.NO_BORDER);
            celdaTitulo.setBackgroundColor(COLOR_BEIGE);
            celdaTitulo.setPadding(20f);
            celdaTitulo.addElement(new Paragraph(tipoComprobante.toUpperCase(), fTitulo));

            PdfPTable tablaNum = new PdfPTable(1);
            tablaNum.setWidthPercentage(60);
            PdfPCell celdaNum = new PdfPCell(new Phrase("N: " + numeroComprobante, fNumero));
            celdaNum.setBorderColor(COLOR_NEGRO);
            celdaNum.setBorderWidth(1.5f);
            celdaNum.setPadding(6f);
            tablaNum.addCell(celdaNum);
            celdaTitulo.addElement(tablaNum);
            tablaHeader.addCell(celdaTitulo);

            // Celda derecha: nombre librería
            PdfPCell celdaEmpresa = new PdfPCell();
            celdaEmpresa.setBorder(Rectangle.NO_BORDER);
            celdaEmpresa.setBackgroundColor(COLOR_BEIGE);
            celdaEmpresa.setPadding(20f);
            celdaEmpresa.setVerticalAlignment(Element.ALIGN_MIDDLE);
            Paragraph pEmpresa = new Paragraph("LIBRERIA\nFANNYSTORE", fTitulo);
            pEmpresa.setAlignment(Element.ALIGN_RIGHT);
            celdaEmpresa.addElement(pEmpresa);
            tablaHeader.addCell(celdaEmpresa);

            document.add(tablaHeader);

            // ══════════════════════════════════════════
            // DATOS DEL CLIENTE | DATOS DE LA EMPRESA
            // ══════════════════════════════════════════
            PdfPTable tablaDatos = new PdfPTable(3);
            tablaDatos.setWidthPercentage(100);
            tablaDatos.setWidths(new float[]{45f, 5f, 45f});
            tablaDatos.setSpacingAfter(20f);

            // Datos del cliente
            PdfPCell celdaCliente = new PdfPCell();
            celdaCliente.setBorder(Rectangle.NO_BORDER);
            celdaCliente.setPaddingRight(15f);
            celdaCliente.addElement(new Paragraph("DATOS DEL CLIENTE", fSeccion));
            celdaCliente.addElement(new Paragraph(" ", fGris));
            celdaCliente.addElement(new Paragraph(nombreCliente, fNormal));
            if (tipoComprobante.equalsIgnoreCase("Factura")) {
                celdaCliente.addElement(new Paragraph("RUC: " + ruc, fNormal));
                celdaCliente.addElement(new Paragraph("Razon Social: " + razonSocial, fNormal));
            }
            celdaCliente.addElement(new Paragraph("Fecha: " + java.time.LocalDate.now(), fNormal));
            tablaDatos.addCell(celdaCliente);

            // Separador vertical
            PdfPCell celdaSep = new PdfPCell();
            celdaSep.setBorder(Rectangle.LEFT);
            celdaSep.setBorderColor(COLOR_GRIS);
            celdaSep.setBorderWidth(1f);
            tablaDatos.addCell(celdaSep);

            // Datos de la empresa
            PdfPCell celdaEmp = new PdfPCell();
            celdaEmp.setBorder(Rectangle.NO_BORDER);
            celdaEmp.setPaddingLeft(15f);
            celdaEmp.addElement(new Paragraph("DATOS DE LA EMPRESA", fSeccion));
            celdaEmp.addElement(new Paragraph(" ", fGris));
            Paragraph pEmpDatos = new Paragraph(
                "Libreria Fannystore\nlibreriafanny@gmail.com\nRUC: 20100000001\nAv. Principal 456, Lima", fNormal);
            pEmpDatos.setAlignment(Element.ALIGN_RIGHT);
            celdaEmp.addElement(pEmpDatos);
            tablaDatos.addCell(celdaEmp);

            document.add(tablaDatos);

            // Línea separadora
            agregarLineaSeparadora(document);

            // ══════════════════════════════════════════
            // TABLA DE PRODUCTOS
            // ══════════════════════════════════════════
            PdfPTable tablaProductos = new PdfPTable(4);
            tablaProductos.setWidthPercentage(100);
            tablaProductos.setWidths(new float[]{45f, 15f, 20f, 20f});
            tablaProductos.setSpacingAfter(10f);

            // Cabecera
            for (String cab : new String[]{"Detalle", "Cantidad", "Precio", "Total"}) {
                PdfPCell c = new PdfPCell(new Phrase(cab, fTabHead));
                c.setBackgroundColor(COLOR_TABLA_CAB);
                c.setPadding(8f);
                c.setBorder(Rectangle.NO_BORDER);
                c.setHorizontalAlignment(cab.equals("Detalle") ? Element.ALIGN_LEFT : Element.ALIGN_CENTER);
                tablaProductos.addCell(c);
            }

            // Filas de productos
            double totalCarrito = 0;
            boolean filaPar = false;
            for (CarritoProducto cp : carrito) {
                BaseColor colorFila = filaPar ? COLOR_FILA_PAR : COLOR_BLANCO;
                filaPar = !filaPar;

                double precio   = cp.getProducto().getPrecio();
                int cantidad    = cp.getCantidad();
                double subtotal = cp.getSubtotal();
                totalCarrito   += subtotal;

                agregarCeldaTabla(tablaProductos, cp.getProducto().getNombre(), fTabBody, colorFila, Element.ALIGN_LEFT);
                agregarCeldaTabla(tablaProductos, String.valueOf(cantidad),              fTabBody, colorFila, Element.ALIGN_CENTER);
                agregarCeldaTabla(tablaProductos, String.format("S/ %.2f", precio),      fTabBody, colorFila, Element.ALIGN_CENTER);
                agregarCeldaTabla(tablaProductos, String.format("S/ %.2f", subtotal),    fTabBody, colorFila, Element.ALIGN_CENTER);
            }

            document.add(tablaProductos);
            agregarLineaSeparadora(document);

            // ══════════════════════════════════════════
            // TOTALES
            // ══════════════════════════════════════════
            double montoDescuento     = 0;
            double totalPostDescuento = totalCarrito;
            if (porcentajeDescuento > 0) {
                montoDescuento     = totalCarrito * (porcentajeDescuento / 100);
                totalPostDescuento = totalCarrito - montoDescuento;
            }
            double igv            = totalPostDescuento * 0.18;
            double totalConIgv    = totalPostDescuento + igv;
            double recargoTarjeta = "Tarjeta".equalsIgnoreCase(metodoPago) ? totalPostDescuento * 0.03 : 0;
            double costoDelivery  = delivery.equalsIgnoreCase("Si") ? 5.0 : 0.0;
            double totalFinal     = totalConIgv + recargoTarjeta + costoDelivery;

            PdfPTable tablaTotales = new PdfPTable(2);
            tablaTotales.setWidthPercentage(45);
            tablaTotales.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tablaTotales.setWidths(new float[]{55f, 45f});
            tablaTotales.setSpacingBefore(10f);
            tablaTotales.setSpacingAfter(20f);

            agregarFilaTotal(tablaTotales, "Subtotal:", String.format("S/ %.2f", totalCarrito), fNormal, fNormal, COLOR_BLANCO);
            if (porcentajeDescuento > 0) {
                agregarFilaTotal(tablaTotales, "Descuento (" + (int)porcentajeDescuento + "%):", "- S/ " + String.format("%.2f", montoDescuento), fNormal, fNormal, COLOR_BLANCO);
                agregarFilaTotal(tablaTotales, "Campana:", nombreCampaña, fGris, fGris, COLOR_BLANCO);
            }
            agregarFilaTotal(tablaTotales, "IGV (18%):", String.format("S/ %.2f", igv), fNormal, fNormal, COLOR_BLANCO);
            if (costoDelivery > 0)
                agregarFilaTotal(tablaTotales, "Delivery:", String.format("S/ %.2f", costoDelivery), fNormal, fNormal, COLOR_BLANCO);
            if (recargoTarjeta > 0)
                agregarFilaTotal(tablaTotales, "Recargo Tarjeta (3%):", String.format("S/ %.2f", recargoTarjeta), fNormal, fNormal, COLOR_BLANCO);

            // Fila TOTAL
            PdfPCell cTLbl = new PdfPCell(new Phrase("TOTAL", fTotalLbl));
            cTLbl.setBackgroundColor(COLOR_NEGRO); cTLbl.setPadding(8f); cTLbl.setBorder(Rectangle.NO_BORDER);
            PdfPCell cTVal = new PdfPCell(new Phrase(String.format("S/ %.2f", totalFinal), fTotalVal));
            cTVal.setBackgroundColor(COLOR_NEGRO); cTVal.setPadding(8f); cTVal.setBorder(Rectangle.NO_BORDER);
            cTVal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tablaTotales.addCell(cTLbl);
            tablaTotales.addCell(cTVal);
            document.add(tablaTotales);

            // ══════════════════════════════════════════
            // INFORMACIÓN DE PAGO
            // ══════════════════════════════════════════
            PdfPTable tablaPago = new PdfPTable(1);
            tablaPago.setWidthPercentage(45);
            tablaPago.setHorizontalAlignment(Element.ALIGN_LEFT);
            tablaPago.setSpacingAfter(20f);

            PdfPCell cPago = new PdfPCell();
            cPago.setBorder(Rectangle.BOX);
            cPago.setBorderColor(COLOR_NEGRO);
            cPago.setBorderWidth(1f);
            cPago.setPadding(10f);
            cPago.addElement(new Paragraph("INFORMACION DE PAGO", fSeccion));
            cPago.addElement(new Paragraph(" ", fGris));
            cPago.addElement(new Paragraph("Metodo: " + metodoPago, fNormal));
            cPago.addElement(new Paragraph("Delivery: " + delivery, fNormal));
            if ("Transferencia".equalsIgnoreCase(metodoPago)) {
                cPago.addElement(new Paragraph("Banco: BCP", fNormal));
                cPago.addElement(new Paragraph("Cuenta: 123-456789-0-12", fNormal));
                cPago.addElement(new Paragraph("CCI: 00212300456789012345", fNormal));
            }
            tablaPago.addCell(cPago);
            document.add(tablaPago);

            // ── Pie de página ──
            Paragraph pie = new Paragraph("WWW.LIBRERIAFANNYSTORE.COM", fGris);
            pie.setAlignment(Element.ALIGN_CENTER);
            document.add(pie);

            document.close();
            System.out.println("Comprobante generado: " + rutaArchivo);
            return rutaArchivo;

        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void agregarLineaSeparadora(Document doc) throws DocumentException {
        PdfPTable linea = new PdfPTable(1);
        linea.setWidthPercentage(100);
        linea.setSpacingAfter(15f);
        PdfPCell c = new PdfPCell();
        c.setBorder(Rectangle.BOTTOM);
        c.setBorderColor(COLOR_GRIS);
        c.setBorderWidth(0.5f);
        c.setFixedHeight(1f);
        linea.addCell(c);
        doc.add(linea);
    }

    private static void agregarCeldaTabla(PdfPTable tabla, String texto, Font fuente,
                                           BaseColor color, int alineacion) {
        PdfPCell c = new PdfPCell(new Phrase(texto, fuente));
        c.setBackgroundColor(color);
        c.setPadding(7f);
        c.setBorder(Rectangle.NO_BORDER);
        c.setHorizontalAlignment(alineacion);
        tabla.addCell(c);
    }

    private static void agregarFilaTotal(PdfPTable tabla, String label, String valor,
                                          Font fLabel, Font fValor, BaseColor color) {
        PdfPCell cL = new PdfPCell(new Phrase(label, fLabel));
        cL.setBorder(Rectangle.NO_BORDER);
        cL.setBackgroundColor(color);
        cL.setPaddingTop(4f); cL.setPaddingBottom(4f);

        PdfPCell cV = new PdfPCell(new Phrase(valor, fValor));
        cV.setBorder(Rectangle.NO_BORDER);
        cV.setBackgroundColor(color);
        cV.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cV.setPaddingTop(4f); cV.setPaddingBottom(4f);

        tabla.addCell(cL);
        tabla.addCell(cV);
    }
}