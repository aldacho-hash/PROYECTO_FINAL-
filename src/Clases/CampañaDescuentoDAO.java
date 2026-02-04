package Clases;

import conexiondb.ConexionSQLServer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class CampañaDescuentoDAO {
    public boolean insertarCampaña(CampañaDescuento camp) {
        String connectionString = "jdbc:sqlserver://localhost:1433;databaseName=BD_TPOO;encrypt=false";
        String query = "INSERT INTO CAMPAÑA_DESCUENTO (nombre, fecha_inicio, fecha_fin, descuento) VALUES (?, ?, ?, ?)";
         try (Connection conn = DriverManager.getConnection(connectionString, "lucianoadm", "hilario123");
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, camp.getNombre());
            stmt.setDate(2, java.sql.Date.valueOf(camp.getInicio()));
            stmt.setDate(3, java.sql.Date.valueOf(camp.getFin()));
            stmt.setDouble(4, camp.getPorcentajeDescuento());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
        
    }
        public CampañaDescuento obtenerCampañaActiva() {
        String query =
            "SELECT nombre, fecha_inicio, fecha_fin, descuento " +
            "FROM CAMPAÑA_DESCUENTO " +
            "WHERE fecha_inicio <= GETDATE() AND fecha_fin >= GETDATE()";

        try (Connection conn = ConexionSQLServer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new CampañaDescuento(
                        rs.getString("nombre"),
                        rs.getDate("fecha_inicio").toLocalDate(),
                        rs.getDate("fecha_fin").toLocalDate(),
                        rs.getDouble("descuento")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; 
    }

    public double obtenerDescuentoActivo() {
        String query =
            "SELECT descuento FROM CAMPAÑA_DESCUENTO " +
            "WHERE fecha_inicio <= GETDATE() AND fecha_fin >= GETDATE()";

        try (Connection conn = ConexionSQLServer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("descuento");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
}